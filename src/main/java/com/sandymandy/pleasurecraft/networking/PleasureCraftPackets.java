package com.sandymandy.pleasurecraft.networking;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.GirlEntityScene;
import com.sandymandy.pleasurecraft.networking.C2S.*;
import com.sandymandy.pleasurecraft.networking.S2C.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.Objects;

public class PleasureCraftPackets {

    public static void registerPackets(){
        PleasureCraft.LOGGER.info("Registering Packet Codecs for PleasureCraft");
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
        PayloadTypeRegistry.playC2S().register(RegisterCustomGirlMessageC2SPacket.ID, RegisterCustomGirlMessageC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(RegisterCustomGirlSoundC2SPacket.ID, RegisterCustomGirlSoundC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(RegisterCustomGirlRandomSoundC2SPacket.ID, RegisterCustomGirlRandomSoundC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(GirlCustomizeC2SPacket.ID, GirlCustomizeC2SPacket.CODEC);



        // --- S2C (server → client) ---
        PayloadTypeRegistry.playS2C().register(ClothingArmorVisibilityS2CPacket.ID, ClothingArmorVisibilityS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SceneOptionsS2CPacket.ID, SceneOptionsS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayCumHudAnimationS2CPacket.ID, PlayCumHudAnimationS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenCustomizeScreenS2CPacket.ID, OpenCustomizeScreenS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(RefreshModelsS2CPacket.ID, RefreshModelsS2CPacket.CODEC);

    }

    public static void registerC2SPackets(){
        PleasureCraft.LOGGER.info("Registering C2S Packets for PleasureCraft");
        // --- C2S (client → server) ---
        ServerPlayNetworking.registerGlobalReceiver(InventoryButtonC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                            var entity = context.player().getWorld().getEntityById(packet.entityId());
                            if (entity instanceof GirlEntityScene girl) {
                                switch (packet.actionId()) {
                                    case "stripOrDressup" -> girl.requestStrip();
                                    case "breakUp" -> girl.breakUp(context.player());
                                    case "setBase" -> girl.setBasePosHere();
                                    case "talk" -> ServerPlayNetworking.send(context.player(), new SceneOptionsS2CPacket(girl.getId(), girl.getCurrentRelationshipLevel(), girl.getSceneOptions()));
                                    case "goToBase" -> girl.teleportToBase();
                                    case "sit" -> girl.setSitting(!girl.isSitting());
                                    case "follow" -> girl.setFollowing(!girl.isFollowing());
                                    case "customize" -> ServerPlayNetworking.send(context.player(), new OpenCustomizeScreenS2CPacket(girl.getId(), girl.getBreastSize(), girl.getBreastOffset()));
                                    default -> PleasureCraft.LOGGER.warn("Unknown Girl interaction: " + packet.actionId());
                                }
                            }
                        }
                ));

        ServerPlayNetworking.registerGlobalReceiver(BonePosSyncC2SPacket.ID,
                (packet, context) -> {
                    var server = context.player().getServer();
                    if (server == null) return;

                    server.execute(() -> {
                        var world = context.player().getWorld();
                        var entity = world.getEntityById(packet.entityId());

                        if (!(entity instanceof GirlEntityScene girl))
                            return;

                        if (!girl.isOwner(context.player())) {
                            // Not the owner — ignore
                            return;
                        }

                        // 3. Safe → update the bone pos
                        girl.setPassengerBonePosition(packet.position());
                    });
                }
        );


        ServerPlayNetworking.registerGlobalReceiver(AnimationSyncC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                            var entity = context.player().getWorld().getEntityById(packet.entityId());
                            if (entity instanceof GirlEntityScene girl) {
                                girl.setOverrideAnim(packet.animationState());
                                girl.setOverrideLoop(packet.loopState());
                                girl.setOverrideHold(packet.holdState());
                            }

                        }
                ));

        ServerPlayNetworking.registerGlobalReceiver(CumKeybindC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getVehicle();
                    if (entity instanceof GirlEntityScene girl) {
                        if (packet.pressed()) {
                            girl.tryTriggerCum();
                        }
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(ThrustKeybindC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getVehicle();
                    if (entity instanceof GirlEntityScene girl) {
                        girl.setThrusting(packet.held());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(StartSceneC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityScene girl) {
                        girl.startScene(packet.sceneOptions());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(InInventoryC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityScene girl) {
                        girl.setInInventory(packet.data());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(AnimationFinishC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityScene girl) {
                        girl.animationFinished();
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(ScenePhaseSyncC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityScene girl) {
                        girl.playPhase(packet.phase());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(StopSceneOnServerC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityScene girl) {
                        girl.stopScene();
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(SoundEventSyncC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityScene girl) {
                        girl.setAnimationKeyFrameEventState(packet.soundEvent());
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(GirlCustomizeC2SPacket.ID,
                (packet, context) -> Objects.requireNonNull(context.player().getServer()).execute(() -> {
                    var entity = context.player().getWorld().getEntityById(packet.entityId());
                    if (entity instanceof GirlEntityScene girl) {

                        girl.setBreastSize(packet.breastSize());
                        girl.setBreastOffset(packet.breastOffset());
                    }
                }));


    }



}
