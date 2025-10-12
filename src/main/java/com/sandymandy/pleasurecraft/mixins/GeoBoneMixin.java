package com.sandymandy.pleasurecraft.mixins;

import com.sandymandy.pleasurecraft.util.renderer.GeoBoneExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import software.bernie.geckolib.cache.object.GeoBone;

@Mixin(GeoBone.class)
public abstract class GeoBoneMixin implements GeoBoneExtension {

    private boolean hidden;

    @Override
    public void setHiddenWithoutHidingChildren(boolean hidden) {
        // Just set this bone’s hidden state without touching children
        this.hidden = hidden;
    }
}
