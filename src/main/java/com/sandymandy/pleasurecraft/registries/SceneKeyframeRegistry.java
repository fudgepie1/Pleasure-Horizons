package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.util.PleasureCraftLangUtils;
import net.minecraft.sound.SoundEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;

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
        registerSound("lucy","stripMSG1", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerMessage("lucy, momo, mika","stripMSG1", "strip");
    }

    private static void lucyPaizuri(){
        //Intro
        registerSound( "lucy","paizuriStartMSG1", PleasureCraftSoundEventRegistry.POUNDING);

        //Slow
        registerSound( "lucy","paizuriSlowMSG1", PleasureCraftSoundEventRegistry.POUNDING);

        //Fast
        registerSound( "lucy","paizuriFastMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound( "lucy","paizuriFastMSG1", List.of(PleasureCraftSoundEventRegistry.LUCY_AHH, PleasureCraftSoundEventRegistry.LUCY_MMM));
    }

    private static void lucyBlowJob(){
        //Intro
        registerSound( "lucy","bjiMSG1", PleasureCraftSoundEventRegistry.LUCY_MMM);
        registerMessage("lucy","bjiMSG1", "blowJob.msg1");
        registerSound( "lucy","bjiMSG2", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerMessage("lucy","bjiMSG2", "blowJob.msg2");
        registerSound( "lucy","bjiMSG3", PleasureCraftSoundEventRegistry.LUCY_AFTERSSESSIONMOAN);
        registerMessage("lucy","bjiMSG3", "blowJob.msg3");
        registerSound( "lucy","bjiMSG4", PleasureCraftSoundEventRegistry.BELLJINGLE);
        registerSound( "lucy","bjiMSG5", PleasureCraftSoundEventRegistry.LUCY_HMPH);
        registerMessage("lucy","bjiMSG5", "blowJob.msg4");
        registerSound( "lucy","bjiMSG6", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerMessage("lucy","bjiMSG6", "blowJob.msg5");
        registerSound( "lucy","bjiMSG7", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerMessage("lucy","bjiMSG7", "blowJob.msg6");
        registerMessage("player","bjiMSG8", "blowJob.msg7");
        registerSound( "lucy","bjiMSG8", PleasureCraftSoundEventRegistry.PLOB);
        registerSound( "lucy","bjiMSG9", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerMessage("lucy","bjiMSG9", "blowJob.msg8");
        registerSound( "lucy","bjiMSG11", List.of(PleasureCraftSoundEventRegistry.LUCY_LIPSOUND, PleasureCraftSoundEventRegistry.LUCY_BJMOAN));

        //Slow
        registerSound( "lucy","bjiMSG12", PleasureCraftSoundEventRegistry.LUCY_LIPSOUND);

        //Fast
        registerSound( "lucy","bjtMSG1", PleasureCraftSoundEventRegistry.LUCY_MMM);
        registerSound( "lucy","bjtMSG1", PleasureCraftSoundEventRegistry.LUCY_LIPSOUND);
    }

    private static void lucyShared(){
        //Cum for BlowJob and Parizuri
        registerSound( "lucy","bjcMSG1", PleasureCraftSoundEventRegistry.LUCY_BJMOAN);
        registerSound( "lucy","bjcMSG2", PleasureCraftSoundEventRegistry.LUCY_BJMOAN);
        registerSound( "lucy","bjcMSG3", PleasureCraftSoundEventRegistry.LUCY_AFTERSSESSIONMOAN);
        registerSound( "lucy","bjcMSG4", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerSound( "lucy","bjcMSG5", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerSound( "lucy","bjcMSG6", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
    }


    private static void lucyDoggy(){
        //Laying on the bed
        registerSound( "lucy","doggyLayOnBedMSG1", PleasureCraftSoundEventRegistry.BEDRUSTLE);
        registerSound( "lucy","doggyLayOnBedMSG2", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerMessage( "lucy","doggyLayOnBedMSG2", "doggyLayOnBed.msg1");
        registerSound( "lucy","doggyLayOnBedMSG3", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerMessage( "lucy","doggyLayOnBedMSG3", "doggyLayOnBed.msg2");
        registerSound( "lucy","doggyLayOnBedMSG4", PleasureCraftSoundEventRegistry.SLAP);

        //Intro
        registerSound("lucy","doggyIntroMSG1", PleasureCraftSoundEventRegistry.TOUCH);
        registerSound("lucy","doggyIntroMSG2", PleasureCraftSoundEventRegistry.TOUCH);
        registerSound("lucy","doggyIntroMSG3", PleasureCraftSoundEventRegistry.BEDRUSTLE);
        registerSound("lucy","doggyIntroMSG4", PleasureCraftSoundEventRegistry.SMALLINSERTS);
        registerSound("lucy","doggyIntroMSG4", PleasureCraftSoundEventRegistry.LUCY_MMM);
        registerSound("lucy","doggyIntroMSG5", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound("lucy","doggyIntroMSG5", PleasureCraftSoundEventRegistry.LUCY_MOAN);

        //Slow
        registerSound("lucy","doggySlowMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound("lucy","doggySlowMSG1", List.of(PleasureCraftSoundEventRegistry.LUCY_MOAN, PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING, PleasureCraftSoundEventRegistry.LUCY_MMM));
        registerSound("lucy","doggySlowMSG2", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);

        //Fast
        registerSound("lucy","doggyFastMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound("lucy","doggyFastMSG1", List.of(PleasureCraftSoundEventRegistry.LUCY_MOAN, PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING, PleasureCraftSoundEventRegistry.LUCY_AHH));

        //Cum
        registerSound("lucy","doggyCumMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound("lucy","doggyCumMSG1", PleasureCraftSoundEventRegistry.CUMINFLATION);
        registerSound("lucy","doggyCumMSG1", PleasureCraftSoundEventRegistry.LUCY_MOAN);
        registerSound("lucy","doggyCumMSG2", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
        registerSound("lucy","doggyCumMSG3", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
        registerSound("lucy","doggyCumMSG4", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
        registerSound("lucy","doggyCumMSG5", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
    }

    // --- Register a fixed sound ---
    public static void registerSound(String girls, String frameKey, SoundEvent event) {
        String[] girlsArray = girls.replaceAll("\\s+", "").split(",");
        for (String girl : girlsArray) {
            SceneKey sceneKey = new SceneKey(girl, frameKey);
            SOUND_EVENTS.computeIfAbsent(sceneKey, k -> new ArrayList<>()).add(event);
        }
    }

    // --- Register a randomizable sound list ---
    public static void registerSound(String girls, String frameKey, List<SoundEvent> events) {
        String[] girlsArray = girls.replaceAll("\\s+", "").split(",");
        for (String girl : girlsArray) {
            SceneKey sceneKey = new SceneKey(girl, frameKey);
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

  /**
   * The way that this gets the messages are through the lang files like the en_us.json for US english
   * So when you register a message you have to give it a key to grab from the lang file.
   * <p>
   * They are saved as {sceneMsg."girlname"."langKey"} so if I were to register the message of "Hello World" to momo with the key of "World"
   * I would have to make a new entry in the lang file as {sceneMsg.momo.World : "Hello World"}.
   * <p>
   */
    public static void registerMessage(String girls, String frameKey, String langKey) {
        String[] girlsArray = girls.replaceAll("\\s+", "").split(",");

        for (String girl : girlsArray) {
            SceneKey sceneKey = new SceneKey(girl, frameKey);
            String key = "sceneMsg." + girl + "." + langKey;
            CHAT_MESSAGES.computeIfAbsent(sceneKey, k -> new ArrayList<>()).add(key);
        }
    }

    public static List<String> getMessage(String girl, String key) {
        List<String> keys = CHAT_MESSAGES.getOrDefault(new SceneKey(girl, key), Collections.emptyList());
        List<String> translated = new ArrayList<>();
        for (String langKey : keys) {
            translated.add(PleasureCraftLangUtils.getStringFromKey(langKey));
        }
        return translated;
    }

    public record SceneKey(String girl, String key) {}}
