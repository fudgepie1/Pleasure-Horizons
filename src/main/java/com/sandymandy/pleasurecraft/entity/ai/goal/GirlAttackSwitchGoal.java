package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Items;

import java.util.EnumSet;

public class GirlAttackSwitchGoal extends Goal {

    private final GirlEntityAI girl;
    private final GirlMeleeAttackGoal meleeGoal;
    private final GirlBowAttackGoal bowGoal;

    private final double switchDistanceSq;

    private Goal activeGoal = null;

    public GirlAttackSwitchGoal(GirlEntityAI girl, double speed, float switchDistance) {
        this.girl = girl;

        this.meleeGoal = new GirlMeleeAttackGoal(girl, speed, false);
        this.bowGoal = new GirlBowAttackGoal(girl, speed, 15, 10);

        this.switchDistanceSq = switchDistance * switchDistance;

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private boolean hasBow() {
        return girl.isHolding(Items.BOW);
    }

    @Override
    public boolean canStart() {
        return girl.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        return girl.getTarget() != null;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    private void swapTo(Goal newGoal) {
        if (activeGoal == newGoal) return;

        // stop old
        if (activeGoal != null) activeGoal.stop();
        activeGoal = newGoal;
        activeGoal.start();
    }

    @Override
    public void stop() {
        if (activeGoal != null) {
            activeGoal.stop();
            activeGoal = null;
        }
    }

    @Override
    public void tick() {
        LivingEntity target = girl.getTarget();
        if (target == null) {
            stop();
            return;
        }

        double distSq = girl.squaredDistanceTo(target);

        boolean canUseBow = hasBow();

        // Decide what to use:
        if (canUseBow && distSq > switchDistanceSq) {
            // Long range → bow
            swapTo(bowGoal);
        } else {
            // Close range → melee
            swapTo(meleeGoal);
        }

        // Let the active goal handle the real attacking
        if (activeGoal != null) {
            activeGoal.tick();
        }
    }
}
