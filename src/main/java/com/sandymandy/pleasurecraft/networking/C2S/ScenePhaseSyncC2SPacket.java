package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.ScenePhase;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ScenePhaseSyncC2SPacket(int entityId, ScenePhase phase) implements CustomPayload {
    public static final Id<ScenePhaseSyncC2SPacket> ID =
            new Id<>(Identifier.of(PleasureCraft.MOD_ID, "sync_scene_phase"));

    public static final PacketCodec<RegistryByteBuf, ScenePhaseSyncC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, ScenePhaseSyncC2SPacket::entityId,
                    ScenePhase.PACKET_CODEC, ScenePhaseSyncC2SPacket::phase,
                    ScenePhaseSyncC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}