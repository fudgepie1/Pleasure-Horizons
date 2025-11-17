package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntityAI;
import com.sandymandy.pleasurecraft.util.variables.SceneOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.List;

public class SlimeEntity extends GirlEntityAI {
    public SlimeEntity(EntityType<? extends GirlEntityAI> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getTameItem() {
        return Items.LILY_PAD;
    }

    @Override
    public String getGirlID() {
        return "slime";
    }

    @Override
    public int getSizeGUI(){return 35;}

    @Override
    public List<SceneOptions> getSceneOptions() {
        return List.of(
                SceneOptions.onPlayer("Blow Job",
                        8,
                        List.of("blowjob_intro"),
                        List.of("blowjob_slow"),
                        List.of("blowjob_fast"),
                        "blowjob_cum",
                        4,
                        false),

                SceneOptions.onBed("Doggy",
                        10,
                        List.of("doggy_intro"),
                        List.of("doggy_slow"),
                        List.of("doggy_fast1"),
                        "doggy_cum",
                        6f,
                        true,
                        false,
                        0f,
                        "doggy_lay_on_bed",
                        "doggy_bed_idle")
        );
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.IS_FALL)) {
            return false;
        }
        return super.damage(world, source, amount);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 15)
                .add(EntityAttributes.MOVEMENT_SPEED, .20)
                .add(EntityAttributes.TEMPT_RANGE, 15)
                .add(EntityAttributes.ATTACK_DAMAGE, 2);
    }
}
