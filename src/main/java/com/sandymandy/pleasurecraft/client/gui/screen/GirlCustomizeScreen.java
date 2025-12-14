package com.sandymandy.pleasurecraft.client.gui.screen;

import com.sandymandy.pleasurecraft.networking.C2S.GirlCustomizeC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Vec3d;

public class GirlCustomizeScreen extends Screen {

    private final int entityId;

    private int breastSize;
    private boolean canGetImpregnated;
    private double breastOffsetX;
    private double breastOffsetY;
    private double breastOffsetZ;


    public GirlCustomizeScreen(int entityId, int breastSize, Vec3d breastOffset, boolean canGetImpregnated) {
        super(Text.literal("Customize Girl"));
        this.entityId = entityId;
        this.breastSize = breastSize;
        this.breastOffsetX = breastOffset.getX();
        this.breastOffsetY = breastOffset.getY();
        this.breastOffsetZ = breastOffset.getZ();
        this.canGetImpregnated = canGetImpregnated;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 4 + 20;

//      Breast Size Slider
        SliderWidget breastSlider = new SliderWidget(centerX - 100, y, 200, 20,
                Text.literal("Breast Size"),
                (breastSize - 25f) / 125f  // convert 25–150 → 0–1
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Breast Size: " + breastSize));
            }

            @Override
            protected void applyValue() {
                breastSize = 25 + (int) (this.value * 125); // convert 0–1 → 25–150
            }
        };
        breastSlider.setTooltip(Tooltip.of(Text.literal("Adjust breast size")));
        this.addDrawableChild(breastSlider);
        y += 30;

//      Breast Offset
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, y, 200, 20,
                Text.literal("Breast Offset (Vec3d)"),
                this.textRenderer
        ));
        y += 20;

//      X FIELD
        TextFieldWidget offsetXField = new TextFieldWidget(this.textRenderer, centerX - 100, y, 60, 20, Text.literal("X"));
        offsetXField.setText(String.valueOf(breastOffsetX));
        offsetXField.setTooltip(Tooltip.of(Text.literal("Offset X")));
        this.addDrawableChild(offsetXField);

//      Y FIELD
        TextFieldWidget offsetYField = new TextFieldWidget(this.textRenderer, centerX - 30, y, 60, 20, Text.literal("Y"));
        offsetYField.setText(String.valueOf(breastOffsetY));
        offsetYField.setTooltip(Tooltip.of(Text.literal("Offset Y")));
        this.addDrawableChild(offsetYField);

//      Z FIELD
        TextFieldWidget offsetZField = new TextFieldWidget(this.textRenderer, centerX + 40, y, 60, 20, Text.literal("Z"));
        offsetZField.setText(String.valueOf(breastOffsetZ));
        offsetZField.setTooltip(Tooltip.of(Text.literal("Offset Z")));
        this.addDrawableChild(offsetZField);

        y += 30;

//      Can Get Impregnated toggle
        ButtonWidget impregnateButton = ButtonWidget.builder(
                Text.literal(getImpregnationLabel()),
                button -> {
                    this.canGetImpregnated = !this.canGetImpregnated;
                    button.setMessage(Text.literal(getImpregnationLabel()));
                }
        ).dimensions(centerX - 100, y, 200, 20).build();

        this.addDrawableChild(impregnateButton);
        y += 30;


//      Confirm
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), button -> {
            // parse updated offset
            breastOffsetX = parseDouble(offsetXField.getText());
            breastOffsetY = parseDouble(offsetYField.getText());
            breastOffsetZ = parseDouble(offsetZField.getText());

            ClientPlayNetworking.send(new GirlCustomizeC2SPacket(
                    this.entityId,
                    this.breastSize,
                    new Vec3d(breastOffsetX, breastOffsetY, breastOffsetZ),
                    this.canGetImpregnated
            ));
            this.close(); // close screen
        }).dimensions(centerX - 100, y, 60, 20).build());

//      Clear
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), button -> {
            ClientPlayNetworking.send(new GirlCustomizeC2SPacket(
                    this.entityId,
                    100,
                    new Vec3d(0, 0, 0),
                    this.canGetImpregnated
            ));
            this.close(); // close screen
        }).dimensions(centerX - 30, y, 60, 20).build());

//      Cancel
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> {
            this.close();
        }).dimensions(centerX + 40, y, 60, 20).build());    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private String getImpregnationLabel() {
        return this.canGetImpregnated ? "Can Get Pregnant: YES" : "Can Get Pregnant: NO";
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawText(
                this.textRenderer,
                "Customize Appearance",
                this.width / 2 - this.textRenderer.getWidth("Customize Appearance") / 2,
                this.height / 4 - 10,
                Colors.WHITE,
                true
        );
    }
}
