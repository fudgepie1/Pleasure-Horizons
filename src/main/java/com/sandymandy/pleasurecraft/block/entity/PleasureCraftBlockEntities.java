package com.sandymandy.pleasurecraft.block.entity;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.block.PleasureCraftBlocks;
import com.sandymandy.pleasurecraft.block.entity.entities.VillageCoreBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PleasureCraftBlockEntities {
    public static BlockEntityType<VillageCoreBlockEntity> VILLAGE_CORE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(PleasureCraft.MOD_ID, "village_core"),
                    FabricBlockEntityTypeBuilder.create(VillageCoreBlockEntity::new, PleasureCraftBlocks.VILLAGE_CORE).build()
    );

    public static void registerBlockEntities() {
        PleasureCraft.LOGGER.info("Registering Block Entities for " + PleasureCraft.MOD_ID);
    }
}