package com.hp.thermalshock.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue FORECAST_HEALTH;
    public static final ModConfigSpec.DoubleValue FORECAST_DAMAGE;
    public static final ModConfigSpec.DoubleValue FORECAST_ZAP_MULTIPLIER;
    public static final ModConfigSpec.IntValue FORECAST_COOLDOWN;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("forecast");
        FORECAST_HEALTH = builder.comment("Forecast 最大生命值 / Forecast maximum health").defineInRange("maxHealth", 36.0, 1.0, 1000000.0);
        FORECAST_DAMAGE = builder.comment("基础攻击力，电击伤害以此乘技能倍率 / Base attack damage used by zap skills").defineInRange("attackDamage", 10.0, 0.0, 1000000.0);
        FORECAST_ZAP_MULTIPLIER = builder.comment("电击技能倍率 / Zap skill damage multiplier").defineInRange("zapMultiplier", 1.0, 0.0, 1000.0);
        FORECAST_COOLDOWN = builder.comment("电击冷却，单位 tick / Zap cooldown in ticks").defineInRange("zapCooldown", 90, 1, 72000);
        builder.pop();
        SPEC = builder.build();
    }

    private CombatConfig() {
    }
}
