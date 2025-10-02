package com.sandymandy.pleasurecraft.tripod;

import net.minecraft.world.dimension.DimensionType;
import com.sandymandy.pleasurecraft.util.FreecamPosition;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.sandymandy.pleasurecraft.Freecam.MC;

public class TripodRegistry {
    private final Map<DimensionType, Map<TripodSlot, FreecamPosition>> tripods = new HashMap<>();

    public @Nullable FreecamPosition get(TripodSlot tripod) {
        return get(dimension(), tripod);
    }

    public @Nullable FreecamPosition get(DimensionType dimension, TripodSlot tripod) {
        return Optional.ofNullable(tripods.get(dimension))
                .map(positions -> positions.get(tripod))
                .orElse(null);
    }

    public void put(TripodSlot tripod, @Nullable FreecamPosition position) {
        put(dimension(), tripod, position);
    }

    public void put(DimensionType dimension, TripodSlot tripod, @Nullable FreecamPosition position) {
        tripods.computeIfAbsent(dimension, TripodRegistry::newEntry)
                .put(tripod, position);

    }

    /**
     * Clear all tripods for all dimensions.
     */
    public void clear() {
        tripods.clear();
    }

    // Get the current dimension
    private static DimensionType dimension() {
        return MC.world.getDimension();
    }

    // Construct a new dimension entry
    private static Map<TripodSlot, FreecamPosition> newEntry(DimensionType dimension) {
        return new EnumMap<>(TripodSlot.class);
    }
}
