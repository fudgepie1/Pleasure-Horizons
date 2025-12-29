package com.sandymandy.pleasurecraft.settlement.building;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.util.managers.SettlementBuildingManager;
import com.sandymandy.pleasurecraft.util.variables.BlockEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;


public class BuildingScanner {

    private final Settlement settlement;

    // --- Configuration ---
    private static final int MAX_VERTICAL_SCAN = 15; // how high to check above each column
    private static final int MIN_CLEARANCE = 2;      // minimum air blocks for walkable interior
    private static final int MIN_VALID_QUADRANTS = 9; // min columns for valid building
    private static final int MAX_GROUND_SEARCH = 20;  // how far down to check for ground

    public BuildingScanner(Settlement settlement) {
        this.settlement = settlement;
    }

    /**
     * Scans from a given position (the inside side of a door or tag).
     * If the origin is floating, it automatically moves it down to floor level.
     */
    public void scanForBuilding(World world, BlockPos origin, BlockPos doorPos, BlockPos tagPos, BuildingType type, PlayerEntity player) {
        if (world.isClient()) return;

        BlockPos groundAligned = findGroundLevel(world, origin);
        if (groundAligned == null) {
            player.sendMessage(Text.literal("[BuildingScanner] Could not find ground below origin.").formatted(Formatting.RED), false);
            return;
        }

        Set<BlockPos> visitedAir = new HashSet<>();
        Set<BlockPos> validQuadrants = new HashSet<>();
        List<BlockEntry> structureBlocks = new ArrayList<>();

        Queue<BlockPos> toVisit = new ArrayDeque<>();
        toVisit.add(groundAligned);

        while (!toVisit.isEmpty()) {
            BlockPos pos = toVisit.poll();
            if (!visitedAir.add(pos)) continue;

            // Check if this column has a roof and enough height
            if (isValidQuadrant(world, pos)) {
                validQuadrants.add(pos);

                // --- Scan Surroundings for Furniture/Walls ---
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = pos.offset(dir);
                    BlockState state = world.getBlockState(neighbor);

                    if (isEmpty(world, neighbor)) {
                        // Only spread horizontally for the floor-plan
                        if (dir.getAxis().isHorizontal() && !visitedAir.contains(neighbor)) {
                            toVisit.add(neighbor);
                        }
                    } else {
                        // It's a solid block (Wall, Bed, Chest, etc.)
                        structureBlocks.add(new BlockEntry(neighbor.toImmutable(), state));
                    }
                }
            }
        }

        // --- Validation ---
        boolean hasSize = validQuadrants.size() >= MIN_VALID_QUADRANTS;
        boolean hasRequirements = checkRequirements(type, structureBlocks, player);

        if (hasSize && hasRequirements) {
            registerBuilding(world, doorPos, tagPos, type, structureBlocks, List.copyOf(validQuadrants), player);
        } else if (!hasSize) {
            player.sendMessage(Text.literal("[BuildingScanner] Invalid building, only " + validQuadrants.size() + " valid quadrants found, minimum required is 9.").formatted(Formatting.RED), false);
        }
    }

    private boolean checkRequirements(BuildingType type, List<BlockEntry> blocks, PlayerEntity player) {
        Map<Object, Integer> requirements = type.getRequirements();

        for (Map.Entry<Object, Integer> entry : requirements.entrySet()) {
            int foundCount = 0;
            Object required = entry.getKey();
            int requiredAmount = entry.getValue();

            for (BlockEntry blockEntry : blocks) {
                BlockState state = blockEntry.state();
                if(!isMainPart(state)) continue;

                if (required instanceof TagKey<?> tag) {
                    if (state.isIn((TagKey<Block>) tag)) foundCount++;
                } else if (required instanceof Block block) {
                    if (state.isOf(block)) foundCount++;
                }
            }

            if (foundCount < requiredAmount) {
                String name = (required instanceof TagKey<?> tag) ? tag.id().getPath() : ((Block) required).getName().getString();
                player.sendMessage(Text.literal("Missing requirement: " + name + " (Found " + foundCount + "/" + requiredAmount + ")").formatted(Formatting.RED), false);
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if this blockstate is the "Main" part of a multi-block object.
     * If it's a bed, we only count the HEAD.
     * If it's a door or tall plant, we only count the LOWER half.
     */
    private boolean isMainPart(BlockState state) {
        // Beds: Only count the head part
        if (state.contains(Properties.BED_PART)) {
            return state.get(Properties.BED_PART) == BedPart.HEAD;
        }
        // Doors, Tall Flowers, etc: Only count the bottom half
        if (state.contains(Properties.DOUBLE_BLOCK_HALF)) {
            return state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        }
        // Normal blocks are always the "main" part
        return true;
    }

    private boolean isEmpty(World world, BlockPos pos) {
        return world.getBlockState(pos).isAir() || world.getBlockState(pos).isIn(BlockTags.WOOL_CARPETS);
    }

    /**
     * Finds the ground level below a given origin position.
     * Moves downward until a non-air block is found, or returns null if none within range.
     */
    private BlockPos findGroundLevel(World world, BlockPos origin) {
        BlockPos.Mutable mutable = origin.mutableCopy();
        for (int i = 0; i < MAX_GROUND_SEARCH; i++) {
            BlockState below = world.getBlockState(mutable.down());
            if (!below.isAir()) {
                // Found ground, return the first air block above it
                return mutable;
            }
            mutable.move(Direction.DOWN);
        }
        return null; // No ground found within limit
    }

    /**
     * Determines if a column is valid interior space: walkable clearance + roof within limit.
     */
    private boolean isValidQuadrant(World world, BlockPos pos) {
        int airHeight = 0;
        boolean hasRoof = false;

        for (int i = 1; i <= MAX_VERTICAL_SCAN; i++) {
            BlockPos check = pos.up(i);
            BlockState state = world.getBlockState(check);

            if (state.isAir()) {
                airHeight++;
            } else {
                hasRoof = true;
                break;
            }
        }

        // Must have walkable clearance AND a roof within scan height
        return airHeight >= MIN_CLEARANCE - 1 && hasRoof;
    }



    /**
     * Registers a successfully scanned building to the settlement.
     */
    private void registerBuilding(World world, BlockPos doorPos, BlockPos tagPos, BuildingType type, List<BlockEntry> structureBlocks, List<BlockPos> validBlocks, PlayerEntity player) {
        SettlementBuilding building = new SettlementBuilding(
                doorPos,
                tagPos,
                type,
                structureBlocks
        );
        if(SettlementBuildingManager.get((ServerWorld) world).getAllBuildings().containsKey(doorPos)) settlement.removeBuilding(doorPos, (ServerWorld) world);
        settlement.addBuilding(doorPos, building, (ServerWorld) world);
        PleasureCraft.LOGGER.info(
                "[BuildingScanner] Registered building with {} valid quadrants.",
                validBlocks.size()
        );
        player.sendMessage(Text.literal("[BuildingScanner] Registered building with " + validBlocks.size() + " valid quadrants.").formatted(Formatting.GREEN), false);

    }
}
