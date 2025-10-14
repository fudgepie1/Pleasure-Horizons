package com.sandymandy.pleasurecraft.block.entity.entities;

import com.sandymandy.pleasurecraft.block.PleasureCraftBlocks;
import com.sandymandy.pleasurecraft.block.entity.PleasureCraftBlockEntities;
import com.sandymandy.pleasurecraft.item.PleasureCraftItems;
import com.sandymandy.pleasurecraft.village.VillageManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VillageCoreBlockEntity extends BlockEntity {
    private UUID villageId;
    private String villageName;

    public VillageCoreBlockEntity(BlockPos pos, BlockState state) {
        super(PleasureCraftBlockEntities.VILLAGE_CORE_BLOCK_ENTITY, pos, state);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public void createVillage(ServerWorld world, String name) {
        var manager = VillageManager.get(world);
        var village = manager.createVillage(pos, name);
        this.villageId = village.getId();
        this.villageName = name;
        markDirty();
    }

    public void removeVillage(ServerWorld world, ServerPlayerEntity player) {
        if (villageId != null) {
            var manager = VillageManager.get(world);
            manager.removeVillage(villageId);
            player.sendMessage(Text.literal("Village '" + villageName + "' has been removed."), false);

            // Drop the item back to player
            ItemStack stack = new ItemStack(PleasureCraftBlocks.VILLAGE_CORE);
            ItemEntity drop = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
            world.spawnEntity(drop);

            // Remove the block safely
            world.removeBlock(pos, false);
        }
    }

    public void openGui(ServerWorld world, ServerPlayerEntity player) {
        // TODO: Open custom GUI/screen handler
        player.sendMessage(Text.literal("Village: " + (villageName != null ? villageName : "Unnamed")), false);
        player.sendMessage(Text.literal("Use the 'Remove Village' button to disband this village."), false);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (villageId != null) nbt.put("VillageId", Uuids.CODEC, villageId);
        if (villageName != null) nbt.putString("VillageName", villageName);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt,registries);
        if (nbt.contains("VillageId")) this.villageId = nbt.get("VillageId", Uuids.CODEC).get();
        if (nbt.contains("VillageName")) this.villageName = nbt.getString("VillageName").get();
    }

    public UUID getVillageId() { return villageId; }
    public String getVillageName() { return villageName; }
}
