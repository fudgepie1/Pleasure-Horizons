package com.sandymandy.pleasurecraft.entity.base;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.ai.goal.BedGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StopMovementGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StripGoal;
import com.sandymandy.pleasurecraft.networking.C2S.AnimationSyncC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.NextAnimationC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.OverrideAnimationStateSyncC2SPacket;
import com.sandymandy.pleasurecraft.util.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.keyframe.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib.animation.keyframe.event.ParticleKeyframeEvent;
import software.bernie.geckolib.animation.keyframe.event.SoundKeyframeEvent;

import java.util.List;

public class SceneEntity extends AbstractGirlEntity{
    private static final TrackedData<SceneOptions> CURRENT_SCENE_OPTIONS = DataTracker.registerData(SceneEntity.class, PleasureCraftTrackedData.SCENE_OPTION);
    private static final TrackedData<com.sandymandy.pleasurecraft.util.ScenePhase> CURRENT_SCENE_PHASE = DataTracker.registerData(SceneEntity.class, PleasureCraftTrackedData.SCENE_PHASE);

    private int timer = 0 ;
    private int introIndex = 0;
    private final float cumThreshold = 5f;
    private float sceneProgress = 0f;
    private boolean isKeyHeld = false;
    public String passengerBoneName = "Torso2";
    BlockPos bedPos;
//    private List<String> animIntro;
//    private List<String> animSlow;
//    private List<String> animFast;
//    private String animCum;
//    private float bedOffset;
//    private List<String> bedIdle;
//    public boolean isBedScene;
//    private int index = 0;

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

    public void playBedIdle(boolean clear) {
        if(!this.getWorld().isClient()) {

            if (!clear) playPhase(ScenePhase.LAYING_DOWN);
            else {
                if (this.getCurrentSceneOptions().bedIdle().contains(this.getCurrentSceneAnim()))
                    playPhase(ScenePhase.NONE);
            }
        }
    }

    public void startScene(PlayerEntity rider, SceneOptions option) {
        if (this.isSceneActive()) return;

        if (this.isSitting()) this.setSitting(false);
        if (!this.isStripped()){
            this.requestStrip();
            this.messageAsEntity(rider, "Be there in a bit, just need to take these clothes off");
            return;
        }

        this.setCurrentSceneOptions(option);
//
//        this.animIntro = option.introAnim();
//        this.animSlow = option.slowAnim();
//        this.animFast = option.fastAnim();
//        this.animCum = option.cumAnim();
//        this.isBedScene = option.isBedScene();
//        this.bedOffset = option.bedOffset();
//        this.bedIdle = option.bedIdle();

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
                this.messageAsEntity(rider, "We need a bed nearby for this...");
                return;
            }

            //  Store target bed pos in entity so the BedGoal can use it
            this.targetBedPos = bedInfo.pos();
            bedPos = bedInfo.pos();


