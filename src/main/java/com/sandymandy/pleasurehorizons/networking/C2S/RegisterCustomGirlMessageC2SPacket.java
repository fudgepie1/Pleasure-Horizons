package com.sandymandy.pleasurehorizons.networking.C2S;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RegisterCustomGirlMessageC2SPacket(String girlID, String key, String message) implements CustomPayload {
    public static final CustomPayload.Id<RegisterCustomGirlMessageC2SPacket> ID = new Id<>(Identifier.of(PleasureHorizons.MOD_ID, "register_custom_girl_messages"));

    public static final PacketCodec<RegistryByteBuf, RegisterCustomGirlMessageC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, RegisterCustomGirlMessageC2SPacket::girlID,
                    PacketCodecs.STRING, RegisterCustomGirlMessageC2SPacket::key,
                    PacketCodecs.STRING, RegisterCustomGirlMessageC2SPacket::message,
                    RegisterCustomGirlMessageC2SPacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
