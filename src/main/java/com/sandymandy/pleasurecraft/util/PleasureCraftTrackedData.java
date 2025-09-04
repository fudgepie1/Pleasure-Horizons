package com.sandymandy.pleasurecraft.util;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public class PleasureCraftTrackedData {

    public static final TrackedDataHandler<SceneOptions> SCENE_OPTION =
            TrackedDataHandler.create(SceneOptions.CODEC);

    public static final TrackedDataHandler<ScenePhase> SCENE_PHASE =
            TrackedDataHandler.create(ScenePhase.CODEC);

    public static void registerTrackedData(){
        PleasureCraft.LOGGER.info("Registering custom TrackedDataHandlers");
        TrackedDataHandlerRegistry.register(PleasureCraftTrackedData.SCENE_OPTION);
        TrackedDataHandlerRegistry.register(PleasureCraftTrackedData.SCENE_PHASE);
    }
}
