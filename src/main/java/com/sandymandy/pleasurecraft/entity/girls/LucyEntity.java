package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.AbstractGirlEntity;
import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.util.SceneOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class LucyEntity extends SceneEntity {

    public LucyEntity(EntityType<? extends AbstractGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getTameItem() {
        return Items.ALLIUM;
    }

    @Override
    protected String getGirlDisplayName() {
        return "Lucy";
    }

    @Override
    public String getGirlID() {
        return "lucy";
    }

    @Override
    protected int getMaxRelationshipLevel() {
        return 10;
    }

    @Override
    public int getSizeGUI(){return 29;}

    @Override
    public float getYAxisGUI(){return 0.0525F;}

    @Override
    public List<SceneOptions> getSceneOptions() {
        return List.of(
                SceneOptions.of("Paizuri",
                        6,
                        List.of("paizuri_intro"),
                        List.of("paizuri_slow"),
                        List.of("paizuri_fast"),
                        "paizuri_cum",
                        true),


                SceneOptions.of("Blow Job",
                        8,
                        List.of("blowjob_intro"),
                        List.of("blowjob_slow"),
                        List.of("blowjob_fast"),
                        "blowjob_cum",
                        false),

                SceneOptions.of("Doggy",
                        10,
                        List.of("doggy_intro"),
                        List.of("doggy_slow"),
                        List.of("doggy_fast1"),
                        "doggy_cum",
                        true,
                        false,
                        0f,
                        List.of("doggy_lay_on_bed", "doggy_bed_idle"))
                );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20)
                .add(EntityAttributes.MOVEMENT_SPEED, .20)
                .add(EntityAttributes.TEMPT_RANGE, 15)
                .add(EntityAttributes.ATTACK_DAMAGE, 2);
    }
}