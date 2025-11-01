package com.sandymandy.pleasurecraft.entity.base;

import com.mojang.authlib.GameProfile;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.networking.C2S.*;
import com.sandymandy.pleasurecraft.networking.S2C.ClothingArmorVisibilityS2CPacket;
import com.sandymandy.pleasurecraft.networking.S2C.PlayCumHudAnimationS2CPacket;
import com.sandymandy.pleasurecraft.registries.PleasureCraftTrackedDataRegistry;
import com.sandymandy.pleasurecraft.registries.SceneKeyframeRegistry;
import com.sandymandy.pleasurecraft.util.PleasureCraftLangUtils;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.util.Utils;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import com.sandymandy.pleasurecraft.util.variables.ScenePhase;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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

public class GirlEntityScene extends TameableGirlEntity implements GeoEntity {
    private static final TrackedData<SceneOptions> CURRENT_SCENE_OPTIONS = DataTracker.registerData(GirlEntityScene.class, PleasureCraftTrackedDataRegistry.SCENE_OPTION);
    private static final TrackedData<ScenePhase> CURRENT_SCENE_PHASE = DataTracker.registerData(GirlEntityScene.class, PleasureCraftTrackedDataRegistry.SCENE_PHASE);
    private static final TrackedData<String> ANIMATION_KEY_FRAME_EVENT = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Float> SCENE_PROGRESS = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> CUM_THRESHOLD = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> THRUSTING = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> INTRO_INDEX = DataTracker.registerData(GirlEntityScene.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Random RANDOM = new Random();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public BlockPos targetBedPos;
    public SceneOptions stripOptions = SceneOptions.EMPTY;
    private boolean requestStrip = false;
    private boolean requestMoveToBed = false;
    private boolean requestMoveToPlayer;
    private String lastSceneAnim = "";
    public String passengerBoneName = "boyCam";
    BlockPos bedPos;
    private boolean swinging = false;
    private long lastSwing = 0L;
    public PlayerEntity scenePlayer = (PlayerEntity) this.getOwner();
    private static final float PROGRESS_SPEED = 0.1f;

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
        builder.add(CURRENT_SCENE_OPTIONS, SceneOptions.EMPTY);
        builder.add(CURRENT_SCENE_PHASE, ScenePhase.NONE);
        builder.add(ANIMATION_KEY_FRAME_EVENT,"");
        builder.add(SCENE_PROGRESS,0f);
        builder.add(CUM_THRESHOLD,5f);
        builder.add(THRUSTING,false);
        builder.add(INTRO_INDEX, 0);
    }

    public void setCurrentSceneOptions(SceneOptions options){
        this.dataTracker.set(CURRENT_SCENE_OPTIONS, options);
    }

    public SceneOptions getCurrentSceneOptions(){
        return this.dataTracker.get(CURRENT_SCENE_OPTIONS);
    }

    public void setCurrentScenePhase(ScenePhase phase){
        this.dataTracker.set(CURRENT_SCENE_PHASE, phase);
    }

    public ScenePhase getCurrentScenePhase(){
        return this.dataTracker.get(CURRENT_SCENE_PHASE);
    }

    public void setAnimationKeyFrameEventState(String  str){
        this.dataTracker.set(ANIMATION_KEY_FRAME_EVENT, str);
    }

    public String getAnimationKeyFrameEvent(){
        return this.dataTracker.get(ANIMATION_KEY_FRAME_EVENT);
    }

    public void setThrusting(boolean  thrust){
        this.dataTracker.set(THRUSTING, thrust);
    }

    public boolean isThrusting(){
        return this.dataTracker.get(THRUSTING);
    }

    public void setIntroIndex(int num){
        this.dataTracker.set(INTRO_INDEX, num);
    }

    public void setSceneProgress(float progress){
        this.dataTracker.set(SCENE_PROGRESS, progress);
    }

    public float getSceneProgress(){
        return this.dataTracker.get(SCENE_PROGRESS);
    }

    public void setCumThreshold(float threshold){
        this.dataTracker.set(CUM_THRESHOLD, threshold);
    }

    public float getCumThreshold(){
        return this.dataTracker.get(CUM_THRESHOLD);
    }

