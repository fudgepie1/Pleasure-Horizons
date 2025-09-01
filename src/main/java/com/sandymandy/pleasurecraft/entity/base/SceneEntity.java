package com.sandymandy.pleasurecraft.entity.base;

import com.sandymandy.pleasurecraft.entity.ai.goal.BedGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StopMovementGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StripGoal;
import com.sandymandy.pleasurecraft.networking.C2S.AnimationSyncC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.NextSceneAnimationC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.OverrideAnimationStateSyncC2SPacket;
import com.sandymandy.pleasurecraft.util.SceneOption;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.util.Utils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

import java.util.List;

public class SceneEntity extends AbstractGirlEntity{
    public String passengerBoneName = "Torso2";
    private ScenePhase currentPhase = ScenePhase.NONE;
    private float sceneProgress = 0f;
    private final float cumThreshold = 5f;
    private boolean isKeyHeld = false;
    private int timer = 0 ;
    BlockPos bedPos;
    private List<String> animIntro;
    private List<String> animSlow;
    private List<String> animFast;
    private String animCum;
    private float bedOffset;
    private List<String> bedIdle;
    public boolean isBedScene;
    private int index = 0;

    private static final float SLOW_SPEED = 0.002f;
    private static final float FAST_SPEED = 0.01f;

    protected SceneEntity(EntityType<? extends AbstractGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    public void playBedIdle(boolean clear) {
        if(!this.getWorld().isClient()) {

            if (!clear) playPhase(ScenePhase.NONE, bedIdle.getFirst(), false, true);
            else {
                if (bedIdle.contains(this.getOverrideAnim()))
                    this.stopOverrideAnimations();
            }
        }
    }


    public enum ScenePhase {
        NONE, IDLE, INTRO, SLOW, FAST, CUM
    }

    public void startScene(PlayerEntity rider, SceneOption option) {
        if (this.isSceneActive()) return;

        if (this.isSitting()) this.setSitting(false);
        if (!this.isStripped()){
            this.requestStrip();
            this.messageAsEntity(rider, "Be there in a bit, just need to take these clothes off");
            return;
        }

        this.animIntro = option.introAnim();
        this.animSlow = option.slowAnim();
        this.animFast = option.fastAnim();
        this.animCum = option.cumAnim();
        this.isBedScene = option.isBedScene();
        this.bedOffset = option.bedOffset();
        this.bedIdle = option.bedIdle();

        if (isBedScene) {
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

    public void stopScene() {
        if (!this.isSceneActive()) return;
        onSceneStop();
        this.setSceneState(false);
        this.getNavigation().stop();
    }

    public void onSceneStart(PlayerEntity rider) {
        rider.setInvisible(true);

        this.sceneProgress = 0f;
        isKeyHeld = false;
        this.targetBedPos = null;
        rider.startRiding(this, false);
        playPhase(ScenePhase.INTRO, this.animIntro.getFirst(), false, true);

        this.setSceneState(true);
    }

    public void onAnimationFinished(String finishedAnim) {
        if(this.getWorld().isClient())return;

        if(finishedAnim.equals(bedIdle.getFirst())){
            playPhase(ScenePhase.IDLE, bedIdle.getLast(), true, false);
            return;
        }

        if (finishedAnim.equals(animCum)) {
            stopScene();
            return;

        }

        if (this.animIntro.contains(finishedAnim)) {

            // Find the index of the finished intro
            this.index = animIntro.indexOf(finishedAnim);


            if (!finishedAnim.equals(animIntro.getLast())) {
                // Play the next intro in the list
                this.playAnimation(animIntro.get(this.index + 1), false, true);
            } else {
                // No more intros -> go to SLOW
                playPhase(ScenePhase.SLOW, getRandomFromList(this.animSlow), true, false);
            }
        }

    }



    private void handleFinishedAnimation(String finishedAnim, List<String> sequence, Runnable onEnd) {
        int index = sequence.indexOf(finishedAnim);
        if (index != -1) {
            if (index < sequence.size() - 1) {
                // Play the next in sequence
                playPhase(currentPhase, sequence.get(index + 1), false, true);
            } else {
                // Sequence finished
                onEnd.run();
            }
        }
    }

    public ScenePhase getCurrentPhase(){
        return currentPhase;
    }


    private void onSceneStop() {
        if (this.hasPassengers()) {
            this.removeAllPassengers();
        }

        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (!player.hasVehicle()) player.setInvisible(false);
        }

        currentPhase = ScenePhase.NONE;
        this.stopOverrideAnimations();
    }

    public void setKeyHeld(boolean held) {
        this.isKeyHeld = held;
    }

    public void playPhase(ScenePhase phase, String animation, boolean loop, boolean holdOnLastFrame) {
        currentPhase = phase;
        this.playAnimation(animation, loop, holdOnLastFrame);
    }

    public void tryTriggerCum() {
        if (this.isSceneActive() && this.getSceneProgress() >= cumThreshold && currentPhase != ScenePhase.CUM) {
            playPhase(ScenePhase.CUM, animCum, false, false);
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

        if(bedPos != null && isBedScene &! Utils.checkForBlockAt(this.getWorld(),bedPos,null,BlockTags.BEDS)){
            stopScene();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setSceneProgress(sceneProgress);
        this.toggleModelBones(List.of("RightLeg", "LeftLeg", "Torso2"), this.isSceneActive());

        // Handle scene exit
        if (this.isSceneActive()) onSceneActive();
        if(!this.hasPassengers()) stopScene();


        PlayerEntity player = (PlayerEntity) this.getFirstPassenger();
        if (player != null) player.setInvisible(true);

        // Handle scene phases
        switch (currentPhase) {
            case SLOW -> {
                sceneProgress += SLOW_SPEED;
                if (isKeyHeld) {
                    playPhase(ScenePhase.FAST, getRandomFromList(this.animFast), true, false);
                }
            }
            case FAST -> {
                sceneProgress += FAST_SPEED;
                if (!isKeyHeld) {
                    playPhase(ScenePhase.SLOW, getRandomFromList(this.animSlow), true, false);
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
        controllerRegistrar.add(new AnimationController<>(this, "controller", 4, this::handleAnimations));
    }

    private <T extends GeoAnimatable> PlayState handleAnimations(AnimationState<T> state) {
        AnimationController<?> controller = state.getController();

        String overrideAnim = this.getOverrideAnim();
        boolean overrideLoop = this.getOverrideLoop();
        boolean overrideHold = this.getOverrideHold();

        // 1. Forced animation override
        if (overrideAnim != null && !overrideAnim.isEmpty()) {
            this.currentAnimState = overrideAnim;
            this.currentLoopState = overrideLoop;
            this.currentHoldState = overrideHold;

            if (controller.getAnimationState() == AnimationController.State.RUNNING) ClientPlayNetworking.send(new OverrideAnimationStateSyncC2SPacket(this.getId(), true));

            if(!overrideLoop) {

                // End override if it was one-shot and finished playing
                if (controller.getAnimationState() == AnimationController.State.STOPPED || controller.getAnimationState() == AnimationController.State.PAUSED) {
                    ClientPlayNetworking.send(new NextSceneAnimationC2SPacket(this.getId(), this.currentAnimState));
                }
            }
        }
        else {
            ClientPlayNetworking.send(new OverrideAnimationStateSyncC2SPacket(this.getId(), false));
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
    private String lastFinishedAnim = "";


    public void animationFinished(String finishedAnimation){
        if (finishedAnimation.equals(lastFinishedAnim)) return; // ignore duplicates
        lastFinishedAnim = finishedAnimation;
        if (this.getWorld().isClient()) return;


        this.setOverrideAnimPlayingState(false);
        this.onAnimationFinished(finishedAnimation);
        if(!isSceneActive()) stopOverrideAnimations();
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
        return bedOffset;
    }

}
