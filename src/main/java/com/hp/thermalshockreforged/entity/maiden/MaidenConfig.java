package com.hp.thermalshockreforged.entity.maiden;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MaidenConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, SINGLE_MULTIPLIER, TRIPLE_MULTIPLIER, LAST_MULTIPLIER, SINGLE_CHANCE;
    public static final ModConfigSpec.IntValue COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder(); builder.push("maiden");
        HEALTH = builder.comment("最大生命值 / Maximum health").defineInRange("maxHealth", 50.0, 1, 1000000); ARMOR = builder.comment("护甲 / Armor").defineInRange("armor", 6.0, 0, 1000); DAMAGE = builder.comment("技能基础攻击力；箭伤害系数还受原生速度影响 / Skill attack base; arrow damage also uses native velocity").defineInRange("attackDamage", 10.0, 0, 2048); SPEED = builder.comment("移动速度 / Movement speed").defineInRange("movementSpeed", .3, 0, 4); RESISTANCE = builder.comment("击退抗性 / Knockback resistance").defineInRange("knockbackResistance", 1.0, 0, 1);
        SINGLE_MULTIPLIER = builder.comment("单圈箭伤害系数倍率 / Single ring arrow base damage multiplier").defineInRange("singleRingArrowMultiplier", 1.0, 0, 100); TRIPLE_MULTIPLIER = builder.comment("三圈前七发倍率 / First seven arrows of each triple ring").defineInRange("tripleRingArrowMultiplier", .7, 0, 100); LAST_MULTIPLIER = builder.comment("三圈每圈末发倍率 / Last arrow of each triple ring").defineInRange("tripleRingLastArrowMultiplier", .5, 0, 100); SINGLE_CHANCE = builder.comment("单圈概率 / Single ring probability").defineInRange("singleRingChance", .5, 0, 1); COOLDOWN = builder.comment("攻击间隔，单位刻 / Attack cooldown in ticks").defineInRange("attackCooldown", 90, 57, 72000);
        builder.pop(); SPEC = builder.build();
    }
    private MaidenConfig() { }
}
