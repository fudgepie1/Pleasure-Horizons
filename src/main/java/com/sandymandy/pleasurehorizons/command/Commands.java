package com.sandymandy.pleasurehorizons.command;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Commands {
    public static void register(){
        PleasureHorizons.LOGGER.info("Registering Commands for " + PleasureHorizons.MOD_NAME);
        CommandRegistrationCallback.EVENT.register(GirlsCommand::register);
    }

}
