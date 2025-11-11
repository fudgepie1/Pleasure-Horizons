package com.sandymandy.pleasurecraft.util;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.json.SceneKeyframeEventLoader;

public class SceneKeyframeEventReloader {

    public static void registerReloader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(PleasureCraft.MOD_ID,"scene_keyframes");
            }

            @Override
            public void reload(net.minecraft.resource.ResourceManager manager) {
                PleasureCraft.LOGGER.info("[SceneKeyframeEventReloader] Reloading Scene Keyframes...");
                SceneKeyframeEventLoader.loadFromAssets(manager);
            }
        });
    }
}
