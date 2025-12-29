package com.sandymandy.pleasurecraft.entity.base.tamable;

import com.sandymandy.pleasurecraft.advancement.criterion.PleasureCraftCriteria;
import com.sandymandy.pleasurecraft.entity.base.GirlEntityScene;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandlerFactory;
import com.sandymandy.pleasurecraft.util.PleasureCraftLangUtils;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.util.managers.TamedGirlManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.sandymandy.pleasurecraft.util.Utils.getPlayerName;
import static com.sandymandy.pleasurecraft.util.Utils.getReadableTameItemName;

public abstract class TameableGirlEntity extends GirlEntityScene implements Tameable {

    protected static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> OWNER_UUID = DataTracker.registerData(
            TameableGirlEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE
    );

    protected TameableGirlEntity(EntityType<? extends GirlEntityScene> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TAMEABLE_FLAGS, (byte)0);
        builder.add(OWNER_UUID, Optional.empty());
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        Item itemInHand = itemStack.getItem();
        if (!this.getWorld().isClient() && this.getOverrideAnim().isEmpty()) {
            if (this.isTamed()) {

                if (this.isOwner(player)) {

                    if (itemInHand.equals(isAttractedTo())) {
                        if (getCurrentRelationshipLevel() < maxRelationshipLevel()) {
                            itemStack.decrementUnlessCreative(1, player);
                            player.sendMessage(Text.literal("She Liked The Gift"), true);
                            setCurrentRelationshipLevel(getCurrentRelationshipLevel() + 1);
                            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
                            return ActionResult.SUCCESS;
                        } else {
                            return ActionResult.PASS;
                        }
                    }

                    if (itemStack.isEmpty()) {
                        if (player.isSneaking()) {
                            this.setSitting(!this.isSitting());
                            this.jumping = false;
                            this.navigation.stop();
                            this.setTarget(null);
                            return ActionResult.SUCCESS.noIncrementStat();
                        }
                        else if (!this.isSceneActive()){
                            player.openHandledScreen(new GirlInventoryScreenHandlerFactory(this));
                            this.setGUIOpenState(true, player);
                            return ActionResult.SUCCESS;
                        }
                    }
                } else {
                    if (itemInHand.equals(isAttractedTo())) {
                        player.sendMessage(Text.of(PleasureCraftLangUtils.getStringFromKey("msg.pleasurecraft.alreadyInRelationship")), true);
                        return ActionResult.FAIL;
                    }
                }
            } else {
                if (itemStack.isEmpty() && player.isSneaking()) {
                    this.getNavigation().findPathTo(player, 20);
                    player.openHandledScreen(new GirlInventoryScreenHandlerFactory(this));
                    this.setGUIOpenState(true, player);
                    return ActionResult.SUCCESS;
                }

                if (itemInHand.equals(isAttractedTo()) && !player.isSneaking()) {
                    itemStack.decrementUnlessCreative(1, player);
                    this.tryTame(player);
                    return ActionResult.SUCCESS;
                } else {
                    // Wrong item OR empty hand (not sneaking)
                    player.sendMessage(Text.literal(
                            "She ignores you. Maybe try giving her a " + getReadableTameItemName(this.isAttractedTo()) + "."
                    ), true);
                    return ActionResult.FAIL;
                }
            }
        }
        return super.interactMob(player, hand);
    }

    private void tryTame(PlayerEntity player) {
        if (this.random.nextInt(3) == 0) {
            this.setTamedBy(player);
            this.navigation.stop();
            setTarget(null);
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            player.sendMessage(Text.literal("You Asked " + getGirlDisplayName() + " Out And She Said §aYes" ), true);
            this.setBasePosHere();
        } else {
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
        }
    }

    public void breakUp(PlayerEntity player) {
        if(!player.getWorld().isClient){
            this.setTamed(false,true); // Mark the entity as untamed
            this.setOwner((LivingEntity) null); // Remove the owner UUID
            TamedGirlManager.get((ServerWorld) this.getWorld()).removeGirl(this.getUuid());
            this.setSitting(false); // Ensure the entity is not sitting
            this.setStripped(false);
            this.dropInventory((ServerWorld) this.getWorld());
            this.setCurrentRelationshipLevel(0);
            if(!isTamed() && !isOwner(player)){
                player.sendMessage(Text.literal("§cYou Broke Up With " + getGirlDisplayName()), true);
            }
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);

        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.isInvulnerableTo(world, source)) return false;

        String damageType = source.getName();
        // If killed by /kill or void, allow normal death
        if (damageType.equals("outOfWorld") || damageType.equals("genericKill")) {
            return super.damage(world, source, amount);
        }

        if(this.isTamed() && (this.getHealth() - amount <= 0.0F) &! (damageType.equals("outOfWorld") || damageType.equals("genericKill") || isMovementLocked())) {
            this.setHealth(getMaxHealth());
            // If basePos is still null, fall back to current position

            // Send a message referencing whichever Pos we have
            PleasureCraftMessages.GlobleMessage(
                    this.getWorld(),
                    getPlayerName((PlayerEntity) this.getOwner()) + "'s " +
                            getGirlDisplayName() + " died and respawned at base: " +
                            this.getBasePos().getX() + ", " +
                            this.getBasePos().getY() + ", " +
                            this.getBasePos().getZ()
            );

            // Drops inventory as if she died
            this.dropInventory(world);

            teleportToBase();


            return false;
        }
        else if(isMovementLocked() &! damageType.equals("outOfWorld") || damageType.equals("genericKill")){
            if(!this.hasPassengers()){
                ((PlayerEntity)this.getOwner()).sendMessage(
                        Text.of(getGirlDisplayName() + " is busy at the moment"), true);
            }
            return false;
        }
        else{
            return super.damage(world, source, amount);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.getWorld() instanceof ServerWorld serverWorld
                && serverWorld.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)
                && this.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.sendMessage(this.getDamageTracker().getDeathMessage());
        }
        super.onDeath(damageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            ServerWorld world = (ServerWorld) this.getWorld();

            if (this.isTamed()) {
                TamedGirlManager.get(world).registerGirl(this);
            }
            else if (TamedGirlManager.get(world).containsGirl(this.getUuid())){
                // not tamed anymore → remove
                TamedGirlManager.get(world).removeGirl(this.getUuid());
            }

            byte b = this.dataTracker.get(TAMEABLE_FLAGS);
            if (isSitting()) {
                this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 1));
            } else {
                this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -2));
            }
        }
    }

    @Override
    public void modelLogic() {
        super.modelLogic();
        this.setBoneSize("boobs", this.getBreastSize(), getBreastMinSize(), getBreastMaxSize());
        this.setBonePos("boobs", this.getBreastOffset());
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    protected void showEmoteParticle(boolean positive) {
        ParticleEffect particleEffect = ParticleTypes.HEART;
        if (!positive) {
            particleEffect = ParticleTypes.SMOKE;
        }

        produceParticles(particleEffect);
    }

    protected void produceParticles(ParticleEffect parameters) {
        for (int i = 0; i < 5; i++) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticleClient(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 0.5f, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES) {
            this.showEmoteParticle(true);
        } else if (status == EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES) {
            this.showEmoteParticle(false);
        } else if (status == EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        }
        else {
            super.handleStatus(status);
        }
    }

    public boolean isTamed() {
        return ((Byte)this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
    }

    public void setTamed(boolean tamed, boolean updateAttributes) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        if (tamed) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 4));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -5));
        }

        if (updateAttributes) {
            this.updateAttributesForTamed();
        }
    }

    protected void updateAttributesForTamed() {
    }

    @Nullable
    @Override
    public LazyEntityReference<LivingEntity> getOwnerReference() {
        return (LazyEntityReference)((Optional)this.dataTracker.get(OWNER_UUID)).orElse((Object)null);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(owner).map(LazyEntityReference::new));
    }

    public void setOwner(@Nullable LazyEntityReference<LivingEntity> owner) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(owner));
    }

    public void setTamedBy(PlayerEntity player) {
        this.setTamed(true, true);
        this.setOwner(player);
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            PleasureCraftCriteria.TAME_GIRL.trigger(serverPlayerEntity, this);        }
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return !this.isOwner(target) && super.canTarget(target);
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Nullable
    @Override
    public Team getScoreboardTeam() {
        Team team = super.getScoreboardTeam();
        if (team != null) {
            return team;
        } else {
            if (this.isTamed()) {
                LivingEntity livingEntity = this.getTopLevelOwner();
                if (livingEntity != null) {
                    return livingEntity.getScoreboardTeam();
                }
            }

            return null;
        }
    }


    @Override
    protected boolean isInSameTeam(Entity other) {
        if (this.isTamed()) {
            LivingEntity livingEntity = this.getTopLevelOwner();
            if (other == livingEntity) {
                return true;
            }

            if (livingEntity != null) {
                return this.isTeamPlayer(other.getScoreboardTeam());
            }
        }

        return super.isInSameTeam(other);
    }

    public void tryTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        if (livingEntity != null) {
            this.tryTeleportNear(livingEntity.getBlockPos());
        }
    }

    public boolean shouldTryTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        return livingEntity != null && this.squaredDistanceTo(this.getOwner()) >= 144.0;
    }

    private void tryTeleportNear(BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            int j = this.random.nextBetween(-3, 3);
            int k = this.random.nextBetween(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = this.random.nextBetween(-1, 1);
                if (this.tryTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.refreshPositionAndAngles(x + 0.5, y, z + 0.5, this.getYaw(), this.getPitch());
            this.navigation.stop();
            return true;
        }
    }



    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this, pos);
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.getWorld().getBlockState(pos.down());
            if (!this.canTeleportOntoLeaves() && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.getBlockPos());
                return this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(blockPos));
            }
        }
    }

    public final boolean cannotFollowOwner() {
        return this.isSitting() || this.hasVehicle() || this.mightBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
    }

    protected boolean canTeleportOntoLeaves() {
        return false;
    }

    @Override
    public void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        LazyEntityReference<LivingEntity> lazyEntityReference = this.getOwnerReference();
        if (lazyEntityReference != null) {
            lazyEntityReference.writeData(view, "Owner");
        }
    }

    @Override
    public void readCustomData(ReadView view) {
        super.readCustomData(view);

        LazyEntityReference<LivingEntity> lazyEntityReference =
                LazyEntityReference.fromDataOrPlayerName(view, "Owner", this.getWorld());

        if (lazyEntityReference != null) {
            try {
                this.dataTracker.set(OWNER_UUID, Optional.of(lazyEntityReference));
                this.setTamed(true, false);
            } catch (Throwable t) {
                this.setTamed(false, true);
            }
        } else {
            this.dataTracker.set(OWNER_UUID, Optional.empty());
            this.setTamed(false, true);
        }
    }



    public class TameableGirlEscapeDangerGoal extends EscapeDangerGoal {
        public TameableGirlEscapeDangerGoal(final double speed, final TagKey<DamageType> dangerousDamageTypes) {
            super(TameableGirlEntity.this, speed, dangerousDamageTypes);
        }

        public TameableGirlEscapeDangerGoal(final double speed) {
            super(TameableGirlEntity.this, speed);
        }

        @Override
        public void tick() {
            if (!TameableGirlEntity.this.cannotFollowOwner() && TameableGirlEntity.this.shouldTryTeleportToOwner()) {
                TameableGirlEntity.this.tryTeleportToOwner();
            }

            super.tick();
        }
    }
}
