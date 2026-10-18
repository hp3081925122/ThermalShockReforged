package com.hp.thermalshockreforged.entity.icesquad;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class BastionCombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue POD_HEALTH, POD_ARMOR;
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[2], ARMOR = new ModConfigSpec.DoubleValue[2], DAMAGE = new ModConfigSpec.DoubleValue[2], SPEED = new ModConfigSpec.DoubleValue[2], RESISTANCE = new ModConfigSpec.DoubleValue[2], KNOCKBACK = new ModConfigSpec.DoubleValue[2], MULTIPLIER = new ModConfigSpec.DoubleValue[2], DASH_SPEED = new ModConfigSpec.DoubleValue[2];
    public static final ModConfigSpec.IntValue[] SALVO_COOLDOWN = new ModConfigSpec.IntValue[2], DASH_COOLDOWN = new ModConfigSpec.IntValue[2];
    static {
        var builder = new ModConfigSpec.Builder();
        for (int k = 0; k < 2; k++) {
            builder.push(k == 0 ? "gunner" : "burner");
            HEALTH[k] = builder.defineInRange("maxHealth", 125.0, 1, 1000000);
            ARMOR[k] = builder.defineInRange("armor", 10.0, 0, 1000);
            DAMAGE[k] = builder.defineInRange("attackDamage", 10.0, 0, 2048);
            SPEED[k] = builder.defineInRange("movementSpeed", k == 0 ? 0.2 : 0.3, 0, 4);
            RESISTANCE[k] = builder.defineInRange("knockbackResistance", 1.0, 0, 1);
            KNOCKBACK[k] = builder.defineInRange("attackKnockback", 0.6, 0, 2);
            MULTIPLIER[k] = builder.defineInRange("projectileDamageMultiplier", 0.2, 0, 100);
            DASH_SPEED[k] = builder.defineInRange("dashVelocity", 4.0, 0, 4);
            SALVO_COOLDOWN[k] = builder.defineInRange("salvoCooldown", 140, 56, 72000);
            DASH_COOLDOWN[k] = builder.defineInRange("dashCooldown", k == 0 ? 45 : 20, 1, 72000);
            builder.pop();
        }
        builder.push("dropPod");
        POD_HEALTH = builder.defineInRange("maxHealth", 300.0, 1, 1000000);
        POD_ARMOR = builder.defineInRange("armor", 90.0, 0, 1000);
        builder.pop();
        SPEC = builder.build();
    }
    private BastionCombatConfig() { }
}
