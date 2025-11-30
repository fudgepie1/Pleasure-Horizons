package com.sandymandy.pleasurecraft.shader;

import org.jetbrains.annotations.Nullable;

public interface GetShaderPreprocessor {
    default @Nullable ShaderPreprocessor pleasurecraft$getShaderPreprocessor() {
        return null;
    }
}
