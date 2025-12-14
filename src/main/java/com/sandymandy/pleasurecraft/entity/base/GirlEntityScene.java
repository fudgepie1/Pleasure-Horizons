package com.sandymandy.pleasurecraft.entity.base;

import com.mojang.authlib.GameProfile;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.entity.ai.goal.*;
import com.sandymandy.pleasurecraft.networking.C2S.*;
import com.sandymandy.pleasurecraft.networking.S2C.ClothingArmorVisibilityS2CPacket;
import com.sandymandy.pleasurecraft.networking.S2C.PlayAttackAnimationS2CPacket;
import com.sandymandy.pleasurecraft.networking.S2C.PlayCumHudAnimationS2CPacket;
import com.sandymandy.pleasurecraft.registries.PleasureCraftSoundEventRegistry;
import com.sandymandy.pleasurecraft.registries.PleasureCraftTrackedDataRegistry;
import com.sandymandy.pleasurecraft.registries.SceneKeyframeEventRegistry;
import com.sandymandy.pleasurecraft.util.PleasureCraftLangUtils;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.util.Utils;
import com.sandymandy.pleasurecraft.util.variables.Scene;
import com.sandymandy.pleasurecraft.util.variables.ScenePhase;
import com.sandymandy.pleasurecraft.util.variables.SceneType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.event.KeyFrameEvent;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;

import java.util.*;

