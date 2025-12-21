package com.sandymandy.pleasurecraft.networking.S2C;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record OpenCustomizeScreenS2CPacket(int entityId, int previewEntityId) implements CustomPayload {
    public static final Id<OpenCustomizeScreenS2CPacket> ID = new Id<>(Identifier.of(PleasureCraft.MOD_ID, "customize_screen"));

    public static final PacketCodec<RegistryByteBuf, OpenCustomizeScreenS2CPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, OpenCustomizeScreenS2CPacket::entityId,
                    PacketCodecs.VAR_INT, OpenCustomizeScreenS2CPacket::previewEntityId,
                    OpenCustomizeScreenS2CPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
