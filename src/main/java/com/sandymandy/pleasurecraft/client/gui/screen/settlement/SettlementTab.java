package com.sandymandy.pleasurecraft.client.gui.screen.settlement;

import com.google.common.collect.Lists;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.SettlementRenderable;
import com.sandymandy.pleasurecraft.settlement.SettlementDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SettlementTab {
    private final MinecraftClient client;
    private final SettlementHubScreen screen;
    private final SettlementTabType type;
    private final int index;
    private final SettlementDisplay display;
    private final ItemStack icon;
    private final Text title;

    private final List<SettlementRenderable> renderables = Lists.newArrayList();

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

    // === Modular Renderable System ===
    public SettlementTab addRenderable(SettlementRenderable renderable) {
        this.renderables.add(renderable);
        return this;
    }

    public List<SettlementRenderable> getRenderables() {
        return renderables;
    }

    // === Rendering ===
    public void render(DrawContext context, int x, int y) {
        if (!this.initialized) {
            this.originX = 117 - (this.maxPanX + this.minPanX) / 2;
            this.originY = 56 - (this.maxPanY + this.minPanY) / 2;
            this.initialized = true;
        }

        context.enableScissor(x, y, x + 234, y + 113);
        context.getMatrices().push();
        context.getMatrices().translate((float)x, (float)y, 0.0F);

        Identifier bgTex = display.getBackground();

        int i = MathHelper.floor(this.originX);
        int j = MathHelper.floor(this.originY);
        int offsetX = i % 16;
        int offsetY = j % 16;

        // Draw tiled background
        for (int m = -1; m <= 15; m++) {
            for (int n = -1; n <= 8; n++) {
                context.drawTexture(RenderLayer::getGuiTextured, bgTex,
                        offsetX + 16 * m, offsetY + 16 * n,
                        0, 0, 16, 16, 16, 16);
            }
        }

        // Render all interactive components
        for (SettlementRenderable renderable : renderables) {
            renderable.render(context, 0, 0);
        }

        context.getMatrices().pop();
        context.disableScissor();
    }

    // === Interaction ===
    public void tick() {
        for (SettlementRenderable renderable : renderables) {
            renderable.tick();
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (SettlementRenderable renderable : renderables) {
            if (renderable.isMouseOver(mouseX, mouseY)) {
                renderable.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    // === Utility ===
    public void move(double dx, double dy) {
        if (this.maxPanX - this.minPanX > 234) {
            this.originX = MathHelper.clamp(this.originX + dx, -(this.maxPanX - 234), 0.0);
        }
        if (this.maxPanY - this.minPanY > 113) {
            this.originY = MathHelper.clamp(this.originY + dy, -(this.maxPanY - 113), 0.0);
        }
    }

    // === Draw Tab Button Background and Icon ===
    public void drawBackground(DrawContext context, int x, int y, boolean selected) {
        type.drawBackground(context, x, y, selected, index);
    }

    public void drawIcon(DrawContext context, int x, int y) {
        type.drawIcon(context, x, y, index, icon);
    }

    public SettlementTabType getType() { return this.type; }
    public Text getTitle() { return this.title; }
    public boolean isClickOnTab(int baseX, int baseY, double mouseX, double mouseY) {
        return type.isClickOnTab(baseX, baseY, index, mouseX, mouseY);
    }

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
