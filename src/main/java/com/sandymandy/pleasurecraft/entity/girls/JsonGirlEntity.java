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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class JsonGirlEntity extends GirlEntityAI {

    private JsonGirlProfile profile;

    public JsonGirlEntity(EntityType<? extends GirlEntityAI> type, World world) {
        super(type, world);
    }

    public void setProfile(JsonGirlProfile profile) {
        this.profile = profile;
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
        if (profile != null) {
            nbt.putString("GirlProfileID", profile.id());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("GirlProfileID")) {
            String id = nbt.getString("GirlProfileID").get();
            JsonGirlProfile profile = JsonGirlLoader.PROFILES.get(id);
            if (profile != null) this.setProfile(profile);
        }
    }

}
