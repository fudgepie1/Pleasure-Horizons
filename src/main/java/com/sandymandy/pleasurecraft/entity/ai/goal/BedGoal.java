package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.base.AbstractGirlEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import com.sandymandy.pleasurecraft.util.Utils;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;


public class BedGoal extends Goal {
    private final AbstractGirlEntity entity;
    private final double speed;
    private PlayerEntity player;
    private final EntityNavigation navigation;
    private Direction bedFacing;
    private Vec3d snapPos;
    private double snapPosX;
    private double snapPosZ;

    public BedGoal(AbstractGirlEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(entity.getNavigation() instanceof MobNavigation) && !(entity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
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
        }
        else if (bedFacing == Direction.WEST){
            this.entity.targetBedPos = new BlockPos(this.entity.targetBedPos.getX() + 1, this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ());
        }
    }

    @Override
    public void tick() {
        handleMovement();
    }

    private void handleMovement(){
        if (this.entity.squaredDistanceTo(this.entity.targetBedPos.toCenterPos()) <= 2.5) {
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
                this.entity.setPosition(this.entity.targetBedPos.getX() + 0.5, this.entity.targetBedPos.getY() + 0.4, this.entity.targetBedPos.getZ() + 0.5);

                // Start the Scene
                startOnContact();
            }
        }
        else {
            this.entity.messageAsEntity("Moving to bed");
            this.navigation.startMovingTo(this.entity.targetBedPos.getX(), this.entity.targetBedPos.getY(), this.entity.targetBedPos.getZ(), this.speed);
        }
    }

    private void startOnContact(){
        if(this.entity.squaredDistanceTo(this.player) <= 1.5){
            entity.getSceneManager().onSceneStart(player);
        }
    }

    @Override
    public void stop() {
        this.entity.setWaitingAtBedState(false);
    }
}
