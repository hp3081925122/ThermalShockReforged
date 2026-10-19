package com.hp.thermalshockreforged.entity.sopel;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SopelConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, PUSH;
    public static final ModConfigSpec.IntValue COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("highDirectorSopel");
        HEALTH = builder.comment("最大生命值 / Maximum health").defineInRange("maxHealth", 450D, 1D, 1024D);
        ARMOR = builder.comment("护甲 / Armor").defineInRange("armor", 12D, 0D, 30D);
        DAMAGE = builder.comment("基础攻击属性；原推移技能不造成生命伤害 / Base attack attribute; the original displacement skill deals no health damage").defineInRange("attackDamage", 3D, 0D, 2048D);
        SPEED = builder.comment("移动速度 / Movement speed").defineInRange("movementSpeed", 0D, 0D, 4D);
        RESISTANCE = builder.comment("击退抗性 / Knockback resistance").defineInRange("knockbackResistance", 1D, 0D, 1D);
        PUSH = builder.comment("推移强度，受目标击退抗性影响 / Displacement strength, reduced by target knockback resistance").defineInRange("pushStrength", .6D, 0D, 2D);
        COOLDOWN = builder.comment("推移冷却刻数 / Displacement cooldown in ticks").defineInRange("cooldownTicks", 45, 1, 72000);
        builder.pop(); SPEC = builder.build();
    }
    private SopelConfig() { }
}
