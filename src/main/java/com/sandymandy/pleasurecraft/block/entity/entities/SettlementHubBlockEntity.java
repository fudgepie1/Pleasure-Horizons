package com.sandymandy.pleasurecraft.block.entity.entities;

import com.sandymandy.pleasurecraft.block.entity.PleasureCraftBlockEntities;
import com.sandymandy.pleasurecraft.screen.SettlementHubScreenHandlerFactory;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.util.managers.SettlementManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.sandymandy.pleasurecraft.util.Utils.getPlayerName;

public class SettlementHubBlockEntity extends BlockEntity {
    private Settlement settlement;
    private UUID settlementId; // ← Store the ID

    public SettlementHubBlockEntity(BlockPos pos, BlockState state) {
        super(PleasureCraftBlockEntities.SETTLEMENT_HUB_BLOCK_ENTITY, pos, state);
    }

    /* === Networking & Sync === */

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    /* === GUI Handling === */

    public void openGui(ServerWorld world, ServerPlayerEntity player) {
        Settlement settlement = getSettlement(); // ← Use getter
        if (settlement != null) {
            PlayerEntity owner = world.getPlayerByUuid(settlement.getOwner());
            if(player.equals(owner)) {
                player.openHandledScreen(new SettlementHubScreenHandlerFactory(settlement));
            }
            else {
                player.sendMessage(Text.of("§cThis is not owned by you"), true);
            }
        }
    }

    /* === Tick === */

    public static void tick(World world, BlockPos pos, BlockState state, SettlementHubBlockEntity be) {
        if (world.isClient()) return;

        Settlement settlement = be.getSettlement(); // ← Use getter
        if (settlement != null) {
            settlement.tick(world);
        }
    }

    /* === Setup === */

    public void initializeWithOwner(ServerWorld world, UUID ownerId) {
        SettlementManager manager = SettlementManager.get(world);

        if (this.settlement == null && this.settlementId == null) { // ← Check both
            PlayerEntity owner = world.getPlayerByUuid(ownerId);
            String name = "Settlement@" + getPos().toShortString();

            if(owner != null) name = getPlayerName(owner) + "'s Settlement";

            this.settlement = manager.createSettlement(getPos(), name, ownerId);
            this.settlementId = this.settlement.getId(); // ← Store ID
            markDirty();
        }
    }

    /* === Persistence === */

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        // Try to get settlement if we have an ID but not the object yet
        if (settlement == null && settlementId != null && world instanceof ServerWorld serverWorld) {
            settlement = SettlementManager.get(serverWorld).getSettlement(settlementId);
        }

        if (settlement != null) {
            view.put("SettlementId", Uuids.CODEC, settlement.getId());
        } else if (settlementId != null) {
            view.put("SettlementId", Uuids.CODEC, settlementId);
        }
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);

        // Just store the UUID for now - we'll fetch the settlement later
        view.read("SettlementId", Uuids.CODEC).ifPresent(id -> {
            this.settlementId = id;
            this.settlement = null; // Clear cached settlement
        });
    }

    /* === Lazy Getter === */

    @Nullable
    public Settlement getSettlement() {
        // If we have the settlement cached, return it
        if (settlement != null) {
            return settlement;
        }

        // If we have an ID but not the settlement, try to fetch it
        if (settlementId != null && world instanceof ServerWorld serverWorld) {
            settlement = SettlementManager.get(serverWorld).getSettlement(settlementId);
        }

        return settlement;
    }

    /* === Sync Utility === */

    public void syncToClient() {
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
            markDirty();
        }
    }
}