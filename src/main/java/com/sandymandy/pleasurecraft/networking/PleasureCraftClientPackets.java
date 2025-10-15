package com.sandymandy.pleasurecraft.networking;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.GirlEntityVisuals;
import com.sandymandy.pleasurecraft.hud.SceneProgressOverlay;
import com.sandymandy.pleasurecraft.networking.S2C.ClothingArmorVisibilityS2CPacket;
import com.sandymandy.pleasurecraft.networking.S2C.PlayCumHudAnimationS2CPacket;
import com.sandymandy.pleasurecraft.networking.S2C.SceneOptionsS2CPacket;
import com.sandymandy.pleasurecraft.client.gui.screen.GirlSceneScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;

@Environment(EnvType.CLIENT)
public class PleasureCraftClientPackets {
    public static void registerS2CPackets(){
        PleasureCraft.LOGGER.info("Registering S2C Packets for PleasureCraft");

        // --- S2C (server → client) ---
        ClientPlayNetworking.registerGlobalReceiver(ClothingArmorVisibilityS2CPacket.ID,
                (packet, context) -> context.client().execute(() -> {
                    var world = context.client().world;
                    if (world == null) return;

                    Entity entity = world.getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityVisuals girl) {
                        int i = 0;
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            girl.armorVisibility.put(slot, packet.armor().get(i));
                            i++;
                        }
                        girl.applyClothingAndArmor();
                    }
                }));

        ClientPlayNetworking.registerGlobalReceiver(SceneOptionsS2CPacket.ID, (packet, context) -> {
            context.client().execute(() -> {
                MinecraftClient.getInstance().setScreen(new GirlSceneScreen(packet.entityId(), packet.currentRelationshipLevel(),packet.options()));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(
                PlayCumHudAnimationS2CPacket.ID,
                (packet, context) -> {
                    // trigger the HUD animation locally
                    context.client().execute(SceneProgressOverlay::triggerCumAnimation);
                }
        );

    }
}