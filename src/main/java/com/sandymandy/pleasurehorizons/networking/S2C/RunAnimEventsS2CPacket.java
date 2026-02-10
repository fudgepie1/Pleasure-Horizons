package com.sandymandy.pleasurehorizons.networking.S2C;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RunAnimEventsS2CPacket(int entityId, String event) implements CustomPayload {

    public static final Id<RunAnimEventsS2CPacket> ID =
            new Id<>(Identifier.of(PleasureHorizons.MOD_ID, "run_anim_events"));

    public static final PacketCodec<RegistryByteBuf, RunAnimEventsS2CPacket> CODEC =
            PacketCodec.tuple(
                PacketCodecs.VAR_INT, RunAnimEventsS2CPacket::entityId,
                PacketCodecs.STRING, RunAnimEventsS2CPacket::event,
                RunAnimEventsS2CPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
