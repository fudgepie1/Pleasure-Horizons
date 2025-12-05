package com.sandymandy.pleasurecraft.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import com.sandymandy.pleasurecraft.config.gui.AutoConfigExtensions;
import com.sandymandy.pleasurecraft.config.gui.BoundedContinuous;
import com.sandymandy.pleasurecraft.config.gui.ModBindingsConfig;
import org.jetbrains.annotations.NotNull;

@Config(name = "pleasurecraft")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        ConfigHolder<ModConfig> holder = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        AutoConfigExtensions.apply(ModConfig.class);
        INSTANCE = holder.getConfig();
    }

    @ConfigEntry.Gui.CollapsibleObject
    public GirlConfig girls = new GirlConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public PlayerConfig player = new PlayerConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public KeybindConfig keybinds = new KeybindConfig();

    public static class GirlConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean boobWindow = false;

        @ConfigEntry.Gui.Tooltip
        public boolean disableShading = false;
    }

    public static class PlayerConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int penisShaftColor = 0xF5C6A5;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int penisHeadColor = 0xF5A7A9;
    }

    public static class KeybindConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean holdThrust = true;
    }

    @ConfigEntry.Category("freecam")
    @ConfigEntry.Gui.CollapsibleObject
    public ControlsConfig controls = new ControlsConfig();

    @ConfigEntry.Category("freecam")
    @ConfigEntry.Gui.CollapsibleObject
    public MovementConfig movement = new MovementConfig();

    @ConfigEntry.Category("freecam")
    @ConfigEntry.Gui.CollapsibleObject
    public VisualConfig visual = new VisualConfig();

    @ConfigEntry.Category("freecam")
    @ConfigEntry.Gui.CollapsibleObject
    public UtilityConfig utility = new UtilityConfig();

    @ConfigEntry.Category("freecam")
    @ConfigEntry.Gui.CollapsibleObject
    public NotificationConfig notification = new NotificationConfig();

    public static class ControlsConfig {
        @ModBindingsConfig
        private Object keys;
    }

    public static class MovementConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public FlightMode flightMode = FlightMode.DEFAULT;

        @ConfigEntry.Gui.Tooltip
        @BoundedContinuous(max = 10)
        public double horizontalSpeed = 1.0;

        @ConfigEntry.Gui.Tooltip
        @BoundedContinuous(max = 10)
        public double verticalSpeed = 1.0;
    }

    public static class VisualConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public Perspective perspective = Perspective.INSIDE;

        @ConfigEntry.Gui.Tooltip
        public boolean showPlayer = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showHand = false;

        @ConfigEntry.Gui.Tooltip
        public boolean fullBright = false;

        @ConfigEntry.Gui.Tooltip
        public boolean showSubmersion = false;
    }

    public static class UtilityConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean disableOnDamage = true;

        public boolean freezePlayer = false;

        public boolean allowInteract = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public InteractionMode interactionMode = InteractionMode.CAMERA;
    }

    public static class NotificationConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean notifyFreecam = true;

        @ConfigEntry.Gui.Tooltip
        public boolean notifyTripod = true;
    }

    public enum FlightMode implements SelectionListEntry.Translatable {
        CREATIVE("creative"),
        DEFAULT("default");

        private final String key;

        FlightMode(String name) {
            this.key = "text.autoconfig.pleasurecraft.option.movement.flightMode." + name;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }
    }

    public enum InteractionMode implements SelectionListEntry.Translatable {
        CAMERA("camera"),
        PLAYER("player");

        private final String key;

        InteractionMode(String name) {
            this.key = "text.autoconfig.pleasurecraft.option.utility.interactionMode." + name;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }
    }

    public enum Perspective implements SelectionListEntry.Translatable {
        FIRST_PERSON("firstPerson"),
        THIRD_PERSON("thirdPerson"),
        THIRD_PERSON_MIRROR("thirdPersonMirror"),
        INSIDE("inside");

        private final String key;

        Perspective(String name) {
            this.key = "text.autoconfig.pleasurecraft.option.visual.perspective." + name;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }
    }
}