package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record InInventoryC2SPacket(int entityId, boolean data ) implements CustomPayload {
    public static final Id<InInventoryC2SPacket> ID = new Id<>(Identifier.of(PleasureCraft.MOD_ID, "in_inventory"));

    public static final PacketCodec<RegistryByteBuf, InInventoryC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, InInventoryC2SPacket::entityId,
                    PacketCodecs.BOOLEAN, InInventoryC2SPacket::data,
                    InInventoryC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
