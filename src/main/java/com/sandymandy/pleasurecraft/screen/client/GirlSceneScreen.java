package com.sandymandy.pleasurecraft.screen.client;

import com.sandymandy.pleasurecraft.networking.C2S.InInventoryC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.StartSceneC2SPacket;
import com.sandymandy.pleasurecraft.screen.InventoryButtonAction;
import com.sandymandy.pleasurecraft.util.SceneOptions;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class GirlSceneScreen extends Screen {
    private final int entityId;
    private final int currentRelationshipLevel;
    private final List<SceneOptions> sceneOptions;

    public GirlSceneScreen(int entityId, int currentRelationshipLevel, List<SceneOptions> sceneOptions) {
        super(Text.literal("Scene Options"));
        this.entityId = entityId;
        this.currentRelationshipLevel = currentRelationshipLevel;
        this.sceneOptions = sceneOptions;
    }

    @Override
    protected void init() {
        int y = this.height / 4;
        for (SceneOptions sceneOptions : sceneOptions) {
            ButtonWidget buttonWidget = ButtonWidget.builder(Text.of(sceneOptions.name()), button -> {
                ClientPlayNetworking.send(new StartSceneC2SPacket(
                        this.entityId,
                        sceneOptions
                ));
                MinecraftClient.getInstance().setScreen(null); // close after sending
            }).dimensions(this.width / 2 - 100, y, 200, 20).build();

            if (this.currentRelationshipLevel < sceneOptions.requiredRelationshipLevel()) {
                buttonWidget.active = false; // disables and grays out
            }

            if (!buttonWidget.active) {
                buttonWidget.setTooltip(Tooltip.of(Text.literal("Requires relationship level " + sceneOptions.requiredRelationshipLevel())));
            }

            this.addDrawableChild(buttonWidget);
            y += 25;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int alpha = 120; // adjust blur opacity
        context.fillGradient(alpha, 0, 0, this.height, this.width, 0xAA000000, 0xAA000000);
        super.render(context, mouseX, mouseY, delta);
    }

}
