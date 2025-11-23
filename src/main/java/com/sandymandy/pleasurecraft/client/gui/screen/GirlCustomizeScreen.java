package com.sandymandy.pleasurecraft.client.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GirlCustomizeScreen extends Screen {
    private final int entityId;

    public GirlCustomizeScreen(int entityId) {
        super(Text.literal("Scene Options"));
        this.entityId = entityId;
    }
}
