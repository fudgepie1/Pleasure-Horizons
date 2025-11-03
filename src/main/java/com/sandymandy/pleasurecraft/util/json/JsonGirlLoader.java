package com.sandymandy.pleasurecraft.util.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.variables.JsonGirlProfile;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonGirlLoader {

    public static final Map<String, JsonGirlProfile> PROFILES = new HashMap<>();

    public static void register() {
        Path dir = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("pleasurecraft/girls");

        try { Files.createDirectories(dir); } catch (Exception ignored) {}

        try (var files = Files.list(dir)) {
            files.filter(f -> f.toString().endsWith(".json"))
                    .forEach(JsonGirlLoader::loadFile);
        } catch (Exception e) {
            PleasureCraft.LOGGER.error("Failed loading girl profiles", e);
        }
    }

    private static void loadFile(Path file) {
        try {
            JsonObject json = JsonParser.parseString(Files.readString(file))
                    .getAsJsonObject();

            JsonGirlProfile profile = JsonGirlParser.parse(json);
            PROFILES.put(profile.id(), profile);

            PleasureCraft.LOGGER.info("Loaded custom girl: {}", profile.id());

        } catch (Exception e) {
            PleasureCraft.LOGGER.error("Error parsing girl JSON: " + file, e);
        }
    }
}
