package com.sandymandy.pleasurecraft.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import com.sandymandy.pleasurecraft.Freecam;
import com.sandymandy.pleasurecraft.config.CollisionBehavior;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateBaseMixin {

    @Shadow public abstract Block getBlock();

    @Inject(method = "getCollisionShape*", at = @At("HEAD"), cancellable = true)
    private void onGetCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext entityShapeContext && entityShapeContext.getEntity() instanceof FreeCamera) {
            // Return early if "Always Check Initial Collision" is on and Freecam isn't enabled yet
            if (ModConfig.INSTANCE.collision.alwaysCheck && !Freecam.isEnabled()) {
                return;
            }
            // Otherwise, check the collision config
            if (CollisionBehavior.isIgnored(getBlock())) {
                cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }
}
