package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntity;
import com.sandymandy.pleasurecraft.entity.base.tamable.SettlementGirlEntityAI;
import com.sandymandy.pleasurecraft.util.variables.Scene;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class MikaEntity extends SettlementGirlEntityAI {

    public MikaEntity(EntityType<? extends SettlementGirlEntityAI> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Item isAttractedTo() {
        return Items.OPEN_EYEBLOSSOM;
    }

    @Override
    public String getGirlID() {
        return "mika";
    }

    @Override
    public int getSizeGUI(){return 25;}

    @Override
    public float getWeaponBoneXRotation() {
        return -80f;
    }

    @Override
    public List<Scene> getScenes() {
        return List.of(
                Scene.onPlayer("Face fuck",
                        6,
                        List.of("carry_intro"),
                        List.of("carry_slow1"),
                        List.of("carry_fast"),
                        "carry_cum",
                        2.5f,
                        false,
                        false,
                        false),

                Scene.onBed("Missionary",
                        8,
                        List.of("missionary_intro"),
                        List.of("missionary_slow"),
                        List.of("missionary_fast"),
                        "missionary_cum",
                        3f,
                        true,
                        false,
                        true,
                        0.5f,
                        "sit_down",
                        "sit_down_idle"),

                Scene.onBed("Cowgirl",
                        10,
                        List.of("cowgirl_intro"),
                        List.of("cowgirl_slow"),
                        List.of("cowgirl_fast"),
                        "cowgirl_cum",
                        3f,
                        true,
                        false,
                        true,
                        0.5f,
                        "sit_down",
                        "sit_down_idle")
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return GirlEntity.createDefaultAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30)
                .add(EntityAttributes.MOVEMENT_SPEED, .18)
                .add(EntityAttributes.ATTACK_DAMAGE, 5);

    }
}