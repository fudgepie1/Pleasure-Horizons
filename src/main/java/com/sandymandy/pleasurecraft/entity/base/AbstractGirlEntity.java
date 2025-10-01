package com.sandymandy.pleasurecraft.entity.base;

import com.mojang.authlib.GameProfile;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.ai.goal.*;
import com.sandymandy.pleasurecraft.networking.S2C.ClothingArmorVisibilityS2CPacket;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandlerFactory;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.registries.PleasureCraftTrackedData;
import com.sandymandy.pleasurecraft.util.SceneOptions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;

import java.util.*;


public abstract class AbstractGirlEntity extends TameableGirlEntity implements GeoEntity {
    private static final TrackedData<Boolean> WAITING_AT_BED = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> LOCKED_STATE = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FROZEN_STATE = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> STRIPPED = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FOLLOWING = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IN_SCENE = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> OVERRIDE_LOOP = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> OVERRIDE_HOLD = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> OVERRIDE_ANIM_PLAYING = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_PLAYER_MODEL_SLIM = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAVING_SEX = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OVERRIDE_ANIM = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> SCENE_ANIM = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> RELATIONSHIP_LEVEL = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Vec3d> PASSENGER_BONE_POSITION = DataTracker.registerData(AbstractGirlEntity.class, PleasureCraftTrackedData.VEC3D);
    private static final TrackedData<BlockPos> BASE_POS = DataTracker.registerData(AbstractGirlEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public Map<String, Boolean> boneVisibility = new HashMap<>();
    public Map<String, Identifier> boneTextureOverrides = new HashMap<>();
    public Map<String, Identifier> playerTexture = new HashMap<>();
    public Map<String, Vec2f> boneUVOffsets = new HashMap<>();
    public final Map<EquipmentSlot, Boolean> armorVisibility = new EnumMap<>(EquipmentSlot.class);
    private LivingEntity attackTarget;
    public Vec3d previousVelocity = Vec3d.ZERO;
    private int ticksSinceLastHit;
    private static final int MAX_TICKS_NO_HIT = 20 * 20;
    public float previousYaw = 0;
    public float passengerYOffset = 0f;
    public String currentAnimState = "idle";
    public boolean currentLoopState = false;
    public boolean currentHoldState = false;
    private boolean requestStrip = false;
    private boolean requestMoveToBed = false;
    private boolean inInventory = false;
    public SceneOptions stripOptions = SceneOptions.EMPTY;
    public BlockPos targetBedPos;
    private boolean requestMoveToPlayer;

    protected Item getTameItem() {
        return Items.DANDELION;
    }

    protected String getGirlDisplayName() {
        return "Null";
    }

    public String getGirlID() {
        return "null";
    }

    protected int getMaxRelationshipLevel(){return 8;}

    public int getSizeGUI(){return 20;}

    public float getYAxisGUI(){return 0.0625F;}

    public List<SceneOptions> getSceneOptions() {
        return new ArrayList<>();
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

    protected AbstractGirlEntity(EntityType<? extends TameableGirlEntity> entityType, World world) {
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
    }




    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new GirlSitGoal(this));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new TameableEscapeDangerGoal(1.5D, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
        this.goalSelector.add(3, new DoorInteractGoal(this) {
            @Override
            protected boolean isDoorOpen() {
                return super.isDoorOpen();
            }
        });
        this.goalSelector.add(4, new GirlAttackGoal(this, 1.5, false));
        this.goalSelector.add(5, new ConditionalGoal(new GirlFollowOwnerGoal(this, 1.0, 10.0F, 2.0F), this::isFollowing));
        this.goalSelector.add(6, new TemptGoal(this, 1.25D, Ingredient.ofItems(getTameItem()), false));
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(8, new ConditionalGoal(new LookAtEntityGoal(this, PlayerEntity.class, 6.0F),() -> !isMovementLocked()));
        this.goalSelector.add(9, new ConditionalGoal(new LookAroundGoal(this),() -> !isMovementLocked()));
        this.targetSelector.add(1, new ConditionalGoal(new GirlTrackOwnerAttackerGoal(this), this::isFollowing));
        this.targetSelector.add(2, new ConditionalGoal(new GirlAttackWithOwnerGoal(this, AbstractGirlEntity.class), this::isFollowing));
        this.targetSelector.add(3, new RevengeGoal(this, PlayerEntity.class, AbstractGirlEntity.class));
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
                        if(getCurrentRelationshipLevel() < getMaxRelationshipLevel()){
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
                                "She ignores you. Maybe try giving her a " + getReadableTameItemName() + "."
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

    public void toggleModelBones(List<String> bones, boolean visible){
        if(!getWorld().isClient){
            return;
        }

        if (this.boneVisibility == null) {
            this.boneVisibility = new HashMap<>();
        }


        for (String boneName : bones) {
            this.boneVisibility.put(boneName, visible);
        }
    }


    public void overrideBoneTexture(String boneName, Identifier texture) {
        if (this.boneTextureOverrides == null) this.boneTextureOverrides = new HashMap<>();
        this.boneTextureOverrides.put(boneName, texture);
    }

    public void overrideBoneUV(List<String> bones, float uOffset, float vOffset) {
        if (this.boneUVOffsets == null) this.boneUVOffsets = new HashMap<>();

        for (String boneName : bones) {
            this.boneUVOffsets.put(boneName, new Vec2f(uOffset, vOffset));
        }
    }

    @Nullable
    public Vec2f getBoneUVOffset(String boneName) {
        if (boneUVOffsets != null && boneUVOffsets.containsKey(boneName)) {
            return boneUVOffsets.get(boneName);
        }
        return null;
    }

    @Nullable
    public Identifier getBoneTexture(String boneName) {
        if (boneTextureOverrides != null && boneTextureOverrides.containsKey(boneName)) {
            return boneTextureOverrides.get(boneName);
        }
        return null;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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
    }

    public void tick() {
        super.tick();
        previousYaw = getYaw();
        previousVelocity = getVelocity();
        updateClothingAndArmor();
        applySkinToBone((PlayerEntity) this.getFirstPassenger());
        this.setMovementLockedState(this.isFrozenInPlace() || this.isWaitingAtBed() || this.isSceneActive());
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (attackTarget != null) {
            ticksSinceLastHit++;

            if (ticksSinceLastHit >= MAX_TICKS_NO_HIT) {
                // Lost interest — stop attacking
                this.setTarget(null);
                attackTarget = null;
                ticksSinceLastHit = 0;
            }
        }

        if(isInInventory()){
            this.navigation.stop();
        }
    }

    public void requestStrip() {
        requestStrip(false, null, null);
    }

    public void requestStrip(SceneOptions options) {
        requestStrip(false, null, options);
    }

    public void requestStrip(boolean sendMessage, @Nullable PlayerEntity player, @Nullable SceneOptions options) {
        this.requestStrip = true;

        if(options != null){
            this.stripOptions = options;
        }

        if(sendMessage){
            if(player != null) messageAsEntity(player,"okie then, just for you tho ^_~");
            else messageAsEntity("okie then, just for you tho ^_~");
        }
    }

    public boolean shouldStrip() {
        if (requestStrip) {
            requestStrip = false;
            return true;
        }
        return false;
    }


    private void updateClothingAndArmor() {
        if (this.getWorld().isClient()) return;

        boolean stripped = isStripped();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            boolean hasArmor = !this.inventory.getArmorStack(slot).isEmpty();
            armorVisibility.put(slot, hasArmor &! stripped);
        }


        List<Boolean> armorList = Arrays.stream(EquipmentSlot.values())
                .map(s -> armorVisibility.getOrDefault(s, false))
                .toList();


        ClothingArmorVisibilityS2CPacket packet =
                new ClothingArmorVisibilityS2CPacket(this.getId(), armorList);

        for (ServerPlayerEntity player : Objects.requireNonNull(this.getServer()).getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, packet);
        }
    }

    public void applyClothingAndArmor() {
        if (!this.getWorld().isClient()) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            List<String> armorBones = getArmorBones().get(slot);
            if (armorBones != null) {
                toggleModelBones(armorBones, armorVisibility.getOrDefault(slot, false));
                // Special rule: hide vagina if armor is in legs slot
                if (slot == EquipmentSlot.LEGS) {
                    boolean legsCovered = armorVisibility.getOrDefault(slot, false);
                    toggleModelBones(Collections.singletonList("vagina"), !legsCovered);
                }
            }

            if (!this.inventory.getArmorStack(slot).isEmpty()) {
                String typeName = this.inventory.getArmorStack(slot).getItem().toString().toLowerCase(); // get item name
                float uv = getArmorU(typeName);
                this.overrideBoneUV(this.getArmorBones().get(slot),uv,0);
            }
        }
    }


    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);

        if (target != null) {
            attackTarget = target;
            ticksSinceLastHit = 0; // reset countdown on new target
        } else {
            attackTarget = null;
            ticksSinceLastHit = 0;
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        boolean success = super.tryAttack(world, target);

        if (success && target == attackTarget) {
            ticksSinceLastHit = 0; // reset timer on successful hit
        }

        return success;
    }


    public Vec3d getPassengerBone(){
        return getPassengerBonePosition();
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        return this.getPassengerBone().add(0,1,0);
    }

    @Override
    public Vec3d getPassengerRidingPos(Entity passenger) {
        /*This is not based on the local coordinates from the entity. it is the global coordinates based on the world. It also has a base offset of -0.6 on the Y Axis
        * This is also based on the client not the server*/
        if(this.getPassengerBone() == Vec3d.ZERO){
            return this.getPos().add(0,this.passengerYOffset,0);
        }
        else {
            return this.getPassengerBone().add(0,this.passengerYOffset,0);
        }
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


    public String getReadableTameItemName() {
        Item tameItem = this.getTameItem();
        Identifier id = Registries.ITEM.getId(tameItem);

        if (id != null) {
            String path = id.getPath(); // e.g., "blue_allium"

            // Capitalize each word split by underscores
            String[] words = path.split("_");
            StringBuilder formatted = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    formatted.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1))
                            .append(" ");
                }
            }

            return formatted.toString().trim(); // "Blue Allium"
        } else {
            return "Unknown Item";
        }
    }

    public void messageAsEntity(String message){
        this.messageAsEntity(null,message);
    }

    public void messageAsEntity(@Nullable PlayerEntity playerEntity, String message){
        if(this.getWorld().isClient()) return;
        String finalMessage = "<"+getGirlDisplayName()+"> " + message;

        if(playerEntity == null){
            new PleasureCraftMessages().GlobleMessage(this.getWorld(), finalMessage);
        }
        else {
            new PleasureCraftMessages().PlayerSpecificMessage(playerEntity,finalMessage);
        }

    }

    public void messageAsOwner(@Nullable PlayerEntity playerEntity, String message) {
        if (this.getWorld().isClient()) return;
        if (playerEntity == null) return;
        GameProfile profile = playerEntity.getGameProfile();
        String finalMessage = "<" + profile.getName() + "> " + message;
        new PleasureCraftMessages().PlayerSpecificMessage(playerEntity, finalMessage);
    }


    private float getArmorU(String armorType){
        if (armorType.contains("turtle")) return 0.10546875f;
        if (armorType.contains("leather")) return 0.0703125f;
        if (armorType.contains("iron")) return 0.03515625f;
        if (armorType.contains("chain")) return 0.052734375f;
        if (armorType.contains("gold")) return 0.0176f;
        if (armorType.contains("netherite")) return 0.087890625f;
        return 0f;
    }

    public void applySkinToBone(PlayerEntity player) {
        Identifier texture;

        // If the playerTexture is null then initialize it
        if (this.playerTexture == null) this.playerTexture = new HashMap<>();

        // Set the base of the player model to Steve so if there isn't a player it has a fallback
        this.overrideBoneTexture("steve", Identifier.of(PleasureCraft.MOD_ID, "textures/player/steve.png"));

        if (player != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerSkinProvider skinProvider = client.getSkinProvider();

            GameProfile profile = player.getGameProfile();

            // Get the skin identifier
            texture = skinProvider.getSkinTextures(profile).texture();
            setIsPlayerModelSlim(skinProvider.getSkinTextures(profile).model() == SkinTextures.Model.SLIM);

            // if isn't null set the player texture
            if (texture != null) {
                this.playerTexture.put("steve", texture);
            }
        }
    }

    public void requestMoveToBed() {
        this.requestMoveToBed = true;
    }

    public boolean shouldMoveToBed() {
        if (requestMoveToBed) {
            requestMoveToBed = false;
            return true;
        }
        return false;
    }

    public void requestMoveToPlayer() {
        this.requestMoveToPlayer = true;
    }

    public boolean shouldMoveToPlayer() {
        if (requestMoveToPlayer) {
            requestMoveToPlayer = false;
            return true;
        }
        return false;
    }


}
