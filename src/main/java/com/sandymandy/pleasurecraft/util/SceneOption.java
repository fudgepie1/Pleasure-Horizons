package com.sandymandy.pleasurecraft.util;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

public record SceneOption(
        String name,
        List<String> introAnim,
        List<String> slowAnim,
        List<String> fastAnim,
        String cumAnim,
        boolean isBedScene,
        float bedOffset,
        List<String> bedIdle

) {
    public static final PacketCodec<RegistryByteBuf, SceneOption> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SceneOption::name,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOption::introAnim,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOption::slowAnim,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOption::fastAnim,
            PacketCodecs.STRING, SceneOption::cumAnim,
            PacketCodecs.BOOLEAN, SceneOption::isBedScene,
            PacketCodecs.FLOAT, SceneOption::bedOffset,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOption::bedIdle,
            SceneOption::new
    );
}