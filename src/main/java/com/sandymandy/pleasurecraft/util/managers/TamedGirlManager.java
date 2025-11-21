package com.sandymandy.pleasurecraft.util.managers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sandymandy.pleasurecraft.entity.base.TameableGirlEntity;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.*;

public class TamedGirlManager extends PersistentState {

    // ownerUUID → list of girl entries owned by that player
    private final Map<UUID, List<GirlEntry>> girls = new HashMap<>();

    // === ENTRY DATA ===
    public static class GirlEntry {
        private final UUID id;     // Girl UUID
        private final String name; // Girl display name
        private final Vec3d pos;   // Position

        public GirlEntry(UUID id, String name, Vec3d pos) {
            this.id = id;
            this.name = name;
            this.pos = pos;
        }

        public UUID id() { return id; }
        public String name() { return name; }
        public Vec3d pos() { return pos; }

        public static final Codec<GirlEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Uuids.CODEC.fieldOf("id").forGetter(GirlEntry::id),
                        Codec.STRING.fieldOf("name").forGetter(GirlEntry::name),
                        Vec3d.CODEC.fieldOf("pos").forGetter(GirlEntry::pos)
                ).apply(instance, GirlEntry::new)
        );
    }

    // === MANAGER CODEC ===
    public static final Codec<TamedGirlManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Uuids.CODEC, Codec.list(GirlEntry.CODEC))
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
        girls.get(ownerId).removeIf(e -> e.id().equals(girl.getUuid()));

        girls.get(ownerId).add(new GirlEntry(
                girl.getUuid(),
                girl.getGirlDisplayName(),
                girl.getPos()
        ));

        markDirty();
    }

    // === UPDATE GIRL ONLY WHEN TAMED ===
    public void updateGirl(TameableGirlEntity girl) {
        if(!girl.isTamed())return;

        registerGirl(girl); // handles add/update safely
    }

    // === REMOVE GIRL BY GIRL UUID FROM ANY OWNER ===
    public void removeGirl(UUID girlId) {
        for (UUID owner : girls.keySet()) {
            girls.get(owner).removeIf(e -> e.id().equals(girlId));
        }
        markDirty();
    }

    // === GET ALL OWNED BY PLAYER ===
    public List<GirlEntry> getGirlsOwnedBy(UUID owner) {
        return girls.getOrDefault(owner, Collections.emptyList());
    }

    // === ALL GIRLS (flattened) ===
    public Collection<GirlEntry> getAllGirls() {
        List<GirlEntry> list = new ArrayList<>();
        for (List<GirlEntry> entries : girls.values()) list.addAll(entries);
        return list;
    }

    public boolean containsGirl(UUID uuid) {
        return girls.values().stream()
                .flatMap(List::stream)
                .anyMatch(entry -> entry.id().equals(uuid));
    }

    public void cleanupDeadGirls(ServerWorld world) {
        for (UUID owner : new HashSet<>(girls.keySet())) {
            List<GirlEntry> list = girls.get(owner);

            list.removeIf(entry ->
                    world.getEntity(entry.id()) == null ||    // not loaded
                            !world.getEntity(entry.id()).isAlive()     // dead
            );

            if (list.isEmpty()) {
                girls.remove(owner);
            }
        }

        markDirty();
    }
}
