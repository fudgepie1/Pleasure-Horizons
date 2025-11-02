package com.sandymandy.pleasurecraft.client.rendering.renderers;

import com.sandymandy.pleasurecraft.client.models.JsonGirlModel;
import com.sandymandy.pleasurecraft.entity.girls.JsonGirlEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import javax.naming.Context;

public class JsonGirlRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends AbstractGirlRenderer<JsonGirlEntity, R> {

    public JsonGirlRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new JsonGirlModel());
    }
}
