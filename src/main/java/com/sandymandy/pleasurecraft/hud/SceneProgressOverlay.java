package com.sandymandy.pleasurecraft.hud;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class SceneProgressOverlay {
    private static final Identifier SCENE_PROGRESS_BAR_TEXTURE = Identifier.of(PleasureCraft.MOD_ID, "textures/gui/scene_progress_bar.png");

    private static boolean active = false;

    public static void setActive(boolean on) {
        active = on;
    }

    public static boolean isActive() {
        return active;
    }


    public static void render(DrawContext context, float sceneProgress, float cumThreshold){
        if (!active) return;

        float ratio = cumThreshold > 0 ? (sceneProgress / cumThreshold) : 0f;
        ratio = Math.min(ratio, 1f);

        int texWidth = 48;
        int texHeight = 175;

        // --- Scale factor ---
        float scale = 1f;

        // Original background size
        int scaledWidth = (int)(texWidth * scale);
        int scaledHeight = (int)(texHeight * scale);

        // Position in top-left corner (with 10px padding)
        int x = 10;
        int yTop = 10;

        // --- Background ---
        context.drawTexture(
                RenderLayer::getGuiTextured, SCENE_PROGRESS_BAR_TEXTURE,
                x, yTop,
                0, 0,                 // u,v in texture
                scaledWidth, scaledHeight,    // draw size on screen (scaled)
                scaledWidth, scaledHeight,  // original texture size
                0xFFFFFFFF
        );

        // --- Fill: plain white rectangle ---
        int insetX = (int)(8 * scale);
        int insetY = (int)(8 * scale);
        int fillWidth = scaledWidth - (insetX * 2);
        int fillHeightMax = scaledHeight - (insetY * 2);

        int filledHeight = (int)(ratio * fillHeightMax);
        if (filledHeight > 0) {
            int fillX = x + insetX;
            int fillY = (yTop + scaledHeight - insetY) - filledHeight; // bottom-anchored inside frame

            int color = 0xEFEFEFEF; // solid white
            context.fill(
                    fillX, fillY,
                    fillX + fillWidth, (yTop + scaledHeight - insetY),
                    color
            );
        }
    }

}
