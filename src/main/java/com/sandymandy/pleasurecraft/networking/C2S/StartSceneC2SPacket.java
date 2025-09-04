package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.SceneOptions;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StartSceneC2SPacket(int entityId, SceneOptions sceneOptions) implements CustomPayload {

    public static final Id<StartSceneC2SPacket> ID = new Id<>(Identifier.of(PleasureCraft.MOD_ID, "start_scene_from_client"));

    public static final PacketCodec<RegistryByteBuf, StartSceneC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, StartSceneC2SPacket::entityId,
                    SceneOptions.CODEC, StartSceneC2SPacket::sceneOptions,
                    StartSceneC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
