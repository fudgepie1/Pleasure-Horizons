package com.sandymandy.pleasurecraft.registries;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.util.variables.Scene;
import com.sandymandy.pleasurecraft.util.variables.ScenePhase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.UUID;

public class PleasureCraftTrackedDataRegistry {

    public static final TrackedDataHandler<Scene> SCENE =
            TrackedDataHandler.create(Scene.PACKET_CODEC);

    public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID =
            TrackedDataHandler.create(Uuids.PACKET_CODEC.collect(PacketCodecs::optional));

    public static final TrackedDataHandler<ScenePhase> SCENE_PHASE =
            TrackedDataHandler.create(ScenePhase.PACKET_CODEC);

    public static final TrackedDataHandler<Vec3d> VEC3D =
            TrackedDataHandler.create(Vec3d.PACKET_CODEC);

    public static void registerTrackedData(){
        PleasureCraft.LOGGER.info("Registering custom TrackedDataHandlers for PleasureCraft");
        FabricTrackedDataRegistry.register(Identifier.of(PleasureCraft.MOD_ID, "scene"), PleasureCraftTrackedDataRegistry.SCENE);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureCraft.MOD_ID, "uuid_optional"), PleasureCraftTrackedDataRegistry.OPTIONAL_UUID);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureCraft.MOD_ID, "scene_phase"), PleasureCraftTrackedDataRegistry.SCENE_PHASE);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureCraft.MOD_ID, "vec3d"), PleasureCraftTrackedDataRegistry.VEC3D);

    }
}
