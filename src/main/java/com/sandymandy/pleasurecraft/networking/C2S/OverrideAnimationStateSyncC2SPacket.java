package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OverrideAnimationStateSyncC2SPacket(int entityId, boolean state) implements CustomPayload {
    public static final Id<OverrideAnimationStateSyncC2SPacket> ID =
            new Id<>(Identifier.of(PleasureCraft.MOD_ID, "sync_override_anim_state"));

    public static final PacketCodec<RegistryByteBuf, OverrideAnimationStateSyncC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, OverrideAnimationStateSyncC2SPacket::entityId,
                    PacketCodecs.BOOLEAN, OverrideAnimationStateSyncC2SPacket::state,
                    OverrideAnimationStateSyncC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}