package com.sandymandy.pleasurecraft.item;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.block.PleasureCraftBlocks;
import com.sandymandy.pleasurecraft.entity.PleasureCraftEntities;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PleasureCraftItemGroups {
    public static final ItemGroup PLEASURE_CRAFT_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PleasureCraft.MOD_ID, "pleasurecraft_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(PleasureCraftEntities.getFirstSpawnEgg()))
                    .displayName(Text.translatable("itemgroup.pleasurecraft.pleasure_craft_items"))
                    .entries((displayContext, entries) -> {
                        PleasureCraftEntities.getAllSpawnEggs().forEach(entries::add);
                        entries.add(PleasureCraftItems.SETTLEMENT_RECRUITMENT_TOKEN);
                        entries.add(PleasureCraftItems.MILK_JUG_EMPTY);
                        entries.add(PleasureCraftItems.MILK_JUG_FULL);
                        entries.add(PleasureCraftItems.MILK_JUG_HALF);
                    }).build());

    public static final ItemGroup PLEASURE_CRAFT_BLOCK_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PleasureCraft.MOD_ID, "pleasurecraft_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(PleasureCraftBlocks.SETTLEMENT_HUB))
                    .displayName(Text.translatable("itemgroup.pleasurecraft.pleasure_craft_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(PleasureCraftBlocks.SETTLEMENT_HUB);
                        entries.add(PleasureCraftBlocks.HOUSE_BUILDING_TAG);
                        entries.add(PleasureCraftBlocks.CARVED_GIRL_PUMPKIN);
                    }).build());


    public static void registerItemGroups(){
        PleasureCraft.LOGGER.info("Registering Item Groups for PleasureCraft");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            PleasureCraftEntities.getAllSpawnEggs().forEach(entries::add);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(PleasureCraftItems.MILK_JUG_EMPTY);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(PleasureCraftItems.MILK_JUG_FULL);
            entries.add(PleasureCraftItems.MILK_JUG_HALF);
        });
    }
}
