package com.sandymandy.pleasurecraft.freecam;

import com.sandymandy.pleasurecraft.config.ModBindings;
import com.sandymandy.pleasurecraft.config.ModConfig;
import com.sandymandy.pleasurecraft.freecam.tripod.TripodRegistry;
import com.sandymandy.pleasurecraft.freecam.tripod.TripodSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class Freecam {

    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final String MOD_ID = "freecam";

    private static boolean freecamEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static boolean disableNextTick = false;
    private static final TripodRegistry tripods = new TripodRegistry();
    private static TripodSlot activeTripod = TripodSlot.NONE;
    private static FreeCamera freeCamera;
    private static Perspective rememberedF5 = null;

    @ApiStatus.Internal
    public static void preTick(MinecraftClient mc) {
        if (disableNextTick && isEnabled()){
            toggle();
        }
        disableNextTick = false;

        if (isEnabled()) {
            if (mc.player != null && mc.player.input instanceof KeyboardInput && !isPlayerControlEnabled()) {
                Input input = new Input();
                PlayerInput keyPresses = mc.player.input.playerInput;
                input.playerInput = new PlayerInput(
                        false,
                        false,
                        false,
                        false,
                        false,
                        keyPresses.sneak(),
                        false
                );
                mc.player.input = input;
            }
        }
    }

    @ApiStatus.Internal
    public static void postTick(MinecraftClient mc) {
        ModBindings.forEach(TextureTickListener::tick);
    }

    @ApiStatus.Internal
    public static void onDisconnect() {
        if (isEnabled()) {
            toggle();
        }
        tripods.clear();
    }

    @ApiStatus.Internal
    public static boolean activateTripodHandler() {
        boolean activated = false;
        for (KeyBinding combo : MC.options.hotbarKeys) {
            while (combo.wasPressed()) {
                toggleTripod(TripodSlot.ofKeyCode(combo.getDefaultKey().getCode()));
                activated = true;
            }
        }
        return activated;
    }

    @ApiStatus.Internal
    public static boolean resetTripodHandler() {
        boolean reset = false;
        for (KeyBinding key : MC.options.hotbarKeys) {
            while (key.wasPressed()) {
                resetCamera(TripodSlot.ofKeyCode(key.getDefaultKey().getCode()));
                reset = true;
            }
        }
        return reset;
    }

    @ApiStatus.AvailableSince("0.3.1")
    public static void toggle() {

        if (tripodEnabled) {
            toggleTripod(activeTripod);
            return;
        }

        if (freecamEnabled) {
            onDisableFreecam();
        } else {
            onEnableFreecam();
        }
        freecamEnabled = !freecamEnabled;
        if (!freecamEnabled) {
            onDisabled();
        }
    }

    private static void toggleTripod(TripodSlot tripod) {
        if (tripod == TripodSlot.NONE) {
            return;
        }

        if (tripodEnabled) {
            if (activeTripod == tripod) {
                onDisableTripod();
                tripodEnabled = false;
            } else {
                onDisableTripod();
                onEnableTripod(tripod);
            }
        } else {
            if (freecamEnabled) {
                toggle();
            }
            onEnableTripod(tripod);
            tripodEnabled = true;
        }
        if (!tripodEnabled) {
            onDisabled();
        }
    }

    @ApiStatus.AvailableSince("1.1.8")
    public static void switchControls() {
        if (!isEnabled()) {
            return;
        }

        if (playerControlEnabled) {
            freeCamera.input = new KeyboardInput(MC.options);
        } else {
            MC.player.input = new KeyboardInput(MC.options);
            freeCamera.input = new Input();
        }
        playerControlEnabled = !playerControlEnabled;
    }

    private static void onEnableTripod(TripodSlot tripod) {
        onEnable();

        FreecamPosition position = tripods.get(tripod);
        boolean chunkLoaded = false;
        if (position != null) {
            ChunkPos chunkPos = position.getChunkPos();
            chunkLoaded = MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z);
        }

        if (!chunkLoaded) {
            resetCamera(tripod);
            position = null;
        }

        freeCamera = new FreeCamera(-420 - tripod.ordinal());
        if (position == null) {
            moveToPlayer();
        } else {
            moveToPosition(position);
        }

        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);
        activeTripod = tripod;

        if (ModConfig.INSTANCE.notification.notifyTripod) {
            MC.player.sendMessage(Text.translatable("msg.freecam.openTripod", tripod), true);
        }
    }

    private static void onDisableTripod() {
        tripods.put(activeTripod, new FreecamPosition(freeCamera));
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notification.notifyTripod) {
                MC.player.sendMessage(Text.translatable("msg.freecam.closeTripod", activeTripod), true);
            }
        }
        activeTripod = TripodSlot.NONE;
    }

    private static void onEnableFreecam() {
        onEnable();
        freeCamera = new FreeCamera(-420);
        moveToPlayer();
        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);

        if (ModConfig.INSTANCE.notification.notifyFreecam) {
            MC.player.sendMessage(Text.translatable("msg.freecam.enable"), true);
        }
    }

    private static void onDisableFreecam() {
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notification.notifyFreecam) {
                MC.player.sendMessage(Text.translatable("msg.freecam.disable"), true);
            }
        }
    }

    private static void onEnable() {
        MC.chunkCullingEnabled = false;

        rememberedF5 = MC.options.getPerspective();
        if (MC.gameRenderer.getCamera().isThirdPerson()) {
            MC.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }

    private static void onDisable() {
        MC.chunkCullingEnabled = true;
        MC.setCameraEntity(MC.player);
        playerControlEnabled = false;
        freeCamera.despawn();
        freeCamera.input = new Input();
        freeCamera = null;

        if (MC.player != null) {
            MC.player.input = new KeyboardInput(MC.options);
        }
    }

    private static void onDisabled() {
        if (rememberedF5 != null) {
            MC.options.setPerspective(rememberedF5);
        }
    }

    private static void resetCamera(TripodSlot tripod) {
        if (tripodEnabled && activeTripod != TripodSlot.NONE && activeTripod == tripod && freeCamera != null) {
            moveToPlayer();
        } else {
            tripods.put(tripod, null);
        }

        if (ModConfig.INSTANCE.notification.notifyTripod) {
            MC.player.sendMessage(Text.translatable("msg.freecam.tripodReset", tripod), true);
        }
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
    public static void moveToEntity(@Nullable Entity entity) {
        if (freeCamera == null) {
            return;
        }
        if (entity == null) {
            moveToPlayer();
            return;
        }
        freeCamera.copyPositionAndRotation(entity);
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
    public static void moveToPosition(@Nullable FreecamPosition position) {
        if (freeCamera == null) {
            return;
        }
        if (position == null) {
            moveToPlayer();
            return;
        }
        freeCamera.applyPosition(position);
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
    public static void moveToPlayer() {
        if (freeCamera == null) {
            return;
        }
        freeCamera.copyPositionAndRotation(MC.player);
        freeCamera.applyPerspective(
                ModConfig.INSTANCE.visual.perspective
        );
    }

    @ApiStatus.AvailableSince("0.4.0")
    public static FreeCamera getFreeCamera() {
        return freeCamera;
    }

    @ApiStatus.AvailableSince("1.2.3")
    public static void disableNextTick() {
        disableNextTick = true;
    }

    @ApiStatus.AvailableSince("0.2.2")
    public static boolean isEnabled() {
        return freecamEnabled || tripodEnabled;
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.0.0")
    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }
}
