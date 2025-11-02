package com.sandymandy.pleasurecraft.mixins.freecam;

import com.sandymandy.pleasurecraft.freecam.Freecam;
import com.sandymandy.pleasurecraft.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.sandymandy.pleasurecraft.freecam.Freecam.MC;
import static com.sandymandy.pleasurecraft.config.ModConfig.InteractionMode.PLAYER;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

@Mixin(ClientPlayerInteractionManager.class)
public class MultiPlayerGameModeMixin {

    // Prevents interacting with blocks when allowInteract is disabled.
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (freecam$disableInteract()) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
    private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entity.equals(MC.player) || freecam$disableInteract()) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interactEntityAtLocation", at = @At("HEAD"), cancellable = true)
    private void onInteractEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entity.equals(MC.player) || freecam$disableInteract()) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    // Prevents attacking self.
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target.equals(MC.player)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean freecam$disableInteract() {
        return Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !freecam$allowInteract();
    }

    @Unique
    private static boolean freecam$allowInteract() {
        return ModConfig.INSTANCE.utility.allowInteract && ModConfig.INSTANCE.utility.interactionMode.equals(PLAYER);
    }
}
