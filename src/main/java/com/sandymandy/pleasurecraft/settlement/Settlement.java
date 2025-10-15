package com.sandymandy.pleasurecraft.settlement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Settlement {
    private final UUID id;
    private final String name;
    private final BlockPos corePos;
    private SettlementResourceData data;
    private final List<UUID> members = new ArrayList<>();

    // === CODEC ===
    public static final Codec<Settlement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("id").forGetter(Settlement::getId),
            Codec.STRING.fieldOf("name").forGetter(Settlement::getName),
            BlockPos.CODEC.fieldOf("corePos").forGetter(Settlement::getCorePos),
            Codec.list(Uuids.CODEC).fieldOf("members").orElse(List.of()).forGetter(Settlement::getMembers),
            SettlementResourceData.CODEC.fieldOf("data").forGetter(Settlement::getData)
    ).apply(instance, (id, name, pos, members, data) -> {
        Settlement s = new Settlement(id, name, pos);
        s.members.addAll(members);
        s.data = data;
        return s;
    }));

    // === PACKET_CODEC ===
    public static final PacketCodec<RegistryByteBuf, Settlement> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public Settlement decode(RegistryByteBuf buf) {
            UUID id = buf.readUuid();
            String name = buf.readString();
            BlockPos pos = buf.readBlockPos();

            int memberCount = buf.readVarInt();
            List<UUID> members = new ArrayList<>(memberCount);
            for (int i = 0; i < memberCount; i++) members.add(buf.readUuid());

            SettlementResourceData data = SettlementResourceData.PACKET_CODEC.decode(buf);

            Settlement s = new Settlement(id, name, pos);
            s.members.addAll(members);
            s.data = data;
            return s;
        }

        @Override
        public void encode(RegistryByteBuf buf, Settlement settlement) {
            buf.writeUuid(settlement.getId());
            buf.writeString(settlement.getName());
            buf.writeBlockPos(settlement.getCorePos());

            buf.writeVarInt(settlement.getMembers().size());
            for (UUID uuid : settlement.getMembers()) buf.writeUuid(uuid);

            SettlementResourceData.PACKET_CODEC.encode(buf, settlement.getData());
        }
    };

    public Settlement(UUID id, String name, BlockPos corePos) {
        this.id = id;
        this.name = name;
        this.corePos = corePos;
        this.data = SettlementResourceData.DEFAULT;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public BlockPos getCorePos() { return corePos; }
    public SettlementResourceData getData() { return data; }

    // === Member handling ===
    public void addMember(GirlEntityAI girl) {
        if (!members.contains(girl.getUuid())) {
            members.add(girl.getUuid());
            girl.setSettlement(this);
        }
    }

    public void removeMember(GirlEntityAI girl) {
        members.remove(girl.getUuid());
        girl.setSettlement(null);
    }

    public List<UUID> getMembers() {
        return List.copyOf(members);
    }

    // === Resource management ===
    public void setMorale(float morale) { data = data.withMorale(morale); }
    public void addResources(int amount) { data = data.withMaterials(data.materials() + amount); }

    // === Ticking ===
    public void tick(World world) {
        // morale decay, food consumption, random events, etc.
        // Example:
        if (world.getTime() % 24000 == 0) { // daily tick
            float newMorale = Math.max(0, data.morale() - 0.01f);
            data = data.withMorale(newMorale);
        }
    }
}
