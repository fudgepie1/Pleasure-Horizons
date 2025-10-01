package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.hud.SceneProgressOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class PleasureCraftHud {

    public static void register() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerAfter(
                IdentifiedLayer.EXPERIENCE_LEVEL,                           // anchor after XP bar
                Identifier.of(PleasureCraft.MOD_ID, "scene_progress_overlay"),         // unique ID
                (context, tickCounter) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    PlayerEntity localPlayer = client.player;

                    if (localPlayer != null && localPlayer.getVehicle() instanceof SceneEntity scene) {
                        if(scene.getAnimationKeyFrameEvent().equals("sexUIon")) SceneProgressOverlay.setActive(true);
                        SceneProgressOverlay.render(context, scene.getSceneProgress(), scene.getCumThreshold());
                    } else {
                        SceneProgressOverlay.setActive(false);
                    }
                }
        ));
    }
}
