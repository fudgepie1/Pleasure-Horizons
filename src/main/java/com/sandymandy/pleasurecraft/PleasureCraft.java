package com.sandymandy.pleasurecraft;

import com.sandymandy.pleasurecraft.advancement.criterion.PleasureCraftCriteria;
import com.sandymandy.pleasurecraft.item.PleasureCraftItemGroups;
import com.sandymandy.pleasurecraft.item.PleasureCraftItems;
import com.sandymandy.pleasurecraft.networking.PleasureCraftPackets;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import com.sandymandy.pleasurecraft.registries.PleasureCraftSoundEvents;
import com.sandymandy.pleasurecraft.registries.PleasureCraftTrackedData;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PleasureCraft implements ModInitializer {
	public static final String MOD_ID = "pleasurecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger("PleasureCraft");

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
}