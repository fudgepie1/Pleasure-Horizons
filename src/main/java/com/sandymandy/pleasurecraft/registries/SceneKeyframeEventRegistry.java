package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.sound.SoundEvent;

import java.util.*;
public class SceneKeyframeEventRegistry {
    private static final Map<SceneKey, List<SoundEvent>> SOUND_EVENTS = new HashMap<>();
    private static final Map<SceneKey, List<SoundEvent>> RANDOM_SOUNDS = new HashMap<>();
    private static final Map<SceneKey, List<String>> CHAT_MESSAGES = new HashMap<>();
    private static final Map<String, List<String>> PLAYER_MESSAGES = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void registerSoundEvents() {
        PleasureCraft.LOGGER.info("Registering Scene Keyframe Events for PleasureCraft");
    }

    public static void registerSound(String girlID, String frameKey, SoundEvent event) {
        frameKey = frameKey.toLowerCase();
        SceneKey key = new SceneKey(girlID, frameKey);
        SOUND_EVENTS.computeIfAbsent(key, k -> new ArrayList<>()).add(event);
    }

    public static void registerSound(String girlID, String frameKey, List<SoundEvent> events) {
        frameKey = frameKey.toLowerCase();
        SceneKey key = new SceneKey(girlID, frameKey);
        RANDOM_SOUNDS.computeIfAbsent(key, k -> new ArrayList<>()).addAll(events);
    }

    public static List<SoundEvent> getSound(String girlID, String key) {
        key = key.toLowerCase();
        List<SoundEvent> result = new ArrayList<>();

        // Go through all registered keys and find ones that "contain" or "start with" the key
        for (Map.Entry<SceneKey, List<SoundEvent>> entry : SOUND_EVENTS.entrySet()) {
            SceneKey sceneKey = entry.getKey();
            if (sceneKey.girlID().equals(girlID) && key.contains(sceneKey.key())) {
                result.addAll(entry.getValue());
            }
        }

        // Add random sounds if partial key matches
        for (Map.Entry<SceneKey, List<SoundEvent>> entry : RANDOM_SOUNDS.entrySet()) {
            SceneKey sceneKey = entry.getKey();
            if (sceneKey.girlID().equals(girlID) && key.contains(sceneKey.key())) {
                List<SoundEvent> pool = entry.getValue();
                if (!pool.isEmpty()) {
                    result.add(pool.get(RANDOM.nextInt(pool.size())));
                }
            }
        }

        return result;
    }


    public static void registerMessage(String girlID, String frameKey, String message) {
        frameKey = frameKey.toLowerCase();
        SceneKey key = new SceneKey(girlID, frameKey);
        CHAT_MESSAGES.computeIfAbsent(key, k -> new ArrayList<>()).add(message);
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

    public static List<String> getMessage(String girlID, String key) {
        key = key.toLowerCase();
        List<String> result = new ArrayList<>();

        for (Map.Entry<SceneKey, List<String>> entry : CHAT_MESSAGES.entrySet()) {
            SceneKey sceneKey = entry.getKey();
            if (sceneKey.girlID().equals(girlID) && key.contains(sceneKey.key())) {
                result.addAll(entry.getValue());
            }
        }

        return result;
    }

    public record SceneKey(String girlID, String key) {
        public SceneKey {
            key = key.toLowerCase(Locale.ROOT); // normalize record field
        }
    }
}
