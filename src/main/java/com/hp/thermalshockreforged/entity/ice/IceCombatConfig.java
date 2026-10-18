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
        double[] health = {32,18,10,50}, armor = {8,4,4,12}, damage = {5,2,3,6}, speed = {0.25,0.2,0.3,0.3}, resistance = {0,0,1,1}, knockback = {0.2,0,0,0.6};
        for (int i = 0; i < 4; i++) {
            builder.push(names[i]);
            HEALTH[i] = builder.defineInRange("maxHealth", health[i], 1, 1000000);
            ARMOR[i] = builder.defineInRange("armor", armor[i], 0, 1000);
            DAMAGE[i] = builder.defineInRange("attackDamage", damage[i], 0, 2048);
            SPEED[i] = builder.defineInRange("movementSpeed", speed[i], 0, 4);
            RESISTANCE[i] = builder.defineInRange("knockbackResistance", resistance[i], 0, 1);
            KNOCKBACK[i] = builder.defineInRange("attackKnockback", knockback[i], 0, 2);
            builder.pop();
        }
        SOLDIER_COOLDOWN = builder.defineInRange("soldierCooldown", 44, 30, 72000);
        DRONE_COOLDOWN = builder.defineInRange("droneCooldown", 100, 21, 72000);
        ZERK_COOLDOWN = builder.defineInRange("zerkCooldown", 32, 11, 72000);
        ZERK_ALT_CHANCE = builder.defineInRange("zerkAlternateChance", 0.2, 0, 1);
        ZERK_ALT_MULTIPLIER = builder.defineInRange("zerkAlternateMultiplier", 2.0 / 3, 0, 100);
        ZERK_LAUNCH = builder.defineInRange("zerkLaunch", 0.6, 0, 1);
        BULLET_KNOCKBACK = builder.defineInRange("cryomancerBulletKnockback", 0.6, 0, 2);
        SPEC = builder.build();
    }
    private IceCombatConfig() { }
}
