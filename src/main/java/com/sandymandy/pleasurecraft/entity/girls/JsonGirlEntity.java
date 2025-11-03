package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import com.sandymandy.pleasurecraft.util.variables.JsonGirlProfile;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class JsonGirlEntity extends GirlEntityAI {

    private JsonGirlProfile profile = JsonGirlProfile.DEFAULT;
    private static final TrackedData<String> GIRL_ID = DataTracker.registerData(JsonGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> GIRL_NAME = DataTracker.registerData(JsonGirlEntity.class, TrackedDataHandlerRegistry.STRING);

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GIRL_ID, "default");
        builder.add(GIRL_NAME, "Default Girl");
    }

    public JsonGirlEntity(EntityType<? extends GirlEntityAI> type, World world) {
        super(type, world);
    }

    public void setProfile(JsonGirlProfile profile) {
        if (profile == null) profile = JsonGirlProfile.DEFAULT;
        this.profile = profile;
    }

    public JsonGirlProfile getProfile() {
        return (profile != null ? profile : JsonGirlProfile.DEFAULT);
    }

    @Override
    public String getGirlID() {
        return this.dataTracker.get(GIRL_ID);
    }

    @Override
    public String getGirlDisplayName() {
        return this.dataTracker.get(GIRL_NAME);
    }

    @Override
    protected Item getTameItem() {
        return getProfile().tameItem();
    }

    @Override
    public List<SceneOptions> getSceneOptions() {
        return getProfile().scenes();
    }

    @Override
    public int getSizeGUI() {
        return getProfile().guiSize();
    }

    @Override
    public float getYAxisGUI() {
        return getProfile().guiYOffset();
    }


    // Save profile ID
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("GirlProfileID", JsonGirlProfile.CODEC, profile);
    }

    // Load profile ID OR fallback to default
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("GirlProfileID")) {
            this.profile = nbt.get("GirlProfileID", JsonGirlProfile.CODEC).orElse(JsonGirlProfile.DEFAULT);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.getWorld().isClient()) this.dataTracker.set(GIRL_ID, getProfile().id());
        if(!this.getWorld().isClient()) this.dataTracker.set(GIRL_NAME, getProfile().name());
    }
}
