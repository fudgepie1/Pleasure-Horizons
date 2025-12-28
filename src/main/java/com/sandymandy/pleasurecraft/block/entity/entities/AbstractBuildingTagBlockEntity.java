package com.sandymandy.pleasurecraft.block.entity.entities;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.settlement.building.BuildingType;
import com.sandymandy.pleasurecraft.util.PleasureCraftMessages;
import com.sandymandy.pleasurecraft.util.Utils;
import com.sandymandy.pleasurecraft.util.managers.SettlementManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.sandymandy.pleasurecraft.block.entity.PleasureCraftBlockEntities.BUILDING_TAG_BLOCK_ENTITY;

public class AbstractBuildingTagBlockEntity extends BlockEntity {

    private BuildingType buildingType = BuildingType.NONE;
    private BlockPos doorPos = new BlockPos(0, -70, 0);
    private UUID settlementId = null;
    private Settlement settlement;

    public AbstractBuildingTagBlockEntity(BlockPos pos, BlockState state, BuildingType type) {
        super(BUILDING_TAG_BLOCK_ENTITY, pos, state);
        if (type != null) this.buildingType = type;
    }

    public BuildingType getBuildingType() { return buildingType; }
    public BlockPos getDoorPos() { return doorPos; }

    @Nullable
    public Settlement getSettlement() {
        if (this.settlement != null) return this.settlement;
        if (this.world != null && !this.world.isClient && this.settlementId != null) {
            this.settlement = SettlementManager.get((ServerWorld) this.world).getSettlement(this.settlementId);
        }
        return this.settlement;
    }

    public void setDoorPos(BlockPos doorPos) { this.doorPos = doorPos; this.markDirty(); }

    public void setSettlement(Settlement settlement) {
        this.settlementId = settlement.getId();
        this.settlement = settlement;
        this.markDirty();
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("BuildingType", BuildingType.CODEC, buildingType);
        view.put("DoorPos", BlockPos.CODEC, doorPos);
        if (settlementId != null) view.put("Settlement", Uuids.CODEC, settlementId);
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);
        view.read("BuildingType", BuildingType.CODEC).ifPresent(value -> this.buildingType = value);
        view.read("DoorPos", BlockPos.CODEC).ifPresent(value -> this.doorPos = value);
        view.read("Settlement", Uuids.CODEC).ifPresent(value -> this.settlementId = value);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractBuildingTagBlockEntity be) {
        if (world.isClient) return;

        BlockPos doorPos = be.getDoorPos();
        // Skip if this tag hasn't been assigned a door yet (using your -70 marker)
        if (doorPos.getY() == -70) return;

        // CRITICAL FIX: Only check the door if the chunk containing the door is actually loaded.
        // If the chunk isn't loaded, world.getBlockState returns AIR, which triggers your removal logic incorrectly.
        if (world.isChunkLoaded(doorPos)) {
            if (!world.getBlockState(doorPos).isIn(BlockTags.DOORS)) {
                Settlement settlement = be.getSettlement();
                if (settlement != null) {
                    settlement.removeBuilding(doorPos);
                    PleasureCraftMessages.GlobleMessage(world, "Building Removed at " + doorPos.toShortString() + " because the door is missing!");
                    be.setDoorPos(new BlockPos(0, -70, 0));
                }
            }
        }
    }

    public ActionResult onInteract(PlayerEntity player, World world, BlockPos pos){
        if (world.isClient) return ActionResult.SUCCESS;

        Settlement nearestSettlement = Utils.findNearestSettlement(world, pos);
        Direction facingDirection = world.getBlockState(pos).get(Properties.HORIZONTAL_FACING);
        BlockPos foundDoor = Utils.findNearbyDoor(world, pos, facingDirection);

        if (foundDoor == null) {
            player.sendMessage(Text.literal("§cYou must place this tag above or beside a door!"), true);
            return ActionResult.FAIL;
        }

        if(nearestSettlement == null || !nearestSettlement.getOwner().equals(player.getUuid())){
            player.sendMessage(Text.of("§cNo nearby settlements found owned by you"), true);
            return ActionResult.FAIL;
        }

        player.sendMessage(Text.of("Registering building to " + nearestSettlement.getName()), true);
        nearestSettlement.registerBuilding(world, foundDoor, facingDirection, pos, this.getBuildingType(), player);

        this.setSettlement(nearestSettlement);
        this.setDoorPos(foundDoor);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        Settlement s = getSettlement();
        if (s != null && doorPos.getY() != -70) {
            s.removeBuilding(doorPos);
            PleasureCraft.LOGGER.info("Removed building at {} because the Tag was broken.", doorPos);
        }
    }

}