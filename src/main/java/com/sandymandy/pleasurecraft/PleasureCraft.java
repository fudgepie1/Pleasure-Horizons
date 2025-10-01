package com.sandymandy.pleasurecraft;

import com.sandymandy.pleasurecraft.advancement.criterion.PleasureCraftCriteria;
import com.sandymandy.pleasurecraft.item.PleasureCraftItemGroups;
import com.sandymandy.pleasurecraft.item.PleasureCraftItems;
import com.sandymandy.pleasurecraft.networking.PleasureCraftPackets;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandler;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import com.sandymandy.pleasurecraft.registries.PleasureCraftSoundEvents;
import com.sandymandy.pleasurecraft.registries.PleasureCraftTrackedData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PleasureCraft implements ModInitializer {
	public static final String MOD_ID = "pleasurecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger("PleasureCraft");

	public static final ExtendedScreenHandlerType<GirlInventoryScreenHandler, GirlScreenData> GIRL_INVENTORY_SCREEN_HANDLER =
			Registry.register(
					Registries.SCREEN_HANDLER,
					Identifier.of(PleasureCraft.MOD_ID, "girl_inventory_screen"),
					new ExtendedScreenHandlerType<>(GirlInventoryScreenHandler::new, GirlScreenData.PACKET_CODEC)
			);

/*	public static final ExtendedScreenHandlerType<GirlInventoryScreenHandler, GirlScreenData> GIRL_TALK_SCREEN_HANDLER =
			Registry.register(
					Registries.SCREEN_HANDLER,
					Identifier.of(PleasureCraft.MOD_ID, "girl_talk_screen"),
					new ExtendedScreenHandlerType<>(GirlInventoryScreenHandler::new, GirlScreenData.PACKET_CODEC)
			);
*/

	@Override
	public void onInitialize() {
		GirlRegistry.register();
		PleasureCraftItemGroups.registerItemGroups();
		PleasureCraftItems.registerModItems();
		PleasureCraftPackets.registerPackets();
		PleasureCraftPackets.registerC2SPackets();
		PleasureCraftCriteria.registerAdvancementCriteria();
		PleasureCraftTrackedData.registerTrackedData();
		PleasureCraftSoundEvents.registerSounds();
	}

	public record GirlScreenData(int entityId) {
		public static final PacketCodec<RegistryByteBuf, GirlScreenData> PACKET_CODEC = PacketCodec.tuple(
				PacketCodecs.VAR_INT,
				GirlScreenData::entityId,
				GirlScreenData::new
		);
	}
}