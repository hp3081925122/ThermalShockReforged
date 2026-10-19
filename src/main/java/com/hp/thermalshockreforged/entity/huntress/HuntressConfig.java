package com.hp.thermalshockreforged.entity.huntress;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class HuntressConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, TRIPLE_MULTIPLIER, SLAM_MULTIPLIER, KNOCKBACK, TRIPLE_CHANCE;
    public static final ModConfigSpec.IntValue ROLLBACK_COOLDOWN, TRIPLE_COOLDOWN, SLAM_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("boss");
        HEALTH = builder.defineInRange("maxHealth", 300.0, 1, 1000000);
        ARMOR = builder.defineInRange("armor", 8.0, 0, 1000);
        DAMAGE = builder.defineInRange("attackDamage", 8.0, 0, 2048);
        SPEED = builder.defineInRange("movementSpeed", .35, 0, 4);
        RESISTANCE = builder.defineInRange("knockbackResistance", 1.0, 0, 1);
        TRIPLE_MULTIPLIER = builder.defineInRange("tripleHitMultiplier", 1.0, 0, 100);
        SLAM_MULTIPLIER = builder.defineInRange("slamMultiplier", 1.25, 0, 100);
        KNOCKBACK = builder.defineInRange("skillKnockback", .6, 0, 2);
        TRIPLE_CHANCE = builder.defineInRange("tripleHitChance", .5, 0, 1);
        ROLLBACK_COOLDOWN = builder.defineInRange("rollbackCooldown", 40, 1, 72000);
        TRIPLE_COOLDOWN = builder.defineInRange("tripleHitCooldown", 40, 21, 72000);
        SLAM_COOLDOWN = builder.defineInRange("slamCooldown", 70, 45, 72000);
        builder.pop(); SPEC = builder.build();
    }
    private HuntressConfig() { }
}
