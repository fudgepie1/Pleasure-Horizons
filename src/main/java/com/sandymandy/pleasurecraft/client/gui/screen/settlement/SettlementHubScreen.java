package com.sandymandy.pleasurecraft.client.gui.screen.settlement;

import com.google.common.collect.Maps;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets.IconButtonComponent;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets.LabelComponent;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets.ProgressBarComponent;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.pages.BuildingsPage;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.pages.ResourcePage;
import com.sandymandy.pleasurecraft.screen.SettlementHubScreenHandler;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.settlement.SettlementDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class SettlementHubScreen extends HandledScreen<SettlementHubScreenHandler> {
    private static final Identifier WINDOW_TEXTURE = Identifier.ofVanilla("textures/gui/advancements/window.png");
    private static final int WINDOW_WIDTH = 252;
    private static final int WINDOW_HEIGHT = 140;
    private static final int PAGE_X = 9;
    private static final int PAGE_Y = 18;
    private static final int PAGE_WIDTH = 234;
    private static final int PAGE_HEIGHT = 113;
    private static final int TITLE_X = 8;
    private static final int TITLE_Y = 6;

    private final Settlement data;
    private final Map<String, SettlementTab> tabs = Maps.newLinkedHashMap();
    @Nullable
    private SettlementTab selectedTab;
    private boolean movingTab;

    public SettlementHubScreen(SettlementHubScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.data = handler.getData();
    }

    @Override
    protected void init() {
        super.init();
        tabs.clear();
        selectedTab = null;

        // Automatically handles index
        addTab("resources", SettlementDisplay.ofBasic(Text.literal("Resources"), Text.literal("Resource overview")))
                .addRenderable( new ResourcePage());

        addTab("storage", SettlementDisplay.ofBasic(Text.literal("Storage"), Text.literal("Stored resources")))
                .addRenderable(new LabelComponent(10, 10, Text.literal("Resources")))
                .addRenderable(new ProgressBarComponent(10, 25, 120, 8, 1,2))
                .addRenderable(new IconButtonComponent(150, 20, new ItemStack(Items.CHEST),
                        btn -> client.player.sendMessage(Text.literal("Opened storage!"), false)));

        addTab("buildings", SettlementDisplay.ofBasic(Text.literal("Buildings"), Text.literal("Resource overview")))
                /*.addRenderable( new BuildingsPage(data))*/;

        // Select first tab automatically
        if (!tabs.isEmpty()) selectedTab = tabs.values().iterator().next();
    }


    private SettlementTab addTab(String id, SettlementDisplay display) {
        int index = tabs.size(); // auto-index based on tab order
        SettlementTab tab = SettlementTab.create(client, this, index, display);

        if (tab != null) {
            tabs.put(id, tab);
        }

        return tab; // return tab so you can chain .addRenderable()
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int x = (this.width - WINDOW_WIDTH) / 2;
        int y = (this.height - WINDOW_HEIGHT) / 2;

        // Draw the background texture + tab buttons
        drawWindow(context, x, y);

        // Draw the current tab contents
        drawTabPage(context, x, y, mouseX, mouseY);

        // Draw tab tooltips if hovered
        drawTabTooltips(context, x, y, mouseX, mouseY);

    }

    private void drawTabPage(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (selectedTab == null) {
            // No tabs = draw empty message
            context.fill(x + PAGE_X, y + PAGE_Y, x + PAGE_X + PAGE_WIDTH, y + PAGE_Y + PAGE_HEIGHT, Colors.BLACK);
            int centerX = x + PAGE_X + PAGE_WIDTH / 2;
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("No Settlement Data"), centerX, y + PAGE_Y + 40, Colors.WHITE);
            return;
        }

        // Draw tab content
        selectedTab.render(context, x + PAGE_X, y + PAGE_Y);
    }

    private void drawWindow(DrawContext context, int x, int y) {
        context.drawTexture(RenderLayer::getGuiTextured, WINDOW_TEXTURE, x, y, 0.0F, 0.0F, WINDOW_WIDTH, WINDOW_HEIGHT, 256, 256);

        if (tabs.size() > 1) {
            for (SettlementTab tab : tabs.values()) {
                tab.drawBackground(context, x, y, tab == selectedTab);
            }

            for (SettlementTab tab : tabs.values()) {
                tab.drawIcon(context, x, y);
            }
        }

        context.drawText(
                textRenderer,
                selectedTab != null ? selectedTab.getTitle() : title,
                x + TITLE_X, y + TITLE_Y,
                0x404040, false
        );
    }

    private void drawTabTooltips(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (tabs.size() > 1) {
            for (SettlementTab tab : tabs.values()) {
                if (tab.isClickOnTab(x, y, mouseX, mouseY)) {
                    context.drawTooltip(textRenderer, tab.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    // === Interaction ===
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - WINDOW_WIDTH) / 2;
        int y = (height - WINDOW_HEIGHT) / 2;

        // Handle tab switching first
        if (button == 0) {
            for (SettlementTab tab : tabs.values()) {
                if (tab.isClickOnTab(x, y, mouseX, mouseY)) {
                    this.selectedTab = tab;
                    return true;
                }
            }
        }

        // Forward mouse clicks to the current tab, but adjust coordinates
        if (selectedTab != null) {
            double localMouseX = mouseX - (x + PAGE_X);
            double localMouseY = mouseY - (y + PAGE_Y);
            selectedTab.mouseClicked(localMouseX, localMouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0) {
            this.movingTab = false;
            return false;
        } else {
            if (!this.movingTab) {
                this.movingTab = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.move(deltaX, deltaY);
            }
            return true;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedTab != null) {
            selectedTab.move(horizontalAmount * 16.0, verticalAmount * 16.0);
            return true;
        }
        return false;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        // background handled by window texture
    }

    @Override
    protected void handledScreenTick() {
        if (selectedTab != null) {
            selectedTab.tick();
        }

        PleasureCraft.LOGGER.info(data.getAllBuildings() +"");
    }
}
