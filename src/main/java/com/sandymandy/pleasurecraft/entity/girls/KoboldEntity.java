package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.entity.base.GirlEntity;
import com.sandymandy.pleasurecraft.entity.base.wild.WildGirlEntity;
import com.sandymandy.pleasurecraft.util.Colors;
import com.sandymandy.pleasurecraft.util.variables.Scene;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KoboldEntity extends WildGirlEntity {

    // ===== Tracked Data =====
    private static final TrackedData<Integer> BODY_SIZE = DataTracker.registerData(KoboldEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PRIMARY_COLOR = DataTracker.registerData(KoboldEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SECONDARY_COLOR = DataTracker.registerData(KoboldEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> IRIS_COLOR = DataTracker.registerData(KoboldEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TOP_HORN_TYPE = DataTracker.registerData(KoboldEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BOTTOM_HORN_TYPE = DataTracker.registerData(KoboldEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    protected Map<EquipmentSlot, List<String>> getArmorBones() {
        Map<EquipmentSlot, List<String>> bones = super.getArmorBones();
        bones.put(EquipmentSlot.LEGS, List.of(
                "armorHip", "armorPantsLowL", "armorPantsUpL",
                "armorPantsLowR", "armorPantsUpR", "armorBootyL",
                "armorBootyR", "armorKneeR", "armorKneeL"
        ));
        return bones;
    }

    @Override
    public int getBreastMaxSize() {
        return 140;
    }

    @Override
    public int getBreastMinSize() {
        return 60;
    }

    public int getBodyMaxSize() {
        return 115;
    }

    public int getBodyMinSize() {
        return 65;
    }

    // Bone Categories
    private List<String> primaryBones() {return List.of(
            "armL", "armR", "torsoR", "torsoL", "neck", "hip", "head",
            "hornDL2", "hornDR2", "hornDL3M", "hornDR3M", "legL", "legR"
    );}

    private List<String> secondaryBones() {return List.of(
            "frontNeck", "layer2", "layer", "vagina", "boobs", "innerCheekRL",
            "innerCheekLL", "down", "down2", "down3", "down4", "down5",
            "hornDL3S", "hornDR3S", "fuckhole"
    );}

    private List<String> irisBones() {return List.of("irisL", "irisR");}

    private List<String> ignoreBones() {
        List<String> bones = new ArrayList<>(List.of(
                "hornUR", "hornUL", "hornDR", "hornDL", "mouth", "eyes",
                "dotL", "dotR", "tailpack", "crown"
        ));

        for (List<String> boneNames : getArmorBones().values())
        {
            bones.addAll(boneNames);
        }

        return  bones;
    };

    // ===== Horn Type Lists =====
    private final List<String> topHornType0 = List.of("hornUL0", "hornUR0");
    private final List<String> topHornType1 = List.of("hornUL1", "hornUR1");
    private final List<String> topHornType2 = List.of("hornUL2", "hornUR2");
    private final List<String> topHornType3 = List.of("hornUL3", "hornUR3");
    private final List<String> topHornType4 = List.of("hornUL4", "hornUR4");
    private final List<String> topHornType5 = List.of("hornUL5", "hornUR5");
    private final List<String> topHornType6 = List.of("hornUL6", "hornUR6");
    private final List<String> topHornType7 = List.of("hornUL7", "hornUR7");

    private final List<String> bottomHornType0 = List.of("hornDL0", "hornDR0");
    private final List<String> bottomHornType1 = List.of("hornDL1", "hornDR1");
    private final List<String> bottomHornType2 = List.of("hornDL2", "hornDR2");

    // Track if we've already applied customization
    private boolean customizationApplied = false;

    public KoboldEntity(EntityType<? extends WildGirlEntity> entityType, World world) {
        super(entityType, world);
        randomizeAppearance();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BODY_SIZE, 100);
        builder.add(PRIMARY_COLOR, Colors.PEACH);
        builder.add(SECONDARY_COLOR, Colors.BANANA);
        builder.add(IRIS_COLOR, Colors.SKY_BLUE);
        builder.add(TOP_HORN_TYPE, 0);
        builder.add(BOTTOM_HORN_TYPE, 0);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (!this.getWorld().isClient()) {
            randomizeAppearance();
        }
    }

    // ===== Color Presets =====
    public enum PatternPresets {
        PEACH_BANANA(Colors.PEACH, Colors.BANANA),
        BLUE_WHITE(Colors.BLUE, Colors.WHITE),
        RED_ORANGE(Colors.RED, Colors.ORANGE),
        GREEN_LIME(Colors.GREEN, Colors.LIME),
        PURPLE_PINK(Colors.PURPLE, Colors.PINK),
        GRAY_DARK(Colors.GRAY, Colors.DARK_GRAY),
        CYAN_TEAL(Colors.CYAN, Colors.TEAL);

        public final int primary;
        public final int secondary;

        PatternPresets(int primary, int secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }
    }

    // ===== Randomization =====
    public void randomizeAppearance() {
        // Random body size
        this.setBodySize(RANDOM.nextInt(getBodyMinSize(), getBodyMaxSize()));

        // Random color
        PatternPresets preset = PatternPresets.values()[RANDOM.nextInt(PatternPresets.values().length)];
        this.setColorPreset(preset);

        Integer irisColor = Colors.ALL_COLORS.get(RANDOM.nextInt(Colors.ALL_COLORS.size()));
        this.setIrisColor(irisColor);

        // Random horn type
        this.setTopHornType(RANDOM.nextInt(0, 7));
        this.setBottomHornType(RANDOM.nextInt(0, 2));

        // Random breast size
        this.setBreastSize(RANDOM.nextInt(getBreastMinSize(), getBreastMaxSize()));
    }

    // ===== Setters =====
    public void setColorPreset(PatternPresets preset) {
        this.dataTracker.set(PRIMARY_COLOR, preset.primary);
        this.dataTracker.set(SECONDARY_COLOR, preset.secondary);
        customizationApplied = false; // Mark for re-application
    }

    public void setBodySize(int size) {
        this.dataTracker.set(BODY_SIZE, size);
        customizationApplied = false;
    }

    public void setPrimaryColor(int color) {
        this.dataTracker.set(PRIMARY_COLOR, color);
        customizationApplied = false;
    }

    public void setSecondaryColor(int color) {
        this.dataTracker.set(SECONDARY_COLOR, color);
        customizationApplied = false;
    }

    public void setIrisColor(int color) {
        this.dataTracker.set(IRIS_COLOR, color);
        customizationApplied = false;
    }

    public void setTopHornType(int type) {
        this.dataTracker.set(TOP_HORN_TYPE, Math.clamp(type, 0, 7));
        customizationApplied = false;
    }

    public void setBottomHornType(int type) {
        this.dataTracker.set(BOTTOM_HORN_TYPE, Math.clamp(type, 0, 2));
        customizationApplied = false;
    }

    // ===== Getters =====
    public int getBodySize() { return this.dataTracker.get(BODY_SIZE); }
    public int getPrimaryColor() { return this.dataTracker.get(PRIMARY_COLOR); }
    public int getSecondaryColor() { return this.dataTracker.get(SECONDARY_COLOR); }
    public int getIrisColor() { return this.dataTracker.get(IRIS_COLOR); }
    public int getTopHornType() { return this.dataTracker.get(TOP_HORN_TYPE); }
    public int getBottomHornType() { return this.dataTracker.get(BOTTOM_HORN_TYPE); }

    // ===== Apply Customizations =====
    private void applyCustomizations() {
        if (!this.getWorld().isClient()) return;

        // Apply

        // Apply colors
        this.overrideBoneColor(primaryBones(), getPrimaryColor());
        this.overrideBoneColor(secondaryBones(), getSecondaryColor());
        this.overrideBoneColor(irisBones(), getIrisColor());
        this.overrideBoneColor(ignoreBones(), Colors.WHITE);

        // Hide all horn types first
        this.setBoneVisibility(topHornType0, false);
        this.setBoneVisibility(topHornType1, false);
        this.setBoneVisibility(topHornType2, false);
        this.setBoneVisibility(topHornType3, false);
        this.setBoneVisibility(topHornType4, false);
        this.setBoneVisibility(topHornType5, false);
        this.setBoneVisibility(topHornType6, false);
        this.setBoneVisibility(topHornType7, false);

        this.setBoneVisibility(bottomHornType0, false);
        this.setBoneVisibility(bottomHornType1, false);
        this.setBoneVisibility(bottomHornType2, false);

        // Show selected horn type
        switch (getTopHornType()) {
            case 0 -> this.setBoneVisibility(topHornType0, true);
            case 1 -> this.setBoneVisibility(topHornType1, true);
            case 2 -> this.setBoneVisibility(topHornType2, true);
            case 3 -> this.setBoneVisibility(topHornType3, true);
            case 4 -> this.setBoneVisibility(topHornType4, true);
            case 5 -> this.setBoneVisibility(topHornType5, true);
            case 6 -> this.setBoneVisibility(topHornType6, true);
            case 7 -> this.setBoneVisibility(topHornType7, true);
        }

        switch (getBottomHornType()) {
            case 0 -> this.setBoneVisibility(bottomHornType0, true);
            case 1 -> this.setBoneVisibility(bottomHornType1, true);
            case 2 -> this.setBoneVisibility(bottomHornType2, true);
        }

        customizationApplied = true;
    }

    // ===== Persistence =====
    @Override
    public void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt("BodySize", getBodySize());
        view.putInt("PrimaryColor", getPrimaryColor());
        view.putInt("SecondaryColor", getSecondaryColor());
        view.putInt("IrisColor", getIrisColor());
        view.putInt("TopHornType", getTopHornType());
        view.putInt("BottomHornType", getBottomHornType());
    }

    @Override
    public void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.dataTracker.set(BODY_SIZE, view.getInt("BodySize", 100));
        this.dataTracker.set(PRIMARY_COLOR, view.getInt("PrimaryColor", Colors.PEACH));
        this.dataTracker.set(SECONDARY_COLOR, view.getInt("SecondaryColor", Colors.BANANA));
        this.dataTracker.set(IRIS_COLOR, view.getInt("IrisColor", Colors.SKY_BLUE));
        this.dataTracker.set(TOP_HORN_TYPE, view.getInt("TopHornType", 0));
        this.dataTracker.set(BOTTOM_HORN_TYPE, view.getInt("BottomHornType", 0));
        customizationApplied = false; // Re-apply on load
    }

    @Override
    public void tick() {
        super.tick();

        // Only apply customizations when needed (on client side)
        if (this.getWorld().isClient() && !customizationApplied) {
            applyCustomizations();
        }

        if(this.getWorld().isClient()) this.setBoneSize("body", getBodySize());
    }

    // ===== Data Tracker Changes =====
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        // Mark for re-application when any customization data changes
        if (data.equals(PRIMARY_COLOR) || data.equals(SECONDARY_COLOR) ||
                data.equals(IRIS_COLOR) || data.equals(TOP_HORN_TYPE) || data.equals(BOTTOM_HORN_TYPE)) {
            customizationApplied = false;
        }
    }

    // Entity Properties
    @Override
    public Item isAttractedTo() {
        return Items.RAW_IRON;
    }

    @Override
    public String getGirlID() {
        return "kobold";
    }

    @Override
    public int getSizeGUI() {
        return 29;
    }

    @Override
    public float getYAxisGUI() {
        return 0.0525F;
    }

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
                .add(EntityAttributes.MOVEMENT_SPEED, .12)
                .add(EntityAttributes.ATTACK_DAMAGE, 2);
    }
}