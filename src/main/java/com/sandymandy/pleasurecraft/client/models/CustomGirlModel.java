package com.sandymandy.pleasurecraft.client.models;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.CustomGirlEntity;
import com.sandymandy.pleasurecraft.registries.PleasureCraftDataTicketRegistry;
import com.sandymandy.pleasurecraft.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.Map;

public class CustomGirlModel extends AbstractGirlModel<CustomGirlEntity>{
    private final Map<String, Boolean> fallbackUsed = new HashMap<>();

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        boolean stripped = renderState.getOrDefaultGeckolibData(PleasureCraftDataTicketRegistry.IS_STRIPPED, false).booleanValue();
        String girlID = renderState.getOrDefaultGeckolibData(PleasureCraftDataTicketRegistry.GIRL_ID, "");

        // Pick the folder based on stripped/dressed state
        String folder = stripped ? "nude/" : "dressed/";

        // Use the model file provided by your getModelFile() method
        String filePath = "geckolib/models/" + folder + girlID + ".geo.json";


        boolean inGui = MinecraftClient.getInstance().currentScreen != null;
        boolean exists = Utils.assetExistsClient(Identifier.of(PleasureCraft.MOD_ID,filePath));

        // First-time fallback or still no model loaded
        if (!exists) {
            fallbackUsed.put(girlID, true);
            if(!inGui) PleasureCraft.LOGGER.error("Model files for " + girlID + " doesn't exist");
            return Identifier.of(PleasureCraft.MOD_ID, folder + "default");
        }

        // Model exists now, but GUI is still open -> keep fallback until GUI closes
        if (inGui && fallbackUsed.getOrDefault(girlID, false)) {
            return Identifier.of(PleasureCraft.MOD_ID, folder + "default");
        }

        // GUI closed, model exists -> switch to actual
        fallbackUsed.put(girlID, false);
        return super.getModelResource(renderState);
    }

    @Override
    public Identifier getAnimationResource(CustomGirlEntity animatable) {
        String folder = "geckolib/animations/";

        String filePath = folder + animatable.getGirlID() + ".animation.json";


        if(Utils.assetExistsClient(Identifier.of(PleasureCraft.MOD_ID, filePath))) {
            return super.getAnimationResource(animatable);
        }
        else {
            return null;
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
