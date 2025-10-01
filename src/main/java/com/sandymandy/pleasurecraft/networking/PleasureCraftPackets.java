package com.sandymandy.pleasurecraft.networking;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.hud.SceneProgressOverlay;
import com.sandymandy.pleasurecraft.networking.C2S.*;
import com.sandymandy.pleasurecraft.networking.S2C.ClothingArmorVisibilityS2CPacket;
import com.sandymandy.pleasurecraft.networking.S2C.PlayCumHudAnimationS2CPacket;
import com.sandymandy.pleasurecraft.networking.S2C.SceneOptionsS2CPacket;
import com.sandymandy.pleasurecraft.screen.client.GirlSceneScreen;
import com.sandymandy.pleasurecraft.registries.PleasureCraftSoundEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;

import java.util.Objects;

public class PleasureCraftPackets {

    public static void registerPackets(){
        // --- C2S (client → server) ---
        PayloadTypeRegistry.playC2S().register(InventoryButtonC2SPacket.ID, InventoryButtonC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(BonePosSyncC2SPacket.ID, BonePosSyncC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AnimationSyncC2SPacket.ID, AnimationSyncC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(CumKeybindC2SPacket.ID, CumKeybindC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ThrustKeybindC2SPacket.ID, ThrustKeybindC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(StartSceneC2SPacket.ID, StartSceneC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(InInventoryC2SPacket.ID, InInventoryC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AnimationFinishC2SPacket.ID, AnimationFinishC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ScenePhaseSyncC2SPacket.ID, ScenePhaseSyncC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(StopSceneOnServerC2SPacket.ID, StopSceneOnServerC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SoundEventSyncC2SPacket.ID, SoundEventSyncC2SPacket.CODEC);


        // --- S2C (server → client) ---
        PayloadTypeRegistry.playS2C().register(ClothingArmorVisibilityS2CPacket.ID, ClothingArmorVisibilityS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SceneOptionsS2CPacket.ID, SceneOptionsS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayCumHudAnimationS2CPacket.ID, PlayCumHudAnimationS2CPacket.CODEC);


    }

    public static void registerC2SPackets(){
        // --- C2S (client → server) ---
        ServerPlayNetworking.registerGlobalReceiver(InventoryButtonC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                            var entity = context.player().getWorld().getEntityById(packet.entityId());
                            if (entity instanceof SceneEntity girl) {
                                switch (packet.actionId()) {
                                    case "stripOrDressup" -> girl.requestStrip(true, context.player(), null);
                                    case "breakUp" -> girl.breakUp(context.player());
                                    case "setBase" -> girl.setBasePosHere();
                                    case "talk" -> ServerPlayNetworking.send(context.player(), new SceneOptionsS2CPacket(girl.getId(), girl.getCurrentRelationshipLevel(), girl.getSceneOptions()));
                                    case "goToBase" -> girl.teleportToBase();
                                    case "sit" -> girl.setSitting(!girl.isSitting());
                                    case "follow" -> girl.setFollowing(!girl.isFollowing());
                                    case "testSound" -> {
                                        girl.playSound(PleasureCraftSoundEvents.LUCY_MOAN, 2f, 1f);
                                        girl.playSound(PleasureCraftSoundEvents.TOUCH, 2f, 1f);
                                        girl.playSound(PleasureCraftSoundEvents.CLAP, 2f, 1f);
                                        girl.playSound(PleasureCraftSoundEvents.CUMINFLATION, 2f, 1f);
                                        girl.playSound(PleasureCraftSoundEvents.BELLJINGLE, 2f, 1f);
                                        girl.playSound(PleasureCraftSoundEvents.LUCY_HEAVYBREATHING, 2f, 1f);
                                        girl.playSound(PleasureCraftSoundEvents.LUCY_HUH, 2f, 1f);
                                        girl.playSound(PleasureCraftSoundEvents.LUCY_HMPH, 2f, 1f);
                                    }
                                    default -> PleasureCraft.LOGGER.warn("Unknown Girl interaction: " + packet.actionId());
                                }
                            }
                        }
                ));

        ServerPlayNetworking.registerGlobalReceiver(BonePosSyncC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                            var entity = context.player().getWorld().getEntityById(packet.entityId());
                            if (entity instanceof SceneEntity girl) {
                                girl.setPassengerBonePosition(packet.position());
                            }

                        }
                ));

        ServerPlayNetworking.registerGlobalReceiver(AnimationSyncC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                            var entity = context.player().getWorld().getEntityById(packet.entityId());
                            if (entity instanceof SceneEntity girl) {
                                girl.setOverrideAnim(packet.animationState());
                                girl.setOverrideLoop(packet.loopState());
                                girl.setOverrideHold(packet.holdState());
                            }

                        }
                ));

        ServerPlayNetworking.registerGlobalReceiver(CumKeybindC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getVehicle();
                    if (entity instanceof SceneEntity girl) {
                        if (packet.pressed()) {
                            girl.tryTriggerCum();
                        }
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(ThrustKeybindC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getVehicle();
                    if (entity instanceof SceneEntity girl) {
                        girl.setThrusting(packet.held());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(StartSceneC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof SceneEntity girl) {
                        girl.startScene(packet.sceneOptions());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(InInventoryC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof SceneEntity girl) {
                        girl.setInInventory(packet.data());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(AnimationFinishC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof SceneEntity girl) {
                        girl.animationFinished();
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(ScenePhaseSyncC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof SceneEntity girl) {
                        girl.playPhase(packet.phase());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(StopSceneOnServerC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof SceneEntity girl) {
                        girl.stopScene();
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(SoundEventSyncC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                            var entity = context.player().getWorld().getEntityById(packet.entityId());
                            if (entity instanceof SceneEntity girl) {
                                girl.setAnimationKeyFrameEventState(packet.soundEvent());
                            }
                        }
                ));
    }

    public static void registerS2CPackets(){
        // --- S2C (server → client) ---
        ClientPlayNetworking.registerGlobalReceiver(ClothingArmorVisibilityS2CPacket.ID,
                (packet, context) -> context.client().execute(() -> {
                    var world = context.client().world;
                    if (world == null) return;

                    Entity entity = world.getEntityById(packet.entityId());
                    if (entity instanceof SceneEntity girl) {
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
