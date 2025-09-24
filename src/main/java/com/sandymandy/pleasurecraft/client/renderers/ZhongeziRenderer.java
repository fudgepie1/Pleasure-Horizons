package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.client.models.ZhongeziModel;
import com.sandymandy.pleasurecraft.entity.girls.ZhongeziEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ZhongeziRenderer<R extends LivingEntityRenderState & GeoRenderState> extends AbstractGirlRenderer<ZhongeziEntity, R> {
    public ZhongeziRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ZhongeziModel());
    }
}
