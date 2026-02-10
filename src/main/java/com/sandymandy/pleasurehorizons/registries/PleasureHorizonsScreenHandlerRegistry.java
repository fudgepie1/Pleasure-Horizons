package com.sandymandy.pleasurehorizons.registries;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.PleasureHorizonsClient;
import com.sandymandy.pleasurehorizons.screen.GirlInventoryScreenHandler;
import com.sandymandy.pleasurehorizons.screen.SettlementHubScreenHandler;
import com.sandymandy.pleasurehorizons.settlement.Settlement;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class PleasureHorizonsScreenHandlerRegistry {
    public static final ExtendedScreenHandlerType<GirlInventoryScreenHandler, PleasureHorizonsClient.GirlScreenData> GIRL_INVENTORY_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(PleasureHorizons.MOD_ID, "girl_inventory_screen"),
                    new ExtendedScreenHandlerType<>(GirlInventoryScreenHandler::new, PleasureHorizonsClient.GirlScreenData.PACKET_CODEC)
            );

    public static final ScreenHandlerType<SettlementHubScreenHandler> SETTLEMENT_HUB_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(PleasureHorizons.MOD_ID, "settlement_hub"),
                    new ExtendedScreenHandlerType<>(SettlementHubScreenHandler::new, Settlement.PACKET_CODEC));


    public static void registerScreenHandlers(){
        PleasureHorizons.LOGGER.info("Registering Screen Handlers for " + PleasureHorizons.MOD_NAME);
    }
}
