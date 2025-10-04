package com.sandymandy.pleasurecraft.mixins;

import com.sandymandy.pleasurecraft.freecam.Freecam;
import com.sandymandy.pleasurecraft.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.sandymandy.pleasurecraft.config.ModBindings.KEY_TOGGLE;
import static com.sandymandy.pleasurecraft.config.ModBindings.KEY_TRIPOD_RESET;
import static com.sandymandy.pleasurecraft.config.ModConfig.InteractionMode.PLAYER;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {

    // Prevents attacks when allowInteract is disabled.
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        if (freecam$disableInteract()) {
            cir.cancel();
        }
    }

    // Prevents item pick when allowInteract is disabled.
    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void onDoItemPick(CallbackInfo ci) {
        if (freecam$disableInteract()) {
            ci.cancel();
        }
    }

    // Prevents block breaking when allowInteract is disabled.
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(CallbackInfo ci) {
        if (freecam$disableInteract()) {
            ci.cancel();
        }
    }

//     Prevents hotbar keys from changing selected slot when freecam key is held
    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void onHandleInputEvents(CallbackInfo ci) {
        if (KEY_TOGGLE.get().isPressed() || KEY_TRIPOD_RESET.get().isPressed()) {
            ci.cancel();
        }
    }

    // Disables freecam if the player disconnects.
    @Inject(method = "disconnect()V", at = @At(value = "HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        Freecam.onDisconnect();
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
