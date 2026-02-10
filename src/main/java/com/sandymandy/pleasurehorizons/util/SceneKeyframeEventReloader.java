package com.sandymandy.pleasurehorizons.util;
import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import com.sandymandy.pleasurehorizons.util.json.SceneKeyframeEventLoader;

public class SceneKeyframeEventReloader {

    public static void registerReloader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(PleasureHorizons.MOD_ID,"scene_keyframes");
            }

            @Override
            public void reload(net.minecraft.resource.ResourceManager manager) {
                PleasureHorizons.LOGGER.info("[SceneKeyframeEventReloader] Reloading Scene Keyframes...");
                SceneKeyframeEventLoader.loadFromAssets(manager);
            }
        });
    }
}
