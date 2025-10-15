package com.sandymandy.pleasurecraft.client.gui.screen.settlement;

import com.google.common.collect.Maps;
import com.sandymandy.pleasurecraft.settlement.SettlementDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class SettlementTab {
    private final MinecraftClient client;
    private final SettlementHubScreen screen;
    private final SettlementTabType type;
    private final int index;
    private final SettlementDisplay display;
    private final ItemStack icon;
    private final Text title;
    private final Map<String, Object> widgets = Maps.newLinkedHashMap(); // placeholder for future node widgets

    private double originX;
    private double originY;
    private int minPanX = Integer.MAX_VALUE;
    private int minPanY = Integer.MAX_VALUE;
    private int maxPanX = Integer.MIN_VALUE;
    private int maxPanY = Integer.MIN_VALUE;
    private float alpha;
    private boolean initialized;

    public SettlementTab(MinecraftClient client, SettlementHubScreen screen, SettlementTabType type, int index, SettlementDisplay display) {
        this.client = client;
        this.screen = screen;
        this.type = type;
        this.index = index;
        this.display = display;
        this.icon = display.getIcon();
        this.title = display.getTitle();
    }

    // === Basic Accessors ===
    public SettlementTabType getType() { return this.type; }
    public int getIndex() { return this.index; }
    public SettlementDisplay getDisplay() { return this.display; }
    public Text getTitle() { return this.title; }

    // === Draw Tab Button Background and Icon ===
    public void drawBackground(DrawContext context, int x, int y, boolean selected) {
        type.drawBackground(context, x, y, selected, index);
    }

    public void drawIcon(DrawContext context, int x, int y) {
        type.drawIcon(context, x, y, index, icon);
    }

    public boolean isClickOnTab(int baseX, int baseY, double mouseX, double mouseY) {
        return type.isClickOnTab(baseX, baseY, index, mouseX, mouseY);
    }

    // === Core Render Logic (Background + Widgets) ===
    public void render(DrawContext context, int x, int y) {
        if (!this.initialized) {
            this.originX = 117 - (this.maxPanX + this.minPanX) / 2;
            this.originY = 56 - (this.maxPanY + this.minPanY) / 2;
            this.initialized = true;
        }

        context.enableScissor(x, y, x + 234, y + 113);
        context.getMatrices().push();
        context.getMatrices().translate((float)x, (float)y, 0.0F);

        Identifier bgTex = display.getBackground().texturePath();

        int i = MathHelper.floor(this.originX);
        int j = MathHelper.floor(this.originY);
        int offsetX = i % 16;
        int offsetY = j % 16;

        // Draw tiled background (like advancement pages)
        for (int m = -1; m <= 15; m++) {
            for (int n = -1; n <= 8; n++) {
                context.drawTexture(RenderLayer::getGuiTextured, bgTex,
                        offsetX + 16 * m, offsetY + 16 * n,
                        0, 0, 16, 16, 16, 16);
            }
        }

        // Example placeholder rendering for tab content
        context.drawCenteredTextWithShadow(client.textRenderer,
                display.getDescription().getString(),
                117, 56, 0xFFFFFF);

        context.getMatrices().pop();
        context.disableScissor();
    }

    // === Tooltip/Alpha Background Handling ===
    public void drawTooltipArea(DrawContext context, int mouseX, int mouseY, int baseX, int baseY) {
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, -200.0F);
        context.fill(0, 0, 234, 113, MathHelper.floor(this.alpha * 255.0F) << 24);

        boolean hovered = false;
        int i = MathHelper.floor(this.originX);
        int j = MathHelper.floor(this.originY);

        // In the future: check widgets for hover tooltip display
        if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113) {
            hovered = true;
        }

        context.getMatrices().pop();
        if (hovered) {
            this.alpha = MathHelper.clamp(this.alpha + 0.02F, 0.0F, 0.3F);
        } else {
            this.alpha = MathHelper.clamp(this.alpha - 0.04F, 0.0F, 1.0F);
        }
    }

    // === Pan / Move ===
    public void move(double dx, double dy) {
        if (this.maxPanX - this.minPanX > 234) {
            this.originX = MathHelper.clamp(this.originX + dx, -(this.maxPanX - 234), 0.0);
        }
        if (this.maxPanY - this.minPanY > 113) {
            this.originY = MathHelper.clamp(this.originY + dy, -(this.maxPanY - 113), 0.0);
        }
    }

    // === Static Factory ===
    public static SettlementTab create(MinecraftClient client, SettlementHubScreen screen, int index, SettlementDisplay display) {
        for (SettlementTabType tabType : SettlementTabType.values()) {
            if (index < tabType.getTabCount()) {
                return new SettlementTab(client, screen, tabType, index, display);
            }
            index -= tabType.getTabCount();
        }
        return null;
    }
}
