package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.block.PleasureCraftBlocks;
import com.sandymandy.pleasurecraft.block.blocks.CarvedGirlPumpkinBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PleasureCraftDispenserBehavior {
    public static void registerDispenserBehavior(){
        PleasureCraft.LOGGER.info("Registering Dispenser Behavior for PleasureCraft");

        DispenserBlock.registerBehavior(PleasureCraftBlocks.CARVED_GIRL_PUMPKIN, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                CarvedGirlPumpkinBlock carvedGirlPumpkinBlock = (CarvedGirlPumpkinBlock)PleasureCraftBlocks.CARVED_GIRL_PUMPKIN;
                if (world.isAir(blockPos) && carvedGirlPumpkinBlock.canDispense(world, blockPos)) {
                    if (!world.isClient) {
                        world.setBlockState(blockPos, carvedGirlPumpkinBlock.getDefaultState(), Block.NOTIFY_ALL);
                        world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
                    }

                    stack.decrement(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(EquippableDispenserBehavior.dispense(pointer, stack));
                }

                return stack;
            }
        });
    }

}
