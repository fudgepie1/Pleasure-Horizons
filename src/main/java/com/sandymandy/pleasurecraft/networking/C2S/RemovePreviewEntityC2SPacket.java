package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RemovePreviewEntityC2SPacket(int entityId, int previewEntityId) implements CustomPayload {
    public static final Id<RemovePreviewEntityC2SPacket> ID = new Id<>(Identifier.of(PleasureCraft.MOD_ID, "remove_preview_entity"));

    public static final PacketCodec<RegistryByteBuf, RemovePreviewEntityC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, RemovePreviewEntityC2SPacket::entityId,
            PacketCodecs.VAR_INT, RemovePreviewEntityC2SPacket::previewEntityId,
            RemovePreviewEntityC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
