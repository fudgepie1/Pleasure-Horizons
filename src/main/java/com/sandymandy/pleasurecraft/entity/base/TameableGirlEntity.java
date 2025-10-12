package com.sandymandy.pleasurecraft.entity.base;

import com.sandymandy.pleasurecraft.advancement.criterion.PleasureCraftCriteria;
import com.sandymandy.pleasurecraft.registries.PleasureCraftTrackedDataRegistry;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandlerFactory;
import com.sandymandy.pleasurecraft.util.PleasureCraftLangUtils;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.util.inventory.GirlInventory;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
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
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;

import java.util.*;

import static com.sandymandy.pleasurecraft.util.Utils.getReadableTameItemName;

public class TameableGirlEntity extends PathAwareEntity implements Tameable {
    private static final TrackedData<Boolean> WAITING_AT_BED = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> LOCKED_STATE = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FROZEN_STATE = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> STRIPPED = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FOLLOWING = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IN_SCENE = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> OVERRIDE_LOOP = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> OVERRIDE_HOLD = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> OVERRIDE_ANIM_PLAYING = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_PLAYER_MODEL_SLIM = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAVING_SEX = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OVERRIDE_ANIM = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> SCENE_ANIM = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> RELATIONSHIP_LEVEL = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> BASE_POS = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Vec3d> PASSENGER_BONE_POSITION = DataTracker.registerData(TameableGirlEntity.class, PleasureCraftTrackedDataRegistry.VEC3D);
    protected static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(TameableGirlEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> OWNER_UUID = DataTracker.registerData(
            TameableGirlEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE
    );
    public Map<String, Boolean> boneVisibility = new HashMap<>();
    public Map<String, Integer> boneColorOverrides = new HashMap<>();
    public Map<String, Identifier> boneTextureOverrides = new HashMap<>();
    public Map<String, Identifier> boneTextureOverridesLayer2 = new HashMap<>();
    public Map<String, Identifier> boneTextureOverridesLayer3 = new HashMap<>();
    public Map<String, Vec2f> boneUVOffsets = new HashMap<>();
    public final Map<EquipmentSlot, Boolean> armorVisibility = new EnumMap<>(EquipmentSlot.class);
    public Vec3d previousVelocity = Vec3d.ZERO;
    public float previousYaw = 0;
    public float passengerYOffset = -0.8f;
    public String currentAnimState = "idle";
    public boolean currentLoopState = false;
    public boolean currentHoldState = false;
    public final GirlInventory inventory = GirlInventory.ofSize();
    private boolean inInventory = false;


    protected TameableGirlEntity(EntityType<? extends TameableGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(WAITING_AT_BED, false);
        builder.add(LOCKED_STATE, false);
        builder.add(FROZEN_STATE, false);
        builder.add(STRIPPED, false);
        builder.add(FOLLOWING, true);
        builder.add(IN_SCENE, false);
        builder.add(OVERRIDE_LOOP, false);
        builder.add(OVERRIDE_HOLD, false);
        builder.add(OVERRIDE_ANIM_PLAYING, false);
        builder.add(IS_PLAYER_MODEL_SLIM, false);
        builder.add(HAVING_SEX, false);
        builder.add(RELATIONSHIP_LEVEL,0);
        builder.add(PASSENGER_BONE_POSITION, Vec3d.ZERO);
        builder.add(BASE_POS, this.getBlockPos());
        builder.add(OVERRIDE_ANIM,"");
        builder.add(SCENE_ANIM,"");
        builder.add(TAMEABLE_FLAGS, (byte)0);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(SITTING, false);
    }

    public void setFollowing(boolean follow) {
        this.dataTracker.set(FOLLOWING, follow);
    }

    public boolean isFollowing() {
        return this.dataTracker.get(FOLLOWING);
    }

    public void setStripped(boolean stripped) {
        this.dataTracker.set(STRIPPED, stripped);
    }

    public boolean isStripped() {
        return this.dataTracker.get(STRIPPED);
    }

    public void setFreeze(boolean locked) {
        this.dataTracker.set(FROZEN_STATE,locked);
    }

    public boolean isFrozenInPlace() {
        return this.dataTracker.get(FROZEN_STATE);
    }

    public void setMovementLockedState(boolean locked) {
        this.dataTracker.set(LOCKED_STATE, locked);
    }

    public boolean isMovementLocked() {
        return this.dataTracker.get(LOCKED_STATE);
    }

    public void setInInventory(boolean state) {
        inInventory = state;
    }

    public boolean isInInventory() {
        return inInventory;
    }

    public void setSceneState(boolean inScene) {
        this.dataTracker.set(IN_SCENE, inScene);
    }

    public boolean isSceneActive() {
        return this.dataTracker.get(IN_SCENE);
    }

    public void setOverrideAnim(String anim){
        this.dataTracker.set(OVERRIDE_ANIM, anim);
    }

    public String getOverrideAnim(){
        return this.dataTracker.get(OVERRIDE_ANIM);
    }

    public void setOverrideLoop(boolean loop){
        this.dataTracker.set(OVERRIDE_LOOP, loop);
    }

    public boolean getOverrideLoopState(){
        return this.dataTracker.get(OVERRIDE_LOOP);
    }

    public void setOverrideHold(boolean hold){
        this.dataTracker.set(OVERRIDE_HOLD, hold);
    }

    public boolean getOverrideHoldState(){
        return this.dataTracker.get(OVERRIDE_HOLD);
    }

    public boolean isWaitingAtBed(){
        return this.dataTracker.get(WAITING_AT_BED);
    }

    public void setWaitingAtBedState(boolean state){
        this.dataTracker.set(WAITING_AT_BED, state);
    }

    public void setIsPlayerModelSlim(boolean isSlim){
        this.dataTracker.set(IS_PLAYER_MODEL_SLIM, isSlim);
    }

    public boolean isPlayerModelSlim(){
        return this.dataTracker.get(IS_PLAYER_MODEL_SLIM);
    }

    public void setHavingSex(boolean state) {
        this.dataTracker.set(HAVING_SEX, state);
    }

    public boolean isHavingSex() {
        return this.dataTracker.get(HAVING_SEX);
    }

    public int getCurrentRelationshipLevel() { return this.dataTracker.get(RELATIONSHIP_LEVEL);}

    public void setCurrentRelationshipLevel(int var) { this.dataTracker.set(RELATIONSHIP_LEVEL, var);}

    public void setPassengerBonePosition(Vec3d position){
        this.dataTracker.set(PASSENGER_BONE_POSITION, position);
    }

    public Vec3d getPassengerBonePosition(){
        return this.dataTracker.get(PASSENGER_BONE_POSITION);
    }

    public void setBasePos(BlockPos block){this.dataTracker.set(BASE_POS, block);}

    public BlockPos getBasePos(){return this.dataTracker.get(BASE_POS);}

    public GirlInventory getInventory() {
        return inventory;
    }

    protected Item getTameItem() {
        return Items.DANDELION;
    }

    public String getGirlID() {
        return "null";
    }

    public String getGirlDisplayName() {
        return PleasureCraftLangUtils.getStringFromKey("entity.pleasurecraft." + getGirlID());
    }

    public int getSizeGUI(){return 20;}

    public float getYAxisGUI(){return 0.0625F;}

    public List<SceneOptions> getSceneOptions() {
        return new ArrayList<>();
    }

    protected int maxRelationshipLevel() {
        try {
            // Get all scene options
            List<SceneOptions> options = getSceneOptions();

            // If null or empty, return default
            if (options == null || options.isEmpty()) {
                return 4;
            }

            // Find max requiredRelationshipLevel
            return options.stream()
                    .map(SceneOptions::requiredRelationshipLevel)
                    .max(Integer::compareTo)
                    .orElse(4); // fallback if stream is empty
        } catch (Exception e) {
            // In case something unexpected happens
            return 4;
        }
    }

    protected Map<EquipmentSlot, List<String>> getArmorBones() {
        Map<EquipmentSlot, List<String>> armor = new HashMap<>();

        armor.put(EquipmentSlot.HEAD, new ArrayList<>(List.of(
                "armorHelmet"
        )));

        armor.put(EquipmentSlot.CHEST, new ArrayList<>(List.of(
                "armorBoobs",
                "armorChest",
                "armorShoulderL",
                "armorShoulderR"
        )));

        armor.put(EquipmentSlot.LEGS, new ArrayList<>(List.of(
                "armorHip",
                "armorPantsLowL",
                "armorPantsUpL",
                "armorPantsLowR",
                "armorPantsUpR",
                "armorBootyL",
                "armorBootyR"
        )));

        armor.put(EquipmentSlot.FEET, new ArrayList<>(List.of(
                "armorShoesL",
                "armorShoesR"
        )));

        return armor;
    }

    public boolean isFoodItem(ItemStack stack) {
        return stack.isIn(ItemTags.WOLF_FOOD);
    }

    @Override
    public boolean shouldRenderName() {
        this.setCustomName(Text.of(getGirlDisplayName()));
        this.setCustomNameVisible(true);
        return true;
    }

    public void setBasePosHere(){
        setBasePos(this.getBlockPos());
    }

    public void teleportToBase() {
        setSitting(true);
        this.teleport(this.getBasePos().getX(), this.getBasePos().getY(), this.getBasePos().getZ(), false);
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item itemInHand = itemStack.getItem();
        if(this.getOverrideAnim().isEmpty()) {
            if (this.isTamed()) {
                if (this.isFoodItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    this.getNavigation().findPathTo(player, 20);
                    this.eat(player, hand, itemStack);
                    FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
                    float f = foodComponent != null ? foodComponent.nutrition() : 1.0F;
                    this.heal(2.0F * f);
                    player.getWorld().sendEntityStatus(this, EntityStatuses.CONSUME_ITEM);
                    return ActionResult.CONSUME;
                }

                if (this.isOwner(player)) {

                    if (itemInHand.equals(getTameItem())) {
                        if(getCurrentRelationshipLevel() < maxRelationshipLevel()){
                            itemStack.decrementUnlessCreative(1, player);
                            player.sendMessage(Text.literal("She Liked The Gift"), true);
                            setCurrentRelationshipLevel(getCurrentRelationshipLevel() + 1);
                            player.getWorld().sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);
                            return ActionResult.CONSUME;
                        }
                        else {
                            return ActionResult.FAIL;
                        }
                    }

                    if (player.isSneaking()) {
                        this.setSitting(!this.isSitting());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget(null);
                        return ActionResult.SUCCESS.noIncrementStat();
                    }
                    else {
                        player.openHandledScreen(new GirlInventoryScreenHandlerFactory(this));
                        this.setInInventory(true);
                        getLookControl().lookAt(player, this.getMaxHeadRotation() + 20, this.getMaxLookPitchChange());
                        return ActionResult.SUCCESS;
                    }

                }
                else {
                    if (itemInHand.equals(getTameItem())) {
                        player.sendMessage(Text.literal("She's Already In A Relationship With Someone"), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            else {

                if (itemStack.isEmpty() && player.isSneaking()) {
                    this.getNavigation().findPathTo(player, 20);
                    player.openHandledScreen(new GirlInventoryScreenHandlerFactory(this));
                    this.setInInventory(true);
                    getLookControl().lookAt(player, this.getMaxHeadRotation() + 20, this.getMaxLookPitchChange());
                    return ActionResult.SUCCESS;
                }

                if (!this.getWorld().isClient) {
                    if (itemInHand.equals(getTameItem()) && !player.isSneaking()) {
                        itemStack.decrementUnlessCreative(1, player);
                        this.tryTame(player);
                        return ActionResult.SUCCESS;
                    } else {
                        // Wrong item OR empty hand (not sneaking)
                        player.sendMessage(Text.literal(
                                "She ignores you. Maybe try giving her a " + getReadableTameItemName(this.getTameItem()) + "."
                        ), true);
                        return ActionResult.FAIL;
                    }
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
            this.setSitting(false); // Ensure the entity is not sitting
            this.setStripped(false);
            this.setCurrentRelationshipLevel(0);
            if(!isTamed() && !isOwner(player)){
                player.sendMessage(Text.literal("§cYou Broke Up With " + getGirlDisplayName()), true);
            }
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);

        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        RegistryWrapper.WrapperLookup registryLookup = this.getWorld().getRegistryManager();
        Inventories.writeNbt(nbt, this.inventory.getItems(), registryLookup);
        nbt.putBoolean("SitSate", this.isSitting());
        nbt.putBoolean("StripState", this.isStripped());
        nbt.putBoolean("SceneState", this.isSceneActive());
        nbt.putInt("RelationshipLevel", this.getCurrentRelationshipLevel());

        nbt.putInt("BaseX", this.getBasePos().getX());
        nbt.putInt("BaseY", this.getBasePos().getY());
        nbt.putInt("BaseZ", this.getBasePos().getZ());

        LazyEntityReference<LivingEntity> lazyEntityReference = this.getOwnerReference();
        if (lazyEntityReference != null) {
            lazyEntityReference.writeNbt(nbt, "Owner");
        }

        nbt.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        RegistryWrapper.WrapperLookup registryLookup = this.getWorld().getRegistryManager();
        Inventories.readNbt(nbt, this.inventory.getItems(), registryLookup);
        this.setSitting(nbt.getBoolean("SitSate").get());
        this.setStripped(nbt.getBoolean("StripState").get());
        this.setCurrentRelationshipLevel(nbt.getInt("RelationshipLevel").get());
        if (nbt.contains("BaseX") && nbt.contains("BaseY") && nbt.contains("BaseZ")) {
            int x = nbt.getInt("BaseX").get();
            int y = nbt.getInt("BaseY").get();
            int z = nbt.getInt("BaseZ").get();
            this.setBasePos(new BlockPos(x, y, z));
        }

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

    @Override
    public void tick() {
        super.tick();
        previousYaw = getYaw();
        previousVelocity = getVelocity();
        this.setMovementLockedState(this.isFrozenInPlace() || this.isWaitingAtBed() || this.isSceneActive());
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

    public Vec3d getPassengerPos() {
        boolean isZero = this.getPassengerBonePosition().lengthSquared() < 1.0E-12; // ~0
        if(isZero){
            return this.getPos().add(1.5, 0.9, 0);
        }
        else {
            return this.getPassengerBonePosition().add(0, this.passengerYOffset, 0);
        }
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        return this.getPassengerPos();
    }

    @Override
    public Vec3d getPassengerRidingPos(Entity passenger) {
        return this.getPassengerPos();
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    protected void dropInventory(ServerWorld world) {
        super.dropInventory(world); // calls standard drop logic
        for (ItemStack stack : this.getInventory().getItems()) {
            if (!stack.isEmpty()) {
                this.dropStack(world,stack);
            }
        }
        this.getInventory().clear();
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
            new PleasureCraftMessages().GlobleMessage(
                    this.getWorld(),
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
                new PleasureCraftMessages().GlobleMessage(
                        this.getWorld(),getGirlDisplayName() + " is busy at the moment");
            }
            return false;
        }
        else{
            return super.damage(world, source, amount);
        }
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!this.isMovementLocked()) {
            super.pushAwayFrom(entity);
        }
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isMovementLocked()) {
            super.takeKnockback(strength, x, z);
        } else {
            this.setVelocity(Vec3d.ZERO); // ensure no leftover knockback velocity
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isMovementLocked();
    }


    @Override
    public void addVelocity(double dx, double dy, double dz) {
        if (!this.isMovementLocked()) {
            super.addVelocity(dx, dy, dz);
        }
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
}
