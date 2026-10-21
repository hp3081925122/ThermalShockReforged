package com.hp.thermalshockreforged.entity.mangler;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ManglerCombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HERALD_HEALTH, HERALD_ARMOR, HERALD_KNOCKBACK, HERALD_RESISTANCE, SUMMON_CHANCE;
    public static final ModConfigSpec.IntValue SUMMON_COOLDOWN, SUMMON_CAP, SUMMON_LIFETIME, BOOST_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("herald");
        HERALD_HEALTH = builder.defineInRange("maxHealth", 275.0, 1.0, 1000000.0);
        HERALD_ARMOR = builder.defineInRange("armor", 16.0, 0.0, 1000.0);
        HERALD_KNOCKBACK = builder.defineInRange("attackKnockback", 0.6, 0.0, 2.0);
        HERALD_RESISTANCE = builder.defineInRange("knockbackResistance", 1.0, 0.0, 1.0);
        SUMMON_CHANCE = builder.defineInRange("summonChance", 0.3, 0.0, 1.0);
        SUMMON_COOLDOWN = builder.defineInRange("summonAttemptCooldown", 40, 1, 72000);
        SUMMON_CAP = builder.defineInRange("maxLivingSummons", 3, 1, 3);
        SUMMON_LIFETIME = builder.defineInRange("summonLifetime", 320, 1, 72000);
        builder.pop();
        BOOST_COOLDOWN = builder.defineInRange("manglerBoostCooldown", 40, 1, 72000);
        SPEC = builder.build();
    }
    private ManglerCombatConfig() { }
}
