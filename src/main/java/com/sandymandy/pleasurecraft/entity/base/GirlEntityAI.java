package com.sandymandy.pleasurecraft.entity.base;

import com.sandymandy.pleasurecraft.entity.ai.goal.*;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class GirlEntityAI extends TameableGirlEntity implements SmartBrainOwner<GirlEntityAI> {

    private LivingEntity attackTarget;
    private int ticksSinceLastHit;
    private static final int MAX_TICKS_NO_HIT = 20 * 20;
    private boolean requestStrip = false;
    private boolean requestMoveToBed = false;
    public SceneOptions stripOptions = SceneOptions.EMPTY;
    public BlockPos targetBedPos;
    private boolean requestMoveToPlayer;
    protected GirlEntityAI(EntityType<? extends TameableGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new GirlSitGoal(this));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new LongDoorInteractGoal(this, true));
        this.goalSelector.add(3, new TameableEscapeDangerGoal(1.5D, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
        this.goalSelector.add(4, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(5, new GirlAttackGoal(this, 1.5, false));
        this.goalSelector.add(6, new ConditionalGoal(new GirlFollowOwnerGoal(this, 1.0, 10.0F, 2.0F), this::isFollowing));
        this.goalSelector.add(7, new GirlStayNearBaseGoal(this, 1.0, 2.0F, 15.0F, 150));
        this.goalSelector.add(8, new TemptGoal(this, 1.25D, Ingredient.ofItems(getTameItem()), false));
        this.goalSelector.add(9, new ConditionalGoal(new LookAtEntityGoal(this, PlayerEntity.class, 6.0F),() -> !isMovementLocked()));
        this.goalSelector.add(10, new ConditionalGoal(new LookAroundGoal(this),() -> !isMovementLocked()));
        this.targetSelector.add(1, new ConditionalGoal(new GirlTrackOwnerAttackerGoal(this), this::isFollowing));
        this.targetSelector.add(2, new ConditionalGoal(new GirlAttackWithOwnerGoal(this, GirlEntityAI.class), this::isFollowing));
        this.targetSelector.add(3, new RevengeGoal(this, PlayerEntity.class, GirlEntityAI.class));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.attackTarget != null) {
            ticksSinceLastHit++;

            if (ticksSinceLastHit >= MAX_TICKS_NO_HIT) {
                // Lost interest — stop attacking
                this.setTarget(null);
                attackTarget = null;
                ticksSinceLastHit = 0;
            }
        }

        if(isInInventory()){
            this.navigation.stop();
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);

        if (target != null) {
            attackTarget = target;
            ticksSinceLastHit = 0; // reset countdown on new target
        } else {
            attackTarget = null;
            ticksSinceLastHit = 0;
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        boolean success = super.tryAttack(world, target);

        if (success && target == attackTarget) {
            ticksSinceLastHit = 0; // reset timer on successful hit
        }

        return success;
    }

    public void requestMoveToBed() {
        this.requestMoveToBed = true;
    }

    public boolean shouldMoveToBed() {
        if (requestMoveToBed) {
            requestMoveToBed = false;
            return true;
        }
        return false;
    }

    public void requestMoveToPlayer() {
        this.requestMoveToPlayer = true;
    }

    public boolean shouldMoveToPlayer() {
        if (requestMoveToPlayer) {
            requestMoveToPlayer = false;
            return true;
        }
        return false;
    }

    public void requestStrip() {
        this.requestStrip(null);
    }

    public void requestStrip(@Nullable SceneOptions options) {
        this.requestStrip = true;

        if(options != null){
            this.stripOptions = options;
        }
    }

    public boolean shouldStrip() {
        if (requestStrip) {
            requestStrip = false;
            return true;
        }
        return false;
    }

    @Override
    public List<? extends ExtendedSensor<? extends GirlEntityAI>> getSensors() {
        return List.of();
    }
}
