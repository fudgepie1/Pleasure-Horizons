package com.sandymandy.pleasurecraft.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.LightmapTextureManager;
import com.sandymandy.pleasurecraft.Freecam;
import com.sandymandy.pleasurecraft.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class LightTextureMixin {

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float onSetBrightnessFactor(Double instance, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            return Float.MAX_VALUE;
        }
        return original.call(instance);
    }
}
