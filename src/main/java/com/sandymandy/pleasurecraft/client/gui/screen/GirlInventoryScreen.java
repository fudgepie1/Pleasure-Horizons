package com.sandymandy.pleasurecraft.client.gui.screen;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.GirlEntity;
import com.sandymandy.pleasurecraft.entity.base.TameableGirlEntity;
import com.sandymandy.pleasurecraft.networking.C2S.InInventoryC2SPacket;
import com.sandymandy.pleasurecraft.registries.InventoryButtonRegistry;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandler;
import com.sandymandy.pleasurecraft.screen.InventoryButtonAction;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class GirlInventoryScreen extends HandledScreen<GirlInventoryScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(PleasureCraft.MOD_ID, "/textures/gui/inventory.png");
    private float xMouse;
    private float yMouse;
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 170;
    private final TameableGirlEntity girl;
    private final PlayerEntity player;



    public GirlInventoryScreen(GirlInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.girl = handler.getGirl();
        this.player = inventory.player;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int alpha = 120; // adjust blur opacity
        super.render(context, mouseX, mouseY, delta);
//        drawMouseoverTooltip(context,mouseX,mouseY);

    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        //Stops the container names from rendering
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int centerX = (width - GUI_WIDTH) / 2;
        int centerY = (height - GUI_HEIGHT) / 2;
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX, centerY, 0, 0, GUI_WIDTH, GUI_HEIGHT, GUI_WIDTH, GUI_HEIGHT);
        InventoryScreen.drawEntity(context, i + 26, j + 8, i + 75, j + 78, this.girl.getSizeGUI(), this.girl.getYAxisGUI(), mouseX, mouseY, this.girl);

        int relLevel = girl.getCurrentRelationshipLevel();


        Identifier HEALTH_BOOST_ICON = Identifier.of(PleasureCraft.MOD_ID, "textures/gui/relationship_heart.png");

        // pick position relative to GUI
        int iconX = centerX;  // adjust position
        int iconY = centerY - 20;

        // draw the effect texture (assumes 18x18 size like vanilla)
        context.drawTexture(RenderPipelines.GUI_TEXTURED, HEALTH_BOOST_ICON,
                iconX, iconY, 0, 0, 18, 18, 18, 18);

        // draw the number next to it
        context.drawText(this.textRenderer, Text.literal(String.valueOf(relLevel)),
                iconX + 20, iconY + 5, Colors.WHITE, true);

    }

    @Override
    public void close() {
        super.close();
        ClientPlayNetworking.send(new InInventoryC2SPacket(this.girl.getId(),false));
    }

    private void drawButton(Text label, InventoryButtonAction action, int x, int y, int buttonWidth, int buttonHeight){

        ButtonWidget button = ButtonWidget.builder(
                label,
                btn -> {
                    if (girl != null && client != null && player != null) {
                        action.action().accept(girl, player);  // Run the button's logic
                        this.client.setScreen(null);
                        ClientPlayNetworking.send(new InInventoryC2SPacket(this.girl.getId(),false));
                    }
                }
        ).dimensions(x, y, buttonWidth, buttonHeight).build();

        if (girl.getCurrentRelationshipLevel() < action.requiredRelationshipLevel()) {
            button.active = false; // disables and grays out
        }

        if (!button.active) {
            button.setTooltip(Tooltip.of(Text.literal("Requires Relationship Level " + action.requiredRelationshipLevel())));
        }

        this.addDrawableChild(button);
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;

        int buttonHeight = 22;
        int buttonWidth = 80;

        int paddingX = 10;
        int paddingY = 4;

        int startX = centerX - (buttonWidth + paddingX);
        int startY = centerY + 15;

        if (girl.isTamed()){
            for (int i = 0; i < InventoryButtonRegistry.BUTTONS_LEFT.size(); i++) {
                InventoryButtonAction action = InventoryButtonRegistry.BUTTONS_LEFT.get(i);
                int y = startY + i * (buttonHeight + paddingY);
                Text dynamicLabel = action.label();



                this.drawButton(dynamicLabel, action, startX, y, buttonWidth, buttonHeight);
            }

            for (int i = 0; i < InventoryButtonRegistry.BUTTONS_RIGHT.size(); i++) {
                InventoryButtonAction action = InventoryButtonRegistry.BUTTONS_RIGHT.get(i);
                int y = startY + i * (buttonHeight + paddingY);
                Text dynamicLabel = action.label();

                if (action.label().getString().equals("Sit") && girl.isSitting()){
                    dynamicLabel = Text.literal("Stand");
                }
                else if (action.label().getString().equals("Follow Me") && girl.isFollowing()){
                    dynamicLabel = Text.literal("Stop Following");
                }

                if (action.label().getString().equals("Strip") && girl.isStripped()) {
                    dynamicLabel = Text.literal("Dress Up");
                }

                this.drawButton(dynamicLabel, action, centerX + 176 + paddingX, y, buttonWidth, buttonHeight);
            }
        }

    }

}
