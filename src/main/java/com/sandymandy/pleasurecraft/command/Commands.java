package com.sandymandy.pleasurecraft.command;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Commands {
    public static void register(){
        PleasureCraft.LOGGER.info("Registering Commands for PleasureCraft");
        CommandRegistrationCallback.EVENT.register(CustomGirlSpawnCommand::register);
        CommandRegistrationCallback.EVENT.register(LocateTamedGirls::register);
    }

}
