package com.sandymandy.pleasurecraft.util.variables;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sandymandy.pleasurecraft.networking.codec.PacketCodecExtra;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public class SceneOptions{

    private final String displayName;
    private final int requiredRelationshipLevel;
    private final List<String> introAnim;
    private final List<String> slowAnim;
    private final List<String> fastAnim;
    private final String cumAnim;
    private final float cumThreshold;
    private final boolean needsToStrip;
    private final SceneType sceneType;
    private final boolean useKeyFrameEvents;
    private final float bedAlignmentOffset;
    private final String layOnBed;
    private final String bedIdle;

    private final List<String> stationaryIntroAnim;   // for STATIONARY
    private final String stationaryLoopAnim;         // for STATIONARY_LOOP
    private final int amountOfLoops;      // for STATIONARY_LOOP

    public static final SceneOptions EMPTY = new SceneOptions("", 0,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),"", 0f,false, SceneType.ON_PLAYER, false, 0f, "", "", new ArrayList<>(), "", 0);

    public static final PacketCodec<RegistryByteBuf, SceneOptions> PACKET_CODEC = PacketCodecExtra.tuple(
            PacketCodecs.STRING, SceneOptions::displayName,
            PacketCodecs.VAR_INT, SceneOptions::requiredRelationshipLevel,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::introAnim,
            PacketCodecs.collection(ArrayList::new , PacketCodecs.STRING), SceneOptions::slowAnim,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::fastAnim,
            PacketCodecs.STRING, SceneOptions::cumAnim,
            PacketCodecs.FLOAT, SceneOptions::cumThreshold,
            PacketCodecs.BOOLEAN, SceneOptions::needsToStrip,
            SceneType.PACKET_CODEC, SceneOptions::sceneType,
            PacketCodecs.BOOLEAN, SceneOptions::useKeyFrameEvents,
            PacketCodecs.FLOAT, SceneOptions::bedAlignmentOffset,
            PacketCodecs.STRING, SceneOptions::layOnBed,
            PacketCodecs.STRING, SceneOptions::bedIdle,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), SceneOptions::stationaryIntroAnim,
            PacketCodecs.STRING, SceneOptions::stationaryLoopAnim,
            PacketCodecs.INTEGER, SceneOptions::amountOfLoops,
            SceneOptions::new
    );

    public static final Codec<SceneOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("displayName").forGetter(SceneOptions::displayName),
            Codec.INT.fieldOf("requiredRelationshipLevel").forGetter(SceneOptions::requiredRelationshipLevel),
            Codec.STRING.listOf().fieldOf("introAnim").forGetter(SceneOptions::introAnim),
            Codec.STRING.listOf().fieldOf("slowAnim").forGetter(SceneOptions::slowAnim),
            Codec.STRING.listOf().fieldOf("fastAnim").forGetter(SceneOptions::fastAnim),
            Codec.STRING.fieldOf("cumAnim").forGetter(SceneOptions::cumAnim),
            Codec.FLOAT.fieldOf("cumThreshold").forGetter(SceneOptions::cumThreshold),
            Codec.BOOL.fieldOf("needsToStrip").forGetter(SceneOptions::needsToStrip),
            SceneType.CODEC.fieldOf("sceneType").forGetter(SceneOptions::sceneType),
            Codec.BOOL.fieldOf("useKeyFrameEvents").forGetter(SceneOptions::useKeyFrameEvents),
            Codec.FLOAT.fieldOf("bedAlignmentOffset").forGetter(SceneOptions::bedAlignmentOffset),
            Codec.STRING.fieldOf("layOnBed").forGetter(SceneOptions::layOnBed),
            Codec.STRING.fieldOf("bedIdle").forGetter(SceneOptions::bedIdle),
            Codec.STRING.listOf().fieldOf("stationaryIntroAnim").forGetter(SceneOptions::stationaryIntroAnim),
            Codec.STRING.fieldOf("stationaryLoopAnim").forGetter(SceneOptions::stationaryLoopAnim),
            Codec.INT.fieldOf("amountOfLoops").forGetter(SceneOptions::amountOfLoops)
    ).apply(instance, SceneOptions::new));

    private SceneOptions(
            String displayName,
            int requiredRelationshipLevel,
            List<String> introAnim,
            List<String> slowAnim,
            List<String> fastAnim,
            String cumAnim,
            float cumThreshold,
            boolean needsToStrip,
            SceneType sceneType,
            boolean useKeyFrameEvents,
            float bedAlignmentOffset,
            String layOnBed,
            String bedIdle,

            // NEW FIELDS:
            List<String> stationaryIntroAnim,
            String stationaryLoopAnim,
            int amountOfLoops
    ) {
        this.displayName = displayName;
        this.requiredRelationshipLevel = requiredRelationshipLevel;
        this.introAnim = introAnim;
        this.slowAnim = slowAnim;
        this.fastAnim = fastAnim;
        this.cumAnim = cumAnim;
        this.cumThreshold = cumThreshold;
        this.needsToStrip = needsToStrip;
        this.sceneType = sceneType;
        this.useKeyFrameEvents = useKeyFrameEvents;
        this.bedAlignmentOffset = bedAlignmentOffset;
        this.layOnBed = layOnBed;
        this.bedIdle = bedIdle;

        this.stationaryIntroAnim = stationaryIntroAnim;
        this.stationaryLoopAnim = stationaryLoopAnim;
        this.amountOfLoops = amountOfLoops;
    }


    public final String displayName() {return this.displayName;}
    public final int requiredRelationshipLevel() {return this.requiredRelationshipLevel;}
    public final List<String> introAnim() {return this.introAnim;}
    public final List<String> slowAnim() {return this.slowAnim;}
    public final List<String> fastAnim() {return this.fastAnim;}
    public final String cumAnim() {return this.cumAnim;}
    public final float cumThreshold() {return this.cumThreshold;}
    public final boolean needsToStrip() {return this.needsToStrip;}
    public final SceneType sceneType() {return this.sceneType;}
    public final boolean useKeyFrameEvents() {return this.useKeyFrameEvents;}
    public final float bedAlignmentOffset() {return this.bedAlignmentOffset;}
    public final String layOnBed() {return this.layOnBed;}
    public final String bedIdle() {return this.bedIdle;}

    public List<String> stationaryIntroAnim() { return stationaryIntroAnim; }
    public String stationaryLoopAnim() { return stationaryLoopAnim; }
    public int amountOfLoops() { return amountOfLoops; }


    public static SceneOptions onBed(
            String name,
            int requiredRelationshipLevel,
            List<String> introAnim,
            List<String> slowAnim,
            List<String> fastAnim,
            String cumAnim,
            float cumThreshold,
            boolean needsToStrip,
            boolean useKeyFrameEvents,
            float bedOffset,
            String layOnBed,
            String bedIdle
    ) {
        return new SceneOptions(
                name, requiredRelationshipLevel, introAnim, slowAnim, fastAnim,
                cumAnim, cumThreshold, needsToStrip,
                SceneType.ON_BED,
                useKeyFrameEvents,
                bedOffset, layOnBed, bedIdle,
                new ArrayList<>(), "", 0
        );
    }


    public static SceneOptions onPlayer(
            String name,
            int requiredRelationshipLevel,
            List<String> introAnim,
            List<String> slowAnim,
            List<String> fastAnim,
            String cumAnim,
            float cumThreshold,
            boolean needsToStrip,
            boolean useKeyFrameEvents
    ) {
        return new SceneOptions(
                name, requiredRelationshipLevel, introAnim, slowAnim, fastAnim,
                cumAnim, cumThreshold, needsToStrip,
                SceneType.ON_PLAYER,
                useKeyFrameEvents,
                0f, "", "",
                new ArrayList<>(), "", 0
        );
    }

    public static SceneOptions stationaryContact(
            String name,
            int requiredRelationshipLevel,
            List<String> introAnim,
            List<String> slowAnim,
            List<String> fastAnim,
            String cumAnim,
            float cumThreshold,
            boolean needsToStrip,
            boolean useKeyFrameEvents,
            String layDown,
            String idle
    ) {
        return new SceneOptions(
                name, requiredRelationshipLevel, introAnim, slowAnim, fastAnim,
                cumAnim, cumThreshold, needsToStrip,
                SceneType.STATIONARY_CONTACT,
                useKeyFrameEvents,
                0f, layDown, idle,
                new ArrayList<>(), "", 0
        );
    }

    public static SceneOptions stationaryIntro(
            String name,
            int requiredRelationshipLevel,
            List<String> stationaryIntroAnim,
            String anim,
            int amountOfLoops,
            boolean needsToStrip
            ) {
        return new SceneOptions(
                name, requiredRelationshipLevel,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                "", 0f, needsToStrip,
                SceneType.STATIONARY_INTRO,
                false, 0f, "", "",
                stationaryIntroAnim,
                anim,
                amountOfLoops
        );
    }

    public static SceneOptions stationary(
            String name,
            int requiredRelationshipLevel,
            String anim,
            int amountOfLoops,
            boolean needsToStrip
            ) {
        return new SceneOptions(
                name, requiredRelationshipLevel,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                "", 0f, needsToStrip,
                SceneType.STATIONARY,

                false, 0f, "", "",

                new ArrayList<>(),
                anim,
                amountOfLoops
        );
    }


}