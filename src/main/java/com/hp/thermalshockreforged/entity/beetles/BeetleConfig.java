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
            String zh = i == 0 ? "重撞甲虫" : "猛击甲虫";
            String en = i == 0 ? "Beetle Crasher" : "Beetle Thrasher";
            builder.push(i == 0 ? "crasher" : "thrasher");
            HEALTH[i] = builder.comment(zh + "最大生命值 / " + en + " maximum health").defineInRange("maxHealth", i == 0 ? 82.0 : 45.0, 1, 1000000);
            ARMOR[i] = builder.comment(zh + "护甲值 / " + en + " armor").defineInRange("armor", i == 0 ? 10.0 : 8.0, 0, 1000);
            DAMAGE[i] = builder.comment(zh + "基础攻击力 / " + en + " base attack damage").defineInRange("attackDamage", i == 0 ? 8.0 : 3.0, 0, 2048);
            SPEED[i] = builder.comment(zh + "移动速度 / " + en + " movement speed").defineInRange("movementSpeed", i == 0 ? 0.0 : 0.2, 0, 4);
            RESISTANCE[i] = builder.comment(zh + "击退抗性 / " + en + " knockback resistance").defineInRange("knockbackResistance", i == 0 ? 1.0 : 0.0, 0, 1);
            builder.pop();
        }
        builder.push("skills");
        CRASHER_STRIKE = builder.comment("重撞甲虫近战重击伤害倍率 / Crasher strike damage multiplier").defineInRange("crasherStrikeMultiplier", 9.0 / 8, 0, 100);
        CRASHER_AURA = builder.comment("重撞甲虫冲撞范围伤害倍率 / Crasher dash aura damage multiplier").defineInRange("crasherAuraMultiplier", 1.5, 0, 100);
        THRASHER_STRIKE = builder.comment("猛击甲虫连击伤害倍率 / Thrasher strike damage multiplier").defineInRange("thrasherStrikeMultiplier", 4.0 / 3, 0, 100);
        STRIKE_KNOCKBACK = builder.comment("重撞甲虫重击击退强度 / Crasher strike knockback").defineInRange("crasherStrikeKnockback", 0.6, 0, 2);
        CRASHER_HEAVY_CHANCE = builder.comment("重撞甲虫触发冲撞技能概率 / Crasher heavy dash trigger chance").defineInRange("crasherHeavyChance", 0.1, 0, 1);
        THRASHER_TRIPLE_CHANCE = builder.comment("猛击甲虫触发三连击概率 / Thrasher triple strike trigger chance").defineInRange("thrasherTripleChance", 0.7, 0, 1);
        CRASHER_STRIKE_COOLDOWN = builder.comment("重撞甲虫重击冷却（tick） / Crasher strike cooldown in ticks").defineInRange("crasherStrikeCooldown", 35, 8, 72000);
        CRASHER_DASH_COOLDOWN = builder.comment("重撞甲虫冲撞冷却（tick） / Crasher dash cooldown in ticks").defineInRange("crasherDashCooldown", 50, 27, 72000);
        THRASHER_TRIPLE_COOLDOWN = builder.comment("猛击甲虫三连击冷却（tick） / Thrasher triple strike cooldown in ticks").defineInRange("thrasherTripleCooldown", 30, 12, 72000);
        THRASHER_DODGE_COOLDOWN = builder.comment("猛击甲虫闪避冷却（tick） / Thrasher dodge cooldown in ticks").defineInRange("thrasherDodgeCooldown", 45, 32, 72000);
        builder.pop();
        SPEC = builder.build();
    }
    private BeetleConfig() { }
}
