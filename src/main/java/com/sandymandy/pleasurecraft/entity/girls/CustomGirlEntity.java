package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import com.sandymandy.pleasurecraft.registries.SceneKeyframeRegistry;
import com.sandymandy.pleasurecraft.util.json.CustomGirlLoader;
import com.sandymandy.pleasurecraft.util.variables.CustomGirlProfile;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import java.util.List;

public class CustomGirlEntity extends GirlEntityAI {

    private CustomGirlProfile profile = CustomGirlProfile.DEFAULT;
    private float lastHitboxHeight = CustomGirlProfile.DEFAULT.hitboxHeight(); // Track last known height

    private static final TrackedData<String> GIRL_ID = DataTracker.registerData(CustomGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> GIRL_NAME = DataTracker.registerData(CustomGirlEntity.class, TrackedDataHandlerRegistry.STRING);

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GIRL_ID, "default");
        builder.add(GIRL_NAME, "Default Girl");
    }

    public CustomGirlEntity(EntityType<? extends GirlEntityAI> type, World world) {
        super(type, world);
    }

    public void setProfile(CustomGirlProfile profile) {
        if (profile == null) profile = CustomGirlProfile.DEFAULT;
        this.profile = profile;
    }

    public CustomGirlProfile getProfile() {
        return (profile != null ? profile : CustomGirlProfile.DEFAULT);
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

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.5f, getProfile().hitboxHeight());}

    // Save profile ID
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("GirlProfileID", profile.id());
    }

    // Load profile ID OR fallback to default
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("GirlProfileID")) {
            String id = nbt.getString("GirlProfileID").orElse("default_girl");
            CustomGirlProfile p = CustomGirlLoader.PROFILES.get(id);
            this.profile = (p != null ? p : CustomGirlProfile.DEFAULT);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.getWorld().isClient()) this.dataTracker.set(GIRL_ID, getProfile().id());
        if(!this.getWorld().isClient()) this.dataTracker.set(GIRL_NAME, getProfile().name());

        float currentHeight = getProfile().hitboxHeight();
        if (currentHeight != lastHitboxHeight) {
            this.lastHitboxHeight = currentHeight;
            this.calculateDimensions();
        }
    }

    @Override
    protected void messageHandler() {
        String key = getAnimationKeyFrameEvent();

        List<String> girlMsgs = SceneKeyframeRegistry.getCustomGirlMessage(this.getGirlID(), key);

        for (String msg : girlMsgs) {
            this.messageAsEntity(false, msg);
        }
    }

    @Override
    protected void soundHandler() {
        String key = getAnimationKeyFrameEvent();

        // Get all sounds for this key
        List<SoundEvent> sounds = SceneKeyframeRegistry.getCustomGirlSound(this.getGirlID(), key);

        // Play all sounds sequentially (or simultaneously)
        for (SoundEvent sound : sounds) {
            this.playSound(sound, 1.0f, 1.0f);
        }
    }
}
