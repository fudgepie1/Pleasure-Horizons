package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.client.gui.screen.hud.SceneProgressOverlay;
import com.sandymandy.pleasurecraft.entity.base.GirlSceneEntity;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class PleasureCraftHudRegistry {

    public static void register() {
        HudElementRegistry.addFirst(Identifier.of(PleasureCraft.MOD_ID, "scene_progress_overlay"),         // unique ID
                (context, tickCounter) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    PlayerEntity localPlayer = client.player;

                    if (localPlayer != null && localPlayer.getVehicle() instanceof GirlSceneEntity scene) {
                        if(scene.getAnimationKeyFrameEvent().contains("sexui")) SceneProgressOverlay.setActive(true);
                        SceneProgressOverlay.render(context, scene.getSceneProgress(), scene.getCumThreshold());
                    } else {
                        SceneProgressOverlay.setActive(false);
                    }
                });
    }
}
