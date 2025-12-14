package com.sandymandy.pleasurecraft.client.gui.screen;

import com.sandymandy.pleasurecraft.entity.base.GirlEntity;
import com.sandymandy.pleasurecraft.entity.girls.KoboldEntity;
import com.sandymandy.pleasurecraft.networking.C2S.KoboldCustomizeC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.RemovePreviewEntityC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.SetGUIOpenStateC2SPacket;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import com.sandymandy.pleasurecraft.util.Colors;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.entity.SpawnReason;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static com.sandymandy.pleasurecraft.util.Utils.getFormattedByUnderscore;
import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public class KoboldCustomizeScreen extends Screen {

    private final int entityId;
    private final KoboldEntity entity;
    private final KoboldEntity previewEntity;

    // Customization values
    private int bodySize;
    private int breastSize;
    private int primaryColor;
    private int secondaryColor;
    private int irisColor;
    private int topHornType;
    private int bottomHornType;

    // Scroll offset
    private double scrollOffset = 0;
    private static final int SCROLL_SPEED = 20;
    private final int maxScrollOffset = 200;

    // Preview settings
    private static final int PREVIEW_SIZE = 140;

    public KoboldCustomizeScreen(int entityId, int previewEntityId) {
        super(Text.literal("Customize Kobold"));
        this.entityId = entityId;
        // Create preview entity (client-side only)
        World world = MinecraftClient.getInstance().world;
        this.previewEntity = (KoboldEntity) world.getEntityById(previewEntityId);
        this.entity = (KoboldEntity) world.getEntityById(entityId);

        // Store current values
        this.bodySize = this.entity.getBodySize();
        this.breastSize = this.entity.getBreastSize();
        this.primaryColor = this.entity.getPrimaryColor();
        this.secondaryColor = this.entity.getSecondaryColor();
        this.irisColor = this.entity.getIrisColor();
        this.topHornType = this.entity.getTopHornType();
        this.bottomHornType = this.entity.getBottomHornType();

    }

    @Override
    protected void init() {
        // Clear previous widgets
        this.clearChildren();

        // Calculate layout dimensions
        int previewWidth = this.width / 4;
        int menuWidth = (this.width * 3) / 4;
        int menuStartX = previewWidth + 20;

        int startY = 20;
        int currentY = startY - (int)scrollOffset;
        int contentWidth = Math.min(400, menuWidth - 40); // Max 400px or fit to screen
        int centerX = menuStartX + (menuWidth - contentWidth) / 2; // Center in menu area

        // Title
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX, currentY, contentWidth, 20,
                Text.literal("§6§lKobold Customization"),
                this.textRenderer
        ));
        currentY += 30;

        // === BODY SIZE SLIDER ===
        SliderWidget bodySizeSlider = new SliderWidget(centerX, currentY, contentWidth, 20,
                Text.literal("Body Size: " + bodySize),
                (bodySize - 65f) / 50f
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Body Size: " + bodySize));
            }

            @Override
            protected void applyValue() {
                bodySize = 65 + (int)(this.value * 50);
                applyPreviewSettings();
            }
        };
        bodySizeSlider.setTooltip(Tooltip.of(Text.literal("Size affects hitbox height\n65 = 1 block, 115 = 1.75 blocks")));
        this.addDrawableChild(bodySizeSlider);
        currentY += 25;

        // === BREAST SIZE SLIDER ===
        SliderWidget breastSlider = new SliderWidget(centerX, currentY, contentWidth, 20,
                Text.literal("Breast Size: " + breastSize),
                (breastSize - 60f) / 55f
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Breast Size: " + breastSize));
            }

            @Override
            protected void applyValue() {
                breastSize = 60 + (int)(this.value * 55);
                applyPreviewSettings();
            }
        };
        breastSlider.setTooltip(Tooltip.of(Text.literal("Adjust breast size (60-115)")));
        this.addDrawableChild(breastSlider);
        currentY += 30;

        // === COLOR PRESETS ===
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX, currentY, contentWidth, 20,
                Text.literal("§eColor Pattern:"),
                this.textRenderer
        ));
        currentY += 20;

        KoboldEntity.PatternPresets[] presets = KoboldEntity.PatternPresets.values();
        int buttonWidth = (contentWidth - 5) / 2; // Two columns with 5px gap
        for (int i = 0; i < presets.length; i++) {
            KoboldEntity.PatternPresets preset = presets[i];
            int row = i / 2;
            int col = i % 2;
            int btnX = centerX + (col * (buttonWidth + 5));
            int btnY = currentY + (row * 25);

            ButtonWidget presetBtn = ButtonWidget.builder(
                    Text.literal(getFormattedByUnderscore(preset.name())),
                    button -> {
                        primaryColor = preset.primary;
                        secondaryColor = preset.secondary;
                        applyPreviewSettings();
                    }
            ).dimensions(btnX, btnY, buttonWidth, 20).build();

            this.addDrawableChild(presetBtn);
        }
        currentY += ((presets.length + 1) / 2) * 25 + 10;

        // === IRIS COLOR ===
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX, currentY, contentWidth, 20,
                Text.literal("§eIris Color:"),
                this.textRenderer
        ));
        currentY += 20;

        int[] commonColors = {
                Colors.SKY_BLUE, Colors.GREEN, Colors.RED,
                Colors.PURPLE, Colors.ORANGE, Colors.YELLOW,
                Colors.PINK, Colors.CYAN, Colors.LIME,
                Colors.WHITE, Colors.GRAY, Colors.BLACK
        };

        int colorButtonWidth = (contentWidth - 10) / 3; // Three columns with gaps
        for (int i = 0; i < commonColors.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int btnX = centerX + (col * (colorButtonWidth + 5));
            int btnY = currentY + (row * 25);
            int color = commonColors[i];

            ButtonWidget colorBtn = ButtonWidget.builder(
                    Text.literal("■").styled(style -> style.withColor(color)),
                    button -> {
                        irisColor = color;
                        applyPreviewSettings();
                    }
            ).dimensions(btnX, btnY, colorButtonWidth, 20).build();

            this.addDrawableChild(colorBtn);
        }
        currentY += ((commonColors.length + 2) / 3) * 25 + 10;

        // === TOP HORNS ===
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX, currentY, contentWidth, 20,
                Text.literal("§eTop Horns:"),
                this.textRenderer
        ));
        currentY += 20;

        int hornButtonWidth = (contentWidth - 15) / 4; // Four columns
        for (int i = 0; i < 8; i++) {
            int row = i / 4;
            int col = i % 4;
            int btnX = centerX + (col * (hornButtonWidth + 5));
            int btnY = currentY + (row * 25);
            final int hornType = i;

            ButtonWidget hornBtn = ButtonWidget.builder(
                    Text.literal("Type " + i),
                    button -> {
                        topHornType = hornType;
                        applyPreviewSettings();
                    }
            ).dimensions(btnX, btnY, hornButtonWidth, 20).build();

            this.addDrawableChild(hornBtn);
        }
        currentY += 55;

        // === BOTTOM HORNS ===
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX, currentY, contentWidth, 20,
                Text.literal("§eBottom Horns:"),
                this.textRenderer
        ));
        currentY += 20;

        int bottomHornWidth = (contentWidth - 10) / 3;
        for (int i = 0; i < 3; i++) {
            int btnX = centerX + (i * (bottomHornWidth + 5));
            final int hornType = i;

            ButtonWidget hornBtn = ButtonWidget.builder(
                    Text.literal("Type " + i),
                    button -> {
                        bottomHornType = hornType;
                        applyPreviewSettings();
                    }
            ).dimensions(btnX, currentY, bottomHornWidth, 20).build();
            if(bottomHornType == hornType) hornBtn.active = false;
            this.addDrawableChild(hornBtn);
        }
        currentY += 30;

        // === RANDOMIZE BUTTON ===
        ButtonWidget randomizeBtn = ButtonWidget.builder(
                Text.literal("§d§lRandomize"),
                button -> {
                    bodySize = GirlEntity.RANDOM.nextInt(65, 116);
                    breastSize = GirlEntity.RANDOM.nextInt(60, 116);

                    KoboldEntity.PatternPresets preset = presets[GirlEntity.RANDOM.nextInt(presets.length)];
                    primaryColor = preset.primary;
                    secondaryColor = preset.secondary;

                    irisColor = commonColors[GirlEntity.RANDOM.nextInt(commonColors.length)];
                    topHornType = GirlEntity.RANDOM.nextInt(0, 8);
                    bottomHornType = GirlEntity.RANDOM.nextInt(0, 3);

                    init();
                }
        ).dimensions(centerX, currentY, contentWidth, 20).build();
        this.addDrawableChild(randomizeBtn);
        currentY += 30;

        // === ACTION BUTTONS ===
        int actionButtonWidth = (contentWidth - 5) / 2;

        ButtonWidget confirmBtn = ButtonWidget.builder(
                Text.literal("Confirm"),
                button -> {
                    ClientPlayNetworking.send(new KoboldCustomizeC2SPacket(
                            entityId, bodySize, breastSize,
                            primaryColor, secondaryColor, irisColor,
                            topHornType, bottomHornType
                    ));
                    this.close();
                }
        ).dimensions(centerX, currentY, actionButtonWidth, 20).build();
        this.addDrawableChild(confirmBtn);

        ButtonWidget cancelBtn = ButtonWidget.builder(
                Text.literal("Cancel"),
                button -> this.close()
        ).dimensions(centerX + actionButtonWidth + 5, currentY, actionButtonWidth, 20).build();
        this.addDrawableChild(cancelBtn);

        currentY += 30;
    }

    private void applyPreviewSettings() {
        previewEntity.setBodySize(bodySize);
        previewEntity.setBreastSize(breastSize);
        previewEntity.setPrimaryColor(primaryColor);
        previewEntity.setSecondaryColor(secondaryColor);
        previewEntity.setIrisColor(irisColor);
        previewEntity.setTopHornType(topHornType);
        previewEntity.setBottomHornType(bottomHornType);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        // Calculate preview area (1/4 of screen on left)
        int previewWidth = this.width / 3;

        // Render preview entity on left side
        renderEntityPreview(context, mouseX, mouseY, previewWidth);
        applyPreviewSettings();

        // Render widgets
        super.render(context, mouseX, mouseY, delta);

        // Scroll indicator (only if needed)
        int scrollBarHeight = Math.max(20, (this.height * this.height) / (this.height + maxScrollOffset));
        int scrollBarY = (int)((this.height - scrollBarHeight) * (scrollOffset / maxScrollOffset));
        context.fill(this.width - 5, scrollBarY, this.width - 3, scrollBarY + scrollBarHeight, 0xFF808080);

        // Instructions at bottom of preview area
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                "§7Move mouse to rotate",
                previewWidth / 2,
                this.height - 20,
                0xFFFFFF
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Only scroll if mouse is on the right side (controls area) and scrolling is needed
        if (mouseX > (double) this.width / 4) {
            scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset - verticalAmount * SCROLL_SPEED));
            init();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void renderEntityPreview(DrawContext context, int mouseX, int mouseY, int previewWidth) {
        if (previewEntity == null) return;

        // Define preview area (left 1/4 of screen)
        int x1 = 10;
        int y1 = 50;
        int x2 = previewWidth - 10;
        int y2 = this.height - 50;

        // Draw entity using the proper 1.21.6 method
        drawEntity(context, x1, y1, x2, y2, PREVIEW_SIZE, 0.0f, mouseX, mouseY, previewEntity);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (previewEntity != null) {
            ClientPlayNetworking.send(new RemovePreviewEntityC2SPacket(entityId, previewEntity.getId()));
        }
        super.close();
        ClientPlayNetworking.send(new SetGUIOpenStateC2SPacket(this.entityId, false));
    }

}