package com.sandymandy.pleasurecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;

public class VillageManager extends PersistentState {

    public static final Codec<VillageManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(VillageData.CODEC).fieldOf("villages").forGetter(VillageManager::getVillageList)
    ).apply(instance, VillageManager::new));

    // Persistent state type registration (correct generic type signature)
    public static final PersistentStateType<VillageManager> TYPE = new PersistentStateType<>(
            "pleasurecraft_villages",
            (ctx) -> new VillageManager(),
            (ctx) -> CODEC,
            DataFixTypes.LEVEL
    );
    private final Map<UUID, VillageData> villages = new HashMap<>();

    // Default constructor for new save files
    public VillageManager() {}

    public VillageManager(List<VillageData> list) {
        for (VillageData v : list) {
            villages.put(v.getId(), v);
        }
    }

    public List<VillageData> getVillageList() {
        return new ArrayList<>(villages.values());
    }

    public static VillageManager get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(TYPE);
    }

    public VillageData createVillage(BlockPos pos, String name) {
        VillageData village = new VillageData(pos, name);
        villages.put(village.getId(), village);
        markDirty();
        return village;
    }

    public VillageData getVillage(UUID id) {
        return villages.get(id);
    }

    public void removeVillage(UUID id) {
        villages.remove(id);
        markDirty();
    }

    public void tick(World world) {
        for (VillageData village : villages.values()) {
            village.tick(world);
        }
    }
}
