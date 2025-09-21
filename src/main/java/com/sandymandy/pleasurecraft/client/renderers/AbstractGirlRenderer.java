package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.util.renderer.OffsetVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.Map;

public abstract class AbstractGirlRenderer<T extends SceneEntity> extends GeoEntityRenderer<T> {


    protected ItemStack mainHandItem;

    public AbstractGirlRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.addRenderLayer(new BlockAndItemGeoLayer<T>(this) {
            private float heldItemScale = 1.0F;

            @Override
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, T entity) {
                if (bone.getName().equals("weapon")) {
                     return AbstractGirlRenderer.this.mainHandItem;
                }
                return null;
            }

            @Override
            protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, T entity) {
                // Always treat it as if held in the right hand
                return ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(
                    MatrixStack matrices,
                    GeoBone bone,
                    ItemStack stack,
                    T entity,
                    VertexConsumerProvider bufferSource,
                    float tickDelta,
                    int light,
                    int overlay
            ) {
                if (stack == AbstractGirlRenderer.this.mainHandItem) {
                    // Rotate around X -90°
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                    if (stack.getItem() instanceof ShieldItem) {
                        matrices.translate(0.0F, 0.125F, -0.25F);
                    }
                    else if (stack.getItem() instanceof SwordItem){
                        matrices.translate(0.0F, 0.05F, 0.0F);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(10.0F));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(5.0F));
                    }
                }

                this.heldItemScale = 0.7F;
                matrices.scale(this.heldItemScale, this.heldItemScale, this.heldItemScale);

                super.renderStackForBone(matrices, bone, stack, entity, bufferSource, tickDelta, light, overlay);
            }
        });


    }

    @Override
    public void defaultRender(MatrixStack poseStack, T animatable, VertexConsumerProvider bufferSource,
                              @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer,
                              float partialTick, int packedLight) {
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, partialTick, packedLight);
    }

    @Override
    public void preRender(MatrixStack poseStack, T entity, BakedGeoModel model,
                          VertexConsumerProvider bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, int color) {
        this.mainHandItem = entity.getMainHandStack();

        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, color);

        if (entity.boneVisibility != null) {
            for (Map.Entry<String, Boolean> entry : entity.boneVisibility.entrySet()) {
                String boneName = entry.getKey();
                boolean isVisible = entry.getValue();

                model.getBone(boneName).ifPresent(bone -> {
                    bone.setHidden(!isVisible);
                    bone.setChildrenHidden(!isVisible);
                });
            }
        }
    }

    @Override
    public void renderFinal(MatrixStack matrices, T entity, BakedGeoModel model,
                            VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer,
                            float tickDelta, int light, int overlay, int color) {

        entity.handlePassengerBone(getGeoModel().getBone(entity.passengerBoneName).get());

        super.renderFinal(matrices, entity, model, vertexConsumers, vertexConsumer,
                tickDelta, light, overlay, color);


    }

    @Override
    public void applyRenderLayers(MatrixStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, T animatable, GeoBone bone,
                                  RenderLayer renderType, VertexConsumerProvider bufferSource,
                                  VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  int renderColor) {

        VertexConsumer targetBuffer = buffer;

        // Apply per-bone texture override if present
        if (animatable.boneTextureOverrides != null && animatable.boneTextureOverrides.containsKey(bone.getName())) {
            var tex = animatable.getBoneTexture(bone.getName());
            if (tex != null) {
                // Create a RenderLayer for this specific texture
                RenderLayer overrideLayer = RenderLayer.getEntityTranslucent(tex);
                // Get a buffer for that layer
                targetBuffer = bufferSource.getBuffer(overrideLayer);
            }
        }

        // Apply per-bone UV offset if present
        if (animatable.boneUVOffsets != null && animatable.boneUVOffsets.containsKey(bone.getName())) {
            Vec2f offset = animatable.getBoneUVOffset(bone.getName());
            if (offset != null) {
                OffsetVertexConsumer offsetBuffer = new OffsetVertexConsumer();
                offsetBuffer.setup(targetBuffer, offset.x, offset.y);
                targetBuffer = offsetBuffer;
            }
        }

        // Call the super method with the final buffer (possibly UV offset + texture override)
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                targetBuffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);

        // 4. Render the player texture last if present
        if (animatable.playerTexture != null && animatable.playerTexture.containsKey(bone.getName())) {
            Identifier playerTex = animatable.playerTexture.get(bone.getName());
            if (playerTex != null) {
                RenderLayer playerLayer = RenderLayer.getEntityTranslucent(playerTex);
                VertexConsumer playerBuffer = bufferSource.getBuffer(playerLayer);


                // Render the bone again with the player texture on top
                super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                        playerBuffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
            }


        }
    }
}
