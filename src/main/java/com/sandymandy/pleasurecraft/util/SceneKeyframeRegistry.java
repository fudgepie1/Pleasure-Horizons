package com.sandymandy.pleasurecraft.util;

import net.minecraft.sound.SoundEvent;

import java.util.*;

public class SceneKeyframeRegistry {
    private static final Map<SceneKey, List<SoundEvent>> SOUND_EVENTS = new HashMap<>();
    private static final Map<SceneKey, List<String>> CHAT_MESSAGES = new HashMap<>();

    public static void registerSoundEvents() {
        lucyDoggy();
        strip();
    }

    private static void strip(){
        registerSound("lucy","stripMSG1", PleasureCraftSounds.LUCY_GIGGLE);
        registerMessage("lucy","stripMSG1", "Hihi~");
    }


    private static void lucyDoggy(){
        //Laying on the bed
        registerSound( "lucy","doggyLayOnBedMSG1", PleasureCraftSounds.BEDRUSTLE);
        registerSound( "lucy","doggyLayOnBedMSG2", PleasureCraftSounds.LUCY_LIGHTBREATHING);
        registerMessage( "lucy","doggyLayOnBedMSG2", "what are you waiting for?~");
        registerSound( "lucy","doggyLayOnBedMSG3", PleasureCraftSounds.LUCY_GIGGLE);
        registerMessage( "lucy","doggyLayOnBedMSG3", "this ass ain't gonna fuck itself...");
        registerSound( "lucy","doggyLayOnBedMSG4", PleasureCraftSounds.SLAP);

        //Intro
        registerSound("lucy","doggyIntroMSG1", PleasureCraftSounds.TOUCH);
        registerSound("lucy","doggyIntroMSG2", PleasureCraftSounds.TOUCH);
        registerSound("lucy","doggyIntroMSG3", PleasureCraftSounds.BEDRUSTLE);
        registerSound("lucy","doggyIntroMSG4", PleasureCraftSounds.SMALLINSERTS);
        registerSound("lucy","doggyIntroMSG4", PleasureCraftSounds.LUCY_MMM);
        registerSound("lucy","doggyIntroMSG5", PleasureCraftSounds.POUNDING);
        registerSound("lucy","doggyIntroMSG5", PleasureCraftSounds.LUCY_MOAN);

    }

    public static void registerSound(String girls, String key, SoundEvent event) {
        String[] girlsArray = girls.replaceAll("\\s+", "").split(",");

        for (String girl : girlsArray) {
            SceneKey sceneKey = new SceneKey(girl, key);
            SOUND_EVENTS.computeIfAbsent(sceneKey, k -> new ArrayList<>()).add(event);
        }
    }

    public static List<SoundEvent> getSound(String girl, String key) {
        return SOUND_EVENTS.getOrDefault(new SceneKey(girl, key), Collections.emptyList());
    }

    public static void registerMessage(String girls, String key, String message) {
        String[] girlsArray = girls.replaceAll("\\s+", "").split(",");

        for (String girl : girlsArray) {
            SceneKey sceneKey = new SceneKey(girl, key);
            CHAT_MESSAGES.computeIfAbsent(sceneKey, k -> new ArrayList<>()).add(message);
        }
    }

    public static List<String> getMessage(String girl, String key) {
        return CHAT_MESSAGES.getOrDefault(new SceneKey(girl, key), Collections.emptyList());
    }




    public record SceneKey(String girl, String key) {}}
