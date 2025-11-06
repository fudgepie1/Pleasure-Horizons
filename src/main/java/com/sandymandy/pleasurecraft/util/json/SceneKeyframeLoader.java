package com.sandymandy.pleasurecraft.util.json;

import com.google.gson.*;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.registries.SceneKeyframeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SceneKeyframeLoader {

    public static void loadFromAssets() {
        var resourceManager = MinecraftClient.getInstance().getResourceManager();

        resourceManager.findResources("scenes", path -> path.getPath().endsWith(".json")).forEach((id, resource) -> {
            try (var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                String girlID = json.get("girl_id").getAsString();
                JsonArray scenes = json.getAsJsonArray("scenes");

                for (JsonElement elem : scenes) {
                    JsonObject scene = elem.getAsJsonObject();
                    String key = scene.get("key").getAsString().toLowerCase();

                    // --- Fixed sounds ---
                    if (scene.has("sounds")) {
                        for (String soundId : jsonArrayToList(scene.getAsJsonArray("sounds"))) {
                            SoundEvent sound = SoundEvent.of(Identifier.of(soundId));
                            SceneKeyframeRegistry.registerCustomGirlSound(girlID, key, sound);
                        }
                    }

                    // --- Random sounds ---
                    if (scene.has("random_sounds")) {
                        List<String> randomIds = jsonArrayToList(scene.getAsJsonArray("random_sounds"));
                        List<SoundEvent> soundEvents = randomIds.stream()
                                .map(idStr -> SoundEvent.of(Identifier.of(idStr)))
                                .toList();
                        SceneKeyframeRegistry.registerCustomGirlSound(girlID, key, soundEvents);
                    }

                    // --- Messages ---
                    if (scene.has("messages")) {
                        for (String message : jsonArrayToList(scene.getAsJsonArray("messages"))) {
                            SceneKeyframeRegistry.registerCustomGirlMessage(girlID, key, message);
                        }
                    }
                }

                PleasureCraft.LOGGER.info("[SceneKeyframeLoader] Loaded custom scene JSON for {}", girlID);

            } catch (Exception e) {
                PleasureCraft.LOGGER.error("[SceneKeyframeLoader] Failed to load scene JSON " + id, e);
            }
        });
    }

    private static List<String> jsonArrayToList(JsonArray array) {
        List<String> list = new ArrayList<>();
        if (array != null) {
            for (JsonElement e : array) list.add(e.getAsString());
        }
        return list;
    }
}
