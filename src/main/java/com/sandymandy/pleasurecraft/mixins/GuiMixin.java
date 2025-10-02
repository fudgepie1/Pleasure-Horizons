package com.sandymandy.pleasurecraft.mixins;

import com.sandymandy.pleasurecraft.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.sandymandy.pleasurecraft.Freecam.MC;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
public class GuiMixin {
    // Makes HUD correspond to the player rather than the FreeCamera.
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPlayer(CallbackInfoReturnable<PlayerEntity> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(MC.player);
        }
    }

    // Don't render equipped-item overlays while Freecam is active
    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderTextureOverlay(DrawContext guiGraphics, Identifier shaderLocation, float alpha, CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }
}
