package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntity;
import com.sandymandy.pleasurecraft.entity.base.wild.WildGirlEntity;
import com.sandymandy.pleasurecraft.util.variables.Scene;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import com.sandymandy.pleasurecraft.util.Colors;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class KoboldEntity extends WildGirlEntity {
    public KoboldEntity(EntityType<? extends WildGirlEntity> entityType, World world) {
        super(entityType, world);
    }

    private final List<String> primaryBones = List.of("armL", "armR", "torsoR", "torsoL", "neck", "hip", "head", "hornDL2", "hornDR2", "hornDL3M", "hornDR3M", "legL", "legR");
    private final List<String> secondaryBones = List.of("frontNeck", "layer2", "layer", "vagina", "boobs", "innerCheekRL", "innerCheekLL", "down", "down2", "down3", "down4", "down5", "hornDL3S", "hornDR3S", "fuckhole");
    private final List<String> irisBones = List.of("irisL", "irisR");
    private final List<String> ignoreBones = List.of("hornUR", "hornUL", "hornDR", "hornDL", "mouth", "eyes", "dotL", "dotR", "tailpack", "crown");

    @Override
    protected Map<EquipmentSlot, List<String>> getArmorBones() {
        Map<EquipmentSlot, List<String>> bones = super.getArmorBones();

        bones.put(EquipmentSlot.LEGS, List.of(
                "armorHip",
                "armorPantsLowL",
                "armorPantsUpL",
                "armorPantsLowR",
                "armorPantsUpR",
                "armorBootyL",
                "armorBootyR",
                "armorKneeR",
                "armorKneeL"));

        return bones;
    }

    @Override
    public Item isAttractedTo() {
        return Items.RAW_IRON;
    }

    @Override
    public String getGirlID() {
        return "kobold";
    }

    @Override
    public int getSizeGUI(){return 29;}

    @Override
    public float getYAxisGUI(){return 0.0525F;}

    @Override
    public float getWeaponBoneXRotation() {
        return -100f;
    }

    @Override
    public boolean hasStripAnim() {
        return false;
    }

    @Override
    public List<Scene> getScenes() {
        return List.of(
                Scene.onPlayer("Blow Job",
                        4,
                        List.of("blowjob_intro"),
                        List.of("blowjob_slow_R", "blowjob_slow_L"),
                        List.of("blowjob_fast"),
                        "blowjob_cum",
                        2.5f,
                        false,
                        false,
                        false),

                Scene.onPlayer("Anal",
                        6,
                        List.of("anal_intro"),
                        List.of("anal_slow"),
                        List.of("anal_fast"),
                        "anal_cum",
                        4.5f,
                        true,
                        true,
                        false)
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return GirlEntity.createDefaultAttributes()
                .add(EntityAttributes.MAX_HEALTH, 15)
                .add(EntityAttributes.MOVEMENT_SPEED, .12 )
                .add(EntityAttributes.ATTACK_DAMAGE, 2);
    }

    @Override
    public void tick() {
        super.tick();
        this.overrideBoneColor(primaryBones, Colors.PEACH);
        this.overrideBoneColor(secondaryBones, Colors.BANANA);
        this.overrideBoneColor(irisBones, Colors.SKY_BLUE);
        this.overrideBoneColor(ignoreBones, Colors.WHITE);
    }
}
