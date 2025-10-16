package com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets;

import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.SettlementRenderable;
import net.minecraft.client.gui.DrawContext;

public class ProgressBarComponent implements SettlementRenderable {
    private final int x, y, width, height;
    private final double current, max;

    public ProgressBarComponent(int x, int y, int width, int height, double current, double max) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.current = current;
        this.max = max;
    }

    @Override
    public void render(DrawContext context, int offsetX, int offsetY) {
        double progress = max > 0 ? (current / max) : 0f;
        progress = Math.min(progress, 1f);
        int filled = (int) (width * progress);

        context.fill(offsetX + x, offsetY + y, offsetX + x + width, offsetY + y + height, 0xFF555555);
        context.fill(offsetX + x, offsetY + y, offsetX + x + filled, offsetY + y + height, 0xFF00AA00);
    }
}
