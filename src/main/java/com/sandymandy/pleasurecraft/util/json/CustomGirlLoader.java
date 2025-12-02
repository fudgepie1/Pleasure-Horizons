package com.sandymandy.pleasurecraft.util.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.variables.CustomGirlProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CustomGirlLoader {

    public static final Map<String, CustomGirlProfile> LOADED_PROFILES = new HashMap<>();
    public static Map<Item, CustomGirlProfile> REGISTERED_PROFILES = new HashMap<>();

    public static void register() {
        Path dir = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("pleasurecraft/girls");

        try { Files.createDirectories(dir); } catch (Exception ignored) {}

        try (var files = Files.list(dir)) {
            files.filter(f -> f.toString().endsWith(".json"))
                    .forEach(CustomGirlLoader::loadFile);
        } catch (Exception e) {
            PleasureCraft.LOGGER.error("[CustomGirlLoader] Failed loading girl profiles", e);
        }

        validateAndRegisterProfiles();
    }

    private static void loadFile(Path file) {
        try {
            JsonObject json = JsonParser.parseString(Files.readString(file))
                    .getAsJsonObject();

            CustomGirlProfile profile = CustomGirlParser.parse(json);
            LOADED_PROFILES.put(profile.id(), profile);

            PleasureCraft.LOGGER.info("[CustomGirlLoader] Loaded custom girl: {}", profile.id());

        } catch (Exception e) {
            PleasureCraft.LOGGER.error("[CustomGirlLoader] Error parsing girl JSON: " + file, e);
        }
    }

    public static CustomGirlProfile checkItem(Item item) {
        if (REGISTERED_PROFILES.isEmpty()) return null;

        if(REGISTERED_PROFILES.containsKey(item)) return REGISTERED_PROFILES.get(item);

        return null;
    }

    private static void validateAndRegisterProfiles() {
        for (CustomGirlProfile profile : LOADED_PROFILES.values()) {

            Item tameItem = profile.tameItem();

            if (!REGISTERED_PROFILES.containsKey(tameItem)) {
                // safe: unique tame item
                REGISTERED_PROFILES.put(tameItem, profile);
                continue;
            }

            // duplicate found
            CustomGirlProfile first = REGISTERED_PROFILES.get(tameItem);

            PleasureCraft.LOGGER.error("""
                            [CustomGirlLoader] Duplicate tame item detected!
                            Item: {}
                            First girl: {}
                            Conflicting girl: {}
                            Skipping registration of {}.
                            """,
                    tameItem,
                    first.id(),
                    profile.id(),
                    profile.id()
            );
        }
    }

    public static CustomGirlProfile getGirlOrDefault(String id){
        for (CustomGirlProfile profile : REGISTERED_PROFILES.values()){
            if(profile.id().equals(id)) return profile;
        }

        return CustomGirlProfile.DEFAULT;
    }
}
