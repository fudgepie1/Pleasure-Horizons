package com.sandymandy.pleasurecraft.util;

import com.sandymandy.pleasurecraft.entity.base.GirlSceneEntity;
import com.sandymandy.pleasurecraft.util.variables.CustomGirlProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@FunctionalInterface
public interface ProfileFactory<T extends GirlSceneEntity> {
    T create(EntityType<T> type, World world, CustomGirlProfile profile);
}