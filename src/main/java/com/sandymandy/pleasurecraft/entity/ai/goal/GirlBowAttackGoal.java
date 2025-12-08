package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

import java.util.EnumSet;

public class GirlBowAttackGoal extends Goal {
    private final GirlEntityAI girl;
    private final double moveSpeed;
    private final float maxRangeSq;
    private int shootCooldown = 0;
    private int seeTicks = 0;

    public GirlBowAttackGoal(GirlEntityAI girl, double speed, float range, int cooldownTicks) {
        this.girl = girl;
        this.moveSpeed = speed;
        this.maxRangeSq = range * range;
        this.shootCooldown = cooldownTicks;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private boolean hasBow() {
        return girl.isHolding(Items.BOW);
    }

    @Override
    public boolean canStart() {
        return girl.getTarget() != null && hasBow();
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void start() {
        girl.setAttacking(true);
        girl.setRunning(true);
    }

    @Override
    public void stop() {
        girl.setAttacking(false);
        girl.setRunning(false);
        girl.clearActiveItem();
        seeTicks = 0;
        shootCooldown = 0;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = girl.getTarget();
        if (target == null) return;

        double distanceSq = girl.squaredDistanceTo(target);
        boolean visible = girl.getVisibilityCache().canSee(target);

        // Track "seeing" ticks
        if (visible) seeTicks++;
        else seeTicks--;

        // ===== MOVEMENT =====
        if (distanceSq > maxRangeSq || seeTicks < 20) {
            // Move toward target
            girl.getNavigation().startMovingTo(target, moveSpeed);

        } else {
            girl.getNavigation().stop();
        }

        // Aim
        girl.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ(), 35F, 35F);

        // Shoot
        if (girl.isUsingItem()) {
            // Already pulling the bow
            int useTime = girl.getItemUseTime();

            if (!visible && seeTicks < -20) {
                // Lost sight too long → stop drawing
                girl.clearActiveItem();
                return;
            }

            if (useTime >= 20) {
                // Fire!
                float pull = BowItem.getPullProgress(useTime);

                girl.shootAt(target, pull);

                girl.clearActiveItem();
                shootCooldown = 20; // cooldown between arrows
            }

        } else if (shootCooldown <= 0 && visible) {
            // Start drawing bow
            girl.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(girl, Items.BOW));
        }

        if (shootCooldown > 0) shootCooldown--;
    }
}
