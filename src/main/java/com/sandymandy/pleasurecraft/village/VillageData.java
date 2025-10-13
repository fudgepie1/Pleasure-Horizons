package com.sandymandy.pleasurecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillageData {
    public static final Codec<VillageData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("id").forGetter(VillageData::getId),
            Codec.STRING.fieldOf("name").forGetter(VillageData::getName),
            BlockPos.CODEC.fieldOf("corePos").forGetter(VillageData::getCorePos),
            Codec.list(Uuids.CODEC).fieldOf("members").forGetter(VillageData::getMembers),
            Codec.FLOAT.fieldOf("morale").orElse(1.0f).forGetter(VillageData::getMorale),
            Codec.INT.fieldOf("resourceCount").orElse(0).forGetter(VillageData::getResourceCount)
    ).apply(instance, VillageData::new));

    private final UUID id;
    private final String name;
    private final BlockPos corePos;
    private final List<UUID> members;
    private float morale;
    private int resourceCount;

    public VillageData(BlockPos pos, String name) {
        this(UUID.randomUUID(), name, pos, new ArrayList<>(), 1.0f, 0);
    }

    public VillageData(UUID id, String name, BlockPos corePos, List<UUID> members, float morale, int resourceCount) {
        this.id = id;
        this.name = name;
        this.corePos = corePos;
        this.members = new ArrayList<>(members);
        this.morale = morale;
        this.resourceCount = resourceCount;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BlockPos getCorePos() {
        return corePos;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public float getMorale() {
        return morale;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public void addMember(GirlEntityAI girl) {
        if (!members.contains(girl.getUuid())) {
            members.add(girl.getUuid());
            girl.setVillage(this);
        }
    }

    public void removeMember(GirlEntityAI girl) {
        members.remove(girl.getUuid());
        girl.setVillage(null);
    }

    public void tick(World world) {
        // TODO: implement morale decay, random events, etc.
    }
}
