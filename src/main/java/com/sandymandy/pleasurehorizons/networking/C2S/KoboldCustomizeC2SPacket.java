package com.sandymandy.pleasurehorizons.networking.C2S;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.sandymandy.pleasurehorizons.PleasureHorizons.MOD_ID;

public record KoboldCustomizeC2SPacket(
        int entityId,
        int bodySize,
        int breastSize,
        int primaryColor,
        int secondaryColor,
        int irisColor,
        int topHornType,
        int bottomHornType
) implements CustomPayload {

    public static final Id<KoboldCustomizeC2SPacket> ID = new Id<>(Identifier.of(MOD_ID, "kobold_customize"));

    public static final PacketCodec<RegistryByteBuf, KoboldCustomizeC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::entityId,
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::bodySize,
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::breastSize,
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::primaryColor,
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::secondaryColor,
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::irisColor,
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::topHornType,
            PacketCodecs.VAR_INT, KoboldCustomizeC2SPacket::bottomHornType,
            KoboldCustomizeC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}