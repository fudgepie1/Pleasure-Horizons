package com.sandymandy.pleasurecraft.mixins.glsl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.sandymandy.pleasurecraft.PleasureCraftClient;
import com.sandymandy.pleasurecraft.shader.GetShaderPreprocessor;
import com.sandymandy.pleasurecraft.shader.ShaderPreprocessor;
import net.minecraft.client.gl.GlImportProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlImportProcessor.class)
public class GlImportProcessorMixin implements GetShaderPreprocessor {
    @Unique
    @WrapOperation(
        method = "parseImports",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gl/GlImportProcessor;loadImport(ZLjava/lang/String;)Ljava/lang/String;"
        )
    )
    private String modifyLightingCalculations(final GlImportProcessor self, final boolean quotesUsed, final String file, final Operation<String> operation, final @Local(ordinal = 0, argsOnly = true) String source) {
        final var original = operation.call(self, quotesUsed, file);
        if (quotesUsed || !ShaderPreprocessor.TARGET_SYSTEM_MOJ_IMPORT.equals(file)) {
            return original;
        }

        if (PleasureCraftClient.areIrisShadersEnabled()) {
            return original;
        }

        final var preprocessor = this.pleasurecraft$getShaderPreprocessor();
        if (preprocessor == null || !preprocessor.containsAllKeywords(source)) {
            return original;
        }

        return preprocessor.process(original);
    }
}
