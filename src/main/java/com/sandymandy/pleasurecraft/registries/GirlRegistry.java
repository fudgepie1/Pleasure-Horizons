package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.PleasureCraftEntities;
import com.sandymandy.pleasurecraft.entity.girls.*;
import com.sandymandy.pleasurecraft.util.ProfileFactory;
import net.minecraft.entity.EntityType;

public class GirlRegistry {
    public static final EntityType<LucyEntity> LUCY = PleasureCraftEntities.registerGirl("lucy", LucyEntity::new, 0.5f, 1.95f, LucyEntity::createAttributes);

    public static final EntityType<MikaEntity> MIKA = PleasureCraftEntities.registerGirl("mika", MikaEntity::new, 0.5f, 1.95f, MikaEntity::createAttributes);

    public static final EntityType<MomoEntity> MOMO = PleasureCraftEntities.registerGirl("momo", MomoEntity::new, 0.5f, 1.65f, MomoEntity::createAttributes);

    public static final EntityType<SlimeEntity> SLIME = PleasureCraftEntities.registerGirl("slime", SlimeEntity::new, 0.5f, 1.65f, SlimeEntity::createAttributes);

    public static final EntityType<JsonGirlEntity> JSON_GIRL  = PleasureCraftEntities.registerGirl(
            "json_girl",
            (type, world) -> new JsonGirlEntity(type, world), // fallback
            0.5f, 1.8f,
            false,
            JsonGirlEntity::createMobAttributes
    );


    public static final ProfileFactory<JsonGirlEntity> JSON_GIRL_FACTORY = JsonGirlEntity::new;


    public static void registerGirls() {
        PleasureCraft.LOGGER.info("Registering Girls for PleasureCraft");
        // Trigger attribute registration
        PleasureCraftEntities.registerAttributes();
    }
}
