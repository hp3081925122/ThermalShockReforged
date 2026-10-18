package com.hp.thermalshockreforged.entity.cavebloomer;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CavebloomerConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, KNOCKBACK, AURA_MULTIPLIER, AURA_HEALTH_FRACTION;
    public static final ModConfigSpec.DoubleValue RAPID_DIRECT, RING_DIRECT, MORTAR_IMPACT, POISON_IMPACT, SLAM_BLOCKED, SLAM_DIRECT;
    public static final ModConfigSpec.IntValue SUMMON_CAP, SUMMON_LIFETIME;
    static {
        var builder = new ModConfigSpec.Builder();
        HEALTH = builder.defineInRange("maxHealth", 325.0, 1, 1000000);
        ARMOR = builder.defineInRange("armor", 10.0, 0, 1000);
        DAMAGE = builder.defineInRange("attackDamage", 7.0, 0, 2048);
        SPEED = builder.defineInRange("movementSpeed", 0.2, 0, 4);
        RESISTANCE = builder.defineInRange("knockbackResistance", 0.2, 0, 1);
        KNOCKBACK = builder.defineInRange("attackKnockback", 0.6, 0, 2);
        AURA_MULTIPLIER = builder.defineInRange("auraDamageMultiplier", 2.0 / 7.0, 0, 100);
        AURA_HEALTH_FRACTION = builder.defineInRange("auraHealthFraction", 0.5, 0, 1);
        RAPID_DIRECT = builder.defineInRange("rapidShotDamageMultiplier", 4.0 / 7.0, 0, 100);
        RING_DIRECT = builder.defineInRange("ringShotDamageMultiplier", 10.0 / 7.0, 0, 100);
        MORTAR_IMPACT = builder.defineInRange("mortarImpactDamageMultiplier", 10.0 / 7.0, 0, 100);
        POISON_IMPACT = builder.defineInRange("poisonImpactDamageMultiplier", 6.0 / 7.0, 0, 100);
        SLAM_BLOCKED = builder.defineInRange("blockedSlamDamageMultiplier", 8.0 / 7.0, 0, 100);
        SLAM_DIRECT = builder.defineInRange("slamDamageMultiplier", 30.0 / 7.0, 0, 100);
        SUMMON_CAP = builder.defineInRange("ownedDandyCap", 40, 5, 1000);
        SUMMON_LIFETIME = builder.defineInRange("ownedDandyLifetimeTicks", 2400, 100, 72000);
        SPEC = builder.build();
    }
    private CavebloomerConfig() { }
}
