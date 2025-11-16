package com.sandymandy.pleasurecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sandymandy.pleasurecraft.util.managers.TamedGirlManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class LocateTamedGirls {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
                literal("girls")
                        .requires(src -> src.hasPermissionLevel(1))
                        .then(literal("locateAll")
                                .executes(ctx -> locateGirls(ctx.getSource()))
                        )
        );
    }

    private static int locateGirls(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        ServerWorld world = player.getWorld();
        TamedGirlManager manager = TamedGirlManager.get(world);

        var owned = manager.getGirlsOwnedBy(player.getUuid());
        if (owned.isEmpty()) {
            player.sendMessage(Text.literal("§cYou have no tamed girls in this world."), false);
            return 0;
        }

        int found = 0;
        for (var entry : owned) {

            found++;

            var pos = entry.pos();
            String name = entry.name();

            player.sendMessage(
                    Text.literal("§d" + name
                            + "§r → X: " + (int) pos.x
                            + " Y: " + (int) pos.y
                            + " Z: " + (int) pos.z),
                    false
            );
        }

        player.sendMessage(Text.literal("§aTotal girls found: §e" + found), false);
        return found;
    }
}
