package com.sandymandy.pleasurecraft.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sandymandy.pleasurecraft.entity.girls.JsonGirlEntity;
import com.sandymandy.pleasurecraft.entity.girls.SlimeEntity;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import com.sandymandy.pleasurecraft.util.variables.JsonGirlProfile;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class JsonGirlSpawnCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("girl")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(literal("spawn")
                                .then(argument("id", StringArgumentType.string())
                                        .executes(ctx -> {
                                            return spawnGirl(ctx.getSource(), StringArgumentType.getString(ctx, "id"), ctx.getSource().getPosition());
                                        })
                                )
                                .then(argument("pos", net.minecraft.command.argument.Vec3ArgumentType.vec3())
                                        .then(argument("id", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    Vec3d pos = net.minecraft.command.argument.Vec3ArgumentType.getVec3(ctx, "pos");
                                                    return spawnGirl(ctx.getSource(), StringArgumentType.getString(ctx, "id"), pos);
                                                })
                                        )
                                )
                        )
        );
    }

    private static int spawnGirl(ServerCommandSource source, String id, Vec3d pos) {
        ServerWorld world = source.getWorld();

        // Validate profile
        JsonGirlProfile profile = JsonGirlLoader.PROFILES.get(id);
        if (profile == null) {
            source.sendError(Text.literal("Girl not found: " + id));
            return 0;
        }

        // Validate position
        BlockPos blockPos = BlockPos.ofFloored(pos);
        if (!world.isValid(blockPos)) {
            source.sendError(Text.literal("Invalid spawn position."));
            return 0;
        }
//        SlimeEntity girl = GirlRegistry.SLIME.create(world, SpawnReason.COMMAND);

        // Create entity
        JsonGirlEntity girl = GirlRegistry.JSON_GIRL.create(world, SpawnReason.COMMAND);
        if (girl == null) {
            source.sendError(Text.literal("Failed to create girl entity."));
            return 0;
        }
//
//        // Apply profile and attributes
        girl.setProfile(profile);
//        girl.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.MAX_HEALTH).setBaseValue(profile.maxHealth());
//        girl.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.MOVEMENT_SPEED).setBaseValue(profile.movementSpeed());
//        girl.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE).setBaseValue(profile.attackDamage());
//        girl.setHealth((float) profile.maxHealth());

        // Position & rotation
        girl.refreshPositionAndAngles(pos.x, pos.y, pos.z, source.getRotation().y, 0);

        // Initialize like vanilla
        girl.initialize(world, world.getLocalDifficulty(girl.getBlockPos()), net.minecraft.entity.SpawnReason.COMMAND, null);

        // Spawn entity
        if (!world.spawnEntity(girl)) {
            source.sendError(Text.literal("Failed to spawn entity in the world."));
            return 0;
        }

        source.sendFeedback(() -> Text.literal("Spawned girl: " + id), true);
        return 1;
    }
}
