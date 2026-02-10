package com.sandymandy.pleasurehorizons.component;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class PleasureHorizonsDataComponentTypes {

    public static final ComponentType<UUID> SETTLEMENT_UUID = register("settlement_uuid", builder -> builder.codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(PleasureHorizons.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentsTypes() {
        PleasureHorizons.LOGGER.info("Registering DataComponentTypes for " + PleasureHorizons.MOD_NAME);
    }
}
