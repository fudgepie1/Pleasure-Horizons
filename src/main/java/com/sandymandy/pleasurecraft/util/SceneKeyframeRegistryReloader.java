package com.sandymandy.pleasurecraft.util;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.json.SceneKeyframeLoader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class SceneKeyframeRegistryReloader {

    public static void registerReloader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(PleasureCraft.MOD_ID,"scene_keyframes");
            }

            @Override
            public void reload(net.minecraft.resource.ResourceManager manager) {
                PleasureCraft.LOGGER.info("[SceneKeyframeRegistryReloader] Reloading Scene Keyframes...");
                SceneKeyframeLoader.loadFromAssets(manager);
            }
        });
    }
}
