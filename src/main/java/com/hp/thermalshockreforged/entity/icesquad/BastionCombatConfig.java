package com.hp.thermalshockreforged.entity.icesquad;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class BastionCombatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue POD_HEALTH, POD_ARMOR;
    public static final ModConfigSpec.DoubleValue[] HEALTH = new ModConfigSpec.DoubleValue[2], ARMOR = new ModConfigSpec.DoubleValue[2], DAMAGE = new ModConfigSpec.DoubleValue[2], SPEED = new ModConfigSpec.DoubleValue[2], RESISTANCE = new ModConfigSpec.DoubleValue[2];
    public static final ModConfigSpec.DoubleValue[] PHASE_RATIO = new ModConfigSpec.DoubleValue[2], PHASE_COOLDOWN_SCALE = new ModConfigSpec.DoubleValue[2], SKILL_KNOCKBACK = new ModConfigSpec.DoubleValue[2];
    public static final ModConfigSpec.DoubleValue SHARD_MULTIPLIER, GRENADE_MULTIPLIER, GRENADE_RADIUS, BOOST_VELOCITY;
    public static final ModConfigSpec.IntValue SALVO_COOLDOWN, GRENADE_COOLDOWN, BOOST_COOLDOWN, GRENADE_COUNT;
    public static final ModConfigSpec.DoubleValue JET_MULTIPLIER, JET_RANGE, JET_ANGLE, CHARGE_MULTIPLIER, CHARGE_SPEED, POOL_MULTIPLIER, POOL_RADIUS;
    public static final ModConfigSpec.IntValue JET_COOLDOWN, CHARGE_COOLDOWN, POOL_DURATION;
    public static final ModConfigSpec.DoubleValue LINE_MULTIPLIER, LINE_LENGTH, MINE_MULTIPLIER, BLIZZARD_MULTIPLIER;
    public static final ModConfigSpec.IntValue LINE_COOLDOWN, MINE_COUNT, MINE_DURATION, MINE_COOLDOWN, BLIZZARD_COUNT, BLIZZARD_COOLDOWN;
    public static final ModConfigSpec.DoubleValue SWEEP_MULTIPLIER, SWEEP_RANGE, NAPALM_RADIUS, VENT_MULTIPLIER, VENT_RADIUS, VENT_KNOCKBACK, ERUPTION_MULTIPLIER, ERUPTION_RADIUS;
    public static final ModConfigSpec.IntValue SWEEP_COOLDOWN, NAPALM_COUNT, NAPALM_DURATION, NAPALM_COOLDOWN, VENT_COOLDOWN, ERUPTION_COOLDOWN;
    public static final ModConfigSpec.DoubleValue THERMAL_MULTIPLIER;
    public static final ModConfigSpec.IntValue THERMAL_COOLDOWN;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.comment("寒冰枪手 Lód / Lód the gunner").push("gunner");
        common(builder, 0);
        SHARD_MULTIPLIER = builder.comment("冰棱连射：每发伤害倍率 / Frost Salvo damage multiplier per shard").defineInRange("shardMultiplier", 0.4, 0, 100);
        SALVO_COOLDOWN = builder.comment("冰棱连射：冷却（刻） / Frost Salvo cooldown in ticks").defineInRange("salvoCooldown", 110, 1, 72000);
        GRENADE_MULTIPLIER = builder.comment("冰霜榴弹：伤害倍率 / Frost Grenade damage multiplier").defineInRange("grenadeMultiplier", 1.0, 0, 100);
        GRENADE_RADIUS = builder.comment("冰霜榴弹：爆炸半径 / Frost Grenade radius").defineInRange("grenadeRadius", 2.5, 0.5, 8);
        GRENADE_COUNT = builder.comment("冰霜榴弹：二阶段每次投掷数量 / Frost Grenade count in phase two").defineInRange("grenadeCount", 3, 1, 8);
        GRENADE_COOLDOWN = builder.comment("冰霜榴弹：冷却（刻） / Frost Grenade cooldown in ticks").defineInRange("grenadeCooldown", 150, 1, 72000);
        BOOST_VELOCITY = builder.comment("后撤推进：水平速度（格/刻），会受碰撞阻挡 / Retreat Boost horizontal velocity, blocked by collision").defineInRange("boostVelocity", 1.1, 0, 2);
        BOOST_COOLDOWN = builder.comment("后撤推进：冷却（刻） / Retreat Boost cooldown in ticks").defineInRange("boostCooldown", 100, 1, 72000);
        LINE_MULTIPLIER = builder.comment("冰脊裂地：每根冰刺伤害倍率 / Frost Rift damage multiplier per spike").defineInRange("lineMultiplier", 0.8, 0, 100);
        LINE_LENGTH = builder.comment("冰脊裂地：长度 / Frost Rift length").defineInRange("lineLength", 14.0, 2, 32);
        LINE_COOLDOWN = builder.comment("冰脊裂地：冷却（刻） / Frost Rift cooldown in ticks").defineInRange("lineCooldown", 160, 1, 72000);
        MINE_MULTIPLIER = builder.comment("冰霜地雷：伤害倍率 / Frost Mine damage multiplier").defineInRange("mineMultiplier", 0.9, 0, 100);
        MINE_COUNT = builder.comment("冰霜地雷：数量，二阶段 +2 / Frost Mine count, +2 in phase two").defineInRange("mineCount", 4, 1, 12);
        MINE_DURATION = builder.comment("冰霜地雷：存在时间（刻） / Frost Mine lifetime in ticks").defineInRange("mineDuration", 300, 20, 6000);
        MINE_COOLDOWN = builder.comment("冰霜地雷：冷却（刻） / Frost Mine cooldown in ticks").defineInRange("mineCooldown", 320, 1, 72000);
        BLIZZARD_MULTIPLIER = builder.comment("暴雪弹幕（二阶段）：每颗伤害倍率 / Blizzard Barrage damage multiplier per shell").defineInRange("blizzardMultiplier", 0.6, 0, 100);
        BLIZZARD_COUNT = builder.comment("暴雪弹幕：炮弹数量 / Blizzard Barrage shell count").defineInRange("blizzardCount", 10, 1, 30);
        BLIZZARD_COOLDOWN = builder.comment("暴雪弹幕：冷却（刻） / Blizzard Barrage cooldown in ticks").defineInRange("blizzardCooldown", 400, 1, 72000);
        builder.pop();
        builder.comment("烈火喷火兵 Pożar / Pożar the burner").push("burner");
        common(builder, 1);
        JET_MULTIPLIER = builder.comment("烈焰喷射：每次灼烧伤害倍率（每 10 刻一次） / Flame Jet damage multiplier per hit, every 10 ticks").defineInRange("jetMultiplier", 0.5, 0, 100);
        JET_RANGE = builder.comment("烈焰喷射：射程 / Flame Jet range").defineInRange("jetRange", 7.0, 1, 24);
        JET_ANGLE = builder.comment("烈焰喷射：锥形半角（度） / Flame Jet half angle in degrees").defineInRange("jetAngle", 30.0, 5, 90);
        JET_COOLDOWN = builder.comment("烈焰喷射：冷却（刻） / Flame Jet cooldown in ticks").defineInRange("jetCooldown", 120, 1, 72000);
        CHARGE_MULTIPLIER = builder.comment("熔炉冲锋：伤害倍率 / Furnace Charge damage multiplier").defineInRange("chargeMultiplier", 1.0, 0, 100);
        CHARGE_SPEED = builder.comment("熔炉冲锋：速度（格/刻），会受碰撞阻挡 / Furnace Charge speed, blocked by collision").defineInRange("chargeSpeed", 0.75, 0.1, 2);
        CHARGE_COOLDOWN = builder.comment("熔炉冲锋：冷却（刻） / Furnace Charge cooldown in ticks").defineInRange("chargeCooldown", 130, 1, 72000);
        POOL_MULTIPLIER = builder.comment("熔渣火池（二阶段冲锋留下）：每次伤害倍率（每 10 刻一次） / Slag Pool damage multiplier per hit, every 10 ticks").defineInRange("poolMultiplier", 0.2, 0, 100);
        POOL_RADIUS = builder.comment("熔渣火池：半径 / Slag Pool radius").defineInRange("poolRadius", 1.2, 0.3, 4);
        POOL_DURATION = builder.comment("熔渣火池：持续（刻） / Slag Pool duration in ticks").defineInRange("poolDuration", 60, 1, 1200);
        SWEEP_MULTIPLIER = builder.comment("炎旋横扫：伤害倍率 / Inferno Sweep damage multiplier").defineInRange("sweepMultiplier", 0.6, 0, 100);
        SWEEP_RANGE = builder.comment("炎旋横扫：半径 / Inferno Sweep radius").defineInRange("sweepRange", 6.0, 1, 16);
        SWEEP_COOLDOWN = builder.comment("炎旋横扫：冷却（刻） / Inferno Sweep cooldown in ticks").defineInRange("sweepCooldown", 200, 1, 72000);
        NAPALM_COUNT = builder.comment("燃油炸弹：二阶段投掷数量 / Napalm Canister count in phase two").defineInRange("napalmCount", 3, 1, 8);
        NAPALM_RADIUS = builder.comment("燃油炸弹：火池半径，伤害使用熔渣火池倍率 / Napalm pool radius, uses Slag Pool multiplier").defineInRange("napalmRadius", 2.0, 0.5, 6);
        NAPALM_DURATION = builder.comment("燃油炸弹：火池持续（刻） / Napalm pool duration in ticks").defineInRange("napalmDuration", 100, 1, 1200);
        NAPALM_COOLDOWN = builder.comment("燃油炸弹：冷却（刻） / Napalm Canister cooldown in ticks").defineInRange("napalmCooldown", 180, 1, 72000);
        VENT_MULTIPLIER = builder.comment("过热排气：伤害倍率 / Overheat Vent damage multiplier").defineInRange("ventMultiplier", 1.2, 0, 100);
        VENT_RADIUS = builder.comment("过热排气：半径 / Overheat Vent radius").defineInRange("ventRadius", 5.0, 1, 12);
        VENT_KNOCKBACK = builder.comment("过热排气：击退强度，受目标击退抗性影响 / Overheat Vent knockback, reduced by knockback resistance").defineInRange("ventKnockback", 0.9, 0, 2);
        VENT_COOLDOWN = builder.comment("过热排气：冷却（刻） / Overheat Vent cooldown in ticks").defineInRange("ventCooldown", 160, 1, 72000);
        ERUPTION_MULTIPLIER = builder.comment("熔火喷发（二阶段）：每根火柱伤害倍率 / Magma Eruption damage multiplier per pillar").defineInRange("eruptionMultiplier", 1.0, 0, 100);
        ERUPTION_RADIUS = builder.comment("熔火喷发：火柱半径 / Magma Eruption pillar radius").defineInRange("eruptionRadius", 1.5, 0.5, 5);
        ERUPTION_COOLDOWN = builder.comment("熔火喷发：冷却（刻） / Magma Eruption cooldown in ticks").defineInRange("eruptionCooldown", 400, 1, 72000);
        builder.pop();
        builder.comment("双子协同 / Twin synergy").push("twin");
        THERMAL_MULTIPLIER = builder.comment("热冲击：冰技能命中燃烧目标、或火技能命中被冰冻标记的目标时追加的伤害倍率（取触发者攻击力） / Thermal Shock bonus damage multiplier").defineInRange("thermalMultiplier", 0.8, 0, 100);
        THERMAL_COOLDOWN = builder.comment("热冲击：同一目标触发间隔（刻） / Thermal Shock per-target cooldown in ticks").defineInRange("thermalCooldown", 40, 1, 72000);
        builder.pop();
        builder.push("dropPod");
        POD_HEALTH = builder.defineInRange("maxHealth", 300.0, 1, 1000000);
        POD_ARMOR = builder.defineInRange("armor", 90.0, 0, 1000);
        builder.pop();
        SPEC = builder.build();
    }
    private static void common(ModConfigSpec.Builder builder, int k) {
        HEALTH[k] = builder.comment("最大生命值 / Maximum health").defineInRange("maxHealth", 125.0, 1, 1000000);
        ARMOR[k] = builder.comment("护甲 / Armor").defineInRange("armor", 10.0, 0, 1000);
        DAMAGE[k] = builder.comment("基础攻击力，所有技能伤害 = 基础攻击力 × 技能倍率 / Base attack damage used by every skill").defineInRange("attackDamage", 10.0, 0, 2048);
        double speed = 0.3;
        if (k == 0) speed = 0.22;
        SPEED[k] = builder.comment("移动速度 / Movement speed").defineInRange("movementSpeed", speed, 0, 4);
        RESISTANCE[k] = builder.comment("自身击退抗性 / Knockback resistance").defineInRange("knockbackResistance", 1.0, 0, 1);
        PHASE_RATIO[k] = builder.comment("生命百分比低于该值进入二阶段；搭档阵亡时也会立即进入 / Health ratio that starts phase two; also starts when the partner dies").defineInRange("phaseRatio", 0.5, 0, 1);
        PHASE_COOLDOWN_SCALE[k] = builder.comment("二阶段技能冷却倍率 / Skill cooldown scale in phase two").defineInRange("phaseCooldownScale", 0.75, 0.1, 1);
        SKILL_KNOCKBACK[k] = builder.comment("技能击退强度，受目标击退抗性影响 / Skill knockback, reduced by target knockback resistance").defineInRange("skillKnockback", 0.4, 0, 2);
    }
    private BastionCombatConfig() { }
}
