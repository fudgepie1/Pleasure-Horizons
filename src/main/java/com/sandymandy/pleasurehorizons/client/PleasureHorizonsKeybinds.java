package com.sandymandy.pleasurehorizons.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PleasureHorizonsKeybinds {
    public static KeyBinding thrustKey;
    public static KeyBinding cumKey;


    public static void register() {
        thrustKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pleasurehorizons.thrust", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z, // default key
                "key.categories.pleasurehorizons" // translation category
        ));

        cumKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pleasurehorizons.cum",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "key.categories.pleasurehorizons"
        ));

  }


}
