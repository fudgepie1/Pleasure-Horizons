package com.sandymandy.pleasurecraft.client.gui.screen.settlement.render;

import net.minecraft.client.gui.DrawContext;

/**
 * A modular GUI component that can be rendered inside a SettlementTab.
 */
public interface SettlementRenderable {

    /** Draws this component inside the tab */
    void render(DrawContext context, int x, int y);

    /** Called every client tick */
    default void tick() {}

    /** Called when the mouse is clicked */
    default void mouseClicked(double mouseX, double mouseY, int button) {}

    /** Whether the mouse is over this component */
    default boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double amount, double horizontalAmount){return false;}

}
