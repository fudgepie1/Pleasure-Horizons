package com.sandymandy.pleasurecraft.entity;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.BiaEntity;
import com.sandymandy.pleasurecraft.entity.girls.LucyEntity;
import com.sandymandy.pleasurecraft.entity.girls.ZhongeziEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class PleasureCraftEntities {
    public static final Identifier LUCY_ID = Identifier.of(PleasureCraft.MOD_ID, "lucy");
    public static final Identifier BIA_ID = Identifier.of(PleasureCraft.MOD_ID, "bia");
    public static final Identifier ZHONGEZI_ID = Identifier.of(PleasureCraft.MOD_ID, "zhongezi");


    private static final RegistryKey<EntityType<?>> LUCY_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, LUCY_ID);

    private static final RegistryKey<EntityType<?>> BIA_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, BIA_ID);

    private static final RegistryKey<EntityType<?>> ZHONGEZI_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, ZHONGEZI_ID);

    public static final EntityType<LucyEntity> LUCY = Registry.register(
            Registries.ENTITY_TYPE,
            LUCY_ID,
            EntityType.Builder.create(LucyEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.5f, 1.95f)
                    .build(LUCY_KEY)
    );

    public static final EntityType<BiaEntity> BIA = Registry.register(
            Registries.ENTITY_TYPE,
            BIA_ID,
            EntityType.Builder.create(BiaEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.5f, 1.65f)
                    .build(BIA_KEY)
    );

    public static final EntityType<ZhongeziEntity> ZHONGEZI = Registry.register(
            Registries.ENTITY_TYPE,
            ZHONGEZI_ID,
            EntityType.Builder.create(ZhongeziEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.5f, 1.65f)
                    .build(ZHONGEZI_KEY)
    );

    public static void register() {
        // Attributes
        FabricDefaultAttributeRegistry.register(LUCY, LucyEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BIA, BiaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ZHONGEZI, ZhongeziEntity.createAttributes());
    }
}

