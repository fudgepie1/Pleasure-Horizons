package com.sandymandy.pleasurecraft.entity.base;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.ai.goal.BedGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.MoveToPlayerGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StopMovementGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StripGoal;
import com.sandymandy.pleasurecraft.networking.C2S.*;
import com.sandymandy.pleasurecraft.util.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.keyframe.event.SoundKeyframeEvent;

import java.util.List;

import static com.sandymandy.pleasurecraft.util.Utils.Round;

public class SceneEntity extends AbstractGirlEntity{
    private static final TrackedData<SceneOptions> CURRENT_SCENE_OPTIONS = DataTracker.registerData(SceneEntity.class, PleasureCraftTrackedData.SCENE_OPTION);
    private static final TrackedData<com.sandymandy.pleasurecraft.util.ScenePhase> CURRENT_SCENE_PHASE = DataTracker.registerData(SceneEntity.class, PleasureCraftTrackedData.SCENE_PHASE);
    private static final TrackedData<String> SOUND = DataTracker.registerData(SceneEntity.class, TrackedDataHandlerRegistry.STRING);

    private int timer = 0;
    private int introIndex = 0;
    private String lastSceneAnim = "";
    private final float cumThreshold = 5f;
    private float sceneProgress = 0f;
    private boolean isKeyHeld = false;
    public String passengerBoneName = "Torso2";
    BlockPos bedPos;
    private boolean swinging = false;
    private long lastSwing = 0L;
    public PlayerEntity scenePlayer = (PlayerEntity) this.getOwner();

    private static final float SLOW_SPEED = 0.002f;
    private static final float FAST_SPEED = 0.01f;

    protected SceneEntity(EntityType<? extends AbstractGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CURRENT_SCENE_OPTIONS, SceneOptions.EMPTY);
        builder.add(CURRENT_SCENE_PHASE, ScenePhase.NONE);
        builder.add(SOUND,"");
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

    public void setSoundEvent(String  str){
        this.dataTracker.set(SOUND, str);
    }

    public String getSoundEvent(){
        return this.dataTracker.get(SOUND);
    }

