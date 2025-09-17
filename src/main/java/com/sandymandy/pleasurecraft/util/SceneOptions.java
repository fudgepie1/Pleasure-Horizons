package com.sandymandy.pleasurecraft.util;

import com.sandymandy.pleasurecraft.networking.codec.PacketCodecExtra;
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
        List<String> bedIdle,
        boolean needsToStrip,
        int requiredRelationshipLevel

) {

    public static final SceneOptions EMPTY = new SceneOptions("",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),"",false,0f, new ArrayList<>(), false, 0);

    public static final PacketCodec<RegistryByteBuf, SceneOptions> CODEC = PacketCodecExtra.tuple(
            PacketCodecs.STRING, SceneOptions::name,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::introAnim,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::slowAnim,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::fastAnim,
            PacketCodecs.STRING, SceneOptions::cumAnim,
            PacketCodecs.BOOLEAN, SceneOptions::isBedScene,
            PacketCodecs.FLOAT, SceneOptions::bedOffset,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::bedIdle,
            PacketCodecs.BOOLEAN, SceneOptions::needsToStrip,
            PacketCodecs.VAR_INT, SceneOptions::requiredRelationshipLevel,
            SceneOptions::new
    );
}