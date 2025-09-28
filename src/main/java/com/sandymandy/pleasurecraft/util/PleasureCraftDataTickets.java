package com.sandymandy.pleasurecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import java.util.Map;

public class PleasureCraftDataTickets {
    public static final DataTicket<Boolean> IS_STRIPPED = DataTicket.create("is_stripped", Boolean.class);
    public static final DataTicket<Boolean> IS_IN_SCENE = DataTicket.create("is_in_scene", Boolean.class);
    public static final DataTicket<String> GIRL_ID = DataTicket.create("girl_id", String.class);
    public static final DataTicket<Integer> ENTITY_ID = DataTicket.create("entity_id", Integer.class);
    public static final DataTicket<Entity> GIRL_FIRST_PASSENGER = DataTicket.create("girl_first_passenger", Entity.class);
    public static final DataTicket<ItemStack> GIRL_MAIN_HAND_STACK = DataTicket.create("girl_main_hand_stack", ItemStack.class);
    public static final DataTicket<Map<String, Boolean>> GIRL_BONE_VISIBILITY = (DataTicket<Map<String, Boolean>>) (Object) DataTicket.create("girl_bone_visibility", Map.class);
    public static final DataTicket<Map<String, Vec2f>> GIRL_BONE_UV_OFFSETS = (DataTicket<Map<String, Vec2f>>) (Object) DataTicket.create("girl_bone_uv_offsets", Map.class);
    public static final DataTicket<Map<String, Identifier>> GIRL_BONE_TEXTURE_OVERRIDES = (DataTicket<Map<String, Identifier>>) (Object) DataTicket.create("girl_bone_texture_overrides", Map.class);
    public static final DataTicket<Map<String, Identifier>> PLAYER_TEXTURES = (DataTicket<Map<String, Identifier>>) (Object) DataTicket.create("player_textures", Map.class);
    public static final DataTicket<String> PASSENGER_BONE_NAME = DataTicket.create("passenger_bone_name", String.class);
}