    public void startScene(SceneOptions option) {
        if (this.isSceneActive()) return;
        if (this.scenePlayer == null) return;

        if (this.isSitting()) this.setSitting(false);

        this.setCurrentSceneOptions(option);

        if (!this.isStripped() && option.needsToStrip()){
            this.requestStrip(option);
            this.messageAsEntity(scenePlayer, "Be there in a bit, just need to take these clothes off");
            return;
        }

        if (isBedScene()) {
            //  Check for a bed before starting
            Utils.BlockInfo bedInfo = Utils.findNearbyBlock(
                    this.getWorld(),
                    this.getBlockPos(),
                    15,// radius
                    null,
                    BlockTags.BEDS
            );

            if (bedInfo == null) {
                this.messageAsEntity(scenePlayer, "We need a bed nearby for this...");
                return;
            }

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

        this.sceneProgress = 0f;
        isKeyHeld = false;
        this.targetBedPos = null;
        scenePlayer.startRiding(this, false);
        introIndex = 0;
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
        introIndex = 0;
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

    public void setKeyHeld(boolean held) {
        this.isKeyHeld = held;
    }

    public void playPhase(ScenePhase phase) {
        if (this.getWorld().isClient()) {
            ClientPlayNetworking.send(new ScenePhaseSyncC2SPacket(this.getId(), phase));
            return;
        }
        setCurrentScenePhase(phase);
        lastSceneAnim = "";
        if (phase != ScenePhase.INTRO) introIndex = 0;
    }

    public void tryTriggerCum() {
        if (this.isSceneActive() && this.getSceneProgress() >= cumThreshold && getCurrentScenePhase() != ScenePhase.CUM) {
            playPhase(ScenePhase.CUM);
        }
    }

    private String getRandomFromList(List<String> list) {
        if (list.size() == 1) return list.getFirst();
        int index = this.getWorld().getRandom().nextInt(list.size());
        return list.get(index);
    }

    private PlayState setSceneAnimIfChanged(AnimationState<?> state, String anim, Animation.LoopType loop) {
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

        timer ++;

        if(timer >= 20 && !(sceneProgress == 0f) && !this.getWorld().isClient && sceneProgress < (cumThreshold + 0.2f) && isHavingSex()){
            if(sceneProgress >= cumThreshold){
                new PleasureCraftMessages().GlobleMessage(this.getWorld(),"Scene Progress: READY TO CUM");
            }
            else new PleasureCraftMessages().GlobleMessage(this.getWorld(),"Scene Progress: "+Round(getSceneProgress(), 2));
            timer = 0;
        }

        if(bedPos != null && isBedScene() && !Utils.checkForBlockAt(this.getWorld(),bedPos,null,BlockTags.BEDS)){
            stopScene();
        }
    }

    private void playerModelLogic(){
        boolean isActivePhase = switch (getCurrentScenePhase()) {
            case NONE, BED_IDLE, LAYING_DOWN -> false; // Inactive/resting
            default -> true; // Active NSFW phases
        };

        this.toggleModelBones(List.of("RightLeg", "LeftLeg", "Torso2"), isActivePhase );

        this.toggleModelBones(List.of("rightLowerArmAlex", "rightArmAlex", "leftLowerArmAlex", "leftArmAlex"), isPlayerModelSlim() && isActivePhase );

        this.toggleModelBones(List.of("rightLowerArmSteve", "rightArmSteve", "leftLowerArmSteve", "leftArmSteve"), !isPlayerModelSlim() && isActivePhase );

    }

    private String lastSoundKey = null;

    private void soundEventHandler() {
        String key = getSoundEvent();

        // If this is the same key as last time, skip
        if (key.equals(lastSoundKey)) {
            setSoundEvent("");
        }

        // Update last key
        lastSoundKey = key;

    }

    private void soundHandler() {
        String key = getSoundEvent();

        // Get all sounds for this key
        List<SoundEvent> sounds = SceneKeyframeRegistry.getSound(this.getGirlID(), key);

        // Play all sounds sequentially (or simultaneously)
        for (SoundEvent sound : sounds) {
            this.playSound(sound, 1.0f, 1.0f);
        }
    }

    private void messageHandler(){
        String key = getSoundEvent();

        List<String> messagesGirl = SceneKeyframeRegistry.getMessage(this.getGirlID(), key);
        List<String> messagesPlayer = SceneKeyframeRegistry.getMessage("player", key);

        for (String messageGirl : messagesGirl) {
            this.messageAsEntity(scenePlayer, messageGirl);
        }

        for (String messagePlayer : messagesPlayer) {
            this.messageAsOwner(scenePlayer, messagePlayer);
        }
    }


    private void handleSceneFootstepSounds(){
        BlockPos posBelow = this.getBlockPos().down();
        BlockState state = this.getWorld().getBlockState(posBelow);
        BlockSoundGroup soundGroup = state.getSoundGroup();
        SoundEvent stepSound = soundGroup.getStepSound();

        if(getSoundEvent().equals("paizuri_startStep")){
            this.playSound(stepSound, 1.0f, 1.0f);
        }

    }

    @Override
    public void tick() {
        super.tick();
        this.setSceneProgress(sceneProgress);
        soundEventHandler();

        PleasureCraft.LOGGER.info(getSoundEvent());



        soundHandler();
        messageHandler();
        handleSceneFootstepSounds();

        this.scenePlayer = (PlayerEntity) this.getOwner();


        playerModelLogic();

        if(!this.getWorld().isClient()) {
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

        // Handle scene phases
        if(!this.getWorld().isClient()) {
            switch (getCurrentScenePhase()) {
                case SLOW -> {
                    sceneProgress += SLOW_SPEED;
                    if (isKeyHeld) {
                        playPhase(ScenePhase.FAST);
                    }
                }
                case FAST -> {
                    sceneProgress += FAST_SPEED;
                    if (!isKeyHeld) {
                        playPhase(ScenePhase.SLOW);
                    }
                }
                default -> {
                } // INTRO and CUM handled elsewhere
            }
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(-4, new MoveToPlayerGoal(this, 1.25D));
        this.goalSelector.add(-3, new BedGoal(this, 1.25D));
        this.goalSelector.add(-2, new StripGoal(this));
        this.goalSelector.add(-1, new StopMovementGoal(this));
        super.initGoals();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "scene", 4, this::handleSceneAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)));
        controllerRegistrar.add(new AnimationController<>(this, "movement", 4, this::handleDefaultAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)));


        // Attack controller, higher priority so it can override
        controllerRegistrar.add(new AnimationController<>(this, "attack", 2, this::handleAttackAnimations));

    }

    private PlayState handleAttackAnimations(AnimationState<SceneEntity> state) {
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
        if (this.swinging && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
//            state.resetCurrentAnimation();


//            this.messageAsEntity("Swing");
            return state.setAndContinue(RawAnimation.begin().then(getAnimationPath("attack1"), Animation.LoopType.PLAY_ONCE));
        }
        else {
//            this.messageAsEntity("Not Swing");
            return PlayState.CONTINUE;
        }


    }

    private PlayState handleDefaultAnimations(AnimationState<SceneEntity> state) {
        if (isSceneActive()) {
            return PlayState.STOP; // Defer to scene controller during scenes
        }
        else {
            AnimationController<?> controller = state.getController();
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

    private PlayState handleSceneAnimations(AnimationState<SceneEntity> state) {
        if (!isSceneActive() || !getOverrideAnim().isEmpty()) return PlayState.STOP;
        final AnimationController<?> controller = state.getController();
        final SceneOptions options = this.getCurrentSceneOptions();


        // Notify server when an animation finishes (only once per cycle)
        if (controller.hasAnimationFinished() && !lastSceneAnim.isEmpty()) {
            ClientPlayNetworking.send(new AnimationFinishC2SPacket(this.getId()));
            lastSceneAnim = ""; // prevent spamming until new anim set
        }

        switch (getCurrentScenePhase()) {
            case LAYING_DOWN -> {
                String laying = options.bedIdle().isEmpty() ? "null" : options.bedIdle().getFirst();
                return setSceneAnimIfChanged(state, laying, Animation.LoopType.PLAY_ONCE);
            }
            case BED_IDLE -> {
                String bedIdle = options.bedIdle().isEmpty() ? "null" : options.bedIdle().getLast();
                return setSceneAnimIfChanged(state, bedIdle, Animation.LoopType.LOOP);
            }
            case INTRO -> {
                List<String> intros = options.introAnim();
                if (intros.isEmpty()) {
                    playPhase(ScenePhase.SLOW);
                    return PlayState.CONTINUE;
                }
                String current = intros.get(Math.min(introIndex, intros.size() - 1));
                return setSceneAnimIfChanged(state, current, Animation.LoopType.PLAY_ONCE);
            }
            case SLOW -> {
                String slow = options.slowAnim().isEmpty() ? "null" : getRandomFromList(options.slowAnim());
                return setSceneAnimIfChanged(state, slow, Animation.LoopType.LOOP);
            }
            case FAST -> {
                String fast = options.fastAnim().isEmpty() ? "null" : getRandomFromList(options.fastAnim());
                return setSceneAnimIfChanged(state, fast, Animation.LoopType.LOOP);
            }
            case CUM -> {
                return setSceneAnimIfChanged(state, options.cumAnim(), Animation.LoopType.PLAY_ONCE);
            }
            default -> {
                return PlayState.STOP;
            }
        }
    }

    public void animationFinished(){
        if (this.getWorld().isClient()) return;
        if (!this.isSceneActive()) return;

        switch (getCurrentScenePhase()) {
            case INTRO -> {
                List<String> intros = getCurrentSceneOptions().introAnim();
                if (introIndex < intros.size() - 1) {
                    introIndex++;
                } else {
                    playPhase(ScenePhase.SLOW);
                }
            }
            case CUM -> stopScene();
            case LAYING_DOWN -> playPhase(ScenePhase.BED_IDLE);
            default -> { /* nothing */ }
        }
    }


    private String getDefaultAnimation(AnimationState<?> state) {
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

    public float getBedOffset(){
        return this.getCurrentSceneOptions().bedAlignmentOffset();
    }

    public boolean isBedScene(){
        return this.getCurrentSceneOptions().isBedScene();
    }

    private static class SoundKeyframeHandler implements AnimationController.SoundKeyframeHandler<SceneEntity> {
        private final SceneEntity entity;

        public SoundKeyframeHandler(SceneEntity entity) {
            this.entity = entity;
        }

        @Override
        public void handle(SoundKeyframeEvent<SceneEntity> event) {
            if(!this.entity.getWorld().isClient()) return;
            String key = event.getKeyframeData().getSound();
            ClientPlayNetworking.send(new SoundEventSyncC2SPacket(this.entity.getId(), key));
        }
    }
}
