package com.sandymandy.pleasurecraft.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec2f;

public interface PleasureCraftPacketCodecs {
    PacketCodec<ByteBuf, Vec2f> VEC2F = new PacketCodec<ByteBuf, Vec2f>() {
        public Vec2f decode(ByteBuf byteBuf) {
            return new Vec2f(byteBuf.readFloat(), byteBuf.readFloat());
        }

        public void encode(ByteBuf byteBuf, Vec2f vec2f) {
            byteBuf.writeFloat(vec2f.x);
            byteBuf.writeFloat(vec2f.y);
        }
    };

}
