package com.sandymandy.pleasurecraft.util;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public record SceneOptions(
        String name,
        List<String> introAnim,
        List<String> slowAnim,
        List<String> fastAnim,
        String cumAnim,
        boolean isBedScene,
        float bedOffset,
        List<String> bedIdle

) {

    public static final SceneOptions EMPTY = new SceneOptions("",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),"",false,0f, new ArrayList<>());

    public static final PacketCodec<RegistryByteBuf, SceneOptions> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SceneOptions::name,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::introAnim,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::slowAnim,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::fastAnim,
            PacketCodecs.STRING, SceneOptions::cumAnim,
            PacketCodecs.BOOLEAN, SceneOptions::isBedScene,
            PacketCodecs.FLOAT, SceneOptions::bedOffset,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::bedIdle,
            SceneOptions::new
    );
}