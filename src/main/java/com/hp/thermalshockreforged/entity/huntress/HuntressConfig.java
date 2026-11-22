package com.hp.thermalshockreforged.entity.huntress;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class HuntressConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, TRIPLE_MULTIPLIER, SLAM_MULTIPLIER, KNOCKBACK, TRIPLE_CHANCE;
    public static final ModConfigSpec.IntValue ROLLBACK_COOLDOWN, TRIPLE_COOLDOWN, SLAM_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("boss");
        HEALTH = builder.comment("女猎手首领最大生命值 / Huntress boss maximum health").defineInRange("maxHealth", 300.0, 1, 1000000);
        ARMOR = builder.comment("女猎手首领护甲值 / Huntress boss armor").defineInRange("armor", 8.0, 0, 1000);
        DAMAGE = builder.comment("女猎手首领基础攻击力 / Huntress boss base attack damage").defineInRange("attackDamage", 8.0, 0, 2048);
        SPEED = builder.comment("女猎手首领移动速度 / Huntress boss movement speed").defineInRange("movementSpeed", .35, 0, 4);
        RESISTANCE = builder.comment("女猎手首领击退抗性 / Huntress boss knockback resistance").defineInRange("knockbackResistance", 1.0, 0, 1);
        TRIPLE_MULTIPLIER = builder.comment("三连斩单段伤害倍率 / Triple hit damage multiplier").defineInRange("tripleHitMultiplier", 1.0, 0, 100);
        SLAM_MULTIPLIER = builder.comment("重劈技能伤害倍率 / Slam skill damage multiplier").defineInRange("slamMultiplier", 1.25, 0, 100);
        KNOCKBACK = builder.comment("技能击退强度 / Skill knockback strength").defineInRange("skillKnockback", .6, 0, 2);
        TRIPLE_CHANCE = builder.comment("优先触发三连斩概率 / Chance to choose triple hit over slam").defineInRange("tripleHitChance", .5, 0, 1);
        ROLLBACK_COOLDOWN = builder.comment("后撤步冷却（tick） / Rollback dodge cooldown in ticks").defineInRange("rollbackCooldown", 40, 1, 72000);
        TRIPLE_COOLDOWN = builder.comment("三连斩冷却（tick） / Triple hit cooldown in ticks").defineInRange("tripleHitCooldown", 40, 21, 72000);
        SLAM_COOLDOWN = builder.comment("重劈冷却（tick） / Slam cooldown in ticks").defineInRange("slamCooldown", 70, 45, 72000);
        builder.pop(); SPEC = builder.build();
    }
    private HuntressConfig() { }
}
