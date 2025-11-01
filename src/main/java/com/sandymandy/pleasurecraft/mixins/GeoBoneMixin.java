package com.sandymandy.pleasurecraft.mixins;

import com.sandymandy.pleasurecraft.util.rendering.GeoBoneExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib.cache.object.GeoBone;

@Environment(EnvType.CLIENT)
@Mixin(GeoBone.class)
public abstract class GeoBoneMixin implements GeoBoneExtension {

    private boolean hidden;

    @Override
    public void setHiddenWithoutHidingChildren(boolean hidden) {
        // Just set this bone’s hidden state without touching children
        this.hidden = hidden;
    }
}
