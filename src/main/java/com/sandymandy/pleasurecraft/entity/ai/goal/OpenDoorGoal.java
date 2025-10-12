package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.TameableGirlEntity;
import net.minecraft.entity.ai.goal.DoorInteractGoal;
import net.minecraft.entity.mob.MobEntity;

public class OpenDoorGoal extends DoorInteractGoal {
    private final boolean closeDoor;
    private int closeTimer;

    public OpenDoorGoal(MobEntity mob, boolean closeDoor) {
        super(mob);
        this.closeDoor = closeDoor;
    }

    @Override
    public void start() {
        super.start();
        this.setDoorOpen(true);
        this.closeTimer = 20; // ticks until close (1 second)
    }

    @Override
    public void tick() {
        super.tick();
        if (this.closeDoor && this.isDoorOpen()) {
            if (--this.closeTimer <= 0) {
                this.setDoorOpen(false);
            }
        }
    }
}
