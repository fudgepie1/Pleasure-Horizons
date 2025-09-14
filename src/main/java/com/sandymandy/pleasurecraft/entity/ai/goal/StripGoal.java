package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StripGoal extends Goal {
    private final SceneEntity girl;
    private int stripTimer;
    private final int stripTimerThreshold ;
    private boolean stripTrigged = false;
    private boolean started = false;

    public StripGoal(SceneEntity girl, int amountOfTickUntilStrip) {
        this.girl = girl;
        this.stripTimerThreshold = amountOfTickUntilStrip;        // 3 seconds (20 ticks per second)
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        // Only start if not already stripping/dressing and a strip request exists
        return girl.getOverrideAnim().isEmpty() && girl.shouldStrip();
    }

    @Override
    public void start() {
        this.stripTimer = 0;                  // reset timer every time it starts
        girl.setFreeze(true);
        girl.playAnimation("strip", false, false); // play strip anim
        stripTrigged = false;
        started = true;
    }

    @Override
    public void tick() {
        if(started) {
            if (!girl.isFrozenInPlace()) girl.setFreeze(true);
            stripTimer++;
//            PleasureCraft.LOGGER.info(stripTimer+"");
            if (stripTimer >= stripTimerThreshold && !stripTrigged) {
//                PleasureCraft.LOGGER.info("TRIGGERD STRIP_________________________________________________________________");
                girl.setStripped(!girl.isStripped()); // toggle stripped state
                stripTrigged = true;
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        // Continue until animation finishes OR timer hasn’t passed yet
        return !stripTrigged || !girl.getOverrideAnim().isEmpty();
    }

    @Override
    public void stop() {
        girl.setFreeze(false);
//        PleasureCraft.LOGGER.info("Stopped");
        started = false;
    }
}
