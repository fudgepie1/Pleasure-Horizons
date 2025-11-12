package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import com.sandymandy.pleasurecraft.util.json.CustomGirlLoader;
import com.sandymandy.pleasurecraft.util.variables.CustomGirlProfile;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class CustomGirlEntity extends GirlEntityAI {

    private CustomGirlProfile profile = CustomGirlProfile.DEFAULT;
    private float lastHitboxHeight = 1f;

    private static final TrackedData<String> GIRL_ID = DataTracker.registerData(CustomGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> GIRL_NAME = DataTracker.registerData(CustomGirlEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Float> HITBOX_HEIGHT = DataTracker.registerData(CustomGirlEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public CustomGirlEntity(EntityType<? extends GirlEntityAI> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GIRL_ID, "default");
        builder.add(GIRL_NAME, "Default Girl");
        builder.add(HITBOX_HEIGHT, 1.95f);
    }

    public void setProfile(CustomGirlProfile profile) {
        if (profile == null) profile = CustomGirlProfile.DEFAULT;
        this.profile = profile;

        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MAX_HEALTH))
                .setBaseValue(profile.maxHealth());
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED))
                .setBaseValue(profile.movementSpeed());
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE))
                .setBaseValue(profile.attackDamage());

        this.setHealth((float) profile.maxHealth());

        // Update hitbox dimension
        if (!this.getWorld().isClient()) {
            this.dataTracker.set(HITBOX_HEIGHT, profile.hitboxHeight());
        }
        this.calculateDimensions();
    }

    public CustomGirlProfile getProfile() {
        return profile != null ? profile : CustomGirlProfile.DEFAULT;
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

    private float getHitBoxHeight() {
        return this.dataTracker.get(HITBOX_HEIGHT);
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        return EntityDimensions.changing(0.5f, getHitBoxHeight());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        // When height changes (client side sync), recalc immediately
        if (data.equals(HITBOX_HEIGHT)) {
            this.calculateDimensions();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            this.dataTracker.set(GIRL_ID, getProfile().id());
            this.dataTracker.set(GIRL_NAME, getProfile().name());

            float currentHeight = getProfile().hitboxHeight();
            if (currentHeight != lastHitboxHeight) {
                this.lastHitboxHeight = currentHeight;
                this.dataTracker.set(HITBOX_HEIGHT, currentHeight);
                this.calculateDimensions();
            }
        }
    }

    @Override
    public void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putString("GirlProfileID", profile.id());
    }

    @Override
    public void readCustomData(ReadView view) {
        super.readCustomData(view);
        String id = view.getString("GirlProfileID", "default_girl");
        CustomGirlProfile p = CustomGirlLoader.PROFILES.get(id);
        this.profile = (p != null ? p : CustomGirlProfile.DEFAULT);
        this.dataTracker.set(HITBOX_HEIGHT, this.profile.hitboxHeight());
        this.calculateDimensions();
    }
}
