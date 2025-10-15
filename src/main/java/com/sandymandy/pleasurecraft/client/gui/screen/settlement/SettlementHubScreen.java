package com.sandymandy.pleasurecraft.client.gui.screen.settlement;

import com.sandymandy.pleasurecraft.screen.SettlementHubScreenHandler;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.settlement.SettlementResourceData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class SettlementHubScreen extends HandledScreen<SettlementHubScreenHandler> {
    private static final Identifier WINDOW_TEXTURE = Identifier.ofVanilla("textures/gui/advancements/window.png");

    private final Settlement data;
    private final Map<String, SettlementTab> tabs = new LinkedHashMap<>();
    private SettlementTab selectedTab;

    private static final int WINDOW_WIDTH = 252;
    private static final int WINDOW_HEIGHT = 140;
    private static final int PAGE_X = 9;
    private static final int PAGE_Y = 18;

    private boolean dragging = false;
    private double lastMouseX;
    private double lastMouseY;

    public SettlementHubScreen(SettlementHubScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.data = handler.getData();
    }

    @Override
    protected void init() {
        super.init();

        tabs.clear();
        tabs.put("resources", new SettlementTab("Resources", data));
        tabs.put("morale", new SettlementTab("Morale", data));
        tabs.put("buildings", new SettlementTab("Buildings", data));
        tabs.put("population", new SettlementTab("Population", data));

        selectedTab = tabs.get("resources");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = (width - WINDOW_WIDTH) / 2;
        int y = (height - WINDOW_HEIGHT) / 2;

        drawWindow(context, x, y);
        drawTabs(context, x, y, mouseX, mouseY);

        if (selectedTab != null) {
            selectedTab.render(context, x + PAGE_X, y + PAGE_Y, mouseX, mouseY, delta);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawWindow(DrawContext context, int x, int y) {
        context.drawTexture(RenderLayer::getGuiTextured, WINDOW_TEXTURE, x, y, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 256, 256);
        context.drawText(textRenderer, selectedTab != null ? Text.literal(selectedTab.getTitle()) : title, x + 8, y + 6, 0x404040, false);
    }

    private void drawTabs(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int i = 0;
        for (SettlementTab tab : tabs.values()) {
            boolean selected = tab == selectedTab;
            tab.drawTabButton(context, x + 10 + (i * 30), y - 24, selected, mouseX, mouseY);
            i++;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - WINDOW_WIDTH) / 2;
        int y = (height - WINDOW_HEIGHT) / 2;

        int i = 0;
        for (SettlementTab tab : tabs.values()) {
            int tabX = x + 10 + (i * 30);
            int tabY = y - 24;
            if (tab.isClicked(mouseX, mouseY, tabX, tabY)) {
                this.selectedTab = tab;
                return true;
            }
            i++;
        }

        if (selectedTab != null && button == 0) {
            this.dragging = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && selectedTab != null) {
            double dx = mouseX - lastMouseX;
            double dy = mouseY - lastMouseY;
            selectedTab.scroll(-dx, -dy);
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedTab != null) {
            selectedTab.scroll(horizontalAmount * 16.0, verticalAmount * 16.0);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {

    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}
