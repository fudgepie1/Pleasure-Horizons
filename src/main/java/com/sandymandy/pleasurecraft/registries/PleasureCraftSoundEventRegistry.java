    package com.sandymandy.pleasurecraft.registries;

    import com.sandymandy.pleasurecraft.PleasureCraft;
    import com.sandymandy.pleasurecraft.util.variables.AIMode;
    import io.netty.buffer.ByteBuf;
    import net.minecraft.network.codec.PacketCodec;
    import net.minecraft.network.codec.PacketCodecs;
    import net.minecraft.registry.Registries;
    import net.minecraft.registry.Registry;
    import net.minecraft.sound.SoundEvent;
    import net.minecraft.util.Identifier;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;

    public class PleasureCraftSoundEventRegistry {

//  MISC________________________________________________________________________________________________________________
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


//  LUCY________________________________________________________________________________________________________________
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

//  MIKA________________________________________________________________________________________________________________
        public static final SoundEvent MIKA_AFTERSSESSIONMOAN = registerSound("mika.aftersessionmoan");
        public static final SoundEvent MIKA_AHH = registerSound("mika.ahh");
        public static final SoundEvent MIKA_BJMOAN = registerSound("mika.bjmoan");
        public static final SoundEvent MIKA_COMETOMOMMY = registerSound("mika.cometomommy");
        public static final SoundEvent MIKA_GIGGLE = registerSound("mika.giggle");
        public static final SoundEvent MIKA_GOODBOY = registerSound("mika.goodboy");
        public static final SoundEvent MIKA_HAPPYOH = registerSound("mika.happyoh");
        public static final SoundEvent MIKA_HEAVYBREATHING = registerSound("mika.heavybreathing");
        public static final SoundEvent MIKA_HMPH = registerSound("mika.hmph");
        public static final SoundEvent MIKA_HUH = registerSound("mika.huh");
        public static final SoundEvent MIKA_LIGHTBREATHING = registerSound("mika.lightbreathing");
        public static final SoundEvent MIKA_LIPSOUND = registerSound("mika.lipsound");
        public static final SoundEvent MIKA_MMM = registerSound("mika.mmm");
        public static final SoundEvent MIKA_MOAN = registerSound("mika.moan");
        public static final SoundEvent MIKA_MOMMYSHORNNY = registerSound("mika.mommyhorny");
        public static final SoundEvent MIKA_SADOH = registerSound("mika.sadoh");
        public static final SoundEvent MIKA_SIGH = registerSound("mika.sigh");

//  MOMO________________________________________________________________________________________________________________
        public static final SoundEvent MOMO_AHH = registerSound("momo.ahh");
        public static final SoundEvent MOMO_BJMOAN = registerSound("momo.bjmoan");
        public static final SoundEvent MOMO_MOAN = registerSound("momo.moan");
        public static final SoundEvent MOMO_GIGGLE = registerSound("momo.giggle");
        public static final SoundEvent MOMO_HAPPYOH = registerSound("momo.happyoh");
        public static final SoundEvent MOMO_SADOH = registerSound("momo.sadoh");
        public static final SoundEvent MOMO_SIGH = registerSound("momo.sigh");
        public static final SoundEvent MOMO_HEY = registerSound("momo.hey");
        public static final SoundEvent MOMO_HMPH = registerSound("momo.hmph");
        public static final SoundEvent MOMO_HUH = registerSound("momo.huh");
        public static final SoundEvent MOMO_MMM = registerSound("momo.mmm");
        public static final SoundEvent MOMO_LIGHTBREATHING = registerSound("momo.lightbreathing");

        public static void registerSoundEvents() {
            PleasureCraft.LOGGER.info("Registering SoundEvents for PleasureCraft");
            SceneKeyframeEventRegistry.registerSoundEvents();
        }

        private static SoundEvent registerSound(String soundPath) {
            Identifier id = Identifier.of(PleasureCraft.MOD_ID, soundPath);
            SoundEvent event = Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
            return event;
        }

        public enum SoundGroup {
                AFTERSSESSIONMOAN(Map.of("lucy", LUCY_AFTERSSESSIONMOAN, "mika", MIKA_AFTERSSESSIONMOAN)),
                AHH(Map.of("lucy", LUCY_AHH, "mika", MIKA_AHH, "momo", MOMO_AHH)),
                GIGGLE(Map.of("lucy", LUCY_GIGGLE, "mika", MIKA_GIGGLE, "momo", MOMO_GIGGLE)),
                HAPPOH(Map.of("lucy", LUCY_HAPPOH, "mika", MIKA_HAPPYOH, "momo", MOMO_HAPPYOH)),
                HEAVYBREATHING(Map.of("lucy", LUCY_HEAVYBREATHING, "mika", MIKA_HEAVYBREATHING)),
                HMPH(Map.of("lucy", LUCY_HMPH, "mika", MIKA_HMPH, "momo", MOMO_HMPH)),
                HUH(Map.of("lucy", LUCY_HUH, "mika", MIKA_HUH, "momo", MOMO_HUH)),
                LIGHTBREATHING(Map.of("lucy", LUCY_LIGHTBREATHING, "mika", MIKA_LIGHTBREATHING, "momo", MOMO_LIGHTBREATHING)),
                LIPSOUND(Map.of("lucy", LUCY_LIPSOUND, "mika", MIKA_LIPSOUND)),
                MMM(Map.of("lucy", LUCY_MMM, "mika", MIKA_MMM, "momo", MOMO_MMM)),
                MOAN(Map.of("lucy", LUCY_MOAN, "mika", MIKA_MOAN, "momo", MOMO_MOAN)),
                SADOH(Map.of("lucy", LUCY_SADOH, "mika", MIKA_SADOH, "momo", MOMO_SADOH)),
                SIGH(Map.of("lucy", LUCY_SIGH, "mika", MIKA_SIGH, "momo", MOMO_SIGH)),
                HEY(Map.of("momo", MOMO_HEY)),
                COMETOMOMMY(Map.of("mika", MIKA_COMETOMOMMY)),
                MOMMYSHORNNY(Map.of("mika", MIKA_MOMMYSHORNNY)),
                GOODBOY(Map.of("mika", MIKA_GOODBOY)),
                BJMOAN(Map.of("lucy", LUCY_BJMOAN, "mika", MIKA_BJMOAN, "momo", MOMO_BJMOAN));

                private final Map<String, SoundEvent> girlToSoundMap;

                SoundGroup(Map<String, SoundEvent> map) {
                        this.girlToSoundMap = map;
                }

                public SoundEvent getSound(String girlId) {
                        // Returns the sound for the specific girl, or null if she doesn't have one in this group
                        return girlToSoundMap.get(girlId.toLowerCase());
                }
        }

    }
