package com.sandymandy.pleasurehorizons.entity.ai.brain;

import com.mojang.serialization.Codec;
import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.util.variables.Scene;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class GirlMemoryTypes {
    public static final MemoryModuleType<Boolean> STRIP_REQUEST =
            register("strip_request"); // Simple trigger flag

    public static final MemoryModuleType<Scene> SCENE_OPTIONS =
            register("scene_options"); // Optional data for the next scene


    private static <T> MemoryModuleType<T> register(String id) {
        return Registry.register(
                Registries.MEMORY_MODULE_TYPE,
                Identifier.of(PleasureHorizons.MOD_ID, id),
                new MemoryModuleType<>(Optional.empty())
        );
    }

    private static <T> MemoryModuleType<T> register(String id, Codec<T> codec) {
        return Registry.register(
                Registries.MEMORY_MODULE_TYPE,
                Identifier.of(PleasureHorizons.MOD_ID, id),
                new MemoryModuleType<>(Optional.of(codec))
        );
    }

    public static void registerMemoryTypes() {
        PleasureHorizons.LOGGER.info("Registering MemoryTypes of " + PleasureHorizons.MOD_NAME);
    }
}
