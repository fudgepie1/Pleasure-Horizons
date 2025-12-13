package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.PleasureCraftEntities;
import com.sandymandy.pleasurecraft.entity.girls.*;
import net.minecraft.entity.EntityType;

public class GirlRegistry {
    public static final EntityType<LucyEntity> LUCY = PleasureCraftEntities.registerGirl("lucy", LucyEntity::new, 0.5f, 1.95f, LucyEntity::createDefaultAttributes);

    public static final EntityType<MikaEntity> MIKA = PleasureCraftEntities.registerGirl("mika", MikaEntity::new, 0.5f, 1.95f, MikaEntity::createAttributes);

    public static final EntityType<MomoEntity> MOMO = PleasureCraftEntities.registerGirl("momo", MomoEntity::new, 0.5f, 1.65f, MomoEntity::createAttributes);

    public static final EntityType<SlimeEntity> SLIME = PleasureCraftEntities.registerGirl("slime", SlimeEntity::new, 0.5f, 1.95f, SlimeEntity::createAttributes);

    public static final EntityType<KoboldEntity> KOBOLD = PleasureCraftEntities.registerGirl("kobold", KoboldEntity::new, 0.5f, 1.75f, KoboldEntity::createAttributes);

    public static final EntityType<CustomGirlEntity> CUSTOM_GIRL = PleasureCraftEntities.registerGirl(
            "custom_girl",
            CustomGirlEntity::new,
            0.5f, 1.95f,false,
            CustomGirlEntity::createDefaultAttributes
    );

    public static void registerGirls() {
        PleasureCraft.LOGGER.info("Registering Girls for PleasureCraft");
        // Trigger attribute registration
        PleasureCraftEntities.registerAttributes();
    }
}
