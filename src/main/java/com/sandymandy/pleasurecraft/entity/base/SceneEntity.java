package com.sandymandy.pleasurecraft.entity.base;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.ai.goal.BedGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StopMovementGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StripGoal;
import com.sandymandy.pleasurecraft.networking.C2S.*;
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

import static com.sandymandy.pleasurecraft.util.Utils.Round;

public class SceneEntity extends AbstractGirlEntity{
    private static final TrackedData<SceneOptions> CURRENT_SCENE_OPTIONS = DataTracker.registerData(SceneEntity.class, PleasureCraftTrackedData.SCENE_OPTION);
    private static final TrackedData<com.sandymandy.pleasurecraft.util.ScenePhase> CURRENT_SCENE_PHASE = DataTracker.registerData(SceneEntity.class, PleasureCraftTrackedData.SCENE_PHASE);

    private int timer = 0 ;
    private int introIndex = 0;
    private String lastSceneAnim = "";
    private final float cumThreshold = 5f;
    private float sceneProgress = 0f;
    private boolean isKeyHeld = false;
    public String passengerBoneName = "Torso2";
    BlockPos bedPos;

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

    public void startScene(PlayerEntity rider, SceneOptions option) {
        if (this.isSceneActive()) return;

        if (this.isSitting()) this.setSitting(false);
        if (!this.isStripped()){
            this.requestStrip();
            this.messageAsEntity(rider, "Be there in a bit, just need to take these clothes off");
            return;
        }

        this.setCurrentSceneOptions(option);

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

        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (!player.hasVehicle()) player.setInvisible(false);
        }
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

    private void setSceneAnimIfChanged(AnimationController<?> c, String anim, Animation.LoopType loop) {
        if (anim == null || anim.isEmpty()) return;

        // Only reset if different from last
        if (!anim.equals(lastSceneAnim)) {
            c.setAnimation(RawAnimation.begin().then(getAnimationPath(anim), loop));
            c.forceAnimationReset();
            lastSceneAnim = anim;
        }
    }

    private void onSceneActive(){
        if(this.getWorld().isClient()) return;

        timer ++;

        if(timer >= 20 && !(sceneProgress == 0f) && !this.getWorld().isClient && sceneProgress < (cumThreshold + 0.2f)){
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

    @Override
    public void tick() {
        super.tick();
        this.setSceneProgress(sceneProgress);

        playerModelLogic();

        if(!this.getWorld().isClient()) {
            this.setSceneState(getCurrentScenePhase() != ScenePhase.NONE);
        }

        PleasureCraft.LOGGER.info(this.getOverrideAnim());

        // Handle scene exit
        if (this.isSceneActive()) onSceneActive();

        boolean isStopPhase = switch (getCurrentScenePhase()) {
            case BED_IDLE, LAYING_DOWN -> false;
            default -> true;
        };
        if(!this.hasPassengers() && this.isSceneActive() && isStopPhase) stopScene();


        PlayerEntity player = (PlayerEntity) this.getFirstPassenger();
        if (player != null) player.setInvisible(true);

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
        this.goalSelector.add(-3, new BedGoal(this, 1.5D));
        this.goalSelector.add(-2, new StripGoal(this, 38));
        this.goalSelector.add(-1, new StopMovementGoal(this));
        super.initGoals();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "scene", 4, this::handleSceneAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)).setParticleKeyframeHandler(new ParticleKeyframeHandler(this)).setCustomInstructionKeyframeHandler(new CustomKeyframeHandler(this)));
        controllerRegistrar.add(new AnimationController<>(this, "movement", 4, this::handleDefaultAnimations).setSoundKeyframeHandler(new SoundKeyframeHandler(this)).setParticleKeyframeHandler(new ParticleKeyframeHandler(this)).setCustomInstructionKeyframeHandler(new CustomKeyframeHandler(this)));

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


            controller.setAnimation(RawAnimation.begin().then
                    (getAnimationPath(this.currentAnimState), loopType));
            return PlayState.CONTINUE;

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
                setSceneAnimIfChanged(controller, laying, Animation.LoopType.PLAY_ONCE);
                return PlayState.CONTINUE;
            }
            case BED_IDLE -> {
                String bedIdle = options.bedIdle().isEmpty() ? "null" : options.bedIdle().getLast();
                setSceneAnimIfChanged(controller, bedIdle, Animation.LoopType.LOOP);
                return PlayState.CONTINUE;
            }
            case INTRO -> {
                List<String> intros = options.introAnim();
                if (intros.isEmpty()) {
                    playPhase(ScenePhase.SLOW);
                    return PlayState.CONTINUE;
                }
                String current = intros.get(Math.min(introIndex, intros.size() - 1));
                setSceneAnimIfChanged(controller, current, Animation.LoopType.PLAY_ONCE);
                return PlayState.CONTINUE;
            }
            case SLOW -> {
                String slow = options.slowAnim().isEmpty() ? "null" : getRandomFromList(options.slowAnim());
                setSceneAnimIfChanged(controller, slow, Animation.LoopType.LOOP);
                return PlayState.CONTINUE;
            }
            case FAST -> {
                String fast = options.fastAnim().isEmpty() ? "null" : getRandomFromList(options.fastAnim());
                setSceneAnimIfChanged(controller, fast, Animation.LoopType.LOOP);
                return PlayState.CONTINUE;
            }
            case CUM -> {
                setSceneAnimIfChanged(controller, options.cumAnim(), Animation.LoopType.PLAY_ONCE);
                return PlayState.CONTINUE;
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
