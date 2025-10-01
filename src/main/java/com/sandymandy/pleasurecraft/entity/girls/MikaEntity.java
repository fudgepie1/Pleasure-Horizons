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

public class MikaEntity extends SceneEntity {

    public MikaEntity(EntityType<? extends AbstractGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getTameItem() {
        return Items.OPEN_EYEBLOSSOM;
    }

    @Override
    protected String getGirlDisplayName() {
        return "Mika";
    }

    @Override
    public String getGirlID() {
        return "mika";
    }

    @Override
    public int getSizeGUI(){return 25;}

    @Override
    public List<SceneOptions> getSceneOptions() {
        return List.of(
                SceneOptions.create("Face fuck",
                        6,
                        List.of("carry_intro"),
                        List.of("carry_slow1"),
                        List.of("carry_fast"),
                        "carry_cum",
                        2.5f,
                        false)
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30)
                .add(EntityAttributes.MOVEMENT_SPEED, .15)
                .add(EntityAttributes.TEMPT_RANGE, 15)
                .add(EntityAttributes.ATTACK_DAMAGE, 5);

    }
}