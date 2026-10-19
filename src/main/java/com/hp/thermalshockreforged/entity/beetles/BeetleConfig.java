package com.hp.thermalshockreforged.entity.beetles;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class BeetleConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[2], ARMOR = new ModConfigSpec.DoubleValue[2], DAMAGE = new ModConfigSpec.DoubleValue[2], SPEED = new ModConfigSpec.DoubleValue[2], RESISTANCE = new ModConfigSpec.DoubleValue[2];
    public static final ModConfigSpec.DoubleValue CRASHER_STRIKE, CRASHER_AURA, THRASHER_STRIKE, STRIKE_KNOCKBACK, CRASHER_HEAVY_CHANCE, THRASHER_TRIPLE_CHANCE;
    public static final ModConfigSpec.IntValue CRASHER_STRIKE_COOLDOWN, CRASHER_DASH_COOLDOWN, THRASHER_TRIPLE_COOLDOWN, THRASHER_DODGE_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        for (int i = 0; i < 2; i++) {
            builder.push(i == 0 ? "crasher" : "thrasher");
            HEALTH[i] = builder.defineInRange("maxHealth", i == 0 ? 82.0 : 45.0, 1, 1000000);
            ARMOR[i] = builder.defineInRange("armor", i == 0 ? 10.0 : 8.0, 0, 1000);
            DAMAGE[i] = builder.defineInRange("attackDamage", i == 0 ? 8.0 : 3.0, 0, 2048);
            SPEED[i] = builder.defineInRange("movementSpeed", i == 0 ? 0.0 : 0.2, 0, 4);
            RESISTANCE[i] = builder.defineInRange("knockbackResistance", i == 0 ? 1.0 : 0.0, 0, 1);
            builder.pop();
        }
        builder.push("skills");
        CRASHER_STRIKE = builder.defineInRange("crasherStrikeMultiplier", 9.0 / 8, 0, 100);
        CRASHER_AURA = builder.defineInRange("crasherAuraMultiplier", 1.5, 0, 100);
        THRASHER_STRIKE = builder.defineInRange("thrasherStrikeMultiplier", 4.0 / 3, 0, 100);
        STRIKE_KNOCKBACK = builder.defineInRange("crasherStrikeKnockback", 0.6, 0, 2);
        CRASHER_HEAVY_CHANCE = builder.defineInRange("crasherHeavyChance", 0.1, 0, 1);
        THRASHER_TRIPLE_CHANCE = builder.defineInRange("thrasherTripleChance", 0.7, 0, 1);
        CRASHER_STRIKE_COOLDOWN = builder.defineInRange("crasherStrikeCooldown", 35, 8, 72000);
        CRASHER_DASH_COOLDOWN = builder.defineInRange("crasherDashCooldown", 50, 27, 72000);
        THRASHER_TRIPLE_COOLDOWN = builder.defineInRange("thrasherTripleCooldown", 30, 12, 72000);
        THRASHER_DODGE_COOLDOWN = builder.defineInRange("thrasherDodgeCooldown", 45, 32, 72000);
        builder.pop();
        SPEC = builder.build();
    }
    private BeetleConfig() { }
}
