package com.hp.thermalshockreforged.entity.monolith;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MonolithConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[2], ARMOR = new ModConfigSpec.DoubleValue[2], DAMAGE = new ModConfigSpec.DoubleValue[2], SPEED = new ModConfigSpec.DoubleValue[2], RESISTANCE = new ModConfigSpec.DoubleValue[2], KNOCKBACK = new ModConfigSpec.DoubleValue[2];
    public static final ModConfigSpec.DoubleValue STRIKE_MULTIPLIER;
    public static final ModConfigSpec.IntValue ATTACK_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        for (int index = 0; index < 2; index++) {
            builder.push(index == 0 ? "active" : "dormant");
            HEALTH[index] = builder.defineInRange("maxHealth", 160.0, 1, 1000000);
            ARMOR[index] = builder.defineInRange("armor", 16.0, 0, 1000);
            DAMAGE[index] = builder.defineInRange("attackDamage", index == 0 ? 14.0 : 12.0, 0, 2048);
            SPEED[index] = builder.defineInRange("movementSpeed", index == 0 ? 0.3 : 0.0, 0, 4);
            RESISTANCE[index] = builder.defineInRange("knockbackResistance", 1.0, 0, 1);
            KNOCKBACK[index] = builder.defineInRange("attackKnockback", 0.6, 0, 2);
            builder.pop();
        }
        builder.push("skills");
        STRIKE_MULTIPLIER = builder.defineInRange("strikeMultiplier", 6.0 / 14, 0, 100);
        ATTACK_COOLDOWN = builder.defineInRange("attackCooldown", 80, 41, 72000);
        builder.pop();
        SPEC = builder.build();
    }
    private MonolithConfig() { }
}
