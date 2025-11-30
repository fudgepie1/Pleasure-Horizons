package com.sandymandy.pleasurecraft.mixins.glsl;

import com.sandymandy.pleasurecraft.PleasureCraftClient;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.shader.GetShaderPreprocessor;
import com.sandymandy.pleasurecraft.shader.ShaderPreprocessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.gl.ShaderLoader$1")
public class ShaderLoader$1Mixin implements GetShaderPreprocessor {
    @Override
    public @Nullable ShaderPreprocessor pleasurecraft$getShaderPreprocessor() {
        if (PleasureCraftClient.areIrisShadersEnabled() || !ModConfig.INSTANCE.girls.disableShading) {
            return null;
        }

        return ShaderPreprocessor.CONDITIONAL;
    }
}
