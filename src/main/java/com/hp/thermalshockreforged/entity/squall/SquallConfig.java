package com.hp.thermalshockreforged.entity.squall;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SquallConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, KNOCKBACK;
    static {
        var builder = new ModConfigSpec.Builder();
        HEALTH = builder.comment("最大生命值 / Maximum health").defineInRange("maxHealth", 554.0, 1, 1000000);
        ARMOR = builder.comment("护甲 / Armor").defineInRange("armor", 0.0, 0, 1000);
        DAMAGE = builder.comment("普通近战伤害 / Melee attack damage").defineInRange("attackDamage", 10.0, 0, 2048);
        SPEED = builder.comment("原生寻路速度 / Navigation movement speed").defineInRange("movementSpeed", 0.0, 0, 4);
        RESISTANCE = builder.comment("击退抗性 / Knockback resistance").defineInRange("knockbackResistance", 1.0, 0, 1);
        KNOCKBACK = builder.comment("普通攻击击退，受目标抗性影响 / Attack knockback, reduced by target resistance").defineInRange("attackKnockback", 0.6, 0, 2);
        SPEC = builder.build();
    }
    private SquallConfig() { }
}
