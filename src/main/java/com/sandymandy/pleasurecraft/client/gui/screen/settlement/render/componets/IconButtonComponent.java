package com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.SettlementRenderable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;

public class IconButtonComponent implements SettlementRenderable {
    private final int x, y;
    private final ItemStack icon;
    private final Consumer<IconButtonComponent> onClick;

    public IconButtonComponent(int x, int y, ItemStack icon, Consumer<IconButtonComponent> onClick) {
        this.x = x;
        this.y = y;
        this.icon = icon;
        this.onClick = onClick;
    }

    @Override
    public void render(DrawContext context, int offsetX, int offsetY) {
        context.drawItem(icon, offsetX + x, offsetY + y);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (onClick != null) {
            onClick.accept(this);
        }
    }
}
