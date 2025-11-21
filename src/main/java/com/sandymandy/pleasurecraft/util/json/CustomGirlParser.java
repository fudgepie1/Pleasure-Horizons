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
            JsonArray array = json.getAsJsonArray("scenes");
            for (JsonElement e : array) {
                scenes.add(parseScene(e.getAsJsonObject()));
            }
        }

        return new CustomGirlProfile(id, name, hitboxHeight, guiSize, guiYOffset, tameItem, health, speed, damage, scenes);
    }

    private static SceneOptions parseScene(JsonObject s) {
        String name = s.get("name").getAsString();
        int level = s.get("required_level").getAsInt();
        boolean needsStrip = s.has("needs_to_strip") && s.get("needs_to_strip").getAsBoolean();

        String type = s.has("scene_type") ? s.get("scene_type").getAsString() : "on_player";

        switch (type) {

            case "on_bed":
                return SceneOptions.onBed(
                        name,
                        level,
                        jsonArrayToList(s.getAsJsonArray("intro_anim")),
                        jsonArrayToList(s.getAsJsonArray("slow_anim")),
                        jsonArrayToList(s.getAsJsonArray("fast_anim")),
                        s.get("cum_anim").getAsString(),
                        s.get("cum_threshold").getAsFloat(),
                        needsStrip,
                        s.has("use_keyframe") && s.get("use_keyframe").getAsBoolean(),
                        s.has("bed_offset") ? s.get("bed_offset").getAsFloat() : 0f,
                        s.has("lay_on_bed_anim") ? s.get("lay_on_bed_anim").getAsString() : "",
                        s.has("bed_idle_anim") ? s.get("bed_idle_anim").getAsString() : ""
                );

            case "on_player":
                return SceneOptions.onPlayer(
                        name,
                        level,
                        jsonArrayToList(s.getAsJsonArray("intro_anim")),
                        jsonArrayToList(s.getAsJsonArray("slow_anim")),
                        jsonArrayToList(s.getAsJsonArray("fast_anim")),
                        s.get("cum_anim").getAsString(),
                        s.get("cum_threshold").getAsFloat(),
                        needsStrip,
                        s.has("use_keyframe") && s.get("use_keyframe").getAsBoolean()
                );

            case "stationary_intro":
                return SceneOptions.stationaryIntro(
                        name,
                        level,
                        jsonArrayToList(s.getAsJsonArray("intro_anim")),
                        s.get("anim").getAsString(),
                        s.get("amount_of_loops").getAsInt(),
                        needsStrip
                );

            case "stationary":
                return SceneOptions.stationary(
                        name,
                        level,
                        s.get("anim").getAsString(),
                        s.get("amount_of_loops").getAsInt(),
                        needsStrip
                );

            default:
                // fallback for old JSON that didn’t have scene_type
                return SceneOptions.onPlayer(
                        name,
                        level,
                        jsonArrayToList(s.getAsJsonArray("intro_anim")),
                        jsonArrayToList(s.getAsJsonArray("slow_anim")),
                        jsonArrayToList(s.getAsJsonArray("fast_anim")),
                        s.get("cum_anim").getAsString(),
                        s.get("cum_threshold").getAsFloat(),
                        needsStrip,
                        s.has("use_keyframe") && s.get("use_keyframe").getAsBoolean()
                );
        }
    }


    private static List<String> jsonArrayToList(JsonArray array) {
        List<String> list = new ArrayList<>();
        if (array != null) {
            for (JsonElement e : array) list.add(e.getAsString());
        }
        return list;
    }
}

