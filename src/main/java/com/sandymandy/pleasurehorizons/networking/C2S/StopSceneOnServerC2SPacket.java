package com.sandymandy.pleasurehorizons.networking.C2S;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StopSceneOnServerC2SPacket(int entityId) implements CustomPayload {
    public static final Id<StopSceneOnServerC2SPacket> ID =
            new Id<>(Identifier.of(PleasureHorizons.MOD_ID, "stop_scene"));

    public static final PacketCodec<RegistryByteBuf, StopSceneOnServerC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, StopSceneOnServerC2SPacket::entityId,
                    StopSceneOnServerC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
