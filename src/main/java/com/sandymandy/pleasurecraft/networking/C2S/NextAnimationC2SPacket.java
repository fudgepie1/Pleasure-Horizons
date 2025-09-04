package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record NextAnimationC2SPacket(int entityId) implements CustomPayload {
    public static final Id<NextAnimationC2SPacket> ID =
            new Id<>(Identifier.of(PleasureCraft.MOD_ID, "sync_scene_progress"));

    public static final PacketCodec<RegistryByteBuf, NextAnimationC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, NextAnimationC2SPacket::entityId,
                    NextAnimationC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}