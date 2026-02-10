package com.sandymandy.pleasurehorizons.networking.C2S;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record RegisterCustomGirlRandomSoundC2SPacket(String girlID, String key, List<SoundEvent> sounds) implements CustomPayload {
    public static final CustomPayload.Id<RegisterCustomGirlRandomSoundC2SPacket> ID = new Id<>(Identifier.of(PleasureHorizons.MOD_ID, "register_custom_girl_random_sounds"));

    public static final PacketCodec<RegistryByteBuf, RegisterCustomGirlRandomSoundC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, RegisterCustomGirlRandomSoundC2SPacket::girlID,
                    PacketCodecs.STRING, RegisterCustomGirlRandomSoundC2SPacket::key,
                    PacketCodecs.collection(ArrayList::new, SoundEvent.PACKET_CODEC), RegisterCustomGirlRandomSoundC2SPacket::sounds,
                    RegisterCustomGirlRandomSoundC2SPacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}