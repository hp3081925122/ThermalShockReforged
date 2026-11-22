package com.hp.thermalshockreforged.entity.cavebloomer;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CavebloomerConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE, KNOCKBACK, AURA_MULTIPLIER, AURA_HEALTH_FRACTION;
    public static final ModConfigSpec.DoubleValue RAPID_DIRECT, RING_DIRECT, MORTAR_IMPACT, POISON_IMPACT, SLAM_BLOCKED, SLAM_DIRECT;
    public static final ModConfigSpec.IntValue SUMMON_CAP, SUMMON_LIFETIME;
    static {
        var builder = new ModConfigSpec.Builder();
        HEALTH = builder.comment("狂怒洞穴绽花最大生命值 / Angry Cavebloomer maximum health").defineInRange("maxHealth", 325.0, 1, 1000000);
        ARMOR = builder.comment("狂怒洞穴绽花护甲值 / Angry Cavebloomer armor").defineInRange("armor", 10.0, 0, 1000);
        DAMAGE = builder.comment("狂怒洞穴绽花基础攻击力 / Angry Cavebloomer base attack damage").defineInRange("attackDamage", 7.0, 0, 2048);
        SPEED = builder.comment("狂怒洞穴绽花移动速度 / Angry Cavebloomer movement speed").defineInRange("movementSpeed", 0.2, 0, 4);
        RESISTANCE = builder.comment("狂怒洞穴绽花击退抗性 / Angry Cavebloomer knockback resistance").defineInRange("knockbackResistance", 0.2, 0, 1);
        KNOCKBACK = builder.comment("狂怒洞穴绽花基础击退强度 / Angry Cavebloomer attack knockback").defineInRange("attackKnockback", 0.6, 0, 2);
        AURA_MULTIPLIER = builder.comment("低血量光环伤害倍率 / Low-health aura damage multiplier").defineInRange("auraDamageMultiplier", 2.0 / 7.0, 0, 100);
        AURA_HEALTH_FRACTION = builder.comment("开启光环的生命值百分比阈值 / Health fraction threshold to activate aura").defineInRange("auraHealthFraction", 0.5, 0, 1);
        RAPID_DIRECT = builder.comment("连射弹直击伤害倍率 / Rapid shot direct damage multiplier").defineInRange("rapidShotDamageMultiplier", 4.0 / 7.0, 0, 100);
        RING_DIRECT = builder.comment("环形弹幕直击伤害倍率 / Ring shot direct damage multiplier").defineInRange("ringShotDamageMultiplier", 10.0 / 7.0, 0, 100);
        MORTAR_IMPACT = builder.comment("迫击炸弹落点爆炸伤害倍率 / Mortar bomb impact damage multiplier").defineInRange("mortarImpactDamageMultiplier", 10.0 / 7.0, 0, 100);
        POISON_IMPACT = builder.comment("剧毒炸弹落点伤害倍率 / Poison bomb impact damage multiplier").defineInRange("poisonImpactDamageMultiplier", 6.0 / 7.0, 0, 100);
        SLAM_BLOCKED = builder.comment("被格挡时重砸伤害倍率 / Blocked slam damage multiplier").defineInRange("blockedSlamDamageMultiplier", 8.0 / 7.0, 0, 100);
        SLAM_DIRECT = builder.comment("未格挡时重砸伤害倍率 / Unblocked slam damage multiplier").defineInRange("slamDamageMultiplier", 30.0 / 7.0, 0, 100);
        SUMMON_CAP = builder.comment("同时存活召唤蒲公英上限 / Maximum active summoned dandy bloomlings").defineInRange("ownedDandyCap", 40, 5, 1000);
        SUMMON_LIFETIME = builder.comment("召唤蒲公英存活时间（tick） / Summoned dandy bloomling lifetime in ticks").defineInRange("ownedDandyLifetimeTicks", 2400, 100, 72000);
        SPEC = builder.build();
    }
    private CavebloomerConfig() { }
}
