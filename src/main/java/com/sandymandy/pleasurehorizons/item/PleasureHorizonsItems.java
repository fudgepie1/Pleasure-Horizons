package com.sandymandy.pleasurehorizons.item;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.item.items.SettlementRecruitContract;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

public class PleasureHorizonsItems {

    private static Item.Settings applyMilkJugComponents(Item.Settings settings) {
        return settings
                .maxCount(1)
                .component(DataComponentTypes.CONSUMABLE, ConsumableComponent.builder()
                        .useAction(UseAction.DRINK)
                        .sound(SoundEvents.ENTITY_GENERIC_DRINK)
                        .consumeParticles(false)
                        .consumeEffect(new ApplyEffectsConsumeEffect(List.of(
                                new StatusEffectInstance(StatusEffects.NIGHT_VISION, 900, 1),
                                new StatusEffectInstance(StatusEffects.JUMP_BOOST, 900, 1),
                                new StatusEffectInstance(StatusEffects.STRENGTH, 900, 1),
                                new StatusEffectInstance(StatusEffects.SPEED, 900, 1)
                        )))
                        .build());
    }

    public static Item SETTLEMENT_RECRUITMENT_TOKEN = registerItem("settlement_recruit_contract",
            settings -> new SettlementRecruitContract(settings.maxCount(16)));

    public static Item MILK_JUG_EMPTY = registerItem("milk_jug_empty", settings -> new Item(settings.maxCount(4)));

    public static Item MILK_JUG_HALF = registerItem("milk_jug_half", settings -> new Item(applyMilkJugComponents(settings).useRemainder(PleasureHorizonsItems.MILK_JUG_EMPTY)));

    public static Item MILK_JUG_FULL = registerItem("milk_jug_full", settings -> new Item(applyMilkJugComponents(settings).useRemainder(PleasureHorizonsItems.MILK_JUG_HALF)));

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(PleasureHorizons.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PleasureHorizons.MOD_ID, name)))));
    }

    public static void registerItems(){
        PleasureHorizons.LOGGER.info("Registering Items for " + PleasureHorizons.MOD_NAME);

    }
}
