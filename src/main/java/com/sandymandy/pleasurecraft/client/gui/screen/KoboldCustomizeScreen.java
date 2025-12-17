package com.sandymandy.pleasurecraft.client.gui.screen;

import com.sandymandy.pleasurecraft.entity.base.GirlEntity;
import com.sandymandy.pleasurecraft.entity.girls.KoboldEntity;
import com.sandymandy.pleasurecraft.networking.C2S.KoboldCustomizeC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.RemovePreviewEntityC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.SetGUIOpenStateC2SPacket;
import com.sandymandy.pleasurecraft.util.Colors;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sandymandy.pleasurecraft.util.Utils.getFormattedByUnderscore;
import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public class KoboldCustomizeScreen extends Screen {

    private final int entityId;
    private final KoboldEntity previewEntity;

    // Customization values
    private int bodySize;
    private int breastSize;
    private int primaryColor;
    private int secondaryColor;
    private int irisColor;
    private int topHornType;
    private int bottomHornType;

    // Button groups for selection tracking
    private final Map<String, List<ButtonWidget>> buttonGroups = new HashMap<>();
    private final Map<ButtonWidget, String> buttonToGroup = new HashMap<>();
    private final Map<String, ButtonWidget> selectedButtons = new HashMap<>();

    // Scroll offset
    private double scrollOffset = 0;
    private static final int SCROLL_SPEED = 20;
    private final int maxScrollOffset = 200;

    // Preview settings
    private static final int PREVIEW_SIZE = 140;

    public KoboldCustomizeScreen(int entityId, int previewEntityId) {
        super(Text.literal("Customize Kobold"));
        this.entityId = entityId;

        World world = MinecraftClient.getInstance().world;
        this.previewEntity = (KoboldEntity) world.getEntityById(previewEntityId);
        KoboldEntity entity = (KoboldEntity) world.getEntityById(entityId);

        this.bodySize = entity.getBodySize();
        this.breastSize = entity.getBreastSize();
        this.primaryColor = entity.getPrimaryColor();
        this.secondaryColor = entity.getSecondaryColor();
        this.irisColor = entity.getIrisColor();
        this.topHornType = entity.getTopHornType();
        this.bottomHornType = entity.getBottomHornType();
    }

    /**
     * Helper method to create a selectable button that's part of a group
     */
    private ButtonWidget createSelectableButton(String groupId, Text message, int x, int y, int width, int height, ButtonWidget.PressAction onPress) {
        ButtonWidget button = ButtonWidget.builder(message, btn -> {
            selectButton(groupId, btn);
            onPress.onPress(btn);
        }).dimensions(x, y, width, height).build();

        // Track this button in its group
        buttonGroups.computeIfAbsent(groupId, k -> new ArrayList<>()).add(button);
        buttonToGroup.put(button, groupId);

        return button;
    }

    /**
     * Select a button and deselect others in the same group
     */
    private void selectButton(String groupId, ButtonWidget button) {
        // Deselect previous button in group
        ButtonWidget previouslySelected = selectedButtons.get(groupId);
        if (previouslySelected != null) {
            previouslySelected.active = true;
        }

        // Select new button
        button.active = false;
        selectedButtons.put(groupId, button);
    }

    /**
     * Mark initial selection without triggering the press action
     */
    private void markAsSelected(String groupId, ButtonWidget button) {
        button.active = false;
        selectedButtons.put(groupId, button);
    }

    @Override
    protected void init() {
        // Clear previous widgets
        this.clearChildren();
        buttonGroups.clear();
        buttonToGroup.clear();
        selectedButtons.clear();

        // Calculate layout dimensions
        int previewWidth = this.width / 4;
        int menuWidth = (this.width * 3) / 4;
        int menuStartX = previewWidth + 20;

        int startY = 20;
        int currentY = startY - (int)scrollOffset;
        int contentWidth = Math.min(400, menuWidth - 40);
        int centerX = menuStartX + (menuWidth - contentWidth) / 2;

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
                (breastSize - 60f) / 100f
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Breast Size: " + breastSize));
            }

            @Override
            protected void applyValue() {
                breastSize = 60 + (int)(this.value * 100);
                applyPreviewSettings();
            }
        };
        breastSlider.setTooltip(Tooltip.of(Text.literal("Adjust breast size (60-160)")));
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
        int buttonWidth = (contentWidth - 5) / 2;
        for (int i = 0; i < presets.length; i++) {
            KoboldEntity.PatternPresets preset = presets[i];
            int row = i / 2;
            int col = i % 2;
            int btnX = centerX + (col * (buttonWidth + 5));
            int btnY = currentY + (row * 25);

            ButtonWidget presetBtn = createSelectableButton(
                    "color_preset",
                    Text.literal(getFormattedByUnderscore(preset.name())),
                    btnX, btnY, buttonWidth, 20,
                    button -> {
                        primaryColor = preset.primary;
                        secondaryColor = preset.secondary;
                        applyPreviewSettings();
                    }
            );

            this.addDrawableChild(presetBtn);

            // Mark current preset as selected
            if (preset.primary == primaryColor && preset.secondary == secondaryColor) {
                markAsSelected("color_preset", presetBtn);
            }
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

        int colorButtonWidth = (contentWidth - 10) / 3;
        for (int i = 0; i < commonColors.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int btnX = centerX + (col * (colorButtonWidth + 5));
            int btnY = currentY + (row * 25);
            int color = commonColors[i];

            ButtonWidget colorBtn = createSelectableButton(
                    "iris_color",
                    Text.literal("■").styled(style -> style.withColor(color)),
                    btnX, btnY, colorButtonWidth, 20,
                    button -> {
                        irisColor = color;
                        applyPreviewSettings();
                    }
            );

            this.addDrawableChild(colorBtn);

            // Mark current color as selected
            if (color == irisColor) {
                markAsSelected("iris_color", colorBtn);
            }
        }
        currentY += ((commonColors.length + 2) / 3) * 25 + 10;

        // === TOP HORNS ===
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX, currentY, contentWidth, 20,
                Text.literal("§eTop Horns:"),
                this.textRenderer
        ));
        currentY += 20;

        int hornButtonWidth = (contentWidth - 15) / 4;
        for (int i = 0; i < 8; i++) {
            int row = i / 4;
            int col = i % 4;
            int btnX = centerX + (col * (hornButtonWidth + 5));
            int btnY = currentY + (row * 25);
            final int hornType = i;

            ButtonWidget hornBtn = createSelectableButton(
                    "top_horn",
                    Text.literal("Type " + i),
                    btnX, btnY, hornButtonWidth, 20,
                    button -> {
                        topHornType = hornType;
                        applyPreviewSettings();
                    }
            );

            this.addDrawableChild(hornBtn);

            // Mark current horn as selected
            if (hornType == topHornType) {
                markAsSelected("top_horn", hornBtn);
            }
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

            ButtonWidget hornBtn = createSelectableButton(
                    "bottom_horn",
                    Text.literal("Type " + i),
                    btnX, currentY, bottomHornWidth, 20,
                    button -> {
                        bottomHornType = hornType;
                        applyPreviewSettings();
                    }
            );

            this.addDrawableChild(hornBtn);

            // Mark current horn as selected
            if (hornType == bottomHornType) {
                markAsSelected("bottom_horn", hornBtn);
            }
        }
        currentY += 30;

        // === RANDOMIZE BUTTON ===
        ButtonWidget randomizeBtn = ButtonWidget.builder(
                Text.literal("§d§lRandomize"),
                button -> {
                    bodySize = GirlEntity.RANDOM.nextInt(65, 116);
                    breastSize = GirlEntity.RANDOM.nextInt(60, 161);

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
        previewEntity.setKoboldBreastSize(breastSize);
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

        if (previewEntity != null) {
            renderEntityPreview(context, mouseX, mouseY, previewWidth);
            applyPreviewSettings();
        }


        super.render(context, mouseX, mouseY, delta);

        // Scroll indicator (only if needed)
        int scrollBarHeight = Math.max(20, (this.height * this.height) / (this.height + maxScrollOffset));
        int scrollBarY = (int)((this.height - scrollBarHeight) * (scrollOffset / maxScrollOffset));
        context.fill(this.width - 5, scrollBarY, this.width - 3, scrollBarY + scrollBarHeight, 0xFF808080);

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX > (double) this.width / 4) {
            scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset - verticalAmount * SCROLL_SPEED));
            init();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void renderEntityPreview(DrawContext context, int mouseX, int mouseY, int previewWidth) {
        int x1 = 10;
        int y1 = 50;
        int x2 = previewWidth - 10;
        int y2 = this.height - 50;

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