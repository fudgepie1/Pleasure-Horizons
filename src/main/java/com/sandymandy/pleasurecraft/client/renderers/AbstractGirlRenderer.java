package com.sandymandy.pleasurecraft.client.renderers;

import com.mojang.datafixers.util.Either;
import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.networking.C2S.BonePosSyncC2SPacket;
import com.sandymandy.pleasurecraft.util.PleasureCraftDataTickets;
import com.sandymandy.pleasurecraft.util.renderer.OffsetVertexConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractGirlRenderer<T extends SceneEntity, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {


    protected ItemStack mainHandItem;

    public AbstractGirlRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.addRenderLayer(new BlockAndItemGeoLayer<T, Void, R>(this) {
            private float heldItemScale = 1.0F;

            @Override
            protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
                List<RenderData<R>> list = new ArrayList<>();

                // Weapon bone
                list.add(new RenderData<>(
                        "weapon",
                        ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                        (bone, state) -> {
                            // Return Either<ItemStack, BlockState>
                            ItemStack stack = AbstractGirlRenderer.this.mainHandItem;
                            return Either.left(stack);
                        }
                ));

                return list;
            }

            @Override
            public void addRenderData(T animatable, Void relatedObject, R renderState) {
                // You don’t need extra data for this layer,
                // but you could attach custom tickets if needed
            }

            @Override
            protected void renderStackForBone(
                    MatrixStack matrices,
                    GeoBone bone,
                    ItemStack stack,
                    ItemDisplayContext displayContext,
                    R renderState,
                    VertexConsumerProvider bufferSource,
                    int light,
                    int overlay
            ) {
                if (bone.getName().equals("weapon") && stack == AbstractGirlRenderer.this.mainHandItem) {
                    // Rotate around X -90°
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));

                    if (stack.getItem() instanceof ShieldItem) {
                        matrices.translate(0.0F, 0.125F, -0.25F);
                    } else if (stack.isIn(ItemTags.SWORDS)) {
                        matrices.translate(0.0F, 0.05F, 0.0F);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(10.0F));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(5.0F));
                    }
                }

                this.heldItemScale = 0.7F;
                matrices.scale(this.heldItemScale, this.heldItemScale, this.heldItemScale);

                // Call parent to actually render
                super.renderStackForBone(matrices, bone, stack, displayContext, renderState, bufferSource, light, overlay);
            }

        });


    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(PleasureCraftDataTickets.IS_STRIPPED, animatable.isStripped());
        renderState.addGeckolibData(PleasureCraftDataTickets.IS_IN_SCENE, animatable.isSceneActive());
        renderState.addGeckolibData(PleasureCraftDataTickets.GIRL_ID, animatable.getGirlID());
        renderState.addGeckolibData(PleasureCraftDataTickets.ENTITY_ID, animatable.getId());
        renderState.addGeckolibData(PleasureCraftDataTickets.GIRL_FIRST_PASSENGER, animatable.getFirstPassenger());
        renderState.addGeckolibData(PleasureCraftDataTickets.GIRL_MAIN_HAND_STACK, animatable.getMainHandStack());
        renderState.addGeckolibData(PleasureCraftDataTickets.GIRL_BONE_VISIBILITY, animatable.boneVisibility);
        renderState.addGeckolibData(PleasureCraftDataTickets.GIRL_BONE_UV_OFFSETS, animatable.boneUVOffsets);
        renderState.addGeckolibData(PleasureCraftDataTickets.GIRL_BONE_TEXTURE_OVERRIDES, new HashMap<>(animatable.boneTextureOverrides));
        renderState.addGeckolibData(PleasureCraftDataTickets.PLAYER_TEXTURES, new HashMap<>(animatable.playerTexture));
        renderState.addGeckolibData(PleasureCraftDataTickets.PASSENGER_BONE_NAME, animatable.passengerBoneName);
    }

    @Override
    public void defaultRender(R renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer) {
        super.defaultRender(renderState, poseStack, bufferSource, renderType, buffer);
    }

    @Override
    public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        this.mainHandItem = renderState.getGeckolibData(PleasureCraftDataTickets.GIRL_MAIN_HAND_STACK);

        super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);

        Map<String, Boolean> boneVisibility = renderState.getGeckolibData(PleasureCraftDataTickets.GIRL_BONE_VISIBILITY);

        if (boneVisibility != null) {
            for (Map.Entry<String, Boolean> entry : boneVisibility.entrySet()) {
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
    public void renderFinal(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        String passengerBoneName = renderState.getGeckolibData(PleasureCraftDataTickets.PASSENGER_BONE_NAME);

        GeoBone bone = getGeoModel().getBone(passengerBoneName).get();

        Vector3d bonePos = bone.getWorldPosition();
        Vec3d passengerBonePos = new Vec3d(bonePos.x, bonePos.y, bonePos.z);
        ClientPlayNetworking.send(new BonePosSyncC2SPacket(renderState.getGeckolibData(PleasureCraftDataTickets.ENTITY_ID), passengerBonePos));
        super.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void applyRenderLayers(R renderState,
                                  MatrixStack poseStack,
                                  BakedGeoModel model,
                                  @Nullable RenderLayer renderType,
                                  VertexConsumerProvider bufferSource,
                                  @Nullable VertexConsumer buffer,
                                  int packedLight,
                                  int packedOverlay,
                                  int renderColor) {

        // First call super to run normal layers
        super.applyRenderLayers(renderState, poseStack, model, renderType, bufferSource, buffer, packedLight, packedOverlay, renderColor);

        // Now perform overlays for any bones needing a texture override
        Map<String, Identifier> boneTexOverrides = renderState.getGeckolibData(PleasureCraftDataTickets.GIRL_BONE_TEXTURE_OVERRIDES);
        Map<String, Identifier> playerTextures = renderState.getGeckolibData(PleasureCraftDataTickets.PLAYER_TEXTURES);

        if (boneTexOverrides != null && !boneTexOverrides.isEmpty()) {
            for (Map.Entry<String, Identifier> e : boneTexOverrides.entrySet()) {
                String boneName = e.getKey();
                Identifier tex = e.getValue();
                if (tex == null) continue;

                // get the bone from the model
                model.getBone(boneName).ifPresent(bone -> {
                    // prepare a translucent entity layer for this texture
                    RenderLayer overrideLayer = RenderLayer.getEntityTranslucent(tex);
                    VertexConsumer overrideBuffer = bufferSource.getBuffer(overrideLayer);

                    // call GeoEntityRenderer's implementation directly to draw this bone with overrideBuffer
                    super.renderRecursively(renderState, poseStack, bone, overrideLayer, bufferSource, overrideBuffer, false, packedLight, packedOverlay, renderColor);
                });
            }
        }

        // Player textures overlay (render on top of whatever)
        if (playerTextures != null && !playerTextures.isEmpty()) {
            for (Map.Entry<String, Identifier> e : playerTextures.entrySet()) {
                String boneName = e.getKey();
                Identifier playerTex = e.getValue();
                if (playerTex == null) continue;

                model.getBone(boneName).ifPresent(bone -> {
                    RenderLayer playerLayer = RenderLayer.getEntityTranslucent(playerTex);
                    VertexConsumer playerBuffer = bufferSource.getBuffer(playerLayer);

                    super.renderRecursively(renderState, poseStack, bone, playerLayer, bufferSource, playerBuffer, false, packedLight, packedOverlay, renderColor);
                });
            }
        }
    }

    @Override
    public void renderRecursively(R renderState,
                                  MatrixStack poseStack,
                                  GeoBone bone,
                                  RenderLayer renderType,
                                  VertexConsumerProvider bufferSource,
                                  VertexConsumer buffer,
                                  boolean isReRender,
                                  int packedLight,
                                  int packedOverlay,
                                  int renderColor) {

        Map<String, Identifier> boneTexOverrides = renderState.getGeckolibData(PleasureCraftDataTickets.GIRL_BONE_TEXTURE_OVERRIDES);
        Map<String, Vec2f> boneUVOffsets = renderState.getGeckolibData(PleasureCraftDataTickets.GIRL_BONE_UV_OFFSETS);

        // Skip rendering this bone in the base pass if it has a texture override
        if (boneTexOverrides != null && boneTexOverrides.containsKey(bone.getName())) {
            return;
        }

        VertexConsumer targetBuffer = buffer;

        // Still allow UV offset
        if (boneUVOffsets != null && boneUVOffsets.containsKey(bone.getName())) {
            Vec2f offset = boneUVOffsets.get(bone.getName());
            if (offset != null) {
                OffsetVertexConsumer offsetBuffer = new OffsetVertexConsumer();
                offsetBuffer.setup(targetBuffer, offset.x, offset.y);
                targetBuffer = offsetBuffer;
            }
        }

        super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource,
                targetBuffer, isReRender, packedLight, packedOverlay, renderColor);
    }
}
