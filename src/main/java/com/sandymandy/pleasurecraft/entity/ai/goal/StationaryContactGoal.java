package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.GirlEntityScene;
import com.sandymandy.pleasurecraft.util.variables.ScenePhase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.Direction;

import java.util.UUID;

public class StationaryContactGoal extends Goal {
    private final GirlEntityScene entity;
    private final EntityNavigation navigation;
    private boolean stop = false;
    public StationaryContactGoal(GirlEntityScene entity) {
        this.entity = entity;
        this.navigation = entity.getNavigation();

    }

    @Override
    public boolean canStart() {
        return this.entity.shouldWaitForPlayer();
    }

    @Override
    public void start() {
        this.stop = false;
    }

    @Override
    public void tick() {
        handleMovement();
        startOnContact();
    }

    private void handleMovement() {
        if (this.entity.scenePlayer != null) {
            this.navigation.stop();

            // Freeze state first
            this.entity.setWaitingForPlayerState(true);

            if (!this.entity.isSceneActive()) {
                this.entity.playPhase(ScenePhase.LAYING_DOWN);
            }
        }
    }

    private void startOnContact() {
        if (!entity.isWaitingForPlayer()) return;

        UUID playerId = entity.scenePlayer.getUuid();

        // Someone else already has this player in a scene
        if (PleasureCraft.activeScenes.containsKey(playerId)) return;

        if (this.entity.squaredDistanceTo(this.entity.scenePlayer) <= 1.5 &&
                entity.getCurrentScenePhase().equals(ScenePhase.BED_IDLE)) {

            PleasureCraft.activeScenes.put(playerId, entity.getUuid()); // reserve player
            this.entity.startRidingScene();
            this.stop = true;
        }
    }

    @Override
    public void stop() {
        this.navigation.stop();
        this.entity.setWaitingForPlayerState(false);
    }

    @Override
    public boolean shouldContinue() {
        return !this.stop;
    }
}
