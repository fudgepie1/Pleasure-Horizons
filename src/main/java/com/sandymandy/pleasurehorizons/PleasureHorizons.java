package com.sandymandy.pleasurehorizons;

import com.sandymandy.pleasurehorizons.advancement.criterion.PleasureHorizonsCriteria;
import com.sandymandy.pleasurehorizons.block.PleasureHorizonsBlocks;
import com.sandymandy.pleasurehorizons.block.entity.PleasureHorizonsBlockEntities;
import com.sandymandy.pleasurehorizons.command.Commands;
import com.sandymandy.pleasurehorizons.component.PleasureHorizonsDataComponentTypes;
import com.sandymandy.pleasurehorizons.entity.ai.brain.GirlMemoryTypes;
import com.sandymandy.pleasurehorizons.item.PleasureHorizonsItemGroups;
import com.sandymandy.pleasurehorizons.item.PleasureHorizonsItems;
import com.sandymandy.pleasurehorizons.networking.PleasureHorizonsPackets;
import com.sandymandy.pleasurehorizons.registries.*;
import com.sandymandy.pleasurehorizons.util.json.CustomGirlLoader;
import com.sandymandy.pleasurehorizons.util.managers.TamedGirlManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PleasureHorizons implements ModInitializer {
	public static final String MOD_ID = "pleasurehorizons";
	public static final String MOD_NAME = "Pleasure Horizons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static Map<UUID, BlockPos> usedBeds = new HashMap<>();
	public static Map<UUID, UUID> activeScenes = new HashMap<>();

	@Override
	public void onInitialize() {
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (!world.isClient()) {
				TamedGirlManager.get(world).cleanupDeadGirls(world);
			}
		});

		PleasureHorizonsPackets.registerPackets();
		PleasureHorizonsPackets.registerC2SPackets();
		PleasureHorizonsItemGroups.registerItemGroups();
		PleasureHorizonsDataComponentTypes.registerDataComponentsTypes();
		PleasureHorizonsItems.registerItems();
		PleasureHorizonsBlockEntities.registerBlockEntities();
		PleasureHorizonsBlocks.registerBlocks();
		PleasureHorizonsCriteria.registerAdvancementCriteria();
		PleasureHorizonsTrackedDataRegistry.registerTrackedData();
		PleasureHorizonsSoundEventRegistry.registerSoundEvents();
		PleasureHorizonsScreenHandlerRegistry.registerScreenHandlers();
		PleasureHorizonsDispenserBehavior.registerDispenserBehavior();
		GirlMemoryTypes.registerMemoryTypes();
		GirlRegistry.registerGirls();
		Commands.register();
		CustomGirlLoader.register();

	}
}