package com.sandymandy.pleasurehorizons.networking.C2S;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ThrustKeybindC2SPacket(Boolean held) implements CustomPayload {

    public static final Id<ThrustKeybindC2SPacket> ID = new Id<>(Identifier.of(PleasureHorizons.MOD_ID, "thrust_keybind_held"));

    public static final PacketCodec<RegistryByteBuf, ThrustKeybindC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOLEAN, ThrustKeybindC2SPacket::held,
                    ThrustKeybindC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}