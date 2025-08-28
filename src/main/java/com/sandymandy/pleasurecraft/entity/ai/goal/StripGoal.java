package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.AbstractGirlEntity;
import net.minecraft.entity.ai.goal.Goal;
import java.util.EnumSet;

public class StripGoal extends Goal {
    private final AbstractGirlEntity girl;
    private int stripTimer;
    private int stripTimerThreshold;

    public StripGoal(AbstractGirlEntity girl) {
        this.girl = girl;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        // Only start if not already stripping/dressing and a strip request exists
        return !girl.isOverrideAnimPlaying() && girl.shouldStrip();
    }

    @Override
    public void start() {
        this.stripTimer = 0;                  // reset timer every time it starts
        this.stripTimerThreshold = 38;        // 3 seconds (20 ticks per second)
        girl.setOverrideFreeze(true);
        girl.setFreeze(true);
        girl.playAnimation("strip", false, false); // play strip anim
    }

    @Override
    public void tick() {
        stripTimer++;

        PleasureCraft.LOGGER.info(stripTimer+"");

        if (stripTimer == stripTimerThreshold) {
            girl.setStripped(!girl.isStripped()); // toggle stripped state
        }
    }

    @Override
    public boolean shouldContinue() {
        // Continue until animation finishes OR timer hasn’t passed yet
        return stripTimer < stripTimerThreshold || girl.isOverrideAnimPlaying();
    }

    @Override
    public void stop() {
        girl.setOverrideFreeze(false);
        girl.setFreeze(false);
    }
}
