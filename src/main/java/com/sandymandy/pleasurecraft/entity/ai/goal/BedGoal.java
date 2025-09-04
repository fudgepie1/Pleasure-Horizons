package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.util.Utils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;


public class BedGoal extends Goal {
    private final SceneEntity entity;
    private final double speed;
    private PlayerEntity player;
    private final EntityNavigation navigation;
    private Direction bedFacing;
    private Vec3d snapPos;
    private Vec3d scenePos;
    private Path pathToBed;

    public BedGoal(SceneEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Control.JUMP));
        if (!(entity.getNavigation() instanceof MobNavigation) && !(entity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for BedGoal");
        }
    }

    @Override
    public boolean canStart() {
        return this.entity.shouldMoveToBed() && this.entity.targetBedPos != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.entity.targetBedPos != null && Utils.checkForBlockAt(this.entity.getWorld(), this.entity.targetBedPos, null, BlockTags.BEDS);
    }

    @Override
    public void start() {
        this.player = (PlayerEntity) this.entity.getOwner();

        var state = this.entity.getWorld().getBlockState(this.entity.targetBedPos);
        if (state.contains(Properties.HORIZONTAL_FACING)) {
            this.bedFacing = state.get(Properties.HORIZONTAL_FACING);
        }
        else {
            this.bedFacing = Direction.NORTH; // default fallback
        }

        if (bedFacing == Direction.NORTH){
            this.entity.targetBedPos = new BlockPos(this.entity.targetBedPos.getX(), this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ() + 1);
            this.snapPos = new Vec3d(this.entity.targetBedPos.getX() + 0.5, this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ() + 1.5);
            this.scenePos = new Vec3d(this.snapPos.getX(), this.snapPos.getY(), this.snapPos.getZ() - entity.getBedOffset());
        }
        else if (bedFacing == Direction.EAST){
            this.snapPos = new Vec3d(this.entity.targetBedPos.getX() - 0.5, this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ() + 0.5);
            this.scenePos = new Vec3d(this.snapPos.getX() + entity.getBedOffset(), this.snapPos.getY(), this.snapPos.getZ());
        }
        else if (bedFacing == Direction.SOUTH){
            this.snapPos = new Vec3d(this.entity.targetBedPos.getX() + 0.5, this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ() - 0.5);
            this.scenePos = new Vec3d(this.snapPos.getX(), this.snapPos.getY(), this.snapPos.getZ() + entity.getBedOffset());
        }
        else if (bedFacing == Direction.WEST){
            this.entity.targetBedPos = new BlockPos(this.entity.targetBedPos.getX() + 1, this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ());
            this.snapPos = new Vec3d(this.entity.targetBedPos.getX() + 1.5, this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ() + 0.5);
            this.scenePos = new Vec3d(this.snapPos.getX() - entity.getBedOffset(), this.snapPos.getY(), this.snapPos.getZ());
        }

        pathToBed = this.navigation.findPathTo(this.entity.targetBedPos, 1);
    }

    @Override
    public void tick() {
        handleMovement();
        startOnContact();
    }

    private void handleMovement(){
        if (this.entity.squaredDistanceTo(this.entity.targetBedPos.toCenterPos()) <= 3) {
            if (player != null) {
                this.navigation.stop();
                // Make the entity Face the direction of the bed
                if (bedFacing != null) {
                    float yaw = Direction.getHorizontalDegreesOrThrow(bedFacing); // Direction → yaw in degrees
                    this.entity.setYaw(yaw);
                    this.entity.setHeadYaw(yaw);
                }

                //Make the entity freeze
                this.entity.setWaitingAtBedState(true);

                // Snap to Bed
                this.entity.setPosition(snapPos);

                // Start the Scene
                this.entity.playBedIdle(false);
            }
        }
        else if (!entity.isWaitingAtBed()) {
            this.navigation.startMovingAlong(pathToBed, this.speed);
        }
    }

    private void startOnContact(){
        if(!entity.isWaitingAtBed()) return;
        if(this.entity.squaredDistanceTo(this.player) <= 1.5 /*&& entity.getCurrentPhase().equals(SceneEntity.ScenePhase.IDLE)*/){
            this.entity.setPosition(scenePos);
            this.entity.onSceneStart(player);
        }
    }

    @Override
    public void stop() {
        this.navigation.stop();
        this.entity.setWaitingAtBedState(false);
        this.entity.playBedIdle(true);
    }

}
