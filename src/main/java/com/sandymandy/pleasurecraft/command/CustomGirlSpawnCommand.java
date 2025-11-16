package com.sandymandy.pleasurecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.sandymandy.pleasurecraft.entity.girls.CustomGirlEntity;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import com.sandymandy.pleasurecraft.util.json.CustomGirlLoader;
import com.sandymandy.pleasurecraft.util.variables.CustomGirlProfile;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CustomGirlSpawnCommand {

    // Suggestion provider for auto-complete
    private static final SuggestionProvider<ServerCommandSource> PROFILE_SUGGESTIONS = (context, builder) -> {
        CustomGirlLoader.PROFILES.keySet().forEach(builder::suggest);
        return CompletableFuture.completedFuture(builder.build());
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
                literal("girls")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(literal("spawn")
                                // /girl spawn <id>
                                .then(argument("id", StringArgumentType.string())
                                        .suggests(PROFILE_SUGGESTIONS)
                                        .executes(ctx -> {
                                            Vec3d pos = ctx.getSource().getPosition();
                                            String id = StringArgumentType.getString(ctx, "id");
                                            return spawnGirl(ctx.getSource(), id, pos);
                                        })
                                        // /girl spawn <id> <pos>
                                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(ctx -> {
                                                    String id = StringArgumentType.getString(ctx, "id");
                                                    BlockPos blockPos = BlockPosArgumentType.getBlockPos(ctx, "pos");
                                                    Vec3d pos = new Vec3d(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
                                                    return spawnGirl(ctx.getSource(), id, pos);
                                                })
                                        )
                                )
                        )
        );
    }

    private static int spawnGirl(ServerCommandSource source, String id, Vec3d pos) {
        ServerWorld world = source.getWorld();

        // Validate profile
        CustomGirlProfile profile = CustomGirlLoader.PROFILES.get(id);
        if (profile == null) {
            source.sendError(Text.literal("Girl profile not found: " + id));
            return 0;
        }

        // Validate position
        BlockPos blockPos = BlockPos.ofFloored(pos);
        if (!world.isValid(blockPos)) {
            source.sendError(Text.literal("Invalid spawn position."));
            return 0;
        }

        // Create entity
        CustomGirlEntity girl = GirlRegistry.CUSTOM_GIRL.create(world, net.minecraft.entity.SpawnReason.COMMAND);
        if (girl == null) {
            source.sendError(Text.literal("Failed to create girl entity."));
            return 0;
        }

        // Apply profile and attributes
        girl.setProfile(profile);


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
