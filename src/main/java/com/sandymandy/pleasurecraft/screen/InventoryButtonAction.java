package com.sandymandy.pleasurecraft.screen;

import com.sandymandy.pleasurecraft.entity.base.GirlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public record InventoryButtonAction(Text label, int requiredRelationshipLevel, BiConsumer<GirlEntity, PlayerEntity> action) {}
