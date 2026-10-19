package com.hp.thermalshockreforged.entity.gravers;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GraverConfig {
    public static final double[] DEFAULT_HEALTH = {11, 100, 12, 36, 24};
    public static final double[] DEFAULT_ARMOR = {1, 1, 0, 4, 4};
    public static final double[] DEFAULT_DAMAGE = {3, 3, 3, 5, 7};
    public static final double[] DEFAULT_SPEED = {.2, .15, .2, .16, .3};
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[5], ARMOR = new ModConfigSpec.DoubleValue[5], DAMAGE = new ModConfigSpec.DoubleValue[5], SPEED = new ModConfigSpec.DoubleValue[5];
    public static final ModConfigSpec.DoubleValue RARE_CHANCE, GUARD_CHANCE, STRIKE_MULTIPLIER, STRIKE_KNOCKBACK;
    public static final ModConfigSpec.IntValue RARE_EXPERIENCE, COOLDOWN;
    public static final ModConfigSpec SPEC;
    static {
        var builder = new ModConfigSpec.Builder();
        String[] names = {"graver", "neonLimeGraver", "assimilator", "assimilatedGraver", "razoraptor"};
        for (int i = 0; i < names.length; i++) {
            builder.push(names[i]);
            HEALTH[i] = builder.defineInRange("maxHealth", DEFAULT_HEALTH[i], 1, 1000000);
            ARMOR[i] = builder.defineInRange("armor", DEFAULT_ARMOR[i], 0, 1000);
            DAMAGE[i] = builder.defineInRange("attackDamage", DEFAULT_DAMAGE[i], 0, 2048);
            SPEED[i] = builder.defineInRange("movementSpeed", DEFAULT_SPEED[i], 0, 4);
            builder.pop();
        }
        RARE_CHANCE = builder.defineInRange("neonLimeSpawnChance", .00001, 0, 1);
        RARE_EXPERIENCE = builder.defineInRange("neonLimeExperience", 5345, 0, 1000000);
        builder.push("razoraptorSkills");
        GUARD_CHANCE = builder.defineInRange("guardChance", .3, 0, 1);
        STRIKE_MULTIPLIER = builder.defineInRange("strikeMultiplier", 5.0 / 7, 0, 100);
        STRIKE_KNOCKBACK = builder.defineInRange("strikeKnockback", .6, 0, 2);
        COOLDOWN = builder.defineInRange("cooldown", 30, 11, 72000);
        builder.pop();
        SPEC = builder.build();
    }
    private GraverConfig() { }
}
