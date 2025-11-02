package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import com.sandymandy.pleasurecraft.util.json.JsonGirlLoader;
import com.sandymandy.pleasurecraft.util.json.JsonGirlProfiles;
import com.sandymandy.pleasurecraft.util.variables.JsonGirlProfile;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class JsonGirlEntity extends GirlEntityAI {

    private JsonGirlProfile profile = JsonGirlProfiles.DEFAULT;

    public JsonGirlEntity(EntityType<? extends GirlEntityAI> type, World world) {
        super(type, world);
    }

    public void setProfile(JsonGirlProfile profile) {
        if (profile == null) profile = JsonGirlProfiles.DEFAULT;
        this.profile = profile;
    }

    public JsonGirlProfile getProfile() {
        return (profile != null ? profile : JsonGirlProfiles.DEFAULT);
    }

    @Override
    public String getGirlID() {
        return getProfile().id();
    }

    @Override
    public String getGirlDisplayName() {
        return getProfile().name();
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
        nbt.putString("GirlProfileID", profile.id());
    }

    // Load profile ID OR fallback to default
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("GirlProfileID")) {
            String id = nbt.getString("GirlProfileID").orElse("default_girl");
            JsonGirlProfile p = JsonGirlLoader.PROFILES.get(id);
            this.profile = (p != null ? p : JsonGirlProfiles.DEFAULT);
        }
    }
}
