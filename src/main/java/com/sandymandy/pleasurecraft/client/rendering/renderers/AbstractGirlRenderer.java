package com.sandymandy.pleasurecraft.client.rendering.renderers;

import com.mojang.datafixers.util.Either;
import com.sandymandy.pleasurecraft.PleasureCraftClient;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.entity.base.GirlSceneEntity;
import com.sandymandy.pleasurecraft.networking.C2S.BonePosSyncC2SPacket;
import com.sandymandy.pleasurecraft.registries.PleasureCraftDataTicketRegistry;
import com.sandymandy.pleasurecraft.util.rendering.OffsetVertexConsumer;
import com.sandymandy.pleasurecraft.util.rendering.UnlitNormalVertexConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
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

import java.util.*;

public abstract class AbstractGirlRenderer<T extends GirlSceneEntity, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {

    protected ItemStack mainHandItem;

    public AbstractGirlRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            public void addRenderData(T animatable, Void relatedObject, R renderState) {
                renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_MAIN_HAND_STACK, animatable.getMainHandStack());
                renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_WEAPON_BONE_ROTATION_X, animatable.getWeaponBoneXRotation());
            }

            @Override
            protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
                List<RenderData<R>> list = new ArrayList<>();

                // Weapon bone
                list.add(new RenderData<>(
                        "weapon",
                        ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                        (bone, state) -> {
                            ItemStack stack = AbstractGirlRenderer.this.mainHandItem;
                            return Either.left(stack);
                        }
                ));
                return list;
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
                String name = bone.getName();
                float boneRotX = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_WEAPON_BONE_ROTATION_X);

                if ("weapon".equals(name)) {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(boneRotX));
                    matrices.scale(0.7F, 0.7F, 0.7F);
                }

                boolean isNotInScene = !renderState.getGeckolibData(PleasureCraftDataTicketRegistry.IS_IN_SCENE);
                if(isNotInScene) super.renderStackForBone(matrices, bone, stack, displayContext, renderState, bufferSource, light, overlay);
            }

        });


    }

    @Override
    public RenderLayer getRenderType(R renderState, @Nullable Identifier texture) {
                return RenderLayer.getEntityTranslucent(texture);
            }

    /**
     * Override this method if the entity should use translucent rendering
     */
    protected boolean shouldUseTranslucentRendering(R renderState) {
        return false; // Default to false
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.IS_STRIPPED, animatable.isStripped());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.IS_IN_SCENE, animatable.isSceneActive());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.HAS_VEHICLE, animatable.hasVehicle());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.IS_SPRINTING, animatable.isSprinting());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_ID, animatable.getGirlID());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.ENTITY_ID, animatable.getId());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_FIRST_PASSENGER, animatable.getFirstPassenger());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_VISIBILITY, animatable.boneVisibility);
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_UV_OFFSETS, animatable.boneUVOffsets);
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_TEXTURE_OVERRIDES, animatable.boneTextureOverrides);
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_TEXTURE_OVERRIDES_LAYER_TWO, new HashMap<>(animatable.boneTextureOverridesLayer2));
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_TEXTURE_OVERRIDES_LAYER_THREE, new HashMap<>(animatable.boneTextureOverridesLayer3));
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_COLOR_OVERRIDES, new HashMap<>(animatable.boneColorOverrides));
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_SIZE_OVERRIDES, new HashMap<>(animatable.boneSizeOverrides));
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_POSITION_OFFSET, new HashMap<>(animatable.bonePositionOffset));
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.PASSENGER_BONE_NAME, animatable.passengerBoneName);
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.YAW, animatable.getYaw());
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.PREVIOUS_YAW, animatable.previousYaw);
        renderState.addGeckolibData(PleasureCraftDataTicketRegistry.PREVIOUS_VELOCITY, animatable.previousVelocity);
    }

    @Override
    public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        this.mainHandItem = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_MAIN_HAND_STACK);

        super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);

        Map<String, Boolean> boneVisibility = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_VISIBILITY);
        Map<String, Vec3d> boneSize = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_SIZE_OVERRIDES);
        Map<String, Vec3d> bonePos = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_POSITION_OFFSET);

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

        if (boneSize != null) {
            for (Map.Entry<String, Vec3d> entry : boneSize.entrySet()) {
                String boneName = entry.getKey();
                Vec3d size = entry.getValue();

                model.getBone(boneName).ifPresent(bone -> {
                    bone.setScaleX(size.toVector3f().x());
                    bone.setScaleY(size.toVector3f().y());
                    bone.setScaleZ(size.toVector3f().z());
                });
            }
        }

        if (bonePos != null) {
            for (Map.Entry<String, Vec3d> entry : bonePos.entrySet()) {
                String boneName = entry.getKey();
                Vec3d size = entry.getValue();

                model.getBone(boneName).ifPresent(bone -> {
                    bone.setPosX(size.toVector3f().x());
                    bone.setPosY(size.toVector3f().y());
                    bone.setPosZ(size.toVector3f().z());
                });
            }
        }
    }

    @Override
    public void renderFinal(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        String passengerBoneName = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.PASSENGER_BONE_NAME);

        if(getGeoModel().getBone(passengerBoneName).isPresent()){
            GeoBone bone = getGeoModel().getBone(passengerBoneName).get();
            Vector3d bonePos = bone.getLocalPosition();
            ClientPlayNetworking.send(new BonePosSyncC2SPacket(renderState.getGeckolibData(PleasureCraftDataTicketRegistry.ENTITY_ID), new Vec3d(bonePos.x, bonePos.y, bonePos.z)));
        }
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
        Map<String, Identifier> boneTexOverrides = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_TEXTURE_OVERRIDES);
        Map<String, Identifier> boneTexOverridesLayer2 = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_TEXTURE_OVERRIDES_LAYER_TWO);
        Map<String, Identifier> boneTexOverridesLayer3 = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_TEXTURE_OVERRIDES_LAYER_THREE);

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

        if (boneTexOverridesLayer2 != null && !boneTexOverridesLayer2.isEmpty()) {
            for (Map.Entry<String, Identifier> e : boneTexOverridesLayer2.entrySet()) {
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

        if (boneTexOverridesLayer3 != null && !boneTexOverridesLayer3.isEmpty()) {
            for (Map.Entry<String, Identifier> e : boneTexOverridesLayer3.entrySet()) {
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

    }

    @Override
    public void defaultRender(R renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer) {
        super.defaultRender(renderState, poseStack, bufferSource, renderType, buffer);
    }
    public static boolean IS_SHADING_DISABLED = ModConfig.INSTANCE.girls.disableShading;
    public static boolean ARE_SHADERS_DISABLED = PleasureCraftClient.areIrisShadersDisabled();

    public static void updateShadingState() {
        IS_SHADING_DISABLED = ModConfig.INSTANCE.girls.disableShading;
        ARE_SHADERS_DISABLED = PleasureCraftClient.areIrisShadersDisabled();
    }

    protected boolean isShadingDisabled() {
        return IS_SHADING_DISABLED && ARE_SHADERS_DISABLED;
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

        if (isShadingDisabled()) {
            buffer = new UnlitNormalVertexConsumer(buffer);
        }

        Map<String, Identifier> boneTexOverrides = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_TEXTURE_OVERRIDES);
        Map<String, Vec2f> boneUVOffsets = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_UV_OFFSETS);
        Map<String, Integer> boneColorOverrides = renderState.getGeckolibData(PleasureCraftDataTicketRegistry.GIRL_BONE_COLOR_OVERRIDES);

        // Skip rendering this bone in the base pass if it has a texture override
        if (boneTexOverrides != null && boneTexOverrides.containsKey(bone.getName())) {
            return;
        }

        VertexConsumer targetBuffer = buffer;
        int targetColor = renderColor;

        // Still allow UV offset
        if (boneUVOffsets != null && boneUVOffsets.containsKey(bone.getName())) {
            Vec2f offset = boneUVOffsets.get(bone.getName());
            if (offset != null) {
                OffsetVertexConsumer offsetBuffer = new OffsetVertexConsumer();
                offsetBuffer.setup(targetBuffer, offset.x, offset.y);
                targetBuffer = offsetBuffer;
            }
        }

        if (boneColorOverrides != null && !boneColorOverrides.isEmpty()) {
            Integer color = boneColorOverrides.get(bone.getName());
            if (color != null){
                targetColor = color;
            }
        }

        super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource,
                targetBuffer, isReRender, packedLight, packedOverlay, targetColor);
    }
}
