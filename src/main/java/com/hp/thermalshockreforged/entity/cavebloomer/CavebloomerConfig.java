package com.hp.thermalshockreforged.entity.cavebloomer;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CavebloomerConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, KNOCKBACK, AURA_MULTIPLIER, AURA_HEALTH_FRACTION;
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
        SPEC = builder.build();
    }
    private CavebloomerConfig() { }
}
