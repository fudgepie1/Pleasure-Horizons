package com.sandymandy.pleasurecraft;

import com.sandymandy.pleasurecraft.advancement.criterion.PleasureCraftCriteria;
import com.sandymandy.pleasurecraft.item.PleasureCraftItemGroups;
import com.sandymandy.pleasurecraft.item.PleasureCraftItems;
import com.sandymandy.pleasurecraft.networking.PleasureCraftPackets;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import com.sandymandy.pleasurecraft.registries.PleasureCraftScreenHandlerRegistry;
import com.sandymandy.pleasurecraft.registries.PleasureCraftSoundEventRegistry;
import com.sandymandy.pleasurecraft.registries.PleasureCraftTrackedDataRegistry;
import com.sandymandy.pleasurecraft.village.VillageManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PleasureCraft implements ModInitializer {
	public static final String MOD_ID = "pleasurecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger("PleasureCraft");

	@Override
	public void onInitialize() {
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (!world.isClient()) {
				VillageManager.get(world).tick(world);
			}
		});

		GirlRegistry.register();
		PleasureCraftItemGroups.registerItemGroups();
		PleasureCraftItems.registerModItems();
		PleasureCraftPackets.registerPackets();
		PleasureCraftPackets.registerC2SPackets();
		PleasureCraftCriteria.registerAdvancementCriteria();
		PleasureCraftTrackedDataRegistry.registerTrackedData();
		PleasureCraftSoundEventRegistry.registerSoundEvents();
		PleasureCraftScreenHandlerRegistry.register();
	}
}