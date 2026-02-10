package com.sandymandy.pleasurehorizons.block;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.block.blocks.CarvedGirlPumpkinBlock;
import com.sandymandy.pleasurehorizons.block.blocks.HouseBuildingTagBlock;
import com.sandymandy.pleasurehorizons.block.blocks.SettlementHubBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class PleasureHorizonsBlocks {
    public static Block SETTLEMENT_HUB = registerBlock("settlement_hub",
            properties -> new SettlementHubBlock(properties.strength(3.5f, 1200.0F).sounds(BlockSoundGroup.LODESTONE).requiresTool()));

    public static Block HOUSE_BUILDING_TAG = registerBlock("house_tag",
            properties -> new HouseBuildingTagBlock(properties.strength(1.0F, 100.0F).pistonBehavior(PistonBehavior.DESTROY)));

    public static Block CARVED_GIRL_PUMPKIN = registerBlock("carved_girl_pumpkin",
            properties -> new CarvedGirlPumpkinBlock(properties
                    .mapColor(MapColor.ORANGE)
                    .strength(1.0F)
                    .sounds(BlockSoundGroup.WOOD)
                    .allowsSpawning(Blocks::always)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> function) {
        Block toRegister = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(PleasureHorizons.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(Registries.BLOCK, Identifier.of(PleasureHorizons.MOD_ID, name), toRegister);
    }

    private static Block registerBlockWithoutBlockItem(String name, Function<AbstractBlock.Settings, Block> function) {
        return Registry.register(Registries.BLOCK, Identifier.of(PleasureHorizons.MOD_ID, name),
                function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(PleasureHorizons.MOD_ID, name)))));
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(PleasureHorizons.MOD_ID, name),
                new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PleasureHorizons.MOD_ID, name)))));
    }

    public static void registerBlocks() {
        PleasureHorizons.LOGGER.info("Registering Block for " + PleasureHorizons.MOD_NAME);
    }
}
