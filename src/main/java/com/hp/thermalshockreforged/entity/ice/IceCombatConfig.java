package com.hp.thermalshockreforged.entity.ice;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class IceCombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[4], ARMOR = new ModConfigSpec.DoubleValue[4], DAMAGE = new ModConfigSpec.DoubleValue[4], SPEED = new ModConfigSpec.DoubleValue[4], RESISTANCE = new ModConfigSpec.DoubleValue[4], KNOCKBACK = new ModConfigSpec.DoubleValue[4];
    public static final ModConfigSpec.IntValue SOLDIER_COOLDOWN, DRONE_COOLDOWN, ZERK_COOLDOWN;
    public static final ModConfigSpec.DoubleValue ZERK_ALT_CHANCE, ZERK_ALT_MULTIPLIER, ZERK_LAUNCH, BULLET_KNOCKBACK;
    static {
        var builder = new ModConfigSpec.Builder();
        String[] names = {"soldier", "cryomancer", "icidrone", "icidrozerk"};
        String[] zhNames = {"霜卫士兵", "霜卫冰法师", "冰蚀无人机", "冰蚀狂战士"};
        double[] health = {32,18,10,50}, armor = {8,4,4,12}, damage = {5,2,3,6}, speed = {0.25,0.3,0.3,0.3}, resistance = {0,0,1,1}, knockback = {0.2,0,0,0.6};
        speed[1] = 0.2;
        for (int i = 0; i < 4; i++) {
            builder.push(names[i]);
            HEALTH[i] = builder.comment(zhNames[i] + "最大生命值 / " + names[i] + " maximum health").defineInRange("maxHealth", health[i], 1, 1000000);
            ARMOR[i] = builder.comment(zhNames[i] + "护甲值 / " + names[i] + " armor").defineInRange("armor", armor[i], 0, 1000);
            DAMAGE[i] = builder.comment(zhNames[i] + "基础攻击力 / " + names[i] + " base attack damage").defineInRange("attackDamage", damage[i], 0, 2048);
            SPEED[i] = builder.comment(zhNames[i] + "移动速度 / " + names[i] + " movement speed").defineInRange("movementSpeed", speed[i], 0, 4);
            RESISTANCE[i] = builder.comment(zhNames[i] + "击退抗性 / " + names[i] + " knockback resistance").defineInRange("knockbackResistance", resistance[i], 0, 1);
            KNOCKBACK[i] = builder.comment(zhNames[i] + "攻击击退强度 / " + names[i] + " attack knockback").defineInRange("attackKnockback", knockback[i], 0, 2);
            builder.pop();
        }
        SOLDIER_COOLDOWN = builder.comment("霜卫士兵技能冷却（tick） / Soldier skill cooldown in ticks").defineInRange("soldierCooldown", 44, 30, 72000);
        DRONE_COOLDOWN = builder.comment("冰蚀无人机攻击冷却（tick） / Icidrone attack cooldown in ticks").defineInRange("droneCooldown", 100, 21, 72000);
        ZERK_COOLDOWN = builder.comment("冰蚀狂战士技能冷却（tick） / Icidrozerk skill cooldown in ticks").defineInRange("zerkCooldown", 32, 11, 72000);
        ZERK_ALT_CHANCE = builder.comment("冰蚀狂战士变招触发概率 / Icidrozerk alternate attack chance").defineInRange("zerkAlternateChance", 0.2, 0, 1);
        ZERK_ALT_MULTIPLIER = builder.comment("冰蚀狂战士变招伤害倍率 / Icidrozerk alternate attack damage multiplier").defineInRange("zerkAlternateMultiplier", 2.0 / 3, 0, 100);
        ZERK_LAUNCH = builder.comment("冰蚀狂战士变招挑飞力度 / Icidrozerk alternate attack vertical launch").defineInRange("zerkLaunch", 0.6, 0, 1);
        BULLET_KNOCKBACK = builder.comment("霜卫冰法师弹体击退强度 / Cryomancer projectile knockback").defineInRange("cryomancerBulletKnockback", 0.6, 0, 2);
        SPEC = builder.build();
    }
    private IceCombatConfig() { }
}
