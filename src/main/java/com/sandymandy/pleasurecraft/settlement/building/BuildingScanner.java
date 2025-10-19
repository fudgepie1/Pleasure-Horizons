package com.sandymandy.pleasurecraft.settlement.building;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
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

    public BuildingScanner(Settlement settlement) {
        this.settlement = settlement;
    }

    /**
     * Scans from a given position (the inside side of a door or tag).
     */
    public void scanForBuilding(World world, UUID id, BlockPos origin, BlockPos doorPos, BlockPos tagPos, BuildingType type, PlayerEntity player) {
        if (world.isClient()) return;

        PleasureCraft.LOGGER.info("[BuildingScanner] Starting scan at {}", origin);

        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> validQuadrants = new HashSet<>();

        Queue<BlockPos> toVisit = new ArrayDeque<>();
        toVisit.add(origin);

        while (!toVisit.isEmpty()) {
            BlockPos pos = toVisit.poll();
            if (!visited.add(pos)) continue; // skip already visited
            if (!isAir(world, pos)) continue;

            // If this spot has no roof within vertical scan range, treat as outside and stop spreading
            if (!hasRoofWithin(world, pos)) continue;

            // Check clearance and roof
            if (isValidQuadrant(world, pos)) {
                validQuadrants.add(pos);

                for (BlockPos poss : validQuadrants) {
                    world.setBlockState(poss, Blocks.GLOWSTONE.getDefaultState());
                }

                // Spread horizontally only inside roofed areas
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    BlockPos neighbor = pos.offset(dir);
                    if (!visited.contains(neighbor) && isAir(world, neighbor)) {
                        toVisit.add(neighbor);
                    }
                }
            }
        }

        if (validQuadrants.size() >= MIN_VALID_QUADRANTS) {
            registerBuilding(id, doorPos, tagPos, type, List.copyOf(validQuadrants));
        } else {
            PleasureCraft.LOGGER.warn(
                    "[BuildingScanner] Invalid building ({} valid quadrants).",
                    validQuadrants.size()
            );
        }
    }

    // --- Utility methods ---

    private boolean isAir(World world, BlockPos pos) {
        return world.getBlockState(pos).isAir();
    }

    /**
     * Checks if there is a solid block (roof) within a given height range.
     * If air extends all the way up, returns false (open sky).
     */
    private boolean hasRoofWithin(World world, BlockPos pos) {
        for (int i = 1; i <= BuildingScanner.MAX_VERTICAL_SCAN; i++) {
            BlockState above = world.getBlockState(pos.up(i));
            if (!above.isAir()) {
                return true; // found roof
            }
        }
        return false; // open to sky beyond scan limit
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
        return airHeight >= MIN_CLEARANCE && hasRoof;
    }

    /**
     * Registers a successfully scanned building to the settlement.
     */
    private void registerBuilding(UUID id, BlockPos doorPos, BlockPos tagPos, BuildingType type, List<BlockPos> validBlocks) {
        SettlementBuilding building = new SettlementBuilding(
                doorPos,
                tagPos,
                type,
                validBlocks
        );

        settlement.addBuilding(id,building);
        PleasureCraft.LOGGER.info(
                "[BuildingScanner] Registered valid building with {} interior quadrants.",
                validBlocks.size()
        );
    }
}
