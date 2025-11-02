package com.sandymandy.pleasurecraft.util.json;

import com.sandymandy.pleasurecraft.util.variables.JsonGirlProfile;
import net.minecraft.item.Items;

import java.util.List;

public class JsonGirlProfiles {
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
