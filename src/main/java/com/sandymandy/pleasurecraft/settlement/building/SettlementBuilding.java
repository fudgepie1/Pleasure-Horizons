package com.sandymandy.pleasurecraft.settlement.building;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sandymandy.pleasurecraft.settlement.SettlementResourceData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static com.sandymandy.pleasurecraft.util.Utils.BLOCK_STATE_PACKET_CODEC;

public record SettlementBuilding(BlockPos doorPos, BlockPos tagPos, BuildingType buildingType, Map<BlockPos, BlockState> structureBlocks) {

    public static final Codec<SettlementBuilding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("doorPos").forGetter(SettlementBuilding::getDoorPos),
            BlockPos.CODEC.fieldOf("tagPos").forGetter(SettlementBuilding::getTagPos),
            BuildingType.CODEC.fieldOf("buildingType").forGetter(SettlementBuilding::getBuildingType),
            Codec.unboundedMap(BlockPos.CODEC, BlockState.CODEC).fieldOf("structureBlocks").forGetter(SettlementBuilding::getStructureBlocks)
    ).apply(instance, SettlementBuilding::new));

    public static final PacketCodec<RegistryByteBuf, SettlementBuilding> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, SettlementBuilding::getDoorPos,
            BlockPos.PACKET_CODEC, SettlementBuilding::getTagPos,
            BuildingType.PACKET_CODEC, SettlementBuilding::getBuildingType,
            PacketCodecs.map(HashMap::new, BlockPos.PACKET_CODEC, BLOCK_STATE_PACKET_CODEC), SettlementBuilding::structureBlocks,
            SettlementBuilding::new
            );

    public SettlementBuilding(BlockPos doorPos, BlockPos tagPos, BuildingType buildingType, Map<BlockPos, BlockState> structureBlocks) {
        this.doorPos = doorPos;
        this.tagPos = tagPos;
        this.buildingType = buildingType;
        this.structureBlocks = structureBlocks;
    }



    public BlockPos getDoorPos() { return doorPos; }
    public BlockPos getTagPos() { return tagPos; }
    public BuildingType getBuildingType() { return buildingType; }
    public Map<BlockPos, BlockState> getStructureBlocks() { return structureBlocks; }
}
