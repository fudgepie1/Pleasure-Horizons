package com.sandymandy.pleasurecraft.util.variables;

import net.minecraft.item.Item;

import java.util.List;

public record JsonGirlProfile(
        String id,
        int guiSize,
        float guiYOffset,
        Item tameItem,
        double maxHealth,
        double movementSpeed,
        double attackDamage,
        List<SceneOptions> scenes
) {}
