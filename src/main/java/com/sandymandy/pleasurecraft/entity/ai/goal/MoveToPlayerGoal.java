package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.util.ScenePhase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class MoveToPlayerGoal extends Goal {
    private final SceneEntity girl;
    private boolean started = false;
    private double speed;

    public MoveToPlayerGoal(SceneEntity girl, double speed) {
        this.girl = girl;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        return girl.shouldMoveToPlayer();
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        handleMovement();
        startOnContact();
    }

    @Override
    public boolean shouldContinue() {
        return !started ;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    private void handleMovement() {
        if (!started) {
            this.girl.getNavigation().startMovingTo(this.girl.scenePlayer, this.speed);
        }
    }

    private void startOnContact(){
        if(this.girl.squaredDistanceTo(this.girl.scenePlayer) <= 2.5){
            this.girl.setVelocity(Vec3d.ZERO);
            this.girl.getNavigation().stop();
            this.girl.onSceneStart();
            this.started = true;
        }
    }
}
