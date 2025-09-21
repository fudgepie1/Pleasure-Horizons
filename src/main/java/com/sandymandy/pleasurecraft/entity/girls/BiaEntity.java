package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.AbstractGirlEntity;
import com.sandymandy.pleasurecraft.entity.base.SceneEntity;
import com.sandymandy.pleasurecraft.util.SceneOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class BiaEntity extends SceneEntity {

    public BiaEntity(EntityType<? extends AbstractGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getTameItem() {
        return Items.OXEYE_DAISY;
    }

    @Override
    protected String getGirlDisplayName() {
        return "Bia";
    }

    @Override
    public String getGirlID() {
        return "bia";
    }

    @Override
    public int getSizeGUI(){return 35;}

    @Override
    public List<SceneOptions> getSceneOptions() {
        return List.of(
                SceneOptions.of("Anal",
                        6,
                        List.of("anal_intro"),
                        List.of("anal_slow"),
                        List.of("anal_fast"),
                        "anal_cum",
                        true,
                        0f,
                        List.of("anal_lay_on_bed", "anal_bed_idle")),

                SceneOptions.of("Doggy",
                        8,
                        List.of("prone_doggy_intro"),
                        List.of("prone_doggy_slow"),
                        List.of("prone_doggy_hard1"/*,"prone_doggy_hard2","prone_doggy_hard3"*/),
                        "prone_doggy_cum",
                        true,
                        1f,
                        List.of("sit_down", "sit_down_idle"))
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20)
                .add(EntityAttributes.MOVEMENT_SPEED, .20)
                .add(EntityAttributes.TEMPT_RANGE, 5)
                .add(EntityAttributes.ATTACK_DAMAGE, 2);

    }



}