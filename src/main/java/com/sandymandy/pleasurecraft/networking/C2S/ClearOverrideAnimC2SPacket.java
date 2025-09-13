package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ClearOverrideAnimC2SPacket(int entityId) implements CustomPayload {
    public static final Id<ClearOverrideAnimC2SPacket> ID =
            new Id<>(Identifier.of(PleasureCraft.MOD_ID, "clear_override_anims"));

    public static final PacketCodec<RegistryByteBuf, ClearOverrideAnimC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, ClearOverrideAnimC2SPacket::entityId,
                    ClearOverrideAnimC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}