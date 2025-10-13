package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityVisuals;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class MoveToPlayerGoal extends Goal {
    private final GirlEntityVisuals girl;
    private boolean started = false;
    private final double speed;

    public MoveToPlayerGoal(GirlEntityVisuals girl, double speed) {
        this.girl = girl;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        return girl.shouldMoveToPlayer();
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
