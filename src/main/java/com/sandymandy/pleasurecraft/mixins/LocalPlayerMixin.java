package com.sandymandy.pleasurecraft.mixins;

import com.sandymandy.pleasurecraft.Freecam;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.sandymandy.pleasurecraft.Freecam.MC;

@Mixin(ClientPlayerEntity.class)
public class LocalPlayerMixin {

    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && this.equals(MC.player)) {
            cir.setReturnValue(true);
        }
    }
}
