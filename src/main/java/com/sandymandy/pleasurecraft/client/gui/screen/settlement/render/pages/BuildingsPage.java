package com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.pages;

import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.SettlementPage;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.settlement.building.SettlementBuilding;
import com.sandymandy.pleasurecraft.util.PleasureCraftLangUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BuildingsPage extends SettlementPage {

    private final Settlement settlement;
    private float scrollOffset = 0;
    private static final int ENTRY_HEIGHT = 20;
    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 150;

    public BuildingsPage(Settlement settlement) {
        this.settlement = settlement;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        // Draw background panel using fill
        int bgColor = 0xAA000000; // semi-transparent black
        ctx.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, bgColor);

        List<SettlementBuilding> buildings = settlement.getBuildings();
        int startY = y + 10 - (int) scrollOffset;

        // Title
        ctx.drawCenteredTextWithShadow(textRenderer, "Buildings (" + buildings.size() + ")", x + PANEL_WIDTH / 2, y + 5, 0xFFFFFF);

        // Draw entries
        for (int i = 0; i < buildings.size(); i++) {
            int entryY = startY + i * ENTRY_HEIGHT;
            if (entryY + ENTRY_HEIGHT < y + 20 || entryY > y + PANEL_HEIGHT - 10) continue; // clip out of panel

            SettlementBuilding b = buildings.get(i);
            drawBuildingEntry(ctx, b, x + 8, entryY, i % 2 == 0);
        }

        super.render(ctx, x, y);
    }

    private void drawBuildingEntry(DrawContext ctx, SettlementBuilding building, int x, int y, boolean shaded) {
        // Alternating row background (for readability)
        int rowColor = shaded ? 0x2200FFAA : 0x2200AAAA;
        ctx.fill(x - 4, y - 2, x + 200, y + ENTRY_HEIGHT - 2, rowColor);

        String typeName = PleasureCraftLangUtils.getStringFromKey(building.getBuildingType().getTranslationKey()); // e.g. "pleasurecraft:house"
        BlockPos pos = building.getDoorPos();

        ctx.drawTextWithShadow(textRenderer, Text.literal(typeName), x, y, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Door: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), x + 10, y + 10, 0xAAAAAA);
    }

//    @Override
//    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double horizontalAmount) {
//        scrollOffset = Math.max(0, scrollOffset - (float) amount * 10);
//        return true;
//    }
}
