package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.client.models.ZongModel;
import com.sandymandy.pleasurecraft.entity.girls.ZongEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ZongRenderer<R extends LivingEntityRenderState & GeoRenderState> extends AbstractGirlRenderer<ZongEntity, R> {
    public ZongRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ZongModel());
    }
}
