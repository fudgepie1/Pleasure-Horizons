package com.sandymandy.pleasurecraft.village;

import org.jetbrains.annotations.Nullable;

public interface VillageMember {
    @Nullable VillageData getVillage();
    void setVillage(@Nullable VillageData village);
    default boolean hasVillage() { return getVillage() != null; }
}
