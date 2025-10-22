package com.sandymandy.pleasurecraft.entity.ai.brain;

import com.mojang.serialization.Codec;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import com.sandymandy.pleasurecraft.PleasureCraft;

import java.util.Optional;

public class GirlMemoryTypes {
    public static final MemoryModuleType<Boolean> STRIP_REQUEST =
            register("strip_request"); // Simple trigger flag

    public static final MemoryModuleType<SceneOptions> SCENE_OPTIONS =
            register("scene_options"); // Optional data for the next scene


    private static <T> MemoryModuleType<T> register(String id) {
        return Registry.register(
                Registries.MEMORY_MODULE_TYPE,
                Identifier.of(PleasureCraft.MOD_ID, id),
                new MemoryModuleType<>(Optional.empty())
        );
    }

    private static <T> MemoryModuleType<T> register(String id, Codec<T> codec) {
        return Registry.register(
                Registries.MEMORY_MODULE_TYPE,
                Identifier.of(PleasureCraft.MOD_ID, id),
                new MemoryModuleType<>(Optional.of(codec))
        );
    }

    public static void registerMemoryTypes() {
        PleasureCraft.LOGGER.info("Registering MemoryTypes of PleasureCraft");
    }
}
