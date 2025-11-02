package com.sandymandy.pleasurecraft.util;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityScene;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@FunctionalInterface
public interface ProfileFactory<T extends GirlEntityScene> {
    T create(EntityType<T> type, World world, com.sandymandy.pleasurecraft.util.variables.JsonGirlProfile profile);
}