package com.hp.thermalshockreforged.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue FORECAST_HEALTH;
    public static final ModConfigSpec.DoubleValue FORECAST_DAMAGE;
    public static final ModConfigSpec.DoubleValue FORECAST_ZAP_MULTIPLIER;
    public static final ModConfigSpec.IntValue FORECAST_COOLDOWN;
    public static final ModConfigSpec.DoubleValue EPSILON_HEALTH;
    public static final ModConfigSpec.DoubleValue EPSILON_ARMOR;
    public static final ModConfigSpec.DoubleValue EPSILON_DAMAGE;
    public static final ModConfigSpec.DoubleValue EPSILON_SPEED;
    public static final ModConfigSpec.DoubleValue EPSILON_FLIGHT_SPEED;
    public static final ModConfigSpec.DoubleValue EPSILON_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue EPSILON_RESISTANCE;
    public static final ModConfigSpec.DoubleValue EPSILON_AURA_MULTIPLIER;
    public static final ModConfigSpec.IntValue EPSILON_COOLDOWN;
    public static final ModConfigSpec.DoubleValue DELTA_HEALTH;
    public static final ModConfigSpec.DoubleValue DELTA_ARMOR;
    public static final ModConfigSpec.DoubleValue DELTA_DAMAGE;
    public static final ModConfigSpec.DoubleValue DELTA_SPEED;
    public static final ModConfigSpec.DoubleValue DELTA_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue DELTA_BULLET_MULTIPLIER;
    public static final ModConfigSpec.IntValue DELTA_COOLDOWN;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("forecast");
        FORECAST_HEALTH = builder.comment("Forecast 最大生命值 / Forecast maximum health").defineInRange("maxHealth", 36.0, 1.0, 1000000.0);
        FORECAST_DAMAGE = builder.comment("基础攻击力，电击伤害以此乘技能倍率 / Base attack damage used by zap skills").defineInRange("attackDamage", 10.0, 0.0, 1000000.0);
        FORECAST_ZAP_MULTIPLIER = builder.comment("电击技能倍率 / Zap skill damage multiplier").defineInRange("zapMultiplier", 1.0, 0.0, 1000.0);
        FORECAST_COOLDOWN = builder.comment("电击冷却，单位 tick / Zap cooldown in ticks").defineInRange("zapCooldown", 90, 1, 72000);
        builder.pop();
        builder.push("protoiEpsilon");
        EPSILON_HEALTH = builder.defineInRange("maxHealth", 20.0, 1.0, 1000000.0);
        EPSILON_ARMOR = builder.defineInRange("armor", 10.0, 0.0, 1000.0);
        EPSILON_DAMAGE = builder.defineInRange("attackDamage", 3.0, 0.0, 1000000.0);
        EPSILON_SPEED = builder.defineInRange("movementSpeed", .3, 0.0, 2.0);
        EPSILON_FLIGHT_SPEED = builder.defineInRange("flyingSpeed", .3, 0.0, 2.0);
        EPSILON_FOLLOW_RANGE = builder.defineInRange("followRange", 64.0, 1.0, 256.0);
        EPSILON_RESISTANCE = builder.defineInRange("knockbackResistance", 1.0, 0.0, 1.0);
        EPSILON_AURA_MULTIPLIER = builder.defineInRange("auraMultiplier", 2.0, 0.0, 1000.0);
        EPSILON_COOLDOWN = builder.defineInRange("attackCooldown", 100, 40, 72000);
        builder.pop();
        builder.push("protoiDelta");
        DELTA_HEALTH = builder.defineInRange("maxHealth", 50.0, 1.0, 1000000.0);
        DELTA_ARMOR = builder.defineInRange("armor", 8.0, 0.0, 1000.0);
        DELTA_DAMAGE = builder.defineInRange("attackDamage", 5.0, 0.0, 1000000.0);
        DELTA_SPEED = builder.defineInRange("movementSpeed", .25, 0.0, 2.0);
        DELTA_FOLLOW_RANGE = builder.defineInRange("followRange", 16.0, 1.0, 256.0);
        DELTA_BULLET_MULTIPLIER = builder.defineInRange("bulletMultiplier", .8, 0.0, 1000.0);
        DELTA_COOLDOWN = builder.defineInRange("attackCooldown", 100, 42, 72000);
        builder.pop();
        SPEC = builder.build();
    }

    private CombatConfig() {
    }
}
