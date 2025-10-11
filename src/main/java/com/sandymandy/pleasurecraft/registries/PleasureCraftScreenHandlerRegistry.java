package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.PleasureCraftClient;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PleasureCraftScreenHandlerRegistry {
    public static final ExtendedScreenHandlerType<GirlInventoryScreenHandler, PleasureCraftClient.GirlScreenData> GIRL_INVENTORY_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(PleasureCraft.MOD_ID, "girl_inventory_screen"),
                    new ExtendedScreenHandlerType<>(GirlInventoryScreenHandler::new, PleasureCraftClient.GirlScreenData.PACKET_CODEC)
            );

/*	public static final ExtendedScreenHandlerType<GirlInventoryScreenHandler, GirlScreenData> GIRL_TALK_SCREEN_HANDLER =
			Registry.register(
					Registries.SCREEN_HANDLER,
					Identifier.of(PleasureCraft.MOD_ID, "girl_talk_screen"),
					new ExtendedScreenHandlerType<>(GirlInventoryScreenHandler::new, GirlScreenData.PACKET_CODEC)
			);
*/

    public static void register(){
        PleasureCraft.LOGGER.info("Registering Screen Handlers for PleasureCraft");
    }
}
