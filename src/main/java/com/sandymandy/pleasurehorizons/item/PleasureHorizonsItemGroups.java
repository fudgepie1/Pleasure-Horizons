package com.sandymandy.pleasurehorizons.item;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.block.PleasureHorizonsBlocks;
import com.sandymandy.pleasurehorizons.entity.PleasureHorizonsEntities;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PleasureHorizonsItemGroups {
    public static final ItemGroup PLEASURE_HORIZONS_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PleasureHorizons.MOD_ID, "pleasurehorizons_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(PleasureHorizonsEntities.getFirstSpawnEgg()))
                    .displayName(Text.translatable("itemgroup.pleasurehorizons.pleasure_horizons_items"))
                    .entries((displayContext, entries) -> {
                        PleasureHorizonsEntities.getAllSpawnEggs().forEach(entries::add);
                        entries.add(PleasureHorizonsItems.SETTLEMENT_RECRUITMENT_TOKEN);
                        entries.add(PleasureHorizonsItems.MILK_JUG_EMPTY);
                        entries.add(PleasureHorizonsItems.MILK_JUG_FULL);
                        entries.add(PleasureHorizonsItems.MILK_JUG_HALF);
                    }).build());

    public static final ItemGroup PLEASURE_HORIZONS_BLOCK_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PleasureHorizons.MOD_ID, "pleasurehorizons_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(PleasureHorizonsBlocks.SETTLEMENT_HUB))
                    .displayName(Text.translatable("itemgroup.pleasurehorizons.pleasure_horizons_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(PleasureHorizonsBlocks.SETTLEMENT_HUB);
                        entries.add(PleasureHorizonsBlocks.HOUSE_BUILDING_TAG);
                        entries.add(PleasureHorizonsBlocks.CARVED_GIRL_PUMPKIN);
                    }).build());


    public static void registerItemGroups(){
        PleasureHorizons.LOGGER.info("Registering Item Groups for " + PleasureHorizons.MOD_NAME);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            PleasureHorizonsEntities.getAllSpawnEggs().forEach(entries::add);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(PleasureHorizonsItems.MILK_JUG_EMPTY);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(PleasureHorizonsItems.MILK_JUG_FULL);
            entries.add(PleasureHorizonsItems.MILK_JUG_HALF);
        });
    }
}
