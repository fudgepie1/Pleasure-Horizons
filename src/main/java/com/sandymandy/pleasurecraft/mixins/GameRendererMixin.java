package com.sandymandy.pleasurecraft.mixins;

import com.sandymandy.pleasurecraft.freecam.Freecam;
import com.sandymandy.pleasurecraft.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.sandymandy.pleasurecraft.freecam.Freecam.MC;
import static com.sandymandy.pleasurecraft.config.ModConfig.InteractionMode.PLAYER;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Disables block outlines when allowInteract is disabled.
    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !freecam$allowInteract()) {
            cir.setReturnValue(false);
        }
    }

    // Makes mouse clicks come from the player rather than the freecam entity when player control is enabled or if interaction mode is set to player.
    @ModifyVariable(method = "findCrosshairTarget", at = @At("HEAD"), argsOnly = true)
    private Entity onUpdateTargetedEntity(Entity entity) {
        if (Freecam.isEnabled() && (Freecam.isPlayerControlEnabled()
                || ModConfig.INSTANCE.utility.interactionMode.equals(ModConfig.InteractionMode.PLAYER))) {
            return MC.player;
        }
        return entity;
    }



    @Unique
    private static boolean freecam$allowInteract() {
        return ModConfig.INSTANCE.utility.allowInteract && ModConfig.INSTANCE.utility.interactionMode.equals(PLAYER);
    }
}
