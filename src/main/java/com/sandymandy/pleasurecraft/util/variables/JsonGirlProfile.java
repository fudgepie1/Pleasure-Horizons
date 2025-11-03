package com.sandymandy.pleasurecraft.util.variables;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.List;

public record JsonGirlProfile(
        String id,
        String name,
        int guiSize,
        float guiYOffset,
        Item tameItem,
        double maxHealth,
        double movementSpeed,
        double attackDamage,
        List<SceneOptions> scenes
) {
    public static final Codec<JsonGirlProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JsonGirlProfile::id),
            Codec.STRING.fieldOf("name").forGetter(JsonGirlProfile::name),
            Codec.INT.fieldOf("guiSize").forGetter(JsonGirlProfile::guiSize),
            Codec.FLOAT.fieldOf("guiYOffset").forGetter(JsonGirlProfile::guiYOffset),
            Registries.ITEM.getCodec().fieldOf("tameItem").forGetter(JsonGirlProfile::tameItem),
            Codec.DOUBLE.fieldOf("maxHealth").forGetter(JsonGirlProfile::maxHealth),
            Codec.DOUBLE.fieldOf("movementSpeed").forGetter(JsonGirlProfile::movementSpeed),
            Codec.DOUBLE.fieldOf("attackDamage").forGetter(JsonGirlProfile::attackDamage),
            SceneOptions.CODEC.listOf().fieldOf("scenes").forGetter(JsonGirlProfile::scenes)
    ).apply(instance, JsonGirlProfile::new));

    public static final JsonGirlProfile DEFAULT = new JsonGirlProfile(
            "default",
            "Default Json Girl",
            30,                   // gui size
            0.05f,                // gui offset
            Items.APPLE,          // tame item
            20.0,                 // health
            0.20,                 // speed
            2.0,                  // damage
            List.of()             // scenes
    );
}
