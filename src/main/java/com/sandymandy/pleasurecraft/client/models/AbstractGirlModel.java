package com.sandymandy.pleasurecraft.client.models;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.entity.base.AbstractGirlEntity;
import com.sandymandy.pleasurecraft.util.renderer.GeoBoneExtension;
import com.sandymandy.pleasurecraft.registries.PleasureCraftDataTickets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public abstract class AbstractGirlModel<T extends AbstractGirlEntity> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // Check if entity is stripped
        boolean stripped = renderState.getOrDefaultGeckolibData(PleasureCraftDataTickets.IS_STRIPPED, false).booleanValue();
        String girlID = renderState.getOrDefaultGeckolibData(PleasureCraftDataTickets.GIRL_ID, "");


        // Pick the folder based on stripped/dressed state
        String folder = stripped ? "nude" : "dressed";

        // Use the model file provided by your getModelFile() method
        String filePath = folder + "/" + girlID;

        return Identifier.of(PleasureCraft.MOD_ID, filePath);
    }


    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String girlID = renderState.getOrDefaultGeckolibData(PleasureCraftDataTickets.GIRL_ID, "");

        String filePath = "textures/entities/" + girlID + ".png";

        return Identifier.of(PleasureCraft.MOD_ID, filePath);
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return Identifier.of(PleasureCraft.MOD_ID, animatable.getGirlID());
    }


    @Override
    public void setCustomAnimations(AnimationState<T> animationState) {

        GeoBone head = getAnimationProcessor().getBone("head");
        boolean isSceneActive = animationState.renderState().getGeckolibData(PleasureCraftDataTickets.IS_IN_SCENE);

        if (head != null && !isSceneActive) {
            float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
            float yaw = animationState.getData(DataTickets.ENTITY_YAW);

            head.setRotX(-pitch * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(-yaw * MathHelper.RADIANS_PER_DEGREE);
        }

        GeoBone headBone = this.getAnimationProcessor().getBone("Head2");
        if (headBone != null) {
            MinecraftClient client = MinecraftClient.getInstance();

            boolean isFirstPerson = client.options.getPerspective().isFirstPerson();
            boolean isPlayerRider = client.cameraEntity == animationState.renderState().getGeckolibData(PleasureCraftDataTickets.GIRL_FIRST_PASSENGER);

            ((GeoBoneExtension) headBone).setHiddenWithoutHidingChildren(isFirstPerson && isPlayerRider);
        }

        GeoBone boobWindow = this.getAnimationProcessor().getBone("boobWindow");
        if (boobWindow != null) {
            boobWindow.setHidden(ModConfig.INSTANCE.girls.boobWindow);
        }

    }
}
