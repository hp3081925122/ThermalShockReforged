package com.hp.thermalshockreforged.entity.mangler;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ManglerCombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HERALD_HEALTH, HERALD_ARMOR, HERALD_KNOCKBACK, HERALD_RESISTANCE, SUMMON_CHANCE;
    public static final ModConfigSpec.IntValue SUMMON_COOLDOWN, SUMMON_CAP, SUMMON_LIFETIME, BOOST_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("herald");
        HERALD_HEALTH = builder.comment("先驱首领最大生命值 / Herald maximum health").defineInRange("maxHealth", 275.0, 1.0, 1000000.0);
        HERALD_ARMOR = builder.comment("先驱首领护甲值 / Herald armor").defineInRange("armor", 16.0, 0.0, 1000.0);
        HERALD_KNOCKBACK = builder.comment("先驱首领攻击击退强度 / Herald attack knockback").defineInRange("attackKnockback", 0.6, 0.0, 2.0);
        HERALD_RESISTANCE = builder.comment("先驱首领击退抗性 / Herald knockback resistance").defineInRange("knockbackResistance", 1.0, 0.0, 1.0);
        SUMMON_CHANCE = builder.comment("先驱召唤撕裂者触发概率 / Herald summon chance").defineInRange("summonChance", 0.3, 0.0, 1.0);
        SUMMON_COOLDOWN = builder.comment("先驱召唤尝试冷却（tick） / Herald summon attempt cooldown in ticks").defineInRange("summonAttemptCooldown", 40, 1, 72000);
        SUMMON_CAP = builder.comment("先驱同时存活召唤物上限 / Maximum living summons per Herald").defineInRange("maxLivingSummons", 3, 1, 3);
        SUMMON_LIFETIME = builder.comment("召唤物存活租期（tick） / Summon lifetime in ticks").defineInRange("summonLifetime", 320, 1, 72000);
        builder.pop();
        BOOST_COOLDOWN = builder.comment("撕裂者冲刺增益冷却（tick） / Mangler boost cooldown in ticks").defineInRange("manglerBoostCooldown", 40, 1, 72000);
        SPEC = builder.build();
    }
    private ManglerCombatConfig() { }
}
