package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityScene;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StripGoal extends Goal {
    private final GirlEntityScene girl; ;
    private boolean stripTrigged = false;
    private boolean started = false;
    private SceneOptions sceneOptions = SceneOptions.EMPTY;

    public StripGoal(GirlEntityScene girl) {
        this.girl = girl;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        // Only start if not already stripping/dressing and a strip request exists
        return girl.getOverrideAnim().isEmpty() && girl.shouldStrip();
    }

    @Override
    public void start() {
        girl.setFreeze(true);
        girl.playAnimation("strip", false, false); // play strip anim
        if(!girl.stripOptions.equals(SceneOptions.EMPTY))
        {
            this.sceneOptions = girl.stripOptions;
            girl.stripOptions = SceneOptions.EMPTY;
        }
        stripTrigged = false;
        started = true;
    }

    @Override
    public void tick() {
        if(started) {
            if (!girl.isFrozenInPlace()) girl.setFreeze(true);
            if (girl.getAnimationKeyFrameEvent().equals("becomeNude".toLowerCase()) && !stripTrigged) {
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
        started = false;
        if(!this.sceneOptions.equals(SceneOptions.EMPTY)){
            girl.startScene(this.sceneOptions);
            this.sceneOptions = SceneOptions.EMPTY;
        }
    }
}