public class GirlEntityScene extends GirlEntity implements GeoEntity {
    private static final TrackedData<Scene> CURRENT_SCENE = DataTracker.registerData(GirlEntityScene.class, PleasureCraftTrackedDataRegistry.SCENE);
    private static final TrackedData<ScenePhase> CURRENT_SCENE_PHASE = DataTracker.registerData(GirlEntityScene.class, PleasureCraftTrackedDataRegistry.SCENE_PHASE);
    private static final TrackedData<String> ANIMATION_KEY_FRAME_EVENT = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> CURRENT_SEX_ANIM = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Float> SCENE_PROGRESS = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> CUM_THRESHOLD = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> STATIONARY_LOOP = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> STATIONARY_LOOP_THRESHOLD = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> THRUSTING = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> INTRO_INDEX = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PREGNANCY_TICKS = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PREGNANCY_DURATION = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> STATIONARY_INDEX = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Optional<UUID>> CURRENT_SCENE_PLAYER = DataTracker.registerData(GirlEntityScene.class, PleasureCraftTrackedDataRegistry.OPTIONAL_UUID);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public BlockPos targetBedPos;
    public Scene stripOptions = Scene.EMPTY;
    private boolean requestStrip = false;
    private boolean requestMoveToBed = false;
    private boolean requestMoveToPlayer;
    private boolean requestWaitForPlayer;
    private String lastSceneAnim = "";
    public String passengerBoneName = "boyCam"; //The name of the bone that the player snaps to when in a scene
    private String lastSoundKey = null;
    BlockPos bedPos;
    private static final int PREGNANCY_DUR = 20 * 60 * 5; // 5 minutes
    private static final float PROGRESS_SPEED = 0.1f;
    private boolean swinging = false;
    private long lastSwing = 0L;

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected GirlEntityScene(EntityType<? extends GirlEntityScene> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CURRENT_SCENE, Scene.EMPTY);
        builder.add(CURRENT_SCENE_PHASE, ScenePhase.NONE);
        builder.add(ANIMATION_KEY_FRAME_EVENT,"");
        builder.add(CURRENT_SEX_ANIM,"");
        builder.add(SCENE_PROGRESS,0f);
        builder.add(CUM_THRESHOLD,5f);
        builder.add(STATIONARY_LOOP,0);
        builder.add(STATIONARY_LOOP_THRESHOLD,0);
        builder.add(PREGNANCY_TICKS,0);
        builder.add(PREGNANCY_DURATION, PREGNANCY_DUR);
        builder.add(THRUSTING,false);
        builder.add(INTRO_INDEX, 0);
        builder.add(STATIONARY_INDEX, 0);
        builder.add(CURRENT_SCENE_PLAYER, Optional.empty());

    }

    public void setCurrentScene(Scene scene){
        this.dataTracker.set(CURRENT_SCENE, scene);
    }

    public Scene getCurrentScene(){
        return this.dataTracker.get(CURRENT_SCENE);
    }

    public void setCurrentScenePhase(ScenePhase phase){
        this.dataTracker.set(CURRENT_SCENE_PHASE, phase);
    }

    public ScenePhase getCurrentScenePhase(){
        return this.dataTracker.get(CURRENT_SCENE_PHASE);
    }

    public void setCurrentSexAnim(String anim){
        this.dataTracker.set(CURRENT_SEX_ANIM, anim);
    }

    public String getCurrentSexAnim() {
        return this.dataTracker.get(CURRENT_SEX_ANIM);
    }

    public void setAnimationKeyFrameEventState(String  str){
        this.dataTracker.set(ANIMATION_KEY_FRAME_EVENT, str);
    }

    public String getAnimationKeyFrameEvent(){
        return this.dataTracker.get(ANIMATION_KEY_FRAME_EVENT);
    }

    public void setThrusting(boolean thrust){
        this.dataTracker.set(THRUSTING, thrust);
    }

    public boolean isThrusting(){
        return this.dataTracker.get(THRUSTING);
    }

    public void setIntroIndex(int num){
        this.dataTracker.set(INTRO_INDEX, num);
    }

    public int getIntroIndex(){
        return this.dataTracker.get(INTRO_INDEX);
    }

    public void setStationaryIndex(int num){
        this.dataTracker.set(STATIONARY_INDEX, num);
    }

    public int getStationaryIndex(){
        return this.dataTracker.get(STATIONARY_INDEX);
    }

    public void setSceneProgress(float progress){
        this.dataTracker.set(SCENE_PROGRESS, progress);
    }

    public float getSceneProgress(){
        return this.dataTracker.get(SCENE_PROGRESS);
    }

    public void setStationaryLoop(int progress){
        this.dataTracker.set(STATIONARY_LOOP, progress);
    }

    public int getStationaryLoop(){
        return this.dataTracker.get(STATIONARY_LOOP);
    }

    public void setStationaryLoopThreshold(int progress){
        this.dataTracker.set(STATIONARY_LOOP_THRESHOLD, progress);
    }

    public int getStationaryLoopThreshold(){
        return this.dataTracker.get(STATIONARY_LOOP_THRESHOLD);
    }

    public void setCumThreshold(float threshold){
        this.dataTracker.set(CUM_THRESHOLD, threshold);
    }

    public float getCumThreshold(){
        return this.dataTracker.get(CUM_THRESHOLD);
    }

    public void setBoneVisibility(List<String> bones, boolean visible){
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

    public void setPregnancyDuration(int progress){
        this.dataTracker.set(PREGNANCY_DURATION, progress);
    }

    public int getPregnancyDuration(){
        return this.dataTracker.get(PREGNANCY_DURATION);
    }

    public void setPregnancyTicks(int progress){
        this.dataTracker.set(PREGNANCY_TICKS, progress);
    }

    public int getPregnancyTicks(){
        return this.dataTracker.get(PREGNANCY_TICKS);
    }

    public @Nullable PlayerEntity getScenePlayer() {
        if (this.getWorld().isClient()) {
            return getScenePlayerClient();
        } else {
            return getScenePlayerServer();
        }
    }

    public @Nullable ServerPlayerEntity getScenePlayerServer() {
        Optional<UUID> opt = this.getDataTracker().get(CURRENT_SCENE_PLAYER);

        if (opt.isEmpty()) return null;
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return null;

        return serverWorld.getServer()
                .getPlayerManager()
                .getPlayer(opt.get());
    }

    @Environment(EnvType.CLIENT)
    public @Nullable PlayerEntity getScenePlayerClient() {
        Optional<UUID> opt = this.getDataTracker().get(CURRENT_SCENE_PLAYER);

        if (opt.isEmpty()) return null;
        if (!(this.getWorld() instanceof ClientWorld clientWorld)) return null;

        UUID uuid = opt.get();

        // ClientWorld keeps all client-side player entities
        return clientWorld.getPlayers()
                .stream()
                .filter(p -> p.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }


    public void setScenePlayer(@Nullable PlayerEntity player) {
        if (player == null) {
            this.getDataTracker().set(CURRENT_SCENE_PLAYER, Optional.empty());
        } else {
            this.getDataTracker().set(CURRENT_SCENE_PLAYER, Optional.of(player.getUuid()));
        }
    }

    public void overrideBoneTexture(String boneName, Identifier texture) {
        this.overrideBoneTexture(List.of(boneName), texture);
    }

    public void overrideBoneTexture(List<String> bones, Identifier texture) {
        if (this.boneTextureOverrides == null) this.boneTextureOverrides = new HashMap<>();

        for (String boneName : bones) {
            this.boneTextureOverrides.put(boneName, texture);
        }
    }

    public void overrideBoneTextureLayer2(String bones, Identifier texture) {
        if (this.boneTextureOverridesLayer2 == null) this.boneTextureOverridesLayer2 = new HashMap<>();

        this.boneTextureOverridesLayer2.put(bones, texture);
    }

    public void overrideBoneTextureLayer3(String bones, Identifier texture) {
        if (this.boneTextureOverridesLayer3 == null) this.boneTextureOverridesLayer3 = new HashMap<>();
        this.boneTextureOverridesLayer3.put(bones, texture);
    }

    public void overrideBoneUV(List<String> bones, float uOffset, float vOffset) {
        if (this.boneUVOffsets == null) this.boneUVOffsets = new HashMap<>();

        for (String boneName : bones) {
            this.boneUVOffsets.put(boneName, new Vec2f(uOffset, vOffset));
        }
    }

    public void overrideBoneColor(List<String> bones, Integer hex) {
        if (this.boneColorOverrides == null) this.boneColorOverrides = new HashMap<>();

        for(String bone : bones) {
            this.boneColorOverrides.put(bone, Utils.withFullAlpha(hex));
        }

    }

    public void setBonePos(String bone, float x, float y, float z) {
        this.setBonePos(bone, new Vec3d(x, y, z));
    }

    public void setBonePos(String bone, Vec3d pos) {
        if (this.bonePositionOffset == null) this.bonePositionOffset = new HashMap<>();

        this.bonePositionOffset.put(bone, pos);
    }

    public void setBoneSize(String bone, float x, float y, float z, float min, float max) {
        if (this.boneSizeOverrides == null) this.boneSizeOverrides = new HashMap<>();

        if(min != 0 && max != 0) {
            x = Math.clamp(x, min, max);
            y = Math.clamp(y, min, max);
            z = Math.clamp(z, min, max);
        }
        this.boneSizeOverrides.put(bone, new Vec3d(x,y,z));

    }

    public void setBoneSize(String bone, int size, int min, int max) {
        float finalSize = (float) size / 100;
        if(min == 0 && max == 0){
            setBoneSize(bone, finalSize, finalSize, finalSize, 0, 0);
            return;
        }
        float finalMin = (float) min / 100;
        float finalMax = (float) max / 100;
        setBoneSize(bone, finalSize, finalSize, finalSize, finalMin, finalMax);
    }

    public void setBoneSize(String bone, int size) {
        setBoneSize(bone, size, 0, 0);
    }


    public void setBoneSize(String bone, int x, int y, int z, int min, int max) {
        float finalX = (float) x / 100;
        float finalY = (float) y / 100;
        float finalZ = (float) z / 100;
        float finalMin = (float) min / 100;
        float finalMax = (float) max / 100;
        setBoneSize(bone, finalX, finalY, finalZ, finalMin, finalMax);
    }

    public float getPregnancyProgress() {
        if (!isPregnant()) return 0f;
        return 1f - (getPregnancyTicks() / (float) getPregnancyDuration());
    }

    public void startScene(PlayerEntity player, Scene option) {
        if (this.isSceneActive()) return;
        this.setScenePlayer(player);
        if(isPregnant()){
            this.getScenePlayer().sendMessage(Text.translatable("msg.pleasurecraft.isPregnant"), false);
            return;
        }
        if (this.getScenePlayer() == null) return;
        if (this.isSitting()) this.setSitting(false);

        this.setCurrentScene(option);

        if (!this.isStripped() && option.needsToStrip()){
            this.requestStrip(option);
            return;
        }

        if(this.useUpRelationShipLevels()) this.setCurrentRelationshipLevel(this.getCurrentRelationshipLevel() - option.requiredRelationshipLevel());

        if (option.sceneType().equals(SceneType.ON_BED)) {
            //  Check for a bed before starting
            Utils.BlockInfo bedInfo = Utils.findNearbyBed(
                    this.getWorld(),
                    this.getBlockPos(),
                    15// radius
            );

            if (bedInfo == null) {
                this.messageAsEntity(false, PleasureCraftLangUtils.getStringFromKey("msg.pleasurecraft.noBedFound"));
                return;
            }

            PleasureCraft.usedBeds.put(this.getUuid(), bedInfo.pos());

            //  Store target bed pos in entity so the BedGoal can use it
            this.targetBedPos = bedInfo.pos();
            bedPos = bedInfo.pos();


            this.requestMoveToBed();
            return;
        }

        if (option.sceneType().equals(SceneType.ON_PLAYER)) {
            this.requestMoveToPlayer();
            return;
        }

        if(option.sceneType().equals(SceneType.STATIONARY_CONTACT)){
            this.requestWaitForPlayer();
            return;
        }

        if (option.sceneType().equals(SceneType.STATIONARY_INTRO)) {
            this.startStationaryIntro(option);
            return;
        }

        this.startStationaryLoop(option);
    }

    public void startRidingScene(PlayerEntity player) {
        SceneType type = getCurrentScene().sceneType();
        if (type.equals(SceneType.STATIONARY_INTRO) || type.equals(SceneType.STATIONARY))
            return;
        player.setInvisible(true);
        this.getScenePlayer().sendMessage(Text.of("msg.pleasurecraft.canGoInToFreeCam"), true);
        this.setSceneProgress(0f);
        this.setCumThreshold(getCurrentScene().cumThreshold());
        setThrusting(false);
        this.targetBedPos = null;
        this.getScenePlayer().startRiding(this, false);
        setIntroIndex(0);
        lastSceneAnim = "";
        playPhase(ScenePhase.INTRO);

        this.setSceneState(true);
    }

    private void startStationaryIntro(Scene option) {
        this.setSceneState(true);
        this.setSceneProgress(0f);
        this.setCurrentScenePhase(ScenePhase.STATIONARY_INTRO);
        this.lastSceneAnim = "";
        this.setStationaryIndex(0);
        this.setStationaryLoop(0);
        this.setStationaryLoopThreshold(option.amountOfLoops());
    }

    private void startStationaryLoop(Scene option) {
        this.setSceneState(true);
        this.setSceneProgress(0f);
        this.setCurrentScenePhase(ScenePhase.STATIONARY);
        this.lastSceneAnim = "";
        this.setStationaryLoop(0);
        this.setStationaryLoopThreshold(option.amountOfLoops());

    }

    public void stopScene() {
        if (!this.isSceneActive()) return;
        if(this.getWorld().isClient()){
            ClientPlayNetworking.send(new StopSceneOnServerC2SPacket(this.getId()));
            return;
        }

        PleasureCraft.usedBeds.remove(this.getUuid());
        if(this.getScenePlayer() != null) PleasureCraft.activeScenes.remove(this.getScenePlayer().getUuid());
        setIntroIndex(0);
        setStationaryIndex(0);
        this.setSceneProgress(0f);
        onSceneStop();
        setCurrentScenePhase(ScenePhase.NONE);
        this.getNavigation().stop();
        if(this.getScenePlayer() != null) this.setScenePlayer(null);
    }

    private void onSceneStop() {
        if (this.hasPassengers()) {
            this.removeAllPassengers();
        }
        if(this.getScenePlayer() != null) this.getScenePlayer().setInvisible(false);
    }

    public void playPhase(ScenePhase phase) {
        if (this.getWorld().isClient()) {
            ClientPlayNetworking.send(new ScenePhaseSyncC2SPacket(this.getId(), phase));
            return;
        }
        setCurrentScenePhase(phase);
        lastSceneAnim = "";
        if (phase != ScenePhase.INTRO) setIntroIndex(0);
    }

    public void tryTriggerCum() {
        if (this.isSceneActive() && this.getSceneProgress() >= this.getCumThreshold() && getCurrentScenePhase() != ScenePhase.CUM) {
            playPhase(ScenePhase.CUM);

            if (!this.getWorld().isClient()) {
                if (this.getFirstPassenger() instanceof ServerPlayerEntity rider) {
                    // send packet to rider only
                    ServerPlayNetworking.send(rider, new PlayCumHudAnimationS2CPacket());
                }
            }
        }
    }

    private PlayState setSceneAnimIfChanged(AnimationTest<?> state, String anim, Animation.LoopType loop) {
        if (anim == null || anim.isEmpty()) return null;

        // Only reset if different from last
        if (!anim.equals(lastSceneAnim)) {
            state.resetCurrentAnimation();
            lastSceneAnim = anim;
            return state.setAndContinue(RawAnimation.begin().then(getAnimationPath(anim), loop));
        }
        return PlayState.CONTINUE;
    }

    private void onSceneActive(){
        this.applySkinToBone(this.getScenePlayer());
        if(this.getWorld().isClient()) return;

        if(bedPos != null && isBedScene() && !Utils.checkForBlockAt(this.getWorld(),bedPos,null,BlockTags.BEDS)){
            stopScene();
        }
    }

    private void modelLogic(){
        if(!this.getWorld().isClient()) return;
        boolean isActivePhase = switch (getCurrentScenePhase()) {
            case NONE, BED_IDLE, LAYING_DOWN -> false; // Inactive/resting
            default -> true; // Active NSFW phases
        };

        this.overrideBoneColor(List.of("nut"), ModConfig.INSTANCE.player.penisHeadColor);
        this.overrideBoneColor(List.of("shaft", "ballL", "ballR"), ModConfig.INSTANCE.player.penisShaftColor);

        this.setBoneVisibility(List.of("RightLeg", "LeftLeg", "Torso2"), isActivePhase );

        List<String> Slim = List.of("rightArmAlex", "rightLowerArmAlex", "leftLowerArmAlex", "leftArmAlex");
        List<String> Wide = List.of("rightArmSteve", "rightLowerArmSteve", "leftLowerArmSteve", "leftArmSteve");

        this.setBoneVisibility(Slim , isPlayerModelSlim() && isActivePhase );

        this.setBoneVisibility(Wide , !isPlayerModelSlim() && isActivePhase );
        this.setBoneSize("boobs", this.getBreastSize(), getBreastMinSize(), getBreastMaxSize());
        this.setBonePos("boobs", this.getBreastOffset());
        int bellySize = isPregnant()
                ? MathHelper.lerp(getPregnancyProgress(), 100, getMaxBellySizeWhenPregnant())
                : 100;

        this.setBoneSize("belly", bellySize);    }

    private void keyFrameEventHandler() {
        String key = getAnimationKeyFrameEvent();

        // If this is the same key as last time, skip
        if (key.equals(lastSoundKey)) {
            setAnimationKeyFrameEventState("");
        }

        // Update last key
        lastSoundKey = key;

    }

    protected void soundHandler() {
        String key = getAnimationKeyFrameEvent();

        // Get all sounds for this key
        List<SoundEvent> sounds = SceneKeyframeEventRegistry.getSound(this.getGirlID(), key);

        // Play all sounds sequentially (or simultaneously)
        for (SoundEvent sound : sounds) {
            this.playSound(sound, 1.0f, 1.0f);
        }
    }

    protected void messageHandler() {
        String key = getAnimationKeyFrameEvent();

        List<String> girlMsgs = SceneKeyframeEventRegistry.getMessage(this.getGirlID(), key);
        List<String> playerMsgs = SceneKeyframeEventRegistry.getPlayerMessage(key);

        for (String msg : girlMsgs) {
            this.messageAsEntity(false, msg);
        }

        for (String msg : playerMsgs) {
            this.messageAsPlayer(msg);
        }
    }

    private void handleSceneFootstepSounds(){
        BlockPos posBelow = this.getBlockPos().down();
        BlockState state = this.getWorld().getBlockState(posBelow);
        BlockSoundGroup soundGroup = state.getSoundGroup();
        SoundEvent stepSound = soundGroup.getStepSound();

        if(getAnimationKeyFrameEvent().equals("paizuri_startStep".toLowerCase())){
            this.playSound(stepSound, 1.0f, 1.0f);
        }

    }

    private void handleSceneSpeed() {
        if (!this.getCurrentScenePhase().equals(ScenePhase.HAVING_SEX)) {
            return;
        }

        String key = getAnimationKeyFrameEvent().toLowerCase();

        if (key.contains("thrust")) {
            this.setSceneProgress(this.getSceneProgress() + PROGRESS_SPEED);
        }

        this.setSceneProgress(Math.clamp(this.getSceneProgress(), 0, this.getCumThreshold()));
    }

    private boolean wasPregnantLastTick = false;

    @Override
    public void tick() {
        super.tick();
        //Rendering
        this.updateClothingAndArmor();
        this.modelLogic();

        //Scene
        keyFrameEventHandler();
        if(this.getWorld().isClient())messageHandler();

        if(!this.getWorld().isClient()) {
            soundHandler();
            handleSceneFootstepSounds();
            this.setSceneState(getCurrentScenePhase() != ScenePhase.NONE);

            boolean InSexPhases = switch (getCurrentScenePhase()) {
                case BED_IDLE, LAYING_DOWN, DIALOG -> false;
                default -> true;
            };

            this.setHavingSex(isSceneActive() && InSexPhases);
        }


        // Handle scene exit
        if (this.isSceneActive()) onSceneActive();

        boolean isStopPhase = switch (getCurrentScenePhase()) {
            case BED_IDLE, LAYING_DOWN, DIALOG, STATIONARY, STATIONARY_INTRO -> false;
            default -> true;
        };

        if(!this.hasPassengers() && this.isSceneActive() && isStopPhase) stopScene();

        if(!this.getWorld().isClient()) {
            handleSceneSpeed();

            if(amountOfUnprotectedSex() >= maxAmountOfSexUntilImpregnation()){
                this.setPregnantState(true);
                this.setAmountOfUnprotectedSex(0);
            }

            if(isPregnant() != wasPregnantLastTick){
                this.setPregnancyTicks(this.getPregnancyDuration());
                wasPregnantLastTick = isPregnant();
            }

            if(!canGetImpregnated()){
                setAmountOfUnprotectedSex(0);
                setPregnantState(false);
                this.setPregnancyTicks(0);
            }

            if (isPregnant() && this.getPregnancyTicks() > 0) {
                this.setPregnancyTicks(this.getPregnancyTicks() - 1);

                // Pregnancy completed
                if (this.getPregnancyTicks()     == 0) {
                    pregnancyFinished();
                }
            }
        }
    }

    public void triggerSwing() {
        this.swinging = true;
        this.lastSwing = this.getWorld().getTime();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Lower = higher priority
        controllerRegistrar.add(new AnimationController<>("girl_animations", 4, this::handleAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)));
        controllerRegistrar.add(new AnimationController<>("girl_attack", 4, this::handleAttackAnimations));
        controllerRegistrar.add(new AnimationController<>("girl_face", 4, this::handleFacialAnimations));
    }

    private PlayState handleFacialAnimations(AnimationTest<GeoAnimatable> state){
        AnimationController<?> controller = state.controller();


        return state.setAndContinue(RawAnimation.begin().then(getAnimationPath("blink"), Animation.LoopType.LOOP));
    }

    private PlayState handleAttackAnimations(AnimationTest<GeoAnimatable> state) {
        AnimationController<?> controller = state.controller();

        // End swing after 7 ticks
        if (this.swinging && this.lastSwing + 7L <= this.getWorld().getTime()) {
            this.swinging = false;
        }

        // If swinging and controller is idle, play attack animation once
        if (this.swinging && controller.getAnimationState() == AnimationController.State.STOPPED) {
            controller.forceAnimationReset();
            return state.setAndContinue(
                    //Get a random animation out of the three
                    RawAnimation.begin().then(getAnimationPath("attack" + RANDOM.nextInt(0, 3)), Animation.LoopType.PLAY_ONCE)
            );
        }

        return PlayState.CONTINUE;

    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        boolean hit = super.tryAttack(world, target);

        if (hit && !this.getWorld().isClient) {
            for (ServerPlayerEntity player : world.getPlayers()){
                ServerPlayNetworking.send(player, new PlayAttackAnimationS2CPacket(this.getId()));
            }
        }

        return hit;
    }


    private PlayState handleAnimations(AnimationTest<GirlEntityScene> state) {
        if (isSceneActive() && getOverrideAnim().isEmpty()) {
            final AnimationController<?> controller = state.controller();
            final Scene options = this.getCurrentScene();

            // Notify server when an animation finishes (only once per cycle)
            if ((controller.hasAnimationFinished() || controller.getAnimationState() == AnimationController.State.PAUSED) && !lastSceneAnim.isEmpty()) {
                ClientPlayNetworking.send(new AnimationFinishC2SPacket(this.getId()));
                lastSceneAnim = ""; // prevent spamming until new anim set
            }

            switch (getCurrentScenePhase()) {
                case LAYING_DOWN -> {
                    String laying = options.bedIdle().isEmpty() ? "null" : options.layOnBed();
                    return setSceneAnimIfChanged(state, laying, Animation.LoopType.HOLD_ON_LAST_FRAME);
                }
                case BED_IDLE -> {
                    String bedIdle = options.bedIdle().isEmpty() ? "null" : options.bedIdle();
                    return setSceneAnimIfChanged(state, bedIdle, Animation.LoopType.LOOP);
                }
                case INTRO -> {
                    List<String> intros = options.introAnim();
                    if (intros.isEmpty()) {
                        playPhase(ScenePhase.HAVING_SEX);
                        return PlayState.CONTINUE;
                    }
                    String current = intros.get(Math.min(getIntroIndex(), intros.size() - 1));
                    return setSceneAnimIfChanged(state, current, Animation.LoopType.HOLD_ON_LAST_FRAME);
                }
                case HAVING_SEX -> {
                    boolean thrustKeyDown = isThrusting();

                    if(getCurrentSexAnim().isBlank()){
                        setCurrentSexAnim(getRandomFromList(options.slowAnim()));
                    }

                    if(options.useKeyFrameEvents()){
                        String key = getAnimationKeyFrameEvent();

                        if (key.contains("switch") && thrustKeyDown) {
                            setCurrentSexAnim(getRandomFromList(options.fastAnim()));
                        }

                        if (key.contains("reset") && thrustKeyDown) {
                            return state.setAndContinue(RawAnimation.begin().then(getAnimationPath(getRandomFromList(options.fastAnim())), Animation.LoopType.LOOP));
                        }

                        if (key.contains("reset") && !thrustKeyDown) {
                            setCurrentSexAnim(getRandomFromList(options.slowAnim()));
                        }

                        return setSceneAnimIfChanged(state, getCurrentSexAnim(), Animation.LoopType.LOOP);
                    }
                    else {
                        List<String> anims = thrustKeyDown ? options.fastAnim() : options.slowAnim();
                        return state.setAndContinue(RawAnimation.begin().then(getAnimationPath(anims.getFirst()), Animation.LoopType.LOOP));
                    }
                }
                case CUM -> {
                    setCurrentSexAnim("");
                    return setSceneAnimIfChanged(state, options.cumAnim(), Animation.LoopType.HOLD_ON_LAST_FRAME);
                }
                case STATIONARY_INTRO -> {
                    List<String> sequence = options.stationaryIntroAnim(); // add to SceneOptions
                    if(sequence.isEmpty()){
                        playPhase(ScenePhase.STATIONARY);
                        return PlayState.CONTINUE;
                    }
                    String current = sequence.get(Math.min(getStationaryIndex(), sequence.size() - 1));
                    return setSceneAnimIfChanged(state, current, Animation.LoopType.HOLD_ON_LAST_FRAME);
                }
                case STATIONARY -> {
                    String loopAnim = options.stationaryLoopAnim();
                    int loopsNeeded = getStationaryLoopThreshold();

                    // Safety: if no animation provided, stop scene
                    if (loopAnim == null || loopAnim.isEmpty()) {
                        stopScene();
                        return PlayState.STOP;
                    }

                    // Has the loop run enough times?
                    if (getStationaryLoop() >= loopsNeeded) {
                        stopScene();
                        return PlayState.STOP;
                    }

                    // Continue looping
                    return setSceneAnimIfChanged(state, loopAnim, Animation.LoopType.HOLD_ON_LAST_FRAME);
                }
                default -> {
                    return PlayState.STOP;
                } // Defer to scene controller during scenes
            }
        }
        else {
            AnimationController<?> controller = state.controller();
            String overrideAnim = this.getOverrideAnim();
            boolean overrideLoop = this.getOverrideLoopState();
            boolean overrideHold = this.getOverrideHoldState();

            // 1. Forced animation override
            if (overrideAnim != null && !overrideAnim.isEmpty()) {
                this.currentAnimState = overrideAnim;
                this.currentLoopState = overrideLoop;
                this.currentHoldState = overrideHold;

                if(!overrideLoop) {

                    // End override if it was one-shot and finished playing
                    if (controller.getAnimationState() == AnimationController.State.STOPPED || controller.getAnimationState() == AnimationController.State.PAUSED) {
                        ClientPlayNetworking.send(new AnimationSyncC2SPacket(this.getId(), "", false, false));
                    }
                }
            }
            else {
                this.currentAnimState = getDefaultAnimation(state);
                this.currentLoopState = true;
            }

            Animation.LoopType loopType;

            if (this.currentLoopState) {
                loopType = Animation.LoopType.LOOP;
            } else if (this.currentHoldState) {
                loopType = Animation.LoopType.HOLD_ON_LAST_FRAME;
            } else {
                loopType = Animation.LoopType.PLAY_ONCE;
            }


            return state.setAndContinue(RawAnimation.begin().then
                    (getAnimationPath(this.currentAnimState), loopType));
        }

    }

    public void animationFinished(){
        if (this.getWorld().isClient()) return;
        if (!this.isSceneActive()) return;

        switch (getCurrentScenePhase()) {
            case INTRO -> {
                List<String> intros = getCurrentScene().introAnim();
                if (getIntroIndex() < intros.size() - 1) {
                    setIntroIndex(getIntroIndex() + 1);
                } else {
                    playPhase(ScenePhase.HAVING_SEX);
                }
            }
            case CUM -> {
                if(this.getCurrentScene().countTowardsImpregnation() && this.canGetImpregnated()) this.setAmountOfUnprotectedSex(this.amountOfUnprotectedSex() + 1);
                stopScene();
            }
            case LAYING_DOWN -> playPhase(ScenePhase.BED_IDLE);
            case STATIONARY_INTRO -> {
                Scene options = getCurrentScene();
                List<String> sequence = options.stationaryIntroAnim();
                if(getStationaryIndex() < sequence.size() - 1){
                    setStationaryIndex(getStationaryIndex() + 1);
                } else {
                    playPhase(ScenePhase.STATIONARY);
                }
            }
            case STATIONARY -> {
                int current = getStationaryLoop();
                int needed = getStationaryLoopThreshold();

                if (current < needed) {
                    setStationaryLoop(current + 1);
                } else {
                    // finished all loops
                    stopScene();
                }
            }
            default -> {}
        }
    }

    private void pregnancyFinished(){
        this.playSound(PleasureCraftSoundEventRegistry.PLOB, 1f,1f);
        this.dropPregnancyLoot(LootTables.END_CITY_TREASURE_CHEST);
        this.setPregnantState(false);
    }

    private void dropPregnancyLoot(RegistryKey<LootTable> lootTableRegistryKey) {
        ServerWorld world = (ServerWorld) this.getWorld();
        LootTable lootTable = world.getServer()
                .getReloadableRegistries()
                .getLootTable(lootTableRegistryKey);

        DamageSource fakeSource = world.getDamageSources().generic();

        LootWorldContext context = new LootWorldContext.Builder(world)
                .add(LootContextParameters.THIS_ENTITY, this)
                .add(LootContextParameters.ORIGIN, this.getPos())
                .add(LootContextParameters.DAMAGE_SOURCE, fakeSource)
                .build(LootContextTypes.ENTITY);

        lootTable.generateLoot(context, this.getLootTableSeed(), stack -> this.dropStack(world, stack));
    }


    private String getRandomFromList(List<String> list) {
        if(list.size() == 1) return list.getFirst();
        return  list.get(RANDOM.nextInt(list.size()));
    }


    private String getDefaultAnimation(AnimationTest<?> state) {
        if (!this.isOnGround() && !isSitting() && !this.hasVehicle() && !isTemporary()) return "fly";
        if (state.isMoving() && !isSitting() && !isRunning() && !isTemporary()) return "walk";
        if (state.isMoving() && !isSitting() && isRunning() && !isTemporary()) return "run";
        if (isSitting() && !isTemporary()) return "sit";
        if (this.hasVehicle() && !isTemporary()) return "ride";
        return "idle";
    }


    // Call this to force an animation

    public void playAnimation(String animationName, boolean loop, boolean holdOnLastFrame) {
        if (!this.getWorld().isClient) { // run only on server
            this.setOverrideAnim(animationName != null ? animationName : "");
            this.setOverrideLoop(loop);
            this.setOverrideHold(holdOnLastFrame);
        } else {
            ClientPlayNetworking.send(new AnimationSyncC2SPacket(this.getId(),
                    animationName != null ? animationName : "",
                    loop,
                    holdOnLastFrame));
        }
    }

    private String getAnimationPath(String animation){
        return "animation." + this.getGirlID() + "." + animation;
    }

    private boolean isGirlArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD
                || slot == EquipmentSlot.CHEST
                || slot == EquipmentSlot.LEGS
                || slot == EquipmentSlot.FEET;
    }


    public void applySkinToBone(PlayerEntity player) {
        if (!this.getWorld().isClient()) return;


        Identifier texture;

        // Set the base of the player model to Steve so if there isn't a player it has a fallback
        this.overrideBoneTexture("steve", Identifier.ofVanilla("textures/entity/player/wide/steve.png"));

        if (player != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerSkinProvider skinProvider = client.getSkinProvider();

            GameProfile profile = player.getGameProfile();

            // Get the skin identifier
            texture = skinProvider.getSkinTextures(profile).texture();
            setIsPlayerModelSlim(skinProvider.getSkinTextures(profile).model() == SkinTextures.Model.SLIM);

            // if isn't null set the player texture
            if (texture != null) {
                this.overrideBoneTexture("steve", texture);
            }

            this.overrideBoneTextureLayer2("steve", Identifier.of(PleasureCraft.MOD_ID,"textures/player/penis.png"));
        }
    }

    private void updateClothingAndArmor() {
        if (this.getWorld().isClient()) return;

        boolean stripped = isStripped();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!isGirlArmorSlot(slot)) continue;
            boolean hasArmor = !this.inventory.getEquipmentStack(slot).isEmpty();
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
            if (!isGirlArmorSlot(slot)) continue;
            List<String> armorBones = getArmorBones().get(slot);
            if (armorBones != null) {
                setBoneVisibility(armorBones, armorVisibility.getOrDefault(slot, false));
                // Special rule: hide vagina if armor is in legs slot
                if (slot == EquipmentSlot.LEGS) {
                    boolean legsCovered = armorVisibility.getOrDefault(slot, false);
                    setBoneVisibility(Collections.singletonList("vagina"), !legsCovered);
                }
            }
            displayArmor(slot);
        }
    }

    private void displayArmor(EquipmentSlot slot){
        if (this.inventory.getEquipmentStack(slot).isEmpty()) {
            return;
        }

        float u = 0;

        float offset = 0.017578125f;

        ItemStack item = this.inventory.getEquipmentStack(slot);

        String armorType = item.toString().toLowerCase();

        if (armorType.contains("diamond")) u = offset;
        if (armorType.contains("gold")) u = offset * 2;
        if (armorType.contains("iron")) u = offset * 3;
        if (armorType.contains("copper")) u = offset * 4;
        if (armorType.contains("chain")) u = offset * 5;
        if (armorType.contains("leather")){
            u = offset * 6;
            this.overrideBoneColor(this.getArmorBones().get(slot), getDyedArmorColor(inventory.getEquipmentStack(slot)));
        }
        if (armorType.contains("turtle")) u = offset * 7;

        this.overrideBoneUV(this.getArmorBones().get(slot),u,0);

    }

    private int getDyedArmorColor(ItemStack stack) {
        if (stack.isEmpty()) return 0xFFFFFF;

        DyedColorComponent dyed = stack.get(DataComponentTypes.DYED_COLOR);
        if (dyed != null) {
            // Returns already-correct RGB integer
            return dyed.rgb();
        }

        // Default base color for leather (same as EquipmentModel)
        return 0xA06540;
    }

    public float getBedOffset(){
        return this.getCurrentScene().bedAlignmentOffset();
    }

    public boolean isBedScene(){
        return this.getCurrentScene().sceneType().equals(SceneType.ON_BED);
    }

    public void messageAsEntity(String message) {
        messageAsEntity(false, message);
    }

    public void messageAsEntity(boolean sendFromServer, String message){
        String finalMessage = "<"+getGirlDisplayName()+"> " + message;

        if((sendFromServer && !this.getWorld().isClient()) || this.getScenePlayer() == null){
            PleasureCraftMessages.GlobleMessage(this.getWorld(), finalMessage);
        }
        else{
            PleasureCraftMessages.PlayerSpecificMessage(this.getScenePlayer(),finalMessage);
        }

    }

    public void messageAsPlayer(String message) {
        if(this.getScenePlayer() == null) return;

        GameProfile profile = this.getScenePlayer().getGameProfile();
        String finalMessage = "<" + profile.getName() + "> " + message;
        PleasureCraftMessages.PlayerSpecificMessage(this.getScenePlayer(), finalMessage);
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

    public void requestWaitForPlayer() {
        this.requestWaitForPlayer = true;
    }

    public boolean shouldWaitForPlayer() {
        if (requestWaitForPlayer) {
            requestWaitForPlayer = false;
            return true;
        }
        return false;
    }

    public void requestStrip() {
        this.requestStrip(null);
    }

    public void requestStrip(@Nullable Scene options) {
        this.requestStrip = true;

        if(options != null){
            this.stripOptions = options;
        }
    }

    public boolean shouldStrip() {
        if (requestStrip) {
            requestStrip = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if(this.isTemporary()) return false;
        return super.damage(world, source, amount);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(-4, new StationaryContactGoal(this));
        this.goalSelector.add(-3, new MoveToPlayerGoal(this, 1.25D));
        this.goalSelector.add(-2, new BedGoal(this, 1.25D));
        this.goalSelector.add(-1, new StripGoal(this));
        this.goalSelector.add(0, new StopMovementGoal(this));
        this.goalSelector.add(1, new GirlSitGoal(this));
    }

    public boolean isCurrentScenePlayer(PlayerEntity player){
        if(this.getScenePlayer() == null) return false;
        return this.getScenePlayer().getUuid().equals(player.getUuid());
    }

    private static class SoundKeyframeHandler implements AnimationController.KeyframeEventHandler<GirlEntityScene, SoundKeyframeData> {

        private final GirlEntityScene entity;

        public SoundKeyframeHandler(GirlEntityScene entity) {
            this.entity = entity;
        }

        @Override
        public void handle(KeyFrameEvent<GirlEntityScene, SoundKeyframeData> event) {
            if (!this.entity.getWorld().isClient()) return;

            String key = event.keyframeData().getSound().toLowerCase();
            ClientPlayNetworking.send(new SoundEventSyncC2SPacket(this.entity.getId(), key));
        }
    }

}
