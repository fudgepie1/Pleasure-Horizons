    package com.sandymandy.pleasurecraft.util;

    import com.sandymandy.pleasurecraft.PleasureCraft;
    import net.minecraft.registry.Registries;
    import net.minecraft.registry.Registry;
    import net.minecraft.sound.SoundEvent;
    import net.minecraft.util.Identifier;

    public class PleasureCraftSounds {

//  MISC_________________________________________________________________________________________________________________
        public static final SoundEvent BEDRUSTLE = registerSound("misc.bedrustle");
        public static final SoundEvent BELLJINGLE = registerSound("misc.belljingle");
        public static final SoundEvent CLAP = registerSound("misc.clap");
        public static final SoundEvent CUMINFLATION = registerSound("misc.cuminflation");
        public static final SoundEvent FLAP = registerSound("misc.flap");
        public static final SoundEvent INSERTS = registerSound("misc.inserts");
        public static final SoundEvent POUNDING = registerSound("misc.pounding");
        public static final SoundEvent SLAP = registerSound("misc.slap");
        public static final SoundEvent SLIDE = registerSound("misc.slide");
        public static final SoundEvent SMALLINSERTS = registerSound("misc.smallinserts");
        public static final SoundEvent TOUCH = registerSound("misc.touch");
        public static final SoundEvent PLOB = registerSound("misc.plob");


//  LUCY_________________________________________________________________________________________________________________
        public static final SoundEvent LUCY_AFTERSSESSIONMOAN = registerSound("lucy.aftersessionmoan");
        public static final SoundEvent LUCY_AHH = registerSound("lucy.ahh");
        public static final SoundEvent LUCY_BJMOAN = registerSound("lucy.bjmoan");
        public static final SoundEvent LUCY_GIGGLE = registerSound("lucy.giggle");
        public static final SoundEvent LUCY_HAPPOH = registerSound("lucy.happyoh");
        public static final SoundEvent LUCY_HEAVYBREATHING = registerSound("lucy.heavybreathing");
        public static final SoundEvent LUCY_HMPH = registerSound("lucy.hmph");
        public static final SoundEvent LUCY_HUH = registerSound("lucy.huh");
        public static final SoundEvent LUCY_LIGHTBREATHING = registerSound("lucy.lightbreathing");
        public static final SoundEvent LUCY_LIPSOUND = registerSound("lucy.lipsound");
        public static final SoundEvent LUCY_MMM = registerSound("lucy.mmm");
        public static final SoundEvent LUCY_MOAN = registerSound("lucy.moan");
        public static final SoundEvent LUCY_SADOH = registerSound("lucy.sadoh");
        public static final SoundEvent LUCY_SIGH = registerSound("lucy.sigh");

        public static void registerSounds() {
            SceneKeyframeRegistry.registerSoundEvents();

            PleasureCraft.LOGGER.info("Registered sounds for PleasureCraft.");
        }

        private static SoundEvent registerSound(String soundPath) {
            Identifier id = Identifier.of(PleasureCraft.MOD_ID, soundPath);
            return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
        }
    }
