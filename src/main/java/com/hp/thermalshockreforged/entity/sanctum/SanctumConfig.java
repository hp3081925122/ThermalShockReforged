package com.hp.thermalshockreforged.entity.sanctum;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SanctumConfig {
    public static final ModConfigSpec SPEC;
    public static final Profile ERRANT, SANCTUM_ERRANT;
    public static final Kilolyte KILOLYTE;
    static {
        var builder = new ModConfigSpec.Builder();
        ERRANT = profile(builder, "errant", "游荡圣卫 / Errant", 62);
        SANCTUM_ERRANT = profile(builder, "sanctum_errant", "圣所游荡圣卫 / Sanctum Errant", 40);
        builder.push("kilolyte");
        KILOLYTE = new Kilolyte(
                builder.comment("千伏圣卫最大生命值 / Kilolyte maximum health").defineInRange("maxHealth", 100D, 1D, 1024D),
                builder.comment("千伏圣卫护甲值 / Kilolyte armor").defineInRange("armor", 10D, 0D, 30D),
                builder.comment("千伏圣卫基础攻击力 / Kilolyte base attack damage").defineInRange("attackDamage", 13D, 0D, 2048D),
                builder.comment("千伏圣卫移动速度 / Kilolyte movement speed").defineInRange("movementSpeed", .17D, 0D, 4D),
                builder.comment("千伏圣卫击退抗性 / Kilolyte knockback resistance").defineInRange("knockbackResistance", 1D, 0D, 1D),
                builder.comment("千伏圣卫近战技能倍率 / Kilolyte melee skill multiplier").defineInRange("meleeMultiplier", 1D, 0D, 100D),
                builder.comment("千伏圣卫近战穿甲附加系数 / Kilolyte melee armor factor").defineInRange("meleeArmorFactor", .6D, 0D, 100D),
                builder.comment("千伏圣卫电击技能倍率 / Kilolyte zap skill multiplier").defineInRange("zapMultiplier", 10D / 13D, 0D, 100D),
                builder.comment("千伏圣卫环形电击触发概率 / Kilolyte ring zap trigger chance").defineInRange("ringChance", .3D, 0D, 1D));
        builder.pop();
        SPEC = builder.build();
    }
    private SanctumConfig() { }
    private static Profile profile(ModConfigSpec.Builder builder, String name, String label, double health) {
        builder.push(name);
        var values = new Profile(
                builder.comment(label + " 最大生命值 / maximum health").defineInRange("maxHealth", health, 1D, 1024D),
                builder.comment(label + " 护甲值 / armor").defineInRange("armor", 6D, 0D, 30D),
                builder.comment(label + " 基础攻击力 / base attack damage").defineInRange("attackDamage", 6D, 0D, 2048D),
                builder.comment(label + " 移动速度 / movement speed").defineInRange("movementSpeed", .3D, 0D, 4D),
                builder.comment(label + " 击退抗性 / knockback resistance").defineInRange("knockbackResistance", .3D, 0D, 1D),
                builder.comment(label + " 攻击击退强度 / attack knockback").defineInRange("attackKnockback", 1D, 0D, 2D),
                builder.comment(label + " 突刺伤害倍率 / strike damage multiplier").defineInRange("strikeMultiplier", 10D / 6D, 0D, 100D),
                builder.comment(label + " 突刺穿甲附加系数 / strike armor factor").defineInRange("strikeArmorFactor", .4D, 0D, 100D),
                builder.comment(label + " 突刺击退强度 / strike knockback").defineInRange("strikeKnockback", 1D, 0D, 1D));
        builder.pop();
        return values;
    }
    public record Profile(ModConfigSpec.DoubleValue health, ModConfigSpec.DoubleValue armor, ModConfigSpec.DoubleValue damage,
                          ModConfigSpec.DoubleValue speed, ModConfigSpec.DoubleValue resistance, ModConfigSpec.DoubleValue knockback,
                          ModConfigSpec.DoubleValue strikeMultiplier, ModConfigSpec.DoubleValue armorFactor, ModConfigSpec.DoubleValue strikeKnockback) { }
    public record Kilolyte(ModConfigSpec.DoubleValue health, ModConfigSpec.DoubleValue armor, ModConfigSpec.DoubleValue damage,
                           ModConfigSpec.DoubleValue speed, ModConfigSpec.DoubleValue resistance, ModConfigSpec.DoubleValue meleeMultiplier,
                           ModConfigSpec.DoubleValue armorFactor, ModConfigSpec.DoubleValue zapMultiplier, ModConfigSpec.DoubleValue ringChance) { }

}
