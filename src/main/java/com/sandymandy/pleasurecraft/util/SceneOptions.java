package com.sandymandy.pleasurecraft.util;

import com.sandymandy.pleasurecraft.networking.codec.PacketCodecExtra;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public class SceneOptions{

    public final String displayName;
    public final int requiredRelationshipLevel;
    public final List<String> introAnim;
    public final List<String> slowAnim;
    public final List<String> fastAnim;
    public final String cumAnim;
    public final boolean needsToStrip;
    public final boolean isBedScene;
    public final float bedAlignmentOffset;
    public final List<String> bedIdle;

    public static final SceneOptions EMPTY = new SceneOptions("", 0,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),"",false, false, 0f, new ArrayList<>());

    public static final PacketCodec<RegistryByteBuf, SceneOptions> PACKET_CODEC = PacketCodecExtra.tuple(
            PacketCodecs.STRING, SceneOptions::displayName,
            PacketCodecs.VAR_INT, SceneOptions::requiredRelationshipLevel,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::introAnim,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::slowAnim,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::fastAnim,
            PacketCodecs.STRING, SceneOptions::cumAnim,
            PacketCodecs.BOOLEAN, SceneOptions::needsToStrip,
            PacketCodecs.BOOLEAN, SceneOptions::isBedScene,
            PacketCodecs.FLOAT, SceneOptions::bedAlignmentOffset,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::bedIdle,
            SceneOptions::new
    );

    private SceneOptions(String displayName, int requiredRelationshipLevel, List<String> introAnim, List<String> slowAnim, List<String> fastAnim, String cumAnim, boolean needsToStrip, boolean isBedScene, float bedAlignmentOffset, List<String> bedIdle){
        this.displayName = displayName;
        this.requiredRelationshipLevel = requiredRelationshipLevel;
        this.introAnim = introAnim;
        this.slowAnim = slowAnim;
        this.fastAnim = fastAnim;
        this.cumAnim = cumAnim;
        this.needsToStrip = needsToStrip;
        this.isBedScene = isBedScene;
        this.bedAlignmentOffset = bedAlignmentOffset;
        this.bedIdle = bedIdle;
    }

    public final String displayName() {return this.displayName;}
    public final int requiredRelationshipLevel() {return this.requiredRelationshipLevel;}
    public final List<String> introAnim() {return this.introAnim;}
    public final List<String> slowAnim() {return this.slowAnim;}
    public final List<String> fastAnim() {return this.fastAnim;}
    public final String cumAnim() {return this.cumAnim;}
    public final boolean needsToStrip() {return this.needsToStrip;}
    public final boolean isBedScene() {return this.isBedScene;}
    public final float bedAlignmentOffset() {return this.bedAlignmentOffset;}
    public final List<String> bedIdle() {return this.bedIdle;}

    public static SceneOptions of(
            String name,
            int requiredRelationshipLevel,
            List<String> introAnim,
            List<String> slowAnim,
            List<String> fastAnim,
            String cumAnim,
            boolean needsToStrip,
            float bedOffset,
            List<String> bedIdle
    ){
        return new SceneOptions(name, requiredRelationshipLevel, introAnim, slowAnim, fastAnim, cumAnim, needsToStrip, true, bedOffset, bedIdle);
    }

    public static SceneOptions of(
            String name,
            int requiredRelationshipLevel,
            List<String> introAnim,
            List<String> slowAnim,
            List<String> fastAnim,
            String cumAnim,
            boolean needsToStrip
    ){
        return new SceneOptions(name, requiredRelationshipLevel, introAnim, slowAnim, fastAnim, cumAnim, needsToStrip, false, 0f, new ArrayList<>());
    }


}