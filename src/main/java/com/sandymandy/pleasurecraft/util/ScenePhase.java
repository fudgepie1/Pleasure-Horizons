package com.sandymandy.pleasurecraft.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public enum ScenePhase {
    NONE,
    LAYING_DOWN,
    BED_IDLE,
    INTRO,
    SLOW,
    FAST,
    CUM;

    /**
     * A PacketCodec for encoding/decoding ScenePhase values over the network.
     * Uses ordinal indexing for efficiency (like vanilla enums).
     */
    public static final PacketCodec<ByteBuf, ScenePhase> CODEC = PacketCodecs.indexed(
            i -> ScenePhase.values()[i],  // Decode: int ordinal -> enum
            ScenePhase::ordinal           // Encode: enum -> int ordinal
    );
}