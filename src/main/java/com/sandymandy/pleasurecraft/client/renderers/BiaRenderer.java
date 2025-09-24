package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.client.models.BiaModel;
import com.sandymandy.pleasurecraft.entity.girls.BiaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BiaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends AbstractGirlRenderer<BiaEntity, R>{
    public BiaRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BiaModel());
    }


}
