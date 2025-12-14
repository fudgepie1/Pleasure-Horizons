package com.sandymandy.pleasurecraft.client.gui.screen;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.networking.C2S.SetGUIOpenStateC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.StartSceneC2SPacket;
import com.sandymandy.pleasurecraft.util.variables.Scene;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.List;

public class GirlSceneScreen extends Screen {
    private final int entityId;
    private final int currentRelationshipLevel;
    private final ItemStack attractedTo;
    private final List<Scene> scene;

    public GirlSceneScreen(int entityId, int currentRelationshipLevel, ItemStack attractedTo, List<Scene> scene) {
        super(Text.literal("Scene Options"));
        this.entityId = entityId;
        this.currentRelationshipLevel = currentRelationshipLevel;
        this.attractedTo = attractedTo;
        this.scene = scene;
    }

    @Override
    protected void init() {
        int y = this.height / 4;
        for (Scene scene : this.scene) {
            ButtonWidget buttonWidget = ButtonWidget.builder(Text.of(scene.displayName()), button -> {
                ClientPlayNetworking.send(new StartSceneC2SPacket(
                        this.entityId,
                        scene
                ));
                this.close();
            }).dimensions(this.width / 2 - 100, y, 200, 20).build();

            if (this.currentRelationshipLevel < scene.requiredRelationshipLevel()) {
                buttonWidget.active = false; // disables and grays out
            }

            if (!buttonWidget.active) {
                buttonWidget.setTooltip(Tooltip.of(Text.literal("Requires Relationship Level " + scene.requiredRelationshipLevel())));
            }

            this.addDrawableChild(buttonWidget);
            y += 25;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Calculate base positions
        int iconY = this.height / 4 - 30;
        int centerX = this.width / 2;

        // Draw item (attracted to) first - positioned to the left
        int itemX = centerX - 30; // 30 pixels left of center
        context.drawItem(this.attractedTo, itemX, iconY);

        // Draw relationship icon after the item
        Identifier RELATIONSHIP_ICON = Identifier.of(PleasureCraft.MOD_ID, "textures/gui/relationship_heart.png");
        int heartX = centerX - 10; // 10 pixels left of center
        context.drawTexture(RenderPipelines.GUI_TEXTURED, RELATIONSHIP_ICON, heartX, iconY, 0, 0, 18, 18, 18, 18);

        // Draw the relationship level number next to the heart
        context.drawText(MinecraftClient.getInstance().textRenderer,
                String.valueOf(currentRelationshipLevel),
                heartX + 20, iconY + 4, Colors.WHITE, true);

        if (mouseX >= itemX && mouseX <= itemX + 16 && mouseY >= iconY && mouseY <= iconY + 16) {
            // Draw tooltip with item name
            context.drawTooltip(MinecraftClient.getInstance().textRenderer,
                    this.attractedTo.getName(),
                    mouseX, mouseY);
        }
    }

    @Override
    public void close() {
        super.close();
        ClientPlayNetworking.send(new SetGUIOpenStateC2SPacket(this.entityId,false));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}