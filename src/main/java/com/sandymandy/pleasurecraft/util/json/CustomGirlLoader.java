package com.sandymandy.pleasurecraft.util.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.variables.CustomGirlProfile;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CustomGirlLoader {

    public static final Map<String, CustomGirlProfile> PROFILES = new HashMap<>();

    public static void register() {
        Path dir = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("pleasurecraft/girls");

        try { Files.createDirectories(dir); } catch (Exception ignored) {}

        try (var files = Files.list(dir)) {
            files.filter(f -> f.toString().endsWith(".json"))
                    .forEach(CustomGirlLoader::loadFile);
        } catch (Exception e) {
            PleasureCraft.LOGGER.error("Failed loading girl profiles", e);
        }
    }

    private static void loadFile(Path file) {
        try {
            JsonObject json = JsonParser.parseString(Files.readString(file))
                    .getAsJsonObject();

            CustomGirlProfile profile = CustomGirlParser.parse(json);
            PROFILES.put(profile.id(), profile);

            PleasureCraft.LOGGER.info("Loaded custom girl: {}", profile.id());

        } catch (Exception e) {
            PleasureCraft.LOGGER.error("Error parsing girl JSON: " + file, e);
        }
    }

    public static CustomGirlProfile getNextProfile(String currentId) {
        if (PROFILES.isEmpty()) return null;

        var keys = PROFILES.keySet().stream().toList();

        int index = keys.indexOf(currentId);
        if (index == -1) index = 0; // if current not found, start at 0

        // cycle
        index = (index + 1) % keys.size();

        return PROFILES.get(keys.get(index));
    }
}
