package com.sandymandy.pleasurecraft.entity.base.wild;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityScene;
import com.sandymandy.pleasurecraft.networking.S2C.SceneOptionsS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class WildGirlEntity extends GirlEntityScene {

    protected WildGirlEntity(EntityType<? extends GirlEntityScene> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new TemptGoal(this, 1D, Ingredient.ofItems(getAttractedTo()), false));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

         this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        // no taming, no inventory, no following

        if (!this.getWorld().isClient() && !this.isSceneActive()) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            if (stack.isOf(getAttractedTo())) {
                if (getCurrentRelationshipLevel() < maxRelationshipLevel()) {
                    stack.decrementUnlessCreative(1, player);
                    player.sendMessage(Text.literal("She Liked The Gift"), true);
                    setCurrentRelationshipLevel(getCurrentRelationshipLevel() + 1);
                    this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            }

            if (stack.isEmpty() || getCurrentRelationshipLevel() > maxRelationshipLevel()) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, new SceneOptionsS2CPacket(this.getId(), this.getCurrentRelationshipLevel(), new ItemStack(getAttractedTo()), this.getScenes()));
                return ActionResult.SUCCESS;
            }

        }

        return super.interactMob(player, hand);
    }

    // Override to prevent following, sitting, etc.
    @Override
    public void setFollowing(boolean follow) {
    }

    @Override
    public void setSitting(boolean sitting) {
    }
}