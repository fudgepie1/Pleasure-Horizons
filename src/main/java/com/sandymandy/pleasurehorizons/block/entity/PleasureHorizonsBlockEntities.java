package com.sandymandy.pleasurehorizons.block.entity;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.block.PleasureHorizonsBlocks;
import com.sandymandy.pleasurehorizons.block.entity.entities.AbstractBuildingTagBlockEntity;
import com.sandymandy.pleasurehorizons.block.entity.entities.SettlementHubBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PleasureHorizonsBlockEntities {
    public static BlockEntityType<SettlementHubBlockEntity> SETTLEMENT_HUB_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(PleasureHorizons.MOD_ID, "settlement_hub"),
                    FabricBlockEntityTypeBuilder.create(SettlementHubBlockEntity::new, PleasureHorizonsBlocks.SETTLEMENT_HUB).build()
    );

    public static BlockEntityType<AbstractBuildingTagBlockEntity> BUILDING_TAG_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(PleasureHorizons.MOD_ID, "building_tag"),
                    FabricBlockEntityTypeBuilder.create(
                            (pos, state) -> new AbstractBuildingTagBlockEntity(pos, state, null),
                            PleasureHorizonsBlocks.HOUSE_BUILDING_TAG
                    ).build()
            );

    public static void registerBlockEntities() {
        PleasureHorizons.LOGGER.info("Registering Block Entities for " + PleasureHorizons.MOD_NAME);
    }
}