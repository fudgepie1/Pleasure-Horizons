package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.util.json.SceneKeyframeLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.SoundEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;

import java.util.*;
public class SceneKeyframeRegistry {
    private static final Map<SceneKey, List<SoundEvent>> SOUND_EVENTS = new HashMap<>();
    private static final Map<SceneKey, List<SoundEvent>> RANDOM_SOUNDS = new HashMap<>();
    private static final Map<SceneKey, List<String>> CHAT_MESSAGES = new HashMap<>();
    private static final Map<String, List<String>> PLAYER_MESSAGES = new HashMap<>();
    private static final Map<CustomGirlSceneKey, List<SoundEvent>> CUSTOM_GIRL_SOUND_EVENTS = new HashMap<>();
    private static final Map<CustomGirlSceneKey, List<SoundEvent>> CUSTOM_GIRL_RANDOM_SOUNDS = new HashMap<>();
    private static final Map<CustomGirlSceneKey, List<String>> CUSTOM_GIRL_CHAT_MESSAGES = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void registerSoundEvents() {
        strip();
        lucyDoggy();
        lucyPaizuri();
        lucyShared();
        lucyBlowJob();
        momoDoggy();
        momoAnal();
        mikaFaceFuck();
        mikaMissionary();
        mikaCowgirl();
        SceneKeyframeLoader.loadFromAssets();
    }

