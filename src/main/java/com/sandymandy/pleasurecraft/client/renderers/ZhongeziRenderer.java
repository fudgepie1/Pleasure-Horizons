package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.client.models.ZhongeziModel;
import com.sandymandy.pleasurecraft.entity.girls.ZhongeziEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class ZhongeziRenderer extends AbstractGirlRenderer<ZhongeziEntity> {
    public ZhongeziRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ZhongeziModel());
    }
}
