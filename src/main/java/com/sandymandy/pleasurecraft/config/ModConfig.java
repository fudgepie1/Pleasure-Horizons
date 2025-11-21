package com.sandymandy.pleasurecraft.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

@Config(name = "pleasurecraft")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        ConfigHolder<ModConfig> holder = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(ModConfig.class);
        INSTANCE = holder.getConfig();
    }

    @ConfigEntry.Gui.CollapsibleObject
    public GirlConfig girls = new GirlConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public PlayerConfig player = new PlayerConfig();


    public static class GirlConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean boobWindow = false;

//        @ConfigEntry.Gui.Tooltip
//        public boolean applyShading = false;
    }

    public static class PlayerConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int penisShaftColor = 0xF5C6A5;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int penisHeadColor = 0xF5A7A9;
    }
}