    private static void strip(){
        registerSound(GirlRegistry.LUCY,"stripMSG1", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerSound(GirlRegistry.MOMO,"stripMSG1", PleasureCraftSoundEventRegistry.MOMO_GIGGLE);
        registerSound(GirlRegistry.MIKA,"stripMSG1", PleasureCraftSoundEventRegistry.MIKA_GIGGLE);
        registerMessage(List.of(GirlRegistry.LUCY,GirlRegistry.MIKA,GirlRegistry.MOMO),"stripMSG1", "strip");
    }

    private static void lucyPaizuri(){
        //Intro
        registerSound(GirlRegistry.LUCY,"paizuriStartMSG1", PleasureCraftSoundEventRegistry.POUNDING);

        //Slow
        registerSound(GirlRegistry.LUCY,"paizuriSlowMSG1", PleasureCraftSoundEventRegistry.POUNDING);

        //Fast
        registerSound(GirlRegistry.LUCY,"paizuriFastMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.LUCY,"paizuriFastMSG1", List.of(PleasureCraftSoundEventRegistry.LUCY_AHH, PleasureCraftSoundEventRegistry.LUCY_MMM));
    }

    private static void lucyBlowJob(){
        //Intro
        registerSound(GirlRegistry.LUCY,"bjiMSG1", PleasureCraftSoundEventRegistry.LUCY_MMM);
        registerMessage(GirlRegistry.LUCY,"bjiMSG1", "blowJob.msg1");
        registerSound(GirlRegistry.LUCY,"bjiMSG2", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerMessage(GirlRegistry.LUCY,"bjiMSG2", "blowJob.msg2");
        registerSound(GirlRegistry.LUCY,"bjiMSG3", PleasureCraftSoundEventRegistry.LUCY_AFTERSSESSIONMOAN);
        registerMessage(GirlRegistry.LUCY,"bjiMSG3", "blowJob.msg3");
        registerSound(GirlRegistry.LUCY,"bjiMSG4", PleasureCraftSoundEventRegistry.BELLJINGLE);
        registerSound(GirlRegistry.LUCY,"bjiMSG5", PleasureCraftSoundEventRegistry.LUCY_HMPH);
        registerMessage(GirlRegistry.LUCY,"bjiMSG5", "blowJob.msg4");
        registerSound(GirlRegistry.LUCY,"bjiMSG6", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerMessage(GirlRegistry.LUCY,"bjiMSG6", "blowJob.msg5");
        registerSound(GirlRegistry.LUCY,"bjiMSG7", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerMessage(GirlRegistry.LUCY,"bjiMSG7", "blowJob.msg6");
        registerPlayerMessage("bjiMSG8", "blowJob.msg7");
        registerSound(GirlRegistry.LUCY,"bjiMSG8", PleasureCraftSoundEventRegistry.PLOB);
        registerSound(GirlRegistry.LUCY,"bjiMSG9", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerMessage(GirlRegistry.LUCY,"bjiMSG9", "blowJob.msg8");
        registerSound(GirlRegistry.LUCY,"bjiMSG11", List.of(PleasureCraftSoundEventRegistry.LUCY_LIPSOUND, PleasureCraftSoundEventRegistry.LUCY_BJMOAN));

        //Slow
        registerSound( GirlRegistry.LUCY,"bjiMSG12", PleasureCraftSoundEventRegistry.LUCY_LIPSOUND);

        //Fast
        registerSound( GirlRegistry.LUCY,"bjtMSG1", PleasureCraftSoundEventRegistry.LUCY_MMM);
        registerSound( GirlRegistry.LUCY,"bjtMSG1", PleasureCraftSoundEventRegistry.LUCY_LIPSOUND);
    }

    private static void lucyShared(){
        //Cum for BlowJob and Parizuri
        registerSound( GirlRegistry.LUCY,"bjcMSG1", PleasureCraftSoundEventRegistry.LUCY_BJMOAN);
        registerSound( GirlRegistry.LUCY,"bjcMSG2", PleasureCraftSoundEventRegistry.LUCY_BJMOAN);
        registerSound( GirlRegistry.LUCY,"bjcMSG3", PleasureCraftSoundEventRegistry.LUCY_AFTERSSESSIONMOAN);
        registerSound( GirlRegistry.LUCY,"bjcMSG4", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerSound( GirlRegistry.LUCY,"bjcMSG5", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerSound( GirlRegistry.LUCY,"bjcMSG6", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
    }


    private static void lucyDoggy(){
        //Laying on the bed
        registerSound( GirlRegistry.LUCY,"doggyLayOnBedMSG1", PleasureCraftSoundEventRegistry.BEDRUSTLE);
        registerSound( GirlRegistry.LUCY,"doggyLayOnBedMSG2", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);
        registerMessage( GirlRegistry.LUCY,"doggyLayOnBedMSG2", "doggyLayOnBed.msg1");
        registerSound( GirlRegistry.LUCY,"doggyLayOnBedMSG3", PleasureCraftSoundEventRegistry.LUCY_GIGGLE);
        registerMessage( GirlRegistry.LUCY,"doggyLayOnBedMSG3", "doggyLayOnBed.msg2");
        registerSound( GirlRegistry.LUCY,"doggyLayOnBedMSG4", PleasureCraftSoundEventRegistry.SLAP);

        //Intro
        registerSound(GirlRegistry.LUCY,"doggyIntroMSG1", PleasureCraftSoundEventRegistry.TOUCH);
        registerSound(GirlRegistry.LUCY,"doggyIntroMSG2", PleasureCraftSoundEventRegistry.TOUCH);
        registerSound(GirlRegistry.LUCY,"doggyIntroMSG3", PleasureCraftSoundEventRegistry.BEDRUSTLE);
        registerSound(GirlRegistry.LUCY,"doggyIntroMSG4", PleasureCraftSoundEventRegistry.SMALLINSERTS);
        registerSound(GirlRegistry.LUCY,"doggyIntroMSG4", PleasureCraftSoundEventRegistry.LUCY_MMM);
        registerSound(GirlRegistry.LUCY,"doggyIntroMSG5", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.LUCY,"doggyIntroMSG5", PleasureCraftSoundEventRegistry.LUCY_MOAN);

        //Slow
        registerSound(GirlRegistry.LUCY,"doggySlowMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.LUCY,"doggySlowMSG1", List.of(PleasureCraftSoundEventRegistry.LUCY_MOAN, PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING, PleasureCraftSoundEventRegistry.LUCY_MMM));
        registerSound(GirlRegistry.LUCY,"doggySlowMSG2", PleasureCraftSoundEventRegistry.LUCY_LIGHTBREATHING);

        //Fast
        registerSound(GirlRegistry.LUCY,"doggyFastMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.LUCY,"doggyFastMSG1", List.of(PleasureCraftSoundEventRegistry.LUCY_MOAN, PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING, PleasureCraftSoundEventRegistry.LUCY_AHH));

        //Cum
        registerSound(GirlRegistry.LUCY,"doggyCumMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.LUCY,"doggyCumMSG1", PleasureCraftSoundEventRegistry.CUMINFLATION);
        registerSound(GirlRegistry.LUCY,"doggyCumMSG1", PleasureCraftSoundEventRegistry.LUCY_MOAN);
        registerSound(GirlRegistry.LUCY,"doggyCumMSG2", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
        registerSound(GirlRegistry.LUCY,"doggyCumMSG3", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
        registerSound(GirlRegistry.LUCY,"doggyCumMSG4", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
        registerSound(GirlRegistry.LUCY,"doggyCumMSG5", PleasureCraftSoundEventRegistry.LUCY_HEAVYBREATHING);
    }

    private static void momoDoggy(){
        //Laying on bed
        registerSound(GirlRegistry.MOMO,"sitdownMSG1", PleasureCraftSoundEventRegistry.MOMO_BREATH);
        registerMessage(GirlRegistry.MOMO, "sitdownMSG1", "sitDown");

        //Sex
        registerSound(GirlRegistry.MOMO, "slide", PleasureCraftSoundEventRegistry.SLIDE);
        registerSound(GirlRegistry.MOMO, "pound", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MOMO, "doggyMoan", List.of(PleasureCraftSoundEventRegistry.MOMO_AHH, PleasureCraftSoundEventRegistry.MOMO_MMM));

        //Cum
        registerSound(GirlRegistry.MOMO, "cum", PleasureCraftSoundEventRegistry.INSERTS);
        registerSound(GirlRegistry.MOMO, "orgasm1", PleasureCraftSoundEventRegistry.MOMO_MMM);
        registerSound(GirlRegistry.MOMO, "orgasm2", PleasureCraftSoundEventRegistry.MOMO_MMM);

    }

    private static void momoAnal(){
        //Laying on bed
        registerSound(GirlRegistry.MOMO,"anal_prepareMSG1", PleasureCraftSoundEventRegistry.PLOB);
        registerSound(GirlRegistry.MOMO,"anal_prepareMSG2", PleasureCraftSoundEventRegistry.BEDRUSTLE);

        //Intro
        registerSound(GirlRegistry.MOMO,"anal_startMSG1", PleasureCraftSoundEventRegistry.MOMO_AHH);
        registerSound(GirlRegistry.MOMO,"anal_startMSG2", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MOMO,"anal_startMSG2", PleasureCraftSoundEventRegistry.MOMO_MMM);

        //Slow
        registerSound(GirlRegistry.MOMO,"anal_slowMSG1", PleasureCraftSoundEventRegistry.MOMO_AHH);
        registerSound(GirlRegistry.MOMO,"anal_slowMSG1", PleasureCraftSoundEventRegistry.POUNDING);

        //Fast
        registerSound(GirlRegistry.MOMO,"anal_fastMSG1", PleasureCraftSoundEventRegistry.MOMO_AHH);
        registerSound(GirlRegistry.MOMO,"anal_fastMSG1", PleasureCraftSoundEventRegistry.POUNDING);

        //Cum
        registerSound(GirlRegistry.MOMO,"anal_cumMSG2", PleasureCraftSoundEventRegistry.MOMO_AHH);
    }

    private static void mikaFaceFuck(){
        //Intro
        registerSound(GirlRegistry.MIKA,"carry_introMSG1", PleasureCraftSoundEventRegistry.MIKA_HMPH);
        registerMessage(GirlRegistry.MIKA,"carry_introMSG1", "faceFuck.msg1");
        registerSound(GirlRegistry.MIKA,"carry_introMSG2", PleasureCraftSoundEventRegistry.MIKA_GIGGLE);
        registerMessage(GirlRegistry.MIKA,"carry_introMSG2", "faceFuck.msg2");
        registerSound(GirlRegistry.MIKA,"lipsound", PleasureCraftSoundEventRegistry.MIKA_LIPSOUND);
        registerSound(GirlRegistry.MIKA, "suckFast", PleasureCraftSoundEventRegistry.POUNDING);

        //Cum
        registerSound(GirlRegistry.MIKA, "cum", PleasureCraftSoundEventRegistry.INSERTS);
        registerSound(GirlRegistry.MIKA, "cum", PleasureCraftSoundEventRegistry.MIKA_LIPSOUND);
    }

    private static void mikaMissionary(){
        //Laying on bed
        registerSound(GirlRegistry.MIKA,"sitdownMSG1", PleasureCraftSoundEventRegistry.MIKA_COMETOMOMMY);
        registerMessage(GirlRegistry.MIKA,"sitdownMSG1", "sitDown");

        //Slow
        registerSound(GirlRegistry.MIKA, "missionary_slowMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "missionary_slowMSG1", PleasureCraftSoundEventRegistry.MIKA_AHH);

        //Fast
        registerSound(GirlRegistry.MIKA, "missionary_fastMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "missionary_fastMSG1", List.of(PleasureCraftSoundEventRegistry.MIKA_AHH, PleasureCraftSoundEventRegistry.MIKA_MOAN));

        //Cum
        registerSound(GirlRegistry.MIKA, "bedRustle", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "bedRustle", PleasureCraftSoundEventRegistry.BEDRUSTLE);
        registerSound(GirlRegistry.MIKA, "missionary_cumMSG1", PleasureCraftSoundEventRegistry.MIKA_AHH);
        registerSound(GirlRegistry.MIKA, "missionary_cumMSG2", PleasureCraftSoundEventRegistry.MIKA_GOODBOY);
        registerMessage(GirlRegistry.MIKA,"missionary_cumMSG2", "goodBoy");
    }

    private static void mikaCowgirl(){
        registerSound(GirlRegistry.MIKA,"cowgirlStartMSG0", PleasureCraftSoundEventRegistry.MIKA_GIGGLE);
        registerMessage(GirlRegistry.MIKA,"cowgirlStartMSG1", "likeWhatYouSee");
        registerSound(GirlRegistry.MIKA, "cowgirlStartMSG2", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "cowgirlStartMSG2", List.of(PleasureCraftSoundEventRegistry.MIKA_AHH, PleasureCraftSoundEventRegistry.MIKA_MOAN));

        //Fast
        registerSound(GirlRegistry.MIKA, "cowgirlfastMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "cowgirlfastMSG1", List.of(PleasureCraftSoundEventRegistry.MIKA_AHH, PleasureCraftSoundEventRegistry.MIKA_MOAN));

        //Cum
        registerSound(GirlRegistry.MIKA, "cowgirlcumMSG1", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "cowgirlcumMSG1", PleasureCraftSoundEventRegistry.MIKA_AHH);
        registerSound(GirlRegistry.MIKA, "cowgirlcumMSG2", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "cowgirlcumMSG2", PleasureCraftSoundEventRegistry.MIKA_MOAN);
        registerSound(GirlRegistry.MIKA, "cowgirlcumMSG3", PleasureCraftSoundEventRegistry.POUNDING);
        registerSound(GirlRegistry.MIKA, "cowgirlcumMSG4", PleasureCraftSoundEventRegistry.MIKA_GOODBOY);
        registerMessage(GirlRegistry.MIKA,"cowgirlcumMSG4", "goodBoy");
    }

    // --- Register a fixed sound ---
    public static void registerSound(EntityType<? extends GeoAnimatable> girl, String frameKey, SoundEvent event) {
        frameKey = frameKey.toLowerCase();
        SceneKey key = new SceneKey(girl, frameKey);
        SOUND_EVENTS.computeIfAbsent(key, k -> new ArrayList<>()).add(event);
    }

    // --- Register a randomizable sound list ---
    public static void registerSound(EntityType<? extends GeoAnimatable> girl, String frameKey, List<SoundEvent> events) {
        frameKey = frameKey.toLowerCase();
        SceneKey key = new SceneKey(girl, frameKey);
        RANDOM_SOUNDS.computeIfAbsent(key, k -> new ArrayList<>()).addAll(events);
    }

    public static void registerCustomGirlSound(String girlID, String frameKey, SoundEvent event) {
        frameKey = frameKey.toLowerCase();
        CustomGirlSceneKey key = new CustomGirlSceneKey(girlID, frameKey);
        CUSTOM_GIRL_SOUND_EVENTS.computeIfAbsent(key, k -> new ArrayList<>()).add(event);
    }

    public static void registerCustomGirlSound(String girlID, String frameKey, List<SoundEvent> events) {
        frameKey = frameKey.toLowerCase();
        CustomGirlSceneKey key = new CustomGirlSceneKey(girlID, frameKey);
        CUSTOM_GIRL_RANDOM_SOUNDS.computeIfAbsent(key, k -> new ArrayList<>()).addAll(events);
    }

    // --- Get sounds: returns all fixed sounds, plus one random from the random list if present ---
    public static List<SoundEvent> getSound(EntityType<?> girl, String key) {
        key = key.toLowerCase();
        List<SoundEvent> result = new ArrayList<>();

        // Go through all registered keys and find ones that "contain" or "start with" the key
        for (Map.Entry<SceneKey, List<SoundEvent>> entry : SOUND_EVENTS.entrySet()) {
            SceneKey sceneKey = entry.getKey();
            if (sceneKey.girl().equals(girl) && key.contains(sceneKey.key())) {
                result.addAll(entry.getValue());
            }
        }

        // Add random sounds if partial key matches
        for (Map.Entry<SceneKey, List<SoundEvent>> entry : RANDOM_SOUNDS.entrySet()) {
            SceneKey sceneKey = entry.getKey();
            if (sceneKey.girl().equals(girl) && key.contains(sceneKey.key())) {
                List<SoundEvent> pool = entry.getValue();
                if (!pool.isEmpty()) {
                    result.add(pool.get(RANDOM.nextInt(pool.size())));
                }
            }
        }

        return result;
    }

    public static List<SoundEvent> getCustomGirlSound(String girlID, String key) {
        key = key.toLowerCase();
        List<SoundEvent> result = new ArrayList<>();

        // Go through all registered keys and find ones that "contain" or "start with" the key
        for (Map.Entry<CustomGirlSceneKey, List<SoundEvent>> entry : CUSTOM_GIRL_SOUND_EVENTS.entrySet()) {
            CustomGirlSceneKey sceneKey = entry.getKey();
            if (sceneKey.girlID().equals(girlID) && key.contains(sceneKey.key())) {
                result.addAll(entry.getValue());
            }
        }

        // Add random sounds if partial key matches
        for (Map.Entry<CustomGirlSceneKey, List<SoundEvent>> entry : CUSTOM_GIRL_RANDOM_SOUNDS.entrySet()) {
            CustomGirlSceneKey sceneKey = entry.getKey();
            if (sceneKey.girlID().equals(girlID) && key.contains(sceneKey.key())) {
                List<SoundEvent> pool = entry.getValue();
                if (!pool.isEmpty()) {
                    result.add(pool.get(RANDOM.nextInt(pool.size())));
                }
            }
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
  // --- Register message(s) ---
    public static void registerMessage(EntityType<?> girl, String frameKey, String langKey) {
      frameKey = frameKey.toLowerCase();
      String girlName = EntityType.getId(girl).getPath();
      SceneKey key = new SceneKey(girl, frameKey);
      String msgKey = "sceneMsg." + girlName + "." + langKey;
      CHAT_MESSAGES.computeIfAbsent(key, k -> new ArrayList<>()).add(msgKey);
    }

    public static void registerMessage(List<EntityType<?>> girls, String frameKey, String langKey) {
        frameKey = frameKey.toLowerCase();
        for (EntityType<?> girl : girls) {
            registerMessage(girl, frameKey, langKey);
        }
    }


    public static void registerCustomGirlMessage(String girlID, String frameKey, String message) {
        frameKey = frameKey.toLowerCase();
        CustomGirlSceneKey key = new CustomGirlSceneKey(girlID, frameKey);
        CUSTOM_GIRL_CHAT_MESSAGES.computeIfAbsent(key, k -> new ArrayList<>()).add(message);
    }

    // --- Register Player message ---
    public static void registerPlayerMessage(String frameKey, String langKey) {
        frameKey = frameKey.toLowerCase();
        String msgKey = "sceneMsg.player." + langKey;
        PLAYER_MESSAGES.computeIfAbsent(frameKey, k -> new ArrayList<>()).add(msgKey);
    }

    public static List<String> getPlayerMessage(String key) {
        key = key.toLowerCase();
        return PLAYER_MESSAGES.getOrDefault(key, Collections.emptyList());
    }

    public static List<String> getMessage(EntityType<?> girl, String key) {
        key = key.toLowerCase();
        List<String> result = new ArrayList<>();

        for (Map.Entry<SceneKey, List<String>> entry : CHAT_MESSAGES.entrySet()) {
            SceneKey sceneKey = entry.getKey();
            if (sceneKey.girl().equals(girl) && key.contains(sceneKey.key())) {
                result.addAll(entry.getValue());
            }
        }

        return result;
    }

    public static List<String> getCustomGirlMessage(String girlID, String key) {
        key = key.toLowerCase();
        List<String> result = new ArrayList<>();

        for (Map.Entry<CustomGirlSceneKey, List<String>> entry : CUSTOM_GIRL_CHAT_MESSAGES.entrySet()) {
            CustomGirlSceneKey sceneKey = entry.getKey();
            if (sceneKey.girlID().equals(girlID) && key.contains(sceneKey.key())) {
                result.addAll(entry.getValue());
            }
        }

        return result;
    }

    public record SceneKey(EntityType<?> girl, String key) {
        public SceneKey {
            key = key.toLowerCase(Locale.ROOT); // normalize record field
        }
    }

    public record CustomGirlSceneKey(String girlID, String key) {
        public CustomGirlSceneKey {
            key = key.toLowerCase(Locale.ROOT); // normalize record field
        }
    }
}
