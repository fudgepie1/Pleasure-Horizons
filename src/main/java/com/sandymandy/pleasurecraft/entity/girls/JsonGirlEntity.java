package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import com.sandymandy.pleasurecraft.util.JsonGirlLoader;
import com.sandymandy.pleasurecraft.util.variables.JsonGirlProfile;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class JsonGirlEntity extends GirlEntityAI {

    private final JsonGirlProfile profile;

    // Profile is required at construction
    // New constructor

    public JsonGirlEntity(EntityType<? extends GirlEntityAI> type, World world, JsonGirlProfile profile) {
        super(type, world);
        this.profile = profile;

        // Apply attributes immediately
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(profile.maxHealth());
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(profile.movementSpeed());
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(profile.attackDamage());
        this.setHealth((float) profile.maxHealth());
    }

    public JsonGirlEntity(EntityType<? extends GirlEntityAI> type, World world) {
        super(type, world);
        this.profile = new JsonGirlProfile("",1,.1f, Items.ACACIA_FENCE,3,2,2,List.of());
    }

    public JsonGirlProfile getProfile() {
        return profile;
    }

    @Override
    public String getGirlID() {
        return profile.id();
    }

    @Override
    protected Item getTameItem() {
        return profile.tameItem();
    }

    @Override
    public List<SceneOptions> getSceneOptions() {
        return profile.scenes();
    }

    @Override
    public int getSizeGUI() {
        return profile.guiSize();
    }

    @Override
    public float getYAxisGUI() {
        return profile.guiYOffset();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("GirlProfileID", profile.id());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("GirlProfileID")) {
            String id = nbt.getString("GirlProfileID").get();
            // Optional: reload profile from loader if needed
        }
    }

    // Attributes can still be generated from the profile
    public static DefaultAttributeContainer.Builder createAttributes(JsonGirlProfile profile) {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, profile.maxHealth())
                .add(EntityAttributes.MOVEMENT_SPEED, profile.movementSpeed())
                .add(EntityAttributes.ATTACK_DAMAGE, profile.attackDamage());
    }
}

