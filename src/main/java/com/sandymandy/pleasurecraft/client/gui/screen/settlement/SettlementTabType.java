package com.sandymandy.pleasurecraft.client.gui.screen.settlement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public enum SettlementTabType {
    ABOVE(
            new Textures(
                    Identifier.ofVanilla("advancements/tab_above_left_selected"),
                    Identifier.ofVanilla("advancements/tab_above_middle_selected"),
                    Identifier.ofVanilla("advancements/tab_above_right_selected")
            ),
            new Textures(
                    Identifier.ofVanilla("advancements/tab_above_left"),
                    Identifier.ofVanilla("advancements/tab_above_middle"),
                    Identifier.ofVanilla("advancements/tab_above_right")
            ),
            28,
            32,
            8
    ),
    BELOW(
            new Textures(
                    Identifier.ofVanilla("advancements/tab_below_left_selected"),
                    Identifier.ofVanilla("advancements/tab_below_middle_selected"),
                    Identifier.ofVanilla("advancements/tab_below_right_selected")
            ),
            new Textures(
                    Identifier.ofVanilla("advancements/tab_below_left"),
                    Identifier.ofVanilla("advancements/tab_below_middle"),
                    Identifier.ofVanilla("advancements/tab_below_right")
            ),
            28,
            32,
            8
    ),
    LEFT(
            new Textures(
                    Identifier.ofVanilla("advancements/tab_left_top_selected"),
                    Identifier.ofVanilla("advancements/tab_left_middle_selected"),
                    Identifier.ofVanilla("advancements/tab_left_bottom_selected")
            ),
            new Textures(
                    Identifier.ofVanilla("advancements/tab_left_top"),
                    Identifier.ofVanilla("advancements/tab_left_middle"),
                    Identifier.ofVanilla("advancements/tab_left_bottom")
            ),
            32,
            28,
            5
    ),
    RIGHT(
            new Textures(
                    Identifier.ofVanilla("advancements/tab_right_top_selected"),
                    Identifier.ofVanilla("advancements/tab_right_middle_selected"),
                    Identifier.ofVanilla("advancements/tab_right_bottom_selected")
            ),
            new Textures(
                    Identifier.ofVanilla("advancements/tab_right_top"),
                    Identifier.ofVanilla("advancements/tab_right_middle"),
                    Identifier.ofVanilla("advancements/tab_right_bottom")
            ),
            32,
            28,
            5
    );


    private final Textures selected;
    private final Textures unselected;
    private final int width;
    private final int height;
    private final int tabCount;

    SettlementTabType(Textures selected, Textures unselected, int width, int height, int tabCount) {
        this.selected = selected;
        this.unselected = unselected;
        this.width = width;
        this.height = height;
        this.tabCount = tabCount;
    }

    public int getTabCount() {
        return this.tabCount;
    }

    public void drawBackground(DrawContext context, int x, int y, boolean selected, int index) {
        Textures tex = selected ? this.selected : this.unselected;
        Identifier texture;
        if (index == 0) texture = tex.first();
        else if (index == tabCount - 1) texture = tex.last();
        else texture = tex.middle();

        context.drawGuiTexture(RenderLayer::getGuiTextured, texture, x + getTabX(index), y + getTabY(index), width, height);
    }

    public void drawIcon(DrawContext context, int x, int y, int index, ItemStack icon) {
        int i = x + getTabX(index);
        int j = y + getTabY(index);
        switch (this) {
            case ABOVE -> { i += 6; j += 9; }
            case BELOW -> { i += 6; j += 6; }
        }
        context.drawItemWithoutEntity(icon, i, j);
    }

    public int getTabX(int index) {
        return (width + 4) * index;
    }

    public int getTabY(int index) {
        return this == ABOVE ? -height + 4 : 136;
    }

    public boolean isClickOnTab(int baseX, int baseY, int index, double mouseX, double mouseY) {
        int i = baseX + getTabX(index);
        int j = baseY + getTabY(index);
        return mouseX > i && mouseX < i + width && mouseY > j && mouseY < j + height;
    }

    @Environment(EnvType.CLIENT)
    public record Textures(Identifier first, Identifier middle, Identifier last) {}
}
