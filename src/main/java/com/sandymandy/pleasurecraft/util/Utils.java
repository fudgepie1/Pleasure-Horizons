package com.sandymandy.pleasurecraft.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

    public static BlockInfo findNearbyBlock(World world, BlockPos center, int radius, @Nullable Block block, @Nullable TagKey<Block> blockTag) {
        for (BlockPos pos : BlockPos.iterate(
                center.add(-radius, -radius, -radius),
                center.add(radius, radius, radius))) {

            BlockState state = world.getBlockState(pos);

            if (isBlockOrTag(state, block, blockTag)) {
                // Found a matching block, collect info
                Direction facing = state.contains(Properties.HORIZONTAL_FACING)
                        ? state.get(Properties.HORIZONTAL_FACING)
                        : Direction.NORTH; // fallback

                return new BlockInfo(pos.toImmutable(), state, facing);
            }
        }
        return null; // none found
    }

    public static boolean checkForBlockAt(World world, BlockPos blockPos, @Nullable Block block, @Nullable TagKey<Block> blockTag){
        BlockState state = world.getBlockState(blockPos);
        return isBlockOrTag(state, block, blockTag);
    }

    private static boolean isBlockOrTag(BlockState state, @Nullable Block block, @Nullable TagKey<Block> tag) {
        if (block != null && state.isOf(block)) {
            return true;
        }
        return tag != null && state.isIn(tag);
    }

    public static float Round(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, RoundingMode.HALF_DOWN).floatValue();

    }

    // simple record to hold info
    public record BlockInfo(BlockPos pos, BlockState state, Direction facing) {}

}
