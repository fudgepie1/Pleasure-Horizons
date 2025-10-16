package com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets;

import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.SettlementRenderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class LabelComponent implements SettlementRenderable {
    private final int x, y;
    private final Text text;
    private final int color;

    public LabelComponent(int x, int y, Text text) {
        this(x, y, text, 0xFFFFFF);
    }

    public LabelComponent(int x, int y, Text text, int color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }

    @Override
    public void render(DrawContext context, int offsetX, int offsetY) {
        context.drawText(MinecraftClient.getInstance().textRenderer, text, offsetX + x, offsetY + y, color, false);
    }
}
