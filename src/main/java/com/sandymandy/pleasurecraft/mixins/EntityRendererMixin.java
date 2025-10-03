package com.sandymandy.pleasurecraft.mixins;

import com.sandymandy.pleasurecraft.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.sandymandy.pleasurecraft.freecam.Freecam.MC;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    // Prevent rendering of nametag in inventory screen
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(EntityRenderState renderState,
                               Text component,
                               MatrixStack poseStack,
                               VertexConsumerProvider multiBufferSource,
                               int packedLightCoords,
                               CallbackInfo ci) {
        if (Freecam.isEnabled() && !((EntityRenderDispatcherAccessor) MC.getEntityRenderDispatcher()).isRenderShadows()) {
            ci.cancel();
        }
    }
}
