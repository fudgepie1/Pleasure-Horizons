package com.sandymandy.pleasurecraft.util.managers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sandymandy.pleasurecraft.entity.base.tamable.TameableGirlEntity;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.*;

public class TamedGirlManager extends PersistentState {

    // ownerUUID → list of girl entries owned by that player
    private final Map<UUID, List<UUID>> girls = new HashMap<>();

    // === MANAGER CODEC ===
    public static final Codec<TamedGirlManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Uuids.CODEC, Codec.list(Uuids.CODEC))
                            .fieldOf("girls")
                            .forGetter(manager -> manager.girls)
            ).apply(instance, map -> {
                TamedGirlManager manager = new TamedGirlManager();

                // Convert ALL loaded lists into mutable array lists
                map.forEach((owner, list) ->
                        manager.girls.put(owner, new ArrayList<>(list))
                );

                return manager;
            })
    );

    public static final PersistentStateType<TamedGirlManager> TYPE =
            new PersistentStateType<>(
                    "pleasurecraft_girls",
                    ctx -> new TamedGirlManager(),
                    ctx -> CODEC,
                    DataFixTypes.LEVEL
            );

    public static TamedGirlManager get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(TYPE);
    }

    // === REGISTER GIRL ONLY WHEN TAMED ===
    public void registerGirl(TameableGirlEntity girl) {
        if (!girl.isTamed()) return;

        LivingEntity owner = girl.getOwner();
        if (owner == null) return; // safety
        UUID ownerId = owner.getUuid();
        girls.computeIfAbsent(ownerId, k -> new ArrayList<>());

        // Avoid duplicates
        girls.get(ownerId).removeIf(e -> e.equals(girl.getUuid()));

        girls.get(ownerId).add(girl.getUuid());

        markDirty();
    }

    // === REMOVE GIRL BY GIRL UUID FROM ANY OWNER ===
    public void removeGirl(UUID girlId) {
        for (UUID owner : girls.keySet()) {
            girls.get(owner).removeIf(e -> e.equals(girlId));
        }
        markDirty();
    }

    // === GET ALL OWNED BY PLAYER ===
    public List<UUID> getGirlsOwnedBy(UUID owner) {
        return girls.getOrDefault(owner, Collections.emptyList());
    }

    // === ALL GIRLS (flattened) ===
    public Collection<UUID> getAllGirls() {
        List<UUID> list = new ArrayList<>();
        for (List<UUID> entries : girls.values()) list.addAll(entries);
        return list;
    }

    public boolean containsGirl(UUID uuid) {
        return girls.values().stream()
                .flatMap(List::stream)
                .anyMatch(entry -> entry.equals(uuid));
    }

    public void cleanupDeadGirls(ServerWorld world) {
        for (UUID owner : new HashSet<>(girls.keySet())) {
            List<UUID> list = girls.get(owner);

            list.removeIf(entry ->
                    world.getEntity(entry) == null ||    // not loaded
                            !world.getEntity(entry).isAlive()     // dead
            );

            if (list.isEmpty()) {
                girls.remove(owner);
            }
        }

        markDirty();
    }
}
