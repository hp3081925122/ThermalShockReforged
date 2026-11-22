package com.hp.thermalshockreforged.entity.gravers;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GraverConfig {
    public static final double[] DEFAULT_HEALTH = {11, 100, 12, 36, 24};
    public static final double[] DEFAULT_ARMOR = {1, 1, 0, 4, 4};
    public static final double[] DEFAULT_DAMAGE = {3, 3, 3, 5, 7};
    public static final double[] DEFAULT_SPEED = {.2, .15, .2, .16, .3};
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[5], ARMOR = new ModConfigSpec.DoubleValue[5], DAMAGE = new ModConfigSpec.DoubleValue[5], SPEED = new ModConfigSpec.DoubleValue[5];
    public static final ModConfigSpec.DoubleValue RARE_CHANCE, GUARD_CHANCE, STRIKE_MULTIPLIER, STRIKE_KNOCKBACK;
    public static final ModConfigSpec.IntValue RARE_EXPERIENCE, COOLDOWN;
    public static final ModConfigSpec SPEC;
    static {
        var builder = new ModConfigSpec.Builder();
        String[] names = {"graver", "neonLimeGraver", "assimilator", "assimilatedGraver", "razoraptor"};
        String[] zhNames = {"掘墓虫", "荧光青柠掘墓虫", "同化体", "被同化掘墓虫", "剃刀迅猛兽"};
        for (int i = 0; i < names.length; i++) {
            builder.push(names[i]);
            HEALTH[i] = builder.comment(zhNames[i] + "最大生命值 / " + names[i] + " maximum health").defineInRange("maxHealth", DEFAULT_HEALTH[i], 1, 1000000);
            ARMOR[i] = builder.comment(zhNames[i] + "护甲值 / " + names[i] + " armor").defineInRange("armor", DEFAULT_ARMOR[i], 0, 1000);
            DAMAGE[i] = builder.comment(zhNames[i] + "基础攻击力 / " + names[i] + " base attack damage").defineInRange("attackDamage", DEFAULT_DAMAGE[i], 0, 2048);
            SPEED[i] = builder.comment(zhNames[i] + "移动速度 / " + names[i] + " movement speed").defineInRange("movementSpeed", DEFAULT_SPEED[i], 0, 4);
            builder.pop();
        }
        RARE_CHANCE = builder.comment("荧光青柠掘墓虫稀有变种生成概率 / Neon lime Graver rare variant spawn chance").defineInRange("neonLimeSpawnChance", .00001, 0, 1);
        RARE_EXPERIENCE = builder.comment("荧光青柠掘墓虫经验掉落值 / Neon lime Graver experience reward").defineInRange("neonLimeExperience", 5345, 0, 1000000);
        builder.push("razoraptorSkills");
        GUARD_CHANCE = builder.comment("剃刀迅猛兽受击招架概率 / Razoraptor guard trigger chance").defineInRange("guardChance", .3, 0, 1);
        STRIKE_MULTIPLIER = builder.comment("剃刀迅猛兽突袭伤害倍率 / Razoraptor strike damage multiplier").defineInRange("strikeMultiplier", 5.0 / 7, 0, 100);
        STRIKE_KNOCKBACK = builder.comment("剃刀迅猛兽突袭击退强度 / Razoraptor strike knockback").defineInRange("strikeKnockback", .6, 0, 2);
        COOLDOWN = builder.comment("剃刀迅猛兽技能冷却（tick） / Razoraptor skill cooldown in ticks").defineInRange("cooldown", 30, 11, 72000);
        builder.pop();
        SPEC = builder.build();
    }
    private GraverConfig() { }
}
