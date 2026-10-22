package com.hp.thermalshockreforged.entity.squall;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SquallConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HEALTH, ARMOR, DAMAGE, SPEED, RESISTANCE;
    public static final ModConfigSpec.DoubleValue DRIFT_SPEED, PHASE_RATIO, PHASE_COOLDOWN_SCALE, SKILL_KNOCKBACK;
    public static final ModConfigSpec.DoubleValue SURGE_MULTIPLIER, SURGE_RADIUS, GALE_MULTIPLIER, GALE_RADIUS, GALE_PULL_RADIUS, GALE_PULL_STRENGTH, BOLT_MULTIPLIER, BOLT_RADIUS;
    public static final ModConfigSpec.IntValue SURGE_COOLDOWN, GALE_COOLDOWN, BOLT_COOLDOWN, BOLT_COUNT, LIGHT_LEVEL;
    public static final ModConfigSpec.DoubleValue LIGHT_DAMAGE_MULTIPLIER, AURA_RADIUS;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("attributes");
        HEALTH = builder.comment("最大生命值 / Maximum health").defineInRange("maxHealth", 554.0, 1, 1000000);
        ARMOR = builder.comment("护甲 / Armor").defineInRange("armor", 0.0, 0, 1000);
        DAMAGE = builder.comment("基础攻击力，所有技能伤害 = 基础攻击力 × 技能倍率 / Base attack damage used by every skill").defineInRange("attackDamage", 10.0, 0, 2048);
        SPEED = builder.comment("原生寻路速度 / Navigation movement speed").defineInRange("movementSpeed", 0.0, 0, 4);
        RESISTANCE = builder.comment("自身击退抗性 / Knockback resistance").defineInRange("knockbackResistance", 1.0, 0, 1);
        DRIFT_SPEED = builder.comment("向目标飘移的速度（格/刻） / Drift speed toward the target in blocks per tick").defineInRange("driftSpeed", 0.12, 0, 1);
        builder.pop();
        builder.push("phase");
        PHASE_RATIO = builder.comment("生命百分比低于该值进入二阶段 / Health ratio that starts phase two").defineInRange("phaseRatio", 0.5, 0, 1);
        PHASE_COOLDOWN_SCALE = builder.comment("二阶段技能冷却倍率 / Skill cooldown scale in phase two").defineInRange("phaseCooldownScale", 0.7, 0.1, 1);
        builder.pop();
        builder.push("skills");
        SKILL_KNOCKBACK = builder.comment("技能击退强度，受目标击退抗性影响 / Skill knockback, reduced by target knockback resistance").defineInRange("skillKnockback", 0.4, 0, 2);
        SURGE_MULTIPLIER = builder.comment("暗涌：伤害倍率 / Umbral Surge damage multiplier").defineInRange("surgeMultiplier", 1.2, 0, 100);
        SURGE_RADIUS = builder.comment("暗涌：爆发半径 / Umbral Surge radius").defineInRange("surgeRadius", 2.0, 0.5, 8);
        SURGE_COOLDOWN = builder.comment("暗涌：冷却（刻） / Umbral Surge cooldown in ticks").defineInRange("surgeCooldown", 70, 1, 2400);
        GALE_MULTIPLIER = builder.comment("风眼：爆发伤害倍率 / Gale Eye burst damage multiplier").defineInRange("galeMultiplier", 0.9, 0, 100);
        GALE_RADIUS = builder.comment("风眼：爆发半径 / Gale Eye burst radius").defineInRange("galeRadius", 4.5, 0.5, 16);
        GALE_PULL_RADIUS = builder.comment("风眼：牵引半径，目标在此范围内才会施放 / Gale Eye pull radius, also its cast range").defineInRange("galePullRadius", 9.0, 1, 32);
        GALE_PULL_STRENGTH = builder.comment("风眼：每刻牵引强度，受目标击退抗性影响 / Gale Eye pull per tick, reduced by knockback resistance").defineInRange("galePullStrength", 0.06, 0, 1);
        GALE_COOLDOWN = builder.comment("风眼：冷却（刻） / Gale Eye cooldown in ticks").defineInRange("galeCooldown", 180, 1, 2400);
        BOLT_MULTIPLIER = builder.comment("夜雷（二阶段）：每道雷伤害倍率 / Night Bolt damage multiplier per strike").defineInRange("boltMultiplier", 1.5, 0, 100);
        BOLT_RADIUS = builder.comment("夜雷：每道雷的半径 / Night Bolt radius per strike").defineInRange("boltRadius", 1.5, 0.5, 8);
        BOLT_COUNT = builder.comment("夜雷：落雷数量 / Night Bolt strike count").defineInRange("boltCount", 3, 1, 8);
        BOLT_COOLDOWN = builder.comment("夜雷：冷却（刻） / Night Bolt cooldown in ticks").defineInRange("boltCooldown", 140, 1, 2400);
        builder.pop();
        builder.push("light");
        LIGHT_LEVEL = builder.comment("自身所在方块光照达到该值时被削弱：无法施法并承受额外伤害 / Block light that weakens it: no casting and extra damage").defineInRange("lightLevel", 11, 0, 15);
        LIGHT_DAMAGE_MULTIPLIER = builder.comment("被光照削弱时承受伤害倍率 / Damage taken multiplier while weakened by light").defineInRange("lightDamageMultiplier", 1.5, 1, 10);
        AURA_RADIUS = builder.comment("黑暗光环半径（仅客户端视觉） / Darkness aura radius, client visual only").defineInRange("auraRadius", 28.0, 4, 64);
        builder.pop();
        SPEC = builder.build();
    }
    private SquallConfig() { }
}
