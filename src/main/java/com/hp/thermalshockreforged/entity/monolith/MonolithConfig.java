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
            String zh = index == 0 ? "觉醒独石巨像" : "休眠独石巨像";
            String en = index == 0 ? "Active Monolith" : "Dormant Monolith";
            builder.push(index == 0 ? "active" : "dormant");
            HEALTH[index] = builder.comment(zh + "最大生命值 / " + en + " maximum health").defineInRange("maxHealth", 160.0, 1, 1000000);
            ARMOR[index] = builder.comment(zh + "护甲值 / " + en + " armor").defineInRange("armor", 16.0, 0, 1000);
            DAMAGE[index] = builder.comment(zh + "基础攻击力 / " + en + " base attack damage").defineInRange("attackDamage", index == 0 ? 14.0 : 12.0, 0, 2048);
            SPEED[index] = builder.comment(zh + "移动速度 / " + en + " movement speed").defineInRange("movementSpeed", index == 0 ? 0.3 : 0.0, 0, 4);
            RESISTANCE[index] = builder.comment(zh + "击退抗性 / " + en + " knockback resistance").defineInRange("knockbackResistance", 1.0, 0, 1);
            KNOCKBACK[index] = builder.comment(zh + "攻击击退强度 / " + en + " attack knockback").defineInRange("attackKnockback", 0.6, 0, 2);
            builder.pop();
        }
        builder.push("skills");
        STRIKE_MULTIPLIER = builder.comment("独石巨像震击技能伤害倍率 / Monolith strike skill damage multiplier").defineInRange("strikeMultiplier", 6.0 / 14, 0, 100);
        ATTACK_COOLDOWN = builder.comment("独石巨像技能攻击冷却（tick） / Monolith skill attack cooldown in ticks").defineInRange("attackCooldown", 80, 41, 72000);
        builder.pop();
        SPEC = builder.build();
    }
    private MonolithConfig() { }
}
