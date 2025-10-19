package com.sandymandy.pleasurecraft.settlement.building;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

/**
 * Scans and validates a building structure by analyzing air-space quadrants,
 * similar to TekTopia's detection algorithm.
 */
public class BuildingScanner {

    private final Settlement settlement;
    private static final int MAX_VERTICAL_SCAN = 15;
    private static final int MIN_CLEARANCE = 2;
    private static final int MIN_VALID_QUADRANTS = 9;

    public BuildingScanner(Settlement settlement) {
        this.settlement = settlement;
    }

    /**
     * Scans from a given position (the inside side of a door or tag).
     */
    public void scanForBuilding(World world, UUID Id, BlockPos origin, BlockPos doorPos, BlockPos tagPos, BuildingType type) {
        if (world.isClient()) return;

        PleasureCraft.LOGGER.info("[BuildingScanner] Starting scan at {}", origin);

        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> validQuadrants = new HashSet<>();

        Queue<BlockPos> toVisit = new ArrayDeque<>();
        toVisit.add(origin);

        while (!toVisit.isEmpty()) {
            BlockPos pos = toVisit.poll();
            if (visited.contains(pos)) continue;
            visited.add(pos);

            if (!isAir(world, pos)) continue;

            // Check vertical clearance for this column
            if (isValidQuadrant(world, pos)) {
                validQuadrants.add(pos);
            }

            // Flood-fill horizontally
            for (Direction dir : Direction.Type.HORIZONTAL) {
                BlockPos neighbor = pos.offset(dir);
                if (!visited.contains(neighbor) && isAir(world, neighbor)) {
                    toVisit.add(neighbor);
                }
            }
        }

        if (validQuadrants.size() >= MIN_VALID_QUADRANTS) {
            registerBuilding(Id, doorPos, tagPos, type, List.copyOf(visited), validQuadrants.size());
        } else {
            PleasureCraft.LOGGER.warn("[BuildingScanner] Invalid building ({} quadrants).", validQuadrants.size());
        }
    }

    private boolean isAir(World world, BlockPos pos) {
        return world.getBlockState(pos).isAir();
    }

    private boolean isValidQuadrant(World world, BlockPos pos) {
        int airHeight = 0;
        boolean hasRoof = false;

        for (int i = 0; i < MAX_VERTICAL_SCAN; i++) {
            BlockPos check = pos.up(i);
            BlockState state = world.getBlockState(check);

            if (state.isAir()) {
                airHeight++;
            } else {
                // Found a solid block above — treat this as a roof
                hasRoof = true;
                break;
            }
        }

        // Valid if at least 2 blocks of walkable space and a roof above
        return airHeight >= MIN_CLEARANCE && hasRoof;
    }


    /**
     * Registers a successfully scanned building to the settlement.
     */
    private void registerBuilding(UUID id, BlockPos doorPos, BlockPos tagPos, BuildingType type, List<BlockPos> blocks, int amountOfValidQuadrants) {

        SettlementBuilding building = new SettlementBuilding(
                id,
                doorPos,
                tagPos,
                type,
                blocks
        );

        settlement.addBuilding(building);
        PleasureCraft.LOGGER.info("[BuildingScanner] Registered valid building with {} quadrants", amountOfValidQuadrants);
    }
}
