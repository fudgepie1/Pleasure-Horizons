package com.sandymandy.pleasurecraft.client.gui.screen;

import com.sandymandy.pleasurecraft.networking.C2S.GirlCustomizeC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class GirlCustomizeScreen extends Screen {

    private final int entityId;

    private int breastSize = 100;
    private int assSize = 100;

    private SliderWidget breastSlider;
    private SliderWidget assSlider;

    public GirlCustomizeScreen(int entityId) {
        super(Text.literal("Customize Girl"));
        this.entityId = entityId;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 4 + 20;

        // BREAST SIZE SLIDER
        // ----------------------------------
        breastSlider = new SliderWidget(centerX - 100, y, 200, 20,
                Text.literal("Breast Size"),
                (breastSize - 25f) / 125f  // convert 25–150 → 0–1
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Breast Size: " + breastSize));
            }

            @Override
            protected void applyValue() {
                breastSize = 25 + (int)(this.value * 125); // convert 0–1 → 25–150
            }
        };
        breastSlider.setTooltip(Tooltip.of(Text.literal("Adjust breast size")));
        this.addDrawableChild(breastSlider);
        y += 30;


/*        // ASS SIZE SLIDER
        // ----------------------------------
        assSlider = new SliderWidget(centerX - 100, y, 200, 20,
                Text.literal("Ass Size"),
                (assSize - 25f) / 125f
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Ass Size: " + assSize));
            }

            @Override
            protected void applyValue() {
                assSize = 25 + (int)(this.value * 125);
            }
        };
        assSlider.setTooltip(Tooltip.of(Text.literal("Adjust ass size")));
        this.addDrawableChild(assSlider);
        y += 40;*/

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), button -> {
            ClientPlayNetworking.send(new GirlCustomizeC2SPacket(
                    this.entityId,
                    this.breastSize,
                    this.assSize
            ));
            MinecraftClient.getInstance().setScreen(null); // close screen
        }).dimensions(centerX - 100, y, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> {
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(centerX + 5, y, 95, 20).build());
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
