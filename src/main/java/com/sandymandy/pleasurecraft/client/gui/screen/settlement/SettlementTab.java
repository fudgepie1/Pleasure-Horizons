package com.sandymandy.pleasurecraft.client.gui.screen.settlement;

import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.settlement.SettlementResourceData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class SettlementTab {
    private static final Identifier TAB_TEXTURE = Identifier.ofVanilla("textures/gui/advancements/tab.png");
    private final String title;
    private final Settlement data;

    private double scrollX = 0;
    private double scrollY = 0;

    public SettlementTab(String title, Settlement data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
        // Example content — replace with your own logic
        context.drawText(MinecraftClient.getInstance().textRenderer, title + " Content Here", x + 10, y + 10, 0xFFFFFF, false);

        // You could render a scrollable list here using scrollY offset
    }

    public void drawTabButton(DrawContext context, int x, int y, boolean selected, int mouseX, int mouseY) {
        int color = selected ? 0xFFFFFFFF : 0xFFAAAAAA;
        context.fill(x, y, x + 28, y + 20, color);
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, title.substring(0, 1), x + 14, y + 6, 0x000000);
    }

    public boolean isClicked(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX <= x + 28 && mouseY >= y && mouseY <= y + 20;
    }

    public void scroll(double dx, double dy) {
        this.scrollX += dx;
        this.scrollY += dy;
    }
}