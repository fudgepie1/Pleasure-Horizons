package com.sandymandy.pleasurehorizons.registries;

import com.sandymandy.pleasurehorizons.PleasureHorizons;
import com.sandymandy.pleasurehorizons.util.variables.Scene;
import com.sandymandy.pleasurehorizons.util.variables.ScenePhase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.UUID;

public class PleasureHorizonsTrackedDataRegistry {

    public static final TrackedDataHandler<Scene> SCENE =
            TrackedDataHandler.create(Scene.PACKET_CODEC);

    public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID =
            TrackedDataHandler.create(Uuids.PACKET_CODEC.collect(PacketCodecs::optional));

    public static final TrackedDataHandler<UUID> UUID =
            TrackedDataHandler.create(Uuids.PACKET_CODEC);

    public static final TrackedDataHandler<ScenePhase> SCENE_PHASE =
            TrackedDataHandler.create(ScenePhase.PACKET_CODEC);

    public static final TrackedDataHandler<Vec3d> VEC3D =
            TrackedDataHandler.create(Vec3d.PACKET_CODEC);

    public static void registerTrackedData(){
        PleasureHorizons.LOGGER.info("Registering custom TrackedDataHandlers for " + PleasureHorizons.MOD_NAME);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureHorizons.MOD_ID, "scene"), PleasureHorizonsTrackedDataRegistry.SCENE);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureHorizons.MOD_ID, "uuid_optional"), PleasureHorizonsTrackedDataRegistry.OPTIONAL_UUID);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureHorizons.MOD_ID, "uuid"), PleasureHorizonsTrackedDataRegistry.UUID);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureHorizons.MOD_ID, "scene_phase"), PleasureHorizonsTrackedDataRegistry.SCENE_PHASE);
        FabricTrackedDataRegistry.register(Identifier.of(PleasureHorizons.MOD_ID, "vec3d"), PleasureHorizonsTrackedDataRegistry.VEC3D);

    }
}
