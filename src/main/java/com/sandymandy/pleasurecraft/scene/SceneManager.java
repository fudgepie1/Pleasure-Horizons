package com.sandymandy.pleasurecraft.scene;

import com.sandymandy.pleasurecraft.entity.base.AbstractGirlEntity;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.util.Utils;
import com.sandymandy.pleasurecraft.util.Utils.BlockInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.List;


public  class SceneManager {

    private final AbstractGirlEntity entity;
    public String passengerBoneName = "Torso2";
    public float passengerYOffset = 0f;
    private ScenePhase currentPhase = ScenePhase.NONE;
    private float sceneProgress = 0f;
    private final float cumThreshold = 5f;
    private boolean isKeyHeld = false;
    private int timer = 0 ;
    BlockPos bedPos;
    // Animations
    private List<String> animIntro;
    private List<String> animSlow;
    private List<String> animFast;
    private String animCum;
    private float bedOffset;
    private List<String> bedIdle;
    public boolean isBedScene;
    private int index = 0;

    // Progress speeds
    private static final float SLOW_SPEED = 0.002f;
    private static final float FAST_SPEED = 0.01f;

    public SceneManager(AbstractGirlEntity entity) {
        this.entity = entity;
    }

    public float bedOffset(){
        return bedOffset;
    }

    public void playBedIdle(boolean clear) {
        if(!entity.getWorld().isClient()) {

            if (!clear) playPhase(ScenePhase.IDLE, bedIdle.getFirst(), false, true);
            else {
                if (bedIdle.contains(entity.getOverrideAnim()))
                    entity.stopOverrideAnimations();
            }
        }
    }


    public enum ScenePhase {
        NONE, IDLE, INTRO, SLOW, FAST, CUM
    }

    public void startScene(PlayerEntity rider, SceneOption option) {
        if (entity.isSceneActive()) return;

        if (entity.isSitting()) entity.setSitting(false);
        if (!entity.isStripped()){
            entity.requestStrip();
            entity.messageAsEntity(rider, "Be there in a bit, just need to take these clothes off");
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
            BlockInfo bedInfo = Utils.findNearbyBlock(
                    entity.getWorld(),
                    entity.getBlockPos(),
                    15,// radius
                    null,
                    BlockTags.BEDS
            );

            if (bedInfo == null) {
                entity.messageAsEntity(rider, "We need a bed nearby for this...");
                return;
            }

            //  Store target bed pos in entity so the BedGoal can use it
            entity.targetBedPos = bedInfo.pos();
            bedPos = bedInfo.pos();


            entity.requestMoveToBed();
            return; // Don't start yet – let the goal handle it
        }


        onSceneStart(rider);
    }

    public void stopScene() {
        if (!entity.isSceneActive()) return;
        onSceneStop();
        entity.setSceneState(false);
    }

    public void onSceneStart(PlayerEntity rider) {
        rider.setInvisible(true);

        entity.setSceneState(true);
        this.sceneProgress = 0f;
        isKeyHeld = false;
        entity.targetBedPos = null;
        rider.startRiding(entity, false);

        playPhase(ScenePhase.INTRO, this.animIntro.getFirst(), false, true);
    }

    public void onAnimationFinished(String finishedAnim) {
        if(this.entity.getWorld().isClient())return;

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
                entity.playAnimation(animIntro.get(this.index + 1), false, true);
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


    private void onSceneStop() {
        if (entity.hasPassengers()) {
            entity.removeAllPassengers();
        }

        for (PlayerEntity player : entity.getWorld().getPlayers()) {
            if (!player.hasVehicle()) player.setInvisible(false);
        }

        currentPhase = ScenePhase.NONE;
        entity.stopOverrideAnimations();
    }

    public void setKeyHeld(boolean held) {
        this.isKeyHeld = held;
    }

    public void playPhase(ScenePhase phase, String animation, boolean loop, boolean holdOnLastFrame) {
        currentPhase = phase;
        entity.playAnimation(animation, loop, holdOnLastFrame);
    }

    public void tryTriggerCum() {
        if (entity.isSceneActive() && this.entity.getSceneProgress() >= cumThreshold && currentPhase != ScenePhase.CUM) {
            playPhase(ScenePhase.CUM, animCum, false, false);
        }
    }

    private String getRandomFromList(List<String> list) {
        if (list.size() == 1) return list.getFirst();
        int index = entity.getWorld().getRandom().nextInt(list.size());
        return list.get(index);
    }

    private void onSceneActive(){
        if(!entity.hasPassengers()) stopScene();

        timer ++;

        if(timer >= 20 && !(sceneProgress == 0f) && !this.entity.getWorld().isClient && sceneProgress < (cumThreshold + 0.2f)){
            if(sceneProgress >= cumThreshold){
                new PleasureCraftMessages().GlobleMessage(entity.getWorld(),"Scene Progress: READY TO CUM");
            }
            else new PleasureCraftMessages().GlobleMessage(entity.getWorld(),"Scene Progress: "+sceneProgress);
            timer = 0;
        }

        if(bedPos != null && isBedScene &! Utils.checkForBlockAt(entity.getWorld(),bedPos,null,BlockTags.BEDS)){
            stopScene();
        }
    }


    public void tick() {
        entity.setSceneProgress(sceneProgress);
        entity.toggleModelBones(List.of("RightLeg", "LeftLeg", "Torso2"), entity.isSceneActive());

        // Handle scene exit
        if (entity.isSceneActive()) onSceneActive();


        PlayerEntity player = (PlayerEntity) entity.getFirstPassenger();
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
}