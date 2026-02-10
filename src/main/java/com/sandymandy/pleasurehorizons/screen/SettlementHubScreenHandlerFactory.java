package com.sandymandy.pleasurehorizons.screen;

import com.sandymandy.pleasurehorizons.settlement.Settlement;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class SettlementHubScreenHandlerFactory implements ExtendedScreenHandlerFactory<Settlement> {
    private final Settlement data;

    public SettlementHubScreenHandlerFactory(Settlement data) {
        this.data = data;
    }

    // Called on the server → sends data to client
    @Override
    public Settlement getScreenOpeningData(ServerPlayerEntity player) {
        return data;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("screen.pleasurecraft.settlement_hub");
    }

    // Called on the client
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SettlementHubScreenHandler(syncId, playerInventory, data);
    }
}
