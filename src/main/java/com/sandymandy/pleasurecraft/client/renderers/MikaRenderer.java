package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.client.models.MikaModel;
import com.sandymandy.pleasurecraft.entity.girls.MikaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MikaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends AbstractGirlRenderer<MikaEntity, R>{
    public MikaRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MikaModel());
    }


}
