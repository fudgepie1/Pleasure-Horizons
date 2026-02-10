package com.sandymandy.pleasurehorizons.networking.C2S;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.util.variables.Scene;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StartSceneC2SPacket(int entityId, Scene scene) implements CustomPayload {

    public static final Id<StartSceneC2SPacket> ID = new Id<>(Identifier.of(PleasureHorizons.MOD_ID, "start_scene_from_client"));

    public static final PacketCodec<RegistryByteBuf, StartSceneC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, StartSceneC2SPacket::entityId,
                    Scene.PACKET_CODEC, StartSceneC2SPacket::scene,
                    StartSceneC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