            this.requestMoveToBed();
            return; // Don't start yet – let the goal handle it
        }


        onSceneStart(rider);
    }

    public void onSceneStart(PlayerEntity rider) {
        rider.setInvisible(true);

        this.sceneProgress = 0f;
        isKeyHeld = false;
        this.targetBedPos = null;
        rider.startRiding(this, false);
        introIndex = 0; // reset
        playPhase(ScenePhase.INTRO);

        this.setSceneState(true);
    }

    public void stopScene() {
        if (!this.isSceneActive()) return;
        onSceneStop();
        this.playPhase(ScenePhase.NONE);
        this.getNavigation().stop();
    }

    private void onSceneStop() {
        if (this.hasPassengers()) {
            this.removeAllPassengers();
        }

        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (!player.hasVehicle()) player.setInvisible(false);
        }

        setCurrentScenePhase(ScenePhase.NONE);
        this.stopOverrideAnimations();
    }

    public void setKeyHeld(boolean held) {
        this.isKeyHeld = held;
    }

    public void playPhase(ScenePhase phase) {
        if (this.getWorld().isClient()) return; // client waits for sync
        setCurrentScenePhase(phase);
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

    private void onSceneActive(){
        if(this.getWorld().isClient()) return;

        timer ++;

        if(timer >= 20 && !(sceneProgress == 0f) && !this.getWorld().isClient && sceneProgress < (cumThreshold + 0.2f)){
            if(sceneProgress >= cumThreshold){
                new PleasureCraftMessages().GlobleMessage(this.getWorld(),"Scene Progress: READY TO CUM");
            }
            else new PleasureCraftMessages().GlobleMessage(this.getWorld(),"Scene Progress: "+sceneProgress);
            timer = 0;
        }

        if(bedPos != null && isBedScene() &! Utils.checkForBlockAt(this.getWorld(),bedPos,null,BlockTags.BEDS)){
            stopScene();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setSceneProgress(sceneProgress);



        if(!this.getWorld().isClient()) {
            this.setSceneState(getCurrentScenePhase() != ScenePhase.NONE);
        }

        boolean isActivePhase = switch (getCurrentScenePhase()) {
            case NONE, BED_IDLE, LAYING_DOWN -> false; // Inactive/resting
            default -> true; // Active NSFW phases
        };
        this.toggleModelBones(List.of("RightLeg", "LeftLeg", "Torso2"), isActivePhase );
        PleasureCraft.LOGGER.info(getCurrentScenePhase()+"");

        // Handle scene exit
        if (this.isSceneActive()) onSceneActive();
        if(!this.hasPassengers()) stopScene();


        PlayerEntity player = (PlayerEntity) this.getFirstPassenger();
        if (player != null) player.setInvisible(true);

        // Handle scene phases
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

    @Override
    protected void initGoals() {
        this.goalSelector.add(-3, new BedGoal(this, 1.5D));
        this.goalSelector.add(-2, new StripGoal(this));
        this.goalSelector.add(-1, new StopMovementGoal(this));
        super.initGoals();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "scene", 4, this::handleSceneAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)).setParticleKeyframeHandler(new ParticleKeyframeHandler(this)).setCustomInstructionKeyframeHandler(new CustomKeyframeHandler(this)));
        controllerRegistrar.add(new AnimationController<>(this, "misc", 4, this::handleMiscAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)).setParticleKeyframeHandler(new ParticleKeyframeHandler(this)).setCustomInstructionKeyframeHandler(new CustomKeyframeHandler(this)));
        controllerRegistrar.add(new AnimationController<>(this, "movement", 4, this::handleMovementAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)).setParticleKeyframeHandler(new ParticleKeyframeHandler(this)).setCustomInstructionKeyframeHandler(new CustomKeyframeHandler(this)));

    }

    private PlayState handleMovementAnimations(AnimationState<SceneEntity> state) {
        if (isSceneActive() || !getOverrideAnim().isEmpty()) {
            return PlayState.STOP; // Defer to scene controller during scenes
        }
        else {
            String anim = getDefaultAnimation(state);
            state.getController().setAnimation(
                    RawAnimation.begin().then(getAnimationPath(anim), Animation.LoopType.LOOP)
            );
            return PlayState.CONTINUE;
        }

    }

    private PlayState handleMiscAnimations(AnimationState<SceneEntity> state) {
        if (isSceneActive() || getOverrideAnim().isEmpty()) {
//            PleasureCraft.LOGGER.info("Stopped");
            return PlayState.STOP;
        }
        else {
            AnimationController<?> controller = state.getController();
            String overrideAnim = this.getOverrideAnim();
            if (overrideAnim == null || overrideAnim.isEmpty()) {
                ClientPlayNetworking.send(new OverrideAnimationStateSyncC2SPacket(this.getId(), false));
                ClientPlayNetworking.send(new NextAnimationC2SPacket(this.getId()));
                stopOverrideAnimations();
                return PlayState.STOP;
            }
            else {
                ClientPlayNetworking.send(new OverrideAnimationStateSyncC2SPacket(this.getId(), true));
            }


            Animation.LoopType loopType =
                    this.getOverrideLoopState() ? Animation.LoopType.LOOP :
                            this.getOverrideHoldState() ? Animation.LoopType.HOLD_ON_LAST_FRAME :
                                    Animation.LoopType.PLAY_ONCE;

            controller.setAnimation(
                    RawAnimation.begin().then(getAnimationPath(overrideAnim), loopType)
            );
            return PlayState.CONTINUE;
        }
    }

    private PlayState handleSceneAnimations(AnimationState<SceneEntity> state) {
        if (!isSceneActive() || !getOverrideAnim().isEmpty()) return PlayState.STOP;
        else {

            SceneOptions options = this.getCurrentSceneOptions();
            String anim = "null";
            Animation.LoopType loopType = Animation.LoopType.LOOP;

            switch (getCurrentScenePhase()) {
                case INTRO -> {
                    // If there are intro animations, step through them in order
                    List<String> intros = options.introAnim();
                    if (!intros.isEmpty()) {
                        if (introIndex >= intros.size()) {
                            // If we've finished all intros → switch to SLOW
                            playPhase(ScenePhase.SLOW);
                        } else {
                            anim = intros.get(introIndex);
                            loopType = Animation.LoopType.HOLD_ON_LAST_FRAME; // play once
                        }
                    }
                }
                case SLOW -> {
                    anim = getRandomFromList(options.slowAnim());
                    loopType = Animation.LoopType.LOOP;
                }
                case FAST -> {
                    anim = getRandomFromList(options.fastAnim());
                    loopType = Animation.LoopType.LOOP;
                }
                case CUM -> {
                    anim = options.cumAnim();
                    loopType = Animation.LoopType.HOLD_ON_LAST_FRAME;
                }
                case BED_IDLE -> {
                    anim = options.bedIdle().isEmpty() ? "null" : options.bedIdle().getLast();
                    loopType = Animation.LoopType.LOOP;
                }
                case LAYING_DOWN -> {
                    anim = options.bedIdle().isEmpty() ? "null" : options.bedIdle().getFirst();
                    loopType = Animation.LoopType.LOOP;
                }
                default -> {
                    anim = "null";
                }
            }


            if (anim == null || anim.isEmpty()) return PlayState.STOP;

            state.getController().setAnimation(RawAnimation.begin().then(getAnimationPath(anim), loopType));
            return PlayState.CONTINUE;
        }
    }


    public void animationFinished() {
        if (this.getWorld().isClient()) return;

        if(!this.isSceneActive()) this.setOverrideAnimPlayingState(false);
        else {
            if (getCurrentScenePhase() == ScenePhase.INTRO) {
                List<String> intros = getCurrentSceneOptions().introAnim();
                if (introIndex < intros.size() - 1) {
                    introIndex++;
                    // force next intro animation
                    playAnimation(intros.get(introIndex), false, true);
                } else {
                    // all intros played → go slow
                    playPhase(ScenePhase.SLOW);
                }
            } else if (getCurrentScenePhase() == ScenePhase.CUM) {
                stopScene();
            }
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

    public void stopOverrideAnimations() {
        ClientPlayNetworking.send(new AnimationSyncC2SPacket(this.getId(), "",false,false));
    }

    private String getAnimationPath(String animation){
        return "animation." + this.getGirlID() + "." + animation;
    }

    public float getBedOffset(){
        return this.getCurrentSceneOptions().bedOffset();
    }

    public boolean isBedScene(){
        return this.getCurrentSceneOptions().isBedScene();
    }

    private class SoundKeyframeHandler implements AnimationController.SoundKeyframeHandler<SceneEntity> {
        private SceneEntity entity;

        public SoundKeyframeHandler(SceneEntity entity) {
            this.entity = entity;
        }

        @Override
        public void handle(SoundKeyframeEvent<SceneEntity> soundKeyframeEvent) {

        }
    }

    private class ParticleKeyframeHandler implements AnimationController.ParticleKeyframeHandler<SceneEntity> {
        private SceneEntity entity;

        public ParticleKeyframeHandler(SceneEntity entity) {
            this.entity = entity;
        }

        @Override
        public void handle(ParticleKeyframeEvent<SceneEntity> particleKeyframeEvent) {

        }
    }

    private class CustomKeyframeHandler implements AnimationController.CustomKeyframeHandler<SceneEntity> {
        private SceneEntity entity;

        public CustomKeyframeHandler(SceneEntity entity) {
            this.entity = entity;
        }

        @Override
        public void handle(CustomInstructionKeyframeEvent<SceneEntity> customInstructionKeyframeEvent) {

        }
    }


}
