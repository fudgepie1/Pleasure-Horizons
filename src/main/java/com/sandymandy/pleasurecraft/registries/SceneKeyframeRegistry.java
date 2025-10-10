package com.sandymandy.pleasurecraft.registries;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundEvent;

import java.util.*;

public class SceneKeyframeRegistry {
    private static final Map<SceneKey, List<SoundEvent>> SOUND_EVENTS = new HashMap<>();
    private static final Map<SceneKey, List<SoundEvent>> RANDOM_SOUNDS = new HashMap<>();
    private static final Map<SceneKey, List<String>> CHAT_MESSAGES = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void registerSoundEvents() {
        strip();
        lucyDoggy();
        lucyPaizuri();
        lucyShared();
        lucyBlowJob();
    }

    private static void strip(){
        registerSound("lucy","stripMSG1", PleasureCraftSoundEvents.LUCY_GIGGLE);
        registerMessage("lucy","stripMSG1", I18n.translate("chat.lucy.strip.MSG1"));
    }

    private static void lucyPaizuri(){
        //Intro
        registerSound( "lucy","paizuriStartMSG1", PleasureCraftSoundEvents.POUNDING);

        //Slow
        registerSound( "lucy","paizuriSlowMSG1", PleasureCraftSoundEvents.POUNDING);

        //Fast
        registerSound( "lucy","paizuriFastMSG1", PleasureCraftSoundEvents.POUNDING);
        registerSound( "lucy","paizuriFastMSG1", List.of(PleasureCraftSoundEvents.LUCY_AHH, PleasureCraftSoundEvents.LUCY_MMM));
    }

    private static void lucyBlowJob(){
        //Intro
        registerSound( "lucy","bjiMSG1", PleasureCraftSoundEvents.LUCY_MMM);
        registerMessage("lucy","bjiMSG1", "What are you...");
        registerSound( "lucy","bjiMSG2", PleasureCraftSoundEvents.LUCY_LIGHTBREATHING);
        registerMessage("lucy","bjiMSG2", "eh... boys...");
        registerSound( "lucy","bjiMSG3", PleasureCraftSoundEvents.LUCY_AFTERSSESSIONMOAN);
        registerMessage("lucy","bjiMSG3", "OHOhh...!");
        registerSound( "lucy","bjiMSG4", PleasureCraftSoundEvents.BELLJINGLE);
        registerSound( "lucy","bjiMSG5", PleasureCraftSoundEvents.LUCY_HMPH);
        registerMessage("lucy","bjiMSG5", "Was this really necessary?!");
        registerSound( "lucy","bjiMSG6", PleasureCraftSoundEvents.LUCY_LIGHTBREATHING);
        registerMessage("lucy","bjiMSG6", "Oh~");
        registerSound( "lucy","bjiMSG7", PleasureCraftSoundEvents.LUCY_GIGGLE);
        registerMessage("lucy","bjiMSG7", "You like it?~");
        registerMessage("player","bjiMSG8", "Yee");
        registerSound( "lucy","bjiMSG8", PleasureCraftSoundEvents.PLOB);
        registerSound( "lucy","bjiMSG9", PleasureCraftSoundEvents.LUCY_GIGGLE);
        registerMessage("lucy","bjiMSG9", "Hihihi~");
        registerSound( "lucy","bjiMSG11", List.of(PleasureCraftSoundEvents.LUCY_LIPSOUND, PleasureCraftSoundEvents.LUCY_BJMOAN));

        //Slow
        registerSound( "lucy","bjiMSG12", PleasureCraftSoundEvents.LUCY_LIPSOUND);

        //Fast
        registerSound( "lucy","bjtMSG1", PleasureCraftSoundEvents.LUCY_MMM);
        registerSound( "lucy","bjtMSG1", PleasureCraftSoundEvents.LUCY_LIPSOUND);
    }

    private static void lucyShared(){
        //Cum for BlowJob and Parizuri
        registerSound( "lucy","bjcMSG1", PleasureCraftSoundEvents.LUCY_BJMOAN);
        registerSound( "lucy","bjcMSG2", PleasureCraftSoundEvents.LUCY_BJMOAN);
        registerSound( "lucy","bjcMSG3", PleasureCraftSoundEvents.LUCY_AFTERSSESSIONMOAN);
        registerSound( "lucy","bjcMSG4", PleasureCraftSoundEvents.LUCY_LIGHTBREATHING);
        registerSound( "lucy","bjcMSG5", PleasureCraftSoundEvents.LUCY_LIGHTBREATHING);
        registerSound( "lucy","bjcMSG6", PleasureCraftSoundEvents.LUCY_LIGHTBREATHING);
    }


