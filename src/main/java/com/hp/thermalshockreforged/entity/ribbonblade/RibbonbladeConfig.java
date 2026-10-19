package com.hp.thermalshockreforged.entity.ribbonblade;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class RibbonbladeConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, MULTIPLIER, KNOCKBACK;
    public static final ModConfigSpec.IntValue STRIKE_COOLDOWN, TAUNT_COOLDOWN, BLOCK_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        HEALTH = builder.comment("最大生命值 / Maximum health").defineInRange("maxHealth", 40.0, 1, 1000000);
        ARMOR = builder.comment("护甲 / Armor").defineInRange("armor", 10.0, 0, 1000);
        DAMAGE = builder.comment("技能基础攻击力 / Base attack damage for skills").defineInRange("attackDamage", 7.0, 0, 2048);
        SPEED = builder.comment("移动速度 / Movement speed").defineInRange("movementSpeed", 0.0, 0, 4);
        MULTIPLIER = builder.comment("挥击伤害倍率，默认7×8/7等于8 / Strike damage multiplier").defineInRange("strikeMultiplier", 8.0 / 7, 0, 100);
        KNOCKBACK = builder.comment("技能击退，受目标抗性影响 / Skill knockback, reduced by target resistance").defineInRange("skillKnockback", .6, 0, 2);
        STRIKE_COOLDOWN = builder.comment("攻击冷却tick，至少保完整17tick蓄力 / Strike cooldown in ticks").defineInRange("strikeCooldown", 20, 17, 72000);
        TAUNT_COOLDOWN = builder.comment("嘲讽冷却tick / Taunt cooldown in ticks").defineInRange("tauntCooldown", 60, 1, 72000);
        BLOCK_COOLDOWN = builder.comment("格挡冷却tick / Block cooldown in ticks").defineInRange("blockCooldown", 40, 1, 72000);
        SPEC = builder.build();
    }
    private RibbonbladeConfig() { }
}
