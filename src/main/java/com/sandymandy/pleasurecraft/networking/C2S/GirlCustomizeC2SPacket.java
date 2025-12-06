package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record GirlCustomizeC2SPacket(int entityId, int breastSize, Vec3d breastOffset, boolean canGetImpregnated) implements CustomPayload {
    public static final Id<GirlCustomizeC2SPacket> ID = new Id<>(Identifier.of(PleasureCraft.MOD_ID, "customize"));

    public static final PacketCodec<RegistryByteBuf, GirlCustomizeC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, GirlCustomizeC2SPacket::entityId,
                    PacketCodecs.VAR_INT, GirlCustomizeC2SPacket::breastSize,
                    Vec3d.PACKET_CODEC, GirlCustomizeC2SPacket::breastOffset,
                    PacketCodecs.BOOLEAN, GirlCustomizeC2SPacket::canGetImpregnated,
                    GirlCustomizeC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