    private static void lucyDoggy(){
        //Laying on the bed
        registerSound( "lucy","doggyLayOnBedMSG1", PleasureCraftSoundEvents.BEDRUSTLE);
        registerSound( "lucy","doggyLayOnBedMSG2", PleasureCraftSoundEvents.LUCY_LIGHTBREATHING);
        registerMessage( "lucy","doggyLayOnBedMSG2", "what are you waiting for?~");
        registerSound( "lucy","doggyLayOnBedMSG3", PleasureCraftSoundEvents.LUCY_GIGGLE);
        registerMessage( "lucy","doggyLayOnBedMSG3", "this ass ain't gonna fuck itself...");
        registerSound( "lucy","doggyLayOnBedMSG4", PleasureCraftSoundEvents.SLAP);

        //Intro
        registerSound("lucy","doggyIntroMSG1", PleasureCraftSoundEvents.TOUCH);
        registerSound("lucy","doggyIntroMSG2", PleasureCraftSoundEvents.TOUCH);
        registerSound("lucy","doggyIntroMSG3", PleasureCraftSoundEvents.BEDRUSTLE);
        registerSound("lucy","doggyIntroMSG4", PleasureCraftSoundEvents.SMALLINSERTS);
        registerSound("lucy","doggyIntroMSG4", PleasureCraftSoundEvents.LUCY_MMM);
        registerSound("lucy","doggyIntroMSG5", PleasureCraftSoundEvents.POUNDING);
        registerSound("lucy","doggyIntroMSG5", PleasureCraftSoundEvents.LUCY_MOAN);

        //Slow
        registerSound("lucy","doggySlowMSG1", PleasureCraftSoundEvents.POUNDING);
        registerSound("lucy","doggySlowMSG1", List.of(PleasureCraftSoundEvents.LUCY_MOAN, PleasureCraftSoundEvents.LUCY_HEAVYBREATHING, PleasureCraftSoundEvents.LUCY_MMM));
        registerSound("lucy","doggySlowMSG2", PleasureCraftSoundEvents.LUCY_LIGHTBREATHING);

        //Fast
        registerSound("lucy","doggyFastMSG1", PleasureCraftSoundEvents.POUNDING);
        registerSound("lucy","doggyFastMSG1", List.of(PleasureCraftSoundEvents.LUCY_MOAN, PleasureCraftSoundEvents.LUCY_HEAVYBREATHING, PleasureCraftSoundEvents.LUCY_AHH));

        //Cum
        registerSound("lucy","doggyCumMSG1", PleasureCraftSoundEvents.POUNDING);
        registerSound("lucy","doggyCumMSG1", PleasureCraftSoundEvents.CUMINFLATION);
        registerSound("lucy","doggyCumMSG1", PleasureCraftSoundEvents.LUCY_MOAN);
        registerSound("lucy","doggyCumMSG2", PleasureCraftSoundEvents.LUCY_HEAVYBREATHING);
        registerSound("lucy","doggyCumMSG3", PleasureCraftSoundEvents.LUCY_HEAVYBREATHING);
        registerSound("lucy","doggyCumMSG4", PleasureCraftSoundEvents.LUCY_HEAVYBREATHING);
        registerSound("lucy","doggyCumMSG5", PleasureCraftSoundEvents.LUCY_HEAVYBREATHING);
    }

    // --- Register a fixed sound ---
    public static void registerSound(String girls, String key, SoundEvent event) {
        String[] girlsArray = girls.replaceAll("\\s+", "").split(",");
        for (String girl : girlsArray) {
            SceneKey sceneKey = new SceneKey(girl, key);
            SOUND_EVENTS.computeIfAbsent(sceneKey, k -> new ArrayList<>()).add(event);
        }
    }

    // --- Register a randomizable sound list ---
    public static void registerSound(String girls, String key, List<SoundEvent> events) {
        String[] girlsArray = girls.replaceAll("\\s+", "").split(",");
        for (String girl : girlsArray) {
            SceneKey sceneKey = new SceneKey(girl, key);
            RANDOM_SOUNDS.computeIfAbsent(sceneKey, k -> new ArrayList<>()).addAll(events);
        }
    }

    // --- Get sounds: returns all fixed sounds, plus one random from the random list if present ---
    public static List<SoundEvent> getSound(String girl, String key) {
        SceneKey sceneKey = new SceneKey(girl, key);
        List<SoundEvent> result = new ArrayList<>();

        // Add all fixed sounds
        List<SoundEvent> fixed = SOUND_EVENTS.get(sceneKey);
        if (fixed != null) result.addAll(fixed);

        // Add one random from the random pool
        List<SoundEvent> randomPool = RANDOM_SOUNDS.get(sceneKey);
        if (randomPool != null && !randomPool.isEmpty()) {
            result.add(randomPool.get(RANDOM.nextInt(randomPool.size())));
        }

        return result;
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
