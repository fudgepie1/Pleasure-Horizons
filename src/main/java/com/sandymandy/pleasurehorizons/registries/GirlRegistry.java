package com.sandymandy.pleasurehorizons.registries;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.entity.PleasureHorizonsEntities;
import com.sandymandy.pleasurehorizons.entity.girls.*;
import net.minecraft.entity.EntityType;

public class GirlRegistry {
    public static final EntityType<LucyEntity> LUCY = PleasureHorizonsEntities.registerGirl("lucy", LucyEntity::new, 0.5f, 1.95f, LucyEntity::createDefaultAttributes);

    public static final EntityType<MikaEntity> MIKA = PleasureHorizonsEntities.registerGirl("mika", MikaEntity::new, 0.5f, 1.95f, MikaEntity::createAttributes);

    public static final EntityType<MomoEntity> MOMO = PleasureHorizonsEntities.registerGirl("momo", MomoEntity::new, 0.5f, 1.65f, MomoEntity::createAttributes);

    public static final EntityType<SlimeEntity> SLIME = PleasureHorizonsEntities.registerGirl("slime", SlimeEntity::new, 0.5f, 1.95f, SlimeEntity::createAttributes);

    public static final EntityType<KoboldEntity> KOBOLD = PleasureHorizonsEntities.registerGirl("kobold", KoboldEntity::new, 0.5f, 1.75f, KoboldEntity::createAttributes);

    public static final EntityType<CoppieEntity> COPPIE = PleasureHorizonsEntities.registerGirl("coppie", CoppieEntity::new, 0.5f, 1.35f, CoppieEntity::createAttributes);

    public static final EntityType<CustomGirlEntity> CUSTOM_GIRL = PleasureHorizonsEntities.registerGirl(
            "custom_girl",
            CustomGirlEntity::new,
            0.5f, 1.95f,false,
            CustomGirlEntity::createDefaultAttributes
    );

    public static void registerGirls() {
        PleasureHorizons.LOGGER.info("Registering Girls for " + PleasureHorizons.MOD_NAME);
        // Trigger attribute registration
        PleasureHorizonsEntities.registerAttributes();
    }
}
