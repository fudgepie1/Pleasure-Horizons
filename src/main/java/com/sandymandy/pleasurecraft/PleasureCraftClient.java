package com.sandymandy.pleasurecraft;


import com.sandymandy.pleasurecraft.client.PleasureCraftKeybinds;
import com.sandymandy.pleasurecraft.client.gui.screen.GirlInventoryScreen;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.SettlementHubScreen;
import com.sandymandy.pleasurecraft.client.rendering.renderers.*;
import com.sandymandy.pleasurecraft.config.ModBindings;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.freecam.Freecam;
import com.sandymandy.pleasurecraft.networking.C2S.CumKeybindC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.ThrustKeybindC2SPacket;
import com.sandymandy.pleasurecraft.networking.PleasureCraftClientPackets;
import com.sandymandy.pleasurecraft.registries.GirlRegistry;
import com.sandymandy.pleasurecraft.registries.PleasureCraftHudRegistry;
import com.sandymandy.pleasurecraft.util.SceneKeyframeEventReloader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.value.Variable;

import static com.sandymandy.pleasurecraft.registries.PleasureCraftScreenHandlerRegistry.GIRL_INVENTORY_SCREEN_HANDLER;
import static com.sandymandy.pleasurecraft.registries.PleasureCraftScreenHandlerRegistry.SETTLEMENT_HUB_SCREEN_HANDLER;


public class PleasureCraftClient implements ClientModInitializer {

    private static boolean thrustToggleState = false;
    private static boolean lastSentThrustState = false;

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        ModBindings.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.START_CLIENT_TICK.register(Freecam::preTick);
        ClientTickEvents.END_CLIENT_TICK.register(Freecam::postTick);
        HandledScreens.register(GIRL_INVENTORY_SCREEN_HANDLER, GirlInventoryScreen::new);
        HandledScreens.register(SETTLEMENT_HUB_SCREEN_HANDLER, SettlementHubScreen::new);

        EntityRendererRegistry.register(GirlRegistry.LUCY, LucyRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.MIKA, MikaRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.MOMO, MomoRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.SLIME, SlimeRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.KOBOLD, KoboldRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.COPPIE, CoppieRenderer::new);
        EntityRendererRegistry.register(GirlRegistry.CUSTOM_GIRL, CustomGirlRenderer::new);
        PleasureCraftKeybinds.register();
        PleasureCraftClientPackets.registerS2CPackets();
        PleasureCraftHudRegistry.register();
        handleKeybinds();
        SceneKeyframeEventReloader.registerReloader();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean current = ModConfig.INSTANCE.girls.disableShading;
            if (current != AbstractGirlRenderer.IS_SHADING_DISABLED) {
                AbstractGirlRenderer.updateShadingState();
            }
        });

    }

    public static boolean areIrisShadersDisabled() {
        try {
            Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object irisApi = irisApiClass.getMethod("getInstance").invoke(null);

            return !((boolean) irisApiClass.getMethod("isShaderPackInUse").invoke(irisApi));

        } catch (Throwable ignored) {
            return true;
        }
    }

    private static void handleKeybinds() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean holdMode = ModConfig.INSTANCE.keybinds.holdThrust;

            boolean newThrustState;

            if (holdMode) {
                newThrustState = PleasureCraftKeybinds.thrustKey.isPressed();
            }
            else {
                if (PleasureCraftKeybinds.thrustKey.wasPressed()) {
                    thrustToggleState = !thrustToggleState;
                }
                newThrustState = thrustToggleState;
            }

            // Only send packet when value actually changed
            if (newThrustState != lastSentThrustState) {
                lastSentThrustState = newThrustState;
                ClientPlayNetworking.send(new ThrustKeybindC2SPacket(newThrustState));
            }

            // Cum key (single press)
            if (PleasureCraftKeybinds.cumKey.wasPressed()) {
                ClientPlayNetworking.send(new CumKeybindC2SPacket(true));
            }
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