    public int getIntroIndex(){
        return this.dataTracker.get(INTRO_INDEX);
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

    public void setBoneSize(String bone, float x, float y, float z) {
        if (this.boneSizeOverrides == null) this.boneSizeOverrides = new HashMap<>();

        x = Math.clamp(x,0.25f,1.5f);
        y = Math.clamp(y,0.25f,1.5f);
        z = Math.clamp(z,0.25f,1.5f);

        this.boneSizeOverrides.put(bone, new Vec3d(x,y,z));

    }

    public void setBoneSize(String bone, int size) {
        float finalSize = (float) size / 100;
        setBoneSize(bone, finalSize, finalSize, finalSize);
    }

    public void setBoneSize(String bone, int x, int y, int z) {
        float finalX = (float) x / 100;
        float finalY = (float) y / 100;
        float finalZ = (float) z / 100;
        setBoneSize(bone, finalX, finalY, finalZ);
    }

    public void startScene(SceneOptions option) {
        if (this.isSceneActive()) return;
        if (this.scenePlayer == null) return;

        if (this.isSitting()) this.setSitting(false);

        this.setCurrentSceneOptions(option);

        if (!this.isStripped() && option.needsToStrip()){
            this.requestStrip(option);
            return;
        }

        if (isBedScene()) {
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
            return; // Don't start yet – let the goal handle it
        }


        this.requestMoveToPlayer();
    }

    public void onSceneStart() {
        scenePlayer.setInvisible(true);

        this.setSceneProgress(0f);
        this.setCumThreshold(getCurrentSceneOptions().cumThreshold());
        setThrusting(false);
        this.targetBedPos = null;
        scenePlayer.startRiding(this, false);
        setIntroIndex(0);
        lastSceneAnim = "";
        playPhase(ScenePhase.INTRO);

        this.setSceneState(true);
    }

    public void stopScene() {
        if (!this.isSceneActive()) return;
        if(this.getWorld().isClient()){
            ClientPlayNetworking.send(new StopSceneOnServerC2SPacket(this.getId()));
            return;
        }

        PleasureCraft.usedBeds.remove(this.getUuid());
        PleasureCraft.activeScenes.remove(this.scenePlayer.getUuid());
        setIntroIndex(0);
        this.setSceneProgress(0f);
        onSceneStop();
        setCurrentScenePhase(ScenePhase.NONE);
        this.getNavigation().stop();
    }

    private void onSceneStop() {
        if (this.hasPassengers()) {
            this.removeAllPassengers();
        }
        scenePlayer.setInvisible(false);
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

    private String getRandomFromList(List<String> list) {
        return list.get(RANDOM.nextInt(list.size()));
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
        if(this.getWorld().isClient()) return;

        if(bedPos != null && isBedScene() && !Utils.checkForBlockAt(this.getWorld(),bedPos,null,BlockTags.BEDS)){
            stopScene();
        }
    }

    private void playerModelLogic(){
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
    }
    private String lastSoundKey = null;


    private void keyFrameEventHandler() {
        String key = getAnimationKeyFrameEvent();

        // If this is the same key as last time, skip
        if (key.equals(lastSoundKey)) {
            setAnimationKeyFrameEventState("");
        }

        // Update last key
        lastSoundKey = key;

    }

    private void soundHandler() {
        String key = getAnimationKeyFrameEvent();

        // Get all sounds for this key
        List<SoundEvent> sounds = SceneKeyframeRegistry.getSound(this.getType(), key);

        // Play all sounds sequentially (or simultaneously)
        for (SoundEvent sound : sounds) {
            this.playSound(sound, 1.0f, 1.0f);
        }


    }

    private void messageHandler() {
        String key = getAnimationKeyFrameEvent();

        List<String> girlMsgs = SceneKeyframeRegistry.getMessage(this.getType(), key);
        List<String> playerMsgs = SceneKeyframeRegistry.getPlayerMessage(key);

        for (String msg : girlMsgs) {
            this.messageAsEntity(false, PleasureCraftLangUtils.getStringFromKey(msg));
        }

        for (String msg : playerMsgs) {
            this.messageAsOwner(PleasureCraftLangUtils.getStringFromKey(msg));
        }
    }


    private void handleSceneFootstepSounds(){
        BlockPos posBelow = this.getBlockPos().down();
        BlockState state = this.getWorld().getBlockState(posBelow);
        BlockSoundGroup soundGroup = state.getSoundGroup();
        SoundEvent stepSound = soundGroup.getStepSound();

        if(getAnimationKeyFrameEvent().equals("paizuri_startStep")){
            this.playSound(stepSound, 1.0f, 1.0f);
        }

    }

    private void handleSceneSpeed() {
        if(!this.getCurrentScenePhase().equals(ScenePhase.HAVING_SEX)){
            return;
        }

        String key = getAnimationKeyFrameEvent();

        if (key.contains("Switch") || key.contains("Reset") || key.contains("thrust")) {
            this.setSceneProgress(this.getSceneProgress() + PROGRESS_SPEED);
        }

        this.setSceneProgress(Math.clamp(this.getSceneProgress(), 0, this.getCumThreshold()));
    }

    @Override
    public void tick() {
        super.tick();
        this.scenePlayer = (PlayerEntity) this.getOwner();
        //Rendering
        this.updateClothingAndArmor();
        this.applySkinToBone(scenePlayer);
        this.playerModelLogic();

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
            case BED_IDLE, LAYING_DOWN, DIALOG -> false;
            default -> true;
        };

        if(!this.hasPassengers() && this.isSceneActive() && isStopPhase) stopScene();

        // Handle scene speed
        if(!this.getWorld().isClient()) {
            handleSceneSpeed();
        }
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("girl_animations", 4, this::handleAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)));
        // Attack controller, higher priority so it can override
        /*controllerRegistrar.add(new AnimationController<>("attack", 2, this::handleAttackAnimations));*/
    }
    /*private PlayState handleAttackAnimations(AnimationTest<SceneEntity> state) {
        // Calculate horizontal velocity (not required, but kept from original)
     double dx = this.getX() - this.prevX;
       double dz = this.getZ() - this.prevZ;

        // Detect swing (built-in swing progress > 0)
        if (this.getHandSwingProgress(state.getPartialTick()) > 0.0F && !this.swinging) {
            this.swinging = true;
            this.lastSwing = this.getWorld().getTime();
        }

        // End swing after 7 ticks
        if (this.swinging && this.lastSwing + 7L <= this.getWorld().getTime()) {
            this.swinging = false;
        }

        // If swinging and controller is idle, play correct animation
        if (this.swinging && state.controller().getAnimationState() == AnimationController.State.STOPPED) {
            state.resetCurrentAnimation();


            this.messageAsEntity("Swing");
            return state.setAndContinue(RawAnimation.begin().then(getAnimationPath("attack1"), Animation.LoopType.PLAY_ONCE));
        }
        else {
            this.messageAsEntity("Not Swing");
            return PlayState.CONTINUE;
        }


    }*/

    private PlayState handleAnimations(AnimationTest<GirlEntityScene> state) {
        if (isSceneActive() && getOverrideAnim().isEmpty()) {
            final AnimationController<?> controller = state.controller();
            final SceneOptions options = this.getCurrentSceneOptions();

            // Notify server when an animation finishes (only once per cycle)
            if ((controller.hasAnimationFinished() || controller.getAnimationState() == AnimationController.State.PAUSED) && !lastSceneAnim.isEmpty()) {
                ClientPlayNetworking.send(new AnimationFinishC2SPacket(this.getId()));
                lastSceneAnim = ""; // prevent spamming until new anim set
            }

            switch (getCurrentScenePhase()) {
                case LAYING_DOWN -> {
                    String laying = options.bedIdle().isEmpty() ? "null" : options.bedIdle().getFirst();
                    return setSceneAnimIfChanged(state, laying, Animation.LoopType.HOLD_ON_LAST_FRAME);
                }
                case BED_IDLE -> {
                    String bedIdle = options.bedIdle().isEmpty() ? "null" : options.bedIdle().getLast();
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

                    if(options.useKeyFrameEvents()){
                        String key = getAnimationKeyFrameEvent();
                        String anim = getRandomFromList(options.slowAnim());

                        if (key.contains("Switch") && thrustKeyDown) {
                            anim = getRandomFromList(options.fastAnim());
                        }

                        if (key.contains("Reset") && thrustKeyDown) {
                            return setSceneAnimIfChanged(state, getRandomFromList(options.fastAnim()), Animation.LoopType.LOOP);
                        }

                        if (key.contains("Reset") && !thrustKeyDown) {
                            anim = getRandomFromList(options.slowAnim());
                        }



                        return setSceneAnimIfChanged(state, anim, Animation.LoopType.LOOP);
                    }
                    else {
                        if(thrustKeyDown){
                            return setSceneAnimIfChanged(state, getRandomFromList(options.fastAnim()), Animation.LoopType.LOOP);
                        }
                        else {
                            return setSceneAnimIfChanged(state, getRandomFromList(options.slowAnim()), Animation.LoopType.LOOP);
                        }

                    }
                }
                case CUM -> {
                    return setSceneAnimIfChanged(state, options.cumAnim(), Animation.LoopType.PLAY_ONCE);
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
                List<String> intros = getCurrentSceneOptions().introAnim();
                if (getIntroIndex() < intros.size() - 1) {
                    setIntroIndex(getIntroIndex() + 1);
                } else {
                    playPhase(ScenePhase.HAVING_SEX);
                }
            }
            case CUM -> stopScene();
            case LAYING_DOWN -> playPhase(ScenePhase.BED_IDLE);
            default -> { /* nothing */ }
        }
    }


    private String getDefaultAnimation(AnimationTest<?> state) {
        if (!this.isOnGround() && !isSitting()) return "fly";
        if (state.isMoving() && !isSitting()) return "walk";
        if (isSitting()) return "sit";
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
        if (this.inventory.getArmorStack(slot).isEmpty()) {
            return;
        }

        float u = 0;

        float offset = 0.017578125f;

        ItemStack item = this.inventory.getArmorStack(slot);

        String armorType = item.toString().toLowerCase();

        if (armorType.contains("diamond")) u = offset;
        if (armorType.contains("gold")) u = offset * 2;
        if (armorType.contains("iron")) u = offset * 3;
        if (armorType.contains("copper")) u = offset * 4;
        if (armorType.contains("chain")) u = offset * 5;
        if (armorType.contains("leather")){
            u = offset * 6;
            this.overrideBoneColor(this.getArmorBones().get(slot), getDyedArmorColor(inventory.getArmorStack(slot)));
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
        return this.getCurrentSceneOptions().bedAlignmentOffset();
    }

    public boolean isBedScene(){
        return this.getCurrentSceneOptions().isBedScene();
    }

    public void messageAsEntity(boolean sendFromServer, String message){
        String finalMessage = "<"+getGirlDisplayName()+"> " + message;

        if(sendFromServer && !this.getWorld().isClient()){
            new PleasureCraftMessages().GlobleMessage(this.getWorld(), finalMessage);
        }
        else {
            new PleasureCraftMessages().PlayerSpecificMessage(scenePlayer,finalMessage);
        }

    }

    public void messageAsOwner(String message) {

        GameProfile profile = scenePlayer.getGameProfile();
        String finalMessage = "<" + profile.getName() + "> " + message;
        new PleasureCraftMessages().PlayerSpecificMessage(scenePlayer, finalMessage);
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

    public void requestStrip() {
        this.requestStrip(null);
    }

    public void requestStrip(@Nullable SceneOptions options) {
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

    private static class SoundKeyframeHandler implements AnimationController.KeyframeEventHandler<GirlEntityScene, SoundKeyframeData> {

        private final GirlEntityScene entity;

        public SoundKeyframeHandler(GirlEntityScene entity) {
            this.entity = entity;
        }

        @Override
        public void handle(KeyFrameEvent<GirlEntityScene, SoundKeyframeData> event) {
            if (!this.entity.getWorld().isClient()) return;

            String key = event.keyframeData().getSound();
            ClientPlayNetworking.send(new SoundEventSyncC2SPacket(this.entity.getId(), key));
        }
    }

}
