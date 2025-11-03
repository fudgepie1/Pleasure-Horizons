package com.sandymandy.pleasurecraft.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sandymandy.pleasurecraft.util.variables.CustomGirlProfile;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

public class CustomGirlParser {

    public static CustomGirlProfile parse(JsonObject json) {
        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();
        float hitboxHeight = json.has("hitbox_height") ? json.get("hitbox_height").getAsFloat() : 1.65f;
        int guiSize = json.has("gui_size") ? json.get("gui_size").getAsInt() : 30;
        float guiYOffset = json.has("gui_y_offset") ? json.get("gui_y_offset").getAsFloat() : 0.05f;

        // Tame item
        String tameItemId = json.has("tame_item") ? json.get("tame_item").getAsString() : "minecraft:allium";
        Item tameItem = Registries.ITEM.get(Identifier.of(tameItemId));

        // Attributes
        JsonObject attr = json.getAsJsonObject("attributes");
        double health = attr.has("health") ? attr.get("health").getAsDouble() : 20;
        double speed = attr.has("speed") ? attr.get("speed").getAsDouble() : 0.2;
        double damage = attr.has("damage") ? attr.get("damage").getAsDouble() : 2;

        // Scene options
        List<SceneOptions> scenes = new ArrayList<>();
        if (json.has("scenes")) {
            JsonArray sceneArray = json.getAsJsonArray("scenes");
            for (JsonElement e : sceneArray) {
                JsonObject s = e.getAsJsonObject();
                scenes.add(SceneOptions.create(
                        s.get("name").getAsString(),
                        s.get("required_level").getAsInt(),
                        jsonArrayToList(s.getAsJsonArray("intro")),
                        jsonArrayToList(s.getAsJsonArray("slow")),
                        jsonArrayToList(s.getAsJsonArray("fast")),
                        s.get("cum").getAsString(),
                        s.get("cum_threshold").getAsFloat(),
                        s.has("needs_to_strip") && s.get("needs_to_strip").getAsBoolean(),
                        s.has("use_keyframe") && s.get("use_keyframe").getAsBoolean(),
                        s.has("is_bed_scene") && s.get("is_bed_scene").getAsBoolean(),
                        s.has("bed_offset") ? s.get("bed_offset").getAsFloat() : 0f,
                        s.has("bed_idle") ? jsonArrayToList(s.getAsJsonArray("bed_idle")) : new ArrayList<>()
                ));
            }
        }

        return new CustomGirlProfile(id, name, hitboxHeight, guiSize, guiYOffset, tameItem, health, speed, damage, scenes);
    }

    private static List<String> jsonArrayToList(JsonArray array) {
        List<String> list = new ArrayList<>();
        if (array != null) {
            for (JsonElement e : array) list.add(e.getAsString());
        }
        return list;
    }
}

