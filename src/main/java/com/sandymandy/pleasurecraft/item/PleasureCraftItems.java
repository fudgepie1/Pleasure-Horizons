package com.sandymandy.pleasurecraft.item;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.PleasureCraftEntities;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;


public class PleasureCraftItems {


    public static final Item LUCY_SPAWN_EGG = registerItem("lucy_spawn_egg",
            setting -> new SpawnEggItem(PleasureCraftEntities.LUCY , setting));

    public static final Item BIA_SPAWN_EGG = registerItem("bia_spawn_egg",
            setting -> new SpawnEggItem(PleasureCraftEntities.BIA, setting));

    public static final Item ZHONGEZI_SPAWN_EGG = registerItem("zhongzi_spawn_egg",
            setting -> new SpawnEggItem(PleasureCraftEntities.ZHONGEZI, setting));


    private static Item registerItem(String name, Function<Item.Settings, Item> factory) {
        Identifier id = Identifier.of(PleasureCraft.MOD_ID, name);
        return Registry.register(Registries.ITEM, id,
                factory.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
    }


    public static void registerModItems(){
        PleasureCraft.LOGGER.info("Registering Items for PleasureCraft");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(LUCY_SPAWN_EGG);
            entries.add(BIA_SPAWN_EGG);
            entries.add(ZHONGEZI_SPAWN_EGG);
        });
    }
}
