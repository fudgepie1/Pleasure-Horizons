package com.sandymandy.pleasurecraft;

import com.sandymandy.pleasurecraft.advancement.criterion.PleasureCraftCriteria;
import com.sandymandy.pleasurecraft.block.PleasureCraftBlocks;
import com.sandymandy.pleasurecraft.block.entity.PleasureCraftBlockEntities;
import com.sandymandy.pleasurecraft.command.Commands;
import com.sandymandy.pleasurecraft.component.PleasureCraftDataComponentTypes;
import com.sandymandy.pleasurecraft.entity.ai.brain.GirlMemoryTypes;
import com.sandymandy.pleasurecraft.item.PleasureCraftItemGroups;
import com.sandymandy.pleasurecraft.item.PleasureCraftItems;
import com.sandymandy.pleasurecraft.networking.PleasureCraftPackets;
import com.sandymandy.pleasurecraft.registries.*;
import com.sandymandy.pleasurecraft.util.managers.SettlementManager;
import com.sandymandy.pleasurecraft.util.json.CustomGirlLoader;
import com.sandymandy.pleasurecraft.util.managers.TamedGirlManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PleasureCraft implements ModInitializer {
	public static final String MOD_ID = "pleasurecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger("PleasureCraft");
	public static Map<UUID, BlockPos> usedBeds = new HashMap<>();
	public static Map<UUID, UUID> activeScenes = new HashMap<>();

	@Override
	public void onInitialize() {
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (!world.isClient()) {
				TamedGirlManager.get(world).cleanupDeadGirls(world);
			}
		});

		PleasureCraftPackets.registerPackets();
		PleasureCraftPackets.registerC2SPackets();
		PleasureCraftItemGroups.registerItemGroups();
		PleasureCraftDataComponentTypes.registerDataComponentsTypes();
		PleasureCraftItems.registerItems();
		PleasureCraftBlockEntities.registerBlockEntities();
		PleasureCraftBlocks.registerBlocks();
		PleasureCraftCriteria.registerAdvancementCriteria();
		PleasureCraftTrackedDataRegistry.registerTrackedData();
		PleasureCraftSoundEventRegistry.registerSoundEvents();
		PleasureCraftScreenHandlerRegistry.registerScreenHandlers();
		PleasureCraftDispenserBehavior.registerDispenserBehavior();
		GirlMemoryTypes.registerMemoryTypes();
		GirlRegistry.registerGirls();
		CustomGirlLoader.register();
		Commands.register();

	}
}