package com.sandymandy.pleasurecraft.entity.base;

import com.sandymandy.pleasurecraft.advancement.criterion.PleasureCraftCriteria;
import com.sandymandy.pleasurecraft.util.inventory.GirlInventory;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TameableGirlEntity extends PathAwareEntity implements Tameable {
     /**
     * The tracked flags of tameable entities. Has the {@code 1} flag for {@linkplain
     * #isInSittingPose() sitting pose} and the {@code 4} flag for {@linkplain
     * #isTamed() tamed}.
     */
     protected static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> OWNER_UUID = DataTracker.registerData(
            TameableGirlEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE
    );
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public final GirlInventory inventory = GirlInventory.ofSize();
    public Vec3d prevVelocity = Vec3d.ZERO;


    protected TameableGirlEntity(EntityType<? extends TameableGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TAMEABLE_FLAGS, (byte)0);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(SITTING, false);
    }

    public GirlInventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return inventory.getArmorStack(slot);
    }

    @Override
    public ItemStack getMainHandStack() {
        return inventory.getHandStack();
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        inventory.setArmorStack(slot, stack);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        LazyEntityReference<LivingEntity> lazyEntityReference = this.getOwnerReference();
        if (lazyEntityReference != null) {
            lazyEntityReference.writeNbt(nbt, "Owner");
        }

        nbt.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        LazyEntityReference<LivingEntity> lazyEntityReference = LazyEntityReference.fromNbtOrPlayerName(nbt, "Owner", this.getWorld());
        if (lazyEntityReference != null) {
            try {
                this.dataTracker.set(OWNER_UUID, Optional.of(lazyEntityReference));
                this.setTamed(true, false);
            } catch (Throwable var4) {
                this.setTamed(false, true);
            }
        } else {
            this.dataTracker.set(OWNER_UUID, Optional.empty());
            this.setTamed(false, true);
        }

        this.setSitting(nbt.getBoolean("Sitting").get());
        this.setInSittingPose(this.isSitting());
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public boolean beforeLeashTick(Entity leashHolder, float distance) {
        if (this.isInSittingPose()) {
            if (distance > 10.0F) {
                this.detachLeash();
            }

            return false;
        } else {
            return super.beforeLeashTick(leashHolder, distance);
        }
    }

    protected void showEmoteParticle(boolean positive) {
        ParticleEffect particleEffect = ParticleTypes.HEART;
        if (!positive) {
            particleEffect = ParticleTypes.SMOKE;
        }

        for (int i = 0; i < 7; i++) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticleClient(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES) {
            this.showEmoteParticle(true);
        } else if (status == EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES) {
            this.showEmoteParticle(false);
        } else {
            super.handleStatus(status);
        }
    }

    public boolean isTamed() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
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

    public boolean isInSittingPose() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
    }

    public void setInSittingPose(boolean inSittingPose) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        if (inSittingPose) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 1));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -2));
        }
    }

    @Nullable
    @Override
    public LazyEntityReference<LivingEntity> getOwnerReference() {
        return (LazyEntityReference<LivingEntity>)this.dataTracker.get(OWNER_UUID).orElse(null);
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
        return this.isOwner(target) ? false : super.canTarget(target);
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        return true;
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
            LivingEntity livingEntity = this.getOwner();
            if (other == livingEntity) {
                return true;
            }

            if (livingEntity != null) {
                return this.isTeamPlayer(other.getScoreboardTeam());
            }
        }

        return super.isInSameTeam(other);
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

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        this.setTarget(null);
        this.dataTracker.set(SITTING, sitting);
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

    public class TameableEscapeDangerGoal extends EscapeDangerGoal {
        public TameableEscapeDangerGoal(final double speed, final TagKey<DamageType> dangerousDamageTypes) {
            super(TameableGirlEntity.this, speed, dangerousDamageTypes);
        }

        public TameableEscapeDangerGoal(final double speed) {
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

    protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
        int i = stack.getCount();
        UseRemainderComponent useRemainderComponent = stack.get(DataComponentTypes.USE_REMAINDER);
        stack.decrementUnlessCreative(1, player);
        if (useRemainderComponent != null) {
            ItemStack itemStack = useRemainderComponent.convert(stack, i, player.isInCreativeMode(), player::giveOrDropStack);
            player.setStackInHand(hand, itemStack);
        }
    }
}
