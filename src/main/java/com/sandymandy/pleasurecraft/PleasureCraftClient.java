package com.sandymandy.pleasurecraft;


import com.sandymandy.pleasurecraft.client.PleasureCraftKeybinds;
import com.sandymandy.pleasurecraft.client.renderers.MikaRenderer;
import com.sandymandy.pleasurecraft.client.renderers.LucyRenderer;
import com.sandymandy.pleasurecraft.client.renderers.MomoRenderer;
import com.sandymandy.pleasurecraft.config.ModBindings;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.freecam.Freecam;
import com.sandymandy.pleasurecraft.networking.PleasureCraftPackets;
import com.sandymandy.pleasurecraft.networking.C2S.CumKeybindC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.ThrustKeybindC2SPacket;
import com.sandymandy.pleasurecraft.registries.PleasureCraftHud;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandler;
import com.sandymandy.pleasurecraft.screen.client.GirlInventoryScreen;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class PleasureCraftClient implements ClientModInitializer {
//    public static ShaderProgram ENTITY_UNLIT_TRANSLUCENT_SHADER;

    public static final ExtendedScreenHandlerType<GirlInventoryScreenHandler, GirlScreenData> GIRL_INVENTORY_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(PleasureCraft.MOD_ID, "girl_inventory_screen"),
                    new ExtendedScreenHandlerType<>(GirlInventoryScreenHandler::new, GirlScreenData.PACKET_CODEC)
            );

/*	public static final ExtendedScreenHandlerType<GirlInventoryScreenHandler, GirlScreenData> GIRL_TALK_SCREEN_HANDLER =
			Registry.register(
					Registries.SCREEN_HANDLER,
					Identifier.of(PleasureCraft.MOD_ID, "girl_talk_screen"),
					new ExtendedScreenHandlerType<>(GirlInventoryScreenHandler::new, GirlScreenData.PACKET_CODEC)
			);
*/

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        ModBindings.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.START_CLIENT_TICK.register(Freecam::preTick);
        ClientTickEvents.END_CLIENT_TICK.register(Freecam::postTick);
        HandledScreens.register(GIRL_INVENTORY_SCREEN_HANDLER, GirlInventoryScreen::new);

        EntityRendererRegistry.register(GirlRegistry.LUCY, LucyRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.MIKA, MikaRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.MOMO, MomoRenderer::new);
        PleasureCraftKeybinds.register();
        PleasureCraftPackets.registerS2CPackets();
        PleasureCraftHud.register();
        handleKeybinds();
    }

    private static void handleKeybinds() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Thrust button held
            boolean thrustHeld = PleasureCraftKeybinds.thrustKey.isPressed();
            ClientPlayNetworking.send(new ThrustKeybindC2SPacket(thrustHeld));

            // Cum button (pressed once)
            ClientPlayNetworking.send(new CumKeybindC2SPacket(PleasureCraftKeybinds.cumKey.wasPressed()));

        });
    }

    public record GirlScreenData(int entityId) {
        public static final PacketCodec<RegistryByteBuf, GirlScreenData> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_INT,
                GirlScreenData::entityId,
                GirlScreenData::new
        );
    }

}
