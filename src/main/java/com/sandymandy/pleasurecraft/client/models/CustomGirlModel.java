package com.sandymandy.pleasurecraft.client.models;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.CustomGirlEntity;
import com.sandymandy.pleasurecraft.registries.PleasureCraftDataTicketRegistry;
import com.sandymandy.pleasurecraft.util.Utils;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class CustomGirlModel extends AbstractGirlModel<CustomGirlEntity>{
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        boolean stripped = renderState.getOrDefaultGeckolibData(PleasureCraftDataTicketRegistry.IS_STRIPPED, false).booleanValue();
        String girlID = renderState.getOrDefaultGeckolibData(PleasureCraftDataTicketRegistry.GIRL_ID, "");


        // Pick the folder based on stripped/dressed state
        String folder = stripped ? "nude" : "dressed";

        // Use the model file provided by your getModelFile() method
        String filePath = "geckolib/models/" + folder + "/" + girlID + ".geo.json";


        if(Utils.assetExistsClient(Identifier.of(PleasureCraft.MOD_ID, filePath))) {
            return super.getModelResource(renderState);
        }
        else {
            return Identifier.of(PleasureCraft.MOD_ID, folder + "default");
        }
    }

    @Override
    public Identifier getAnimationResource(CustomGirlEntity animatable) {
        String folder = "geckolib/animations/";

        String filePath = folder + animatable.getGirlID() + ".animation.json";


        if(Utils.assetExistsClient(Identifier.of(PleasureCraft.MOD_ID, filePath))) {
            return super.getAnimationResource(animatable);
        }
        else {
            return Identifier.of(PleasureCraft.MOD_ID, "default");
        }
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String girlID = renderState.getOrDefaultGeckolibData(PleasureCraftDataTicketRegistry.GIRL_ID, "");

        String folder = "textures/entities/";

        String filePath = folder + girlID + ".png";
        if(Utils.assetExistsClient(Identifier.of(PleasureCraft.MOD_ID, filePath))) {
            return super.getTextureResource(renderState);
        }
        else {
            return Identifier.of(PleasureCraft.MOD_ID, folder + "default.png");
        }
    }
}
