package com.hp.thermalshockreforged.entity.titans;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TitanCombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] ARMOR = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] DAMAGE = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] SPEED = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] FOLLOW_RANGE = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] RESISTANCE = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] KNOCKBACK = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.DoubleValue[] SWING_MULTIPLIER = new ModConfigSpec.DoubleValue[3];
    public static final ModConfigSpec.IntValue[] COOLDOWN = new ModConfigSpec.IntValue[3];

    static {
        var builder = new ModConfigSpec.Builder();
        for (var kind : SkeletalTitanEntity.Kind.values()) {
            int index = kind.ordinal();
            builder.push(kind.id());
            HEALTH[index] = builder.comment("最大生命值 / Maximum health").defineInRange("maxHealth", 32.0, 1.0, 1000000.0);
            ARMOR[index] = builder.comment("护甲 / Armor").defineInRange("armor", 6.0, 0.0, 1000.0);
            DAMAGE[index] = builder.comment("基础近战伤害 / Base melee damage").defineInRange("attackDamage", 6.0, 0.0, 1000000.0);
            SPEED[index] = builder.comment("移动速度 / Movement speed").defineInRange("movementSpeed", kind.speed(), 0.0, 2.0);
            FOLLOW_RANGE[index] = builder.comment("跟随距离 / Follow range").defineInRange("followRange", 32.0, 1.0, 256.0);
            RESISTANCE[index] = builder.comment("击退抗性 / Knockback resistance").defineInRange("knockbackResistance", .2, 0.0, 1.0);
            KNOCKBACK[index] = builder.comment("原生近战击退，受目标抗性影响 / Native melee knockback respects target resistance").defineInRange("meleeKnockback", .5, 0.0, 2.0);
            SWING_MULTIPLIER[index] = builder.comment("蓄力挥击倍率，默认基础6乘7/6等于原7点 / Charged swing damage multiplier").defineInRange("swingMultiplier", 7.0 / 6.0, 0.0, 1000.0);
            COOLDOWN[index] = builder.comment("挥击冷却tick，至少保完整20tick蓄力 / Swing cooldown in ticks").defineInRange("swingCooldown", 50, 20, 72000);
            builder.pop();
        }
        SPEC = builder.build();
    }

    private TitanCombatConfig() { }
}
