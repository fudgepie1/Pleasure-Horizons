package com.sandymandy.pleasurecraft.client.gui.screen.settlement.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class SettlementPage implements SettlementRenderable {
    protected final List<SettlementRenderable> components = new ArrayList<>();
    public final MinecraftClient client = MinecraftClient.getInstance();
    public final TextRenderer textRenderer = client.textRenderer;

    public SettlementPage addComponent(SettlementRenderable component) {
        components.add(component);
        return this;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        for (SettlementRenderable comp : components) {
            comp.render(ctx, x, y);
        }
    }
}