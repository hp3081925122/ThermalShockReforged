package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.gametest.TsrGameTests;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public final class CurrentBastionSkillServerTests {
    private static final String[] COOLDOWNS = {"salvoCooldown", "grenadeCooldown", "boostCooldown", "riftCooldown", "mineCooldown", "blizzardCooldown", "jetCooldown", "chargeCooldown", "sweepCooldown", "napalmCooldown", "ventCooldown", "eruptionCooldown"};

    public static void register() {
        TsrGameTests.add("history_bastion_current_grenade_fuse_detonates_with_configured_damage", CurrentBastionSkillServerTests::grenadeFuseDetonatesWithConfiguredDamage, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_rift_line_hits_and_applies_frost", CurrentBastionSkillServerTests::riftLineHitsAndAppliesFrost, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_mines_arm_trigger_and_detonate", CurrentBastionSkillServerTests::minesArmTriggerAndDetonate, 120, "supernova_server");
        TsrGameTests.add("history_bastion_current_blizzard_shells_detonate_after_fuse", CurrentBastionSkillServerTests::blizzardShellsDetonateAfterFuse, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_jet_scorches_aimed_target", CurrentBastionSkillServerTests::jetScorchesAimedTarget, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_charge_hits_target_during_run", CurrentBastionSkillServerTests::chargeHitsTargetDuringRun, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_sweep_damages_target_during_full_arc", CurrentBastionSkillServerTests::sweepDamagesTargetDuringFullArc, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_napalm_commits_and_burns_a_pool", CurrentBastionSkillServerTests::napalmCommitsAndBurnsAPool, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_enraged_eruption_schedules_player_strikes", CurrentBastionSkillServerTests::enragedEruptionSchedulesPlayerStrikes, 100, "supernova_server");
    }

    public static void grenadeFuseDetonatesWithConfiguredDamage(GameTestHelper h) {
        var f = fixture(h, false, false, 10, "grenadeCooldown");
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.GRENADE, "Normal AI selection must enter the frost grenade cast; " + state(f)));
        h.runAfterDelay(42, () -> {
            double expected = 40 * BastionCombatConfig.GRENADE_MULTIPLIER.get();
            assertHits(h, f.target(), expected, "The grenade must resolve configured frost damage");
            h.assertTrue(f.target().hasEffect(net.minecraft.world.effect.MobEffects.SLOWNESS), "The grenade must apply its slow effect");
            h.assertTrue(f.boss().cast() == BastionEntity.NONE, "The grenade cast must finish after committing its timed strike");
            cleanup(f);
            h.succeed();
        });
    }

    public static void riftLineHitsAndAppliesFrost(GameTestHelper h) {
        var f = fixture(h, false, false, 9, "riftCooldown");
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.RIFT, "Normal AI selection must enter the frost rift cast"));
        h.runAfterDelay(34, () -> {
            double expected = 40 * BastionCombatConfig.LINE_MULTIPLIER.get();
            assertHits(h, f.target(), expected, "Each rift spike hit must use configured frost damage");
            h.assertTrue(f.target().hasEffect(net.minecraft.world.effect.MobEffects.SLOWNESS), "The scheduled rift line must apply its slow effect");
            cleanup(f);
            h.succeed();
        });
    }

    public static void minesArmTriggerAndDetonate(GameTestHelper h) {
        var f = fixture(h, false, false, 10, "mineCooldown");
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.MINES, "Normal AI selection must enter the mine cast; " + state(f)));
        h.runAfterDelay(31, () -> {
            var mines = privateList(f.boss(), "mines");
            h.assertTrue(mines.size() == BastionCombatConfig.MINE_COUNT.get(), "The scheduled mine payloads must become armed mines after flight");
            int initialMineCount = mines.size();
            Vec3 at = (Vec3) privateField(mines.get(0), "at");
            f.target().setPos(at.x, at.y, at.z);
            h.runAfterDelay(8, () -> {
                double expected = 40 * BastionCombatConfig.MINE_MULTIPLIER.get();
                assertHits(h, f.target(), expected, "An approached mine must use configured damage");
                h.assertTrue(privateList(f.boss(), "mines").size() < initialMineCount, "A detonated mine must be removed from the active hazard set");
                cleanup(f);
                h.succeed();
            });
        });
    }

    public static void blizzardShellsDetonateAfterFuse(GameTestHelper h) {
        var f = fixture(h, false, true, 12, "blizzardCooldown");
        putOnStructureFloor(h, f.target());
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.BLIZZARD, "Enraged Gunner AI must select its blizzard mortar cast"));
        h.runAfterDelay(43, () -> {
            double expected = 40 * BastionCombatConfig.BLIZZARD_MULTIPLIER.get();
            assertHits(h, f.target(), expected, "Each blizzard hit must use configured frost damage; cast=" + f.boss().cast() + ", castTicks=" + privateField(f.boss(), "castTicks") + ", strikes=" + privateList(f.boss(), "strikes") + ", allHits=" + f.target().hits + ", target=" + f.target().position() + ", boss=" + f.boss().position());
            h.assertTrue(f.target().hasEffect(net.minecraft.world.effect.MobEffects.SLOWNESS), "A blizzard shell must apply its frost effect");
            cleanup(f);
            h.succeed();
        });
    }

    public static void jetScorchesAimedTarget(GameTestHelper h) {
        var f = fixture(h, true, false, 6, "jetCooldown");
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.JET, "Burner AI must select the aimed jet at close range"));
        h.runAfterDelay(23, () -> {
            double expected = 40 * BastionCombatConfig.JET_MULTIPLIER.get();
            assertHits(h, f.target(), expected, "Each jet pulse must use configured damage");
            h.assertTrue(f.target().getRemainingFireTicks() > 0, "The jet pulse must ignite the aimed target");
            cleanup(f);
            h.succeed();
        });
    }

    public static void chargeHitsTargetDuringRun(GameTestHelper h) {
        var f = fixture(h, true, false, 8, "chargeCooldown");
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.CHARGE, "Burner AI must enter charge windup at medium range"));
        h.runAfterDelay(36, () -> {
            double expected = 40 * BastionCombatConfig.CHARGE_MULTIPLIER.get();
            assertHits(h, f.target(), expected, "Each charge hit must use configured damage");
            h.assertTrue(f.target().getRemainingFireTicks() > 0, "The charge run must ignite its victim");
            cleanup(f);
            h.succeed();
        });
    }

    public static void sweepDamagesTargetDuringFullArc(GameTestHelper h) {
        var f = fixture(h, true, false, 4, "sweepCooldown");
        List<ProbeCow> targets = new ArrayList<>();
        targets.add(f.target());
        for (int i = 1; i < 24; i++) {
            double angle = Math.PI * 2 * i / 24;
            targets.add(cow(h, f.boss().position().add(Math.cos(angle) * 4, 0, Math.sin(angle) * 4)));
        }
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.SWEEP, "Burner AI must enter the rotating sweep at close range"));
        h.runAfterDelay(44, () -> {
            double expected = 40 * BastionCombatConfig.SWEEP_MULTIPLIER.get();
            List<Double> hits = targets.stream().flatMap(cow -> cow.hits().stream()).toList();
            double thermal = 40 * BastionCombatConfig.THERMAL_MULTIPLIER.get();
            h.assertTrue(hits.stream().anyMatch(amount -> Math.abs(amount - expected) < .05)
                    && hits.stream().allMatch(amount -> Math.abs(amount - expected) < .05 || Math.abs(amount - thermal) < .05), "Sweep primary and thermal damage events must use their configured multipliers; Bastion hits=" + hits);
            h.assertTrue(targets.stream().anyMatch(cow -> cow.getRemainingFireTicks() > 0), "A sweep hit must ignite its victim");
            targets.forEach(ProbeCow::discard);
            f.boss().discard();
            h.succeed();
        });
    }

    public static void napalmCommitsAndBurnsAPool(GameTestHelper h) {
        var f = fixture(h, true, false, 10, "napalmCooldown");
        putOnStructureFloor(h, f.target());
        h.runAfterDelay(2, () -> h.assertTrue(f.boss().cast() == BastionEntity.NAPALM, "Burner AI must select napalm at its configured medium range; " + state(f)));
        h.runAfterDelay(34, () -> {
            double expected = 40 * BastionCombatConfig.POOL_MULTIPLIER.get();
            assertHits(h, f.target(), expected, "Each napalm pool hit must use configured damage");
            h.assertTrue(f.target().getRemainingFireTicks() > 0, "The landed napalm strike must commit a burning damage pool");
            cleanup(f);
            h.succeed();
        });
    }

    public static void enragedEruptionSchedulesPlayerStrikes(GameTestHelper h) {
        var boss = createBoss(h, true);
        boss.setHealth(boss.getMaxHealth() * .2F);
        TrackingPlayer target = PlayerInteractionTests.connect(h, new TrackingPlayer(h.getLevel()), GameType.SURVIVAL, 15.5, 2.5, 8.5);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10000);
        target.getAttribute(Attributes.ARMOR).setBaseValue(0);
        target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        target.setHealth(10000);
        double before = target.getHealth();
        target.setPos(boss.position().add(10, 0, 0));
        boss.setTarget(target);
        h.assertTrue(h.getLevel().addFreshEntity(boss), "Enraged Burner fixture must join the level");
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(40);
        BastionEntity.thermalShock(h.getLevel(), target, boss, true);
        h.runAfterDelay(1, () -> setCooldowns(boss, "eruptionCooldown"));
        h.runAfterDelay(2, () -> h.assertTrue(boss.enraged() && boss.cast() == BastionEntity.ERUPTION, "Enraged Burner AI must select eruption and schedule its opening wave; cast=" + boss.cast() + ", enraged=" + boss.enraged() + ", target=" + boss.getTarget() + ", targetAlive=" + target.isAlive() + ", distance=" + boss.distanceTo(target) + ", inLevel=" + h.getLevel().getEntity(target.getUUID())));
        h.runAfterDelay(25, () -> {
            double expected = 40 * BastionCombatConfig.ERUPTION_MULTIPLIER.get();
            double perStrike = expected * (1 + BastionCombatConfig.THERMAL_MULTIPLIER.get());
            double dealt = before - target.getHealth();
            List<Double> bossHits = target.hits.stream().filter(hit -> hit.source() == boss).map(Hit::amount).toList();
            double bossDamage = bossHits.stream().mapToDouble(Double::doubleValue).sum();
            h.assertTrue(bossDamage >= perStrike - .05 && bossDamage <= perStrike * 3 + .05 && Math.abs(bossDamage / perStrike - Math.rint(bossDamage / perStrike)) < .01 && dealt >= bossDamage - .05, "Each eruption pillar and thermal follow-up must use configured damage; before=" + before + ", after=" + target.getHealth() + ", expectedPerStrike=" + perStrike + ", total=" + dealt + ", bossDamage=" + bossDamage + ", bossHits=" + bossHits + ", allHits=" + target.hits);
            h.assertTrue(target.getRemainingFireTicks() > 0, "The delayed eruption pillar must ignite its targeted player");
            boss.discard();
            PlayerInteractionTests.release(h, target);
            h.succeed();
        });
    }

    private static Fixture fixture(GameTestHelper h, boolean burner, boolean enraged, double distance, String enabledCooldown) {
        var boss = createBoss(h, burner);
        boss.setNoGravity(true);
        ProbeCow target = cow(h, boss.position().add(distance, 0, 0));
        boss.setTarget(target);
        h.assertTrue(h.getLevel().addFreshEntity(boss), "Bastion fixture must join the server level");
        if (enraged) boss.setHealth(boss.getMaxHealth() * .2F);
        setCooldowns(boss, enabledCooldown);
        if (enraged) h.runAfterDelay(1, () -> setCooldowns(boss, enabledCooldown));
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(40);
        return new Fixture(boss, target);
    }

    private static BastionEntity createBoss(GameTestHelper h, boolean burner) {
        var type = (burner ? IceSquadContent.BURNER : IceSquadContent.GUNNER).get();
        var boss = type.create(h.getLevel(), EntitySpawnReason.COMMAND);
        boss.setPos(Vec3.atCenterOf(h.absolutePos(new BlockPos(5, 2, 8))));
        boss.setNoGravity(true);
        boss.setPersistenceRequired();
        boss.applyConfiguredAttributes();
        return boss;
    }

    private static ProbeCow cow(GameTestHelper h, Vec3 pos) {
        var cow = new ProbeCow(h.getLevel());
        cow.setNoAi(true);
        cow.setNoGravity(true);
        cow.setPersistenceRequired();
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10000);
        cow.getAttribute(Attributes.ARMOR).setBaseValue(0);
        cow.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        cow.setHealth(10000);
        cow.setPos(pos);
        h.assertTrue(h.getLevel().addFreshEntity(cow), "Bastion target cow must join the server level");
        return cow;
    }

    private static void putOnStructureFloor(GameTestHelper h, ProbeCow target) {
        Vec3 pos = target.position();
        BlockPos.MutableBlockPos cursor = BlockPos.containing(pos).mutable();
        for (int step = 0; step < 6; step++) {
            BlockPos below = cursor.below();
            if (!h.getLevel().getBlockState(below).getCollisionShape(h.getLevel(), below).isEmpty()) break;
            cursor.move(net.minecraft.core.Direction.DOWN);
        }
        target.setPos(pos.x, cursor.getY(), pos.z);
    }

    private static void setCooldowns(BastionEntity boss, String enabled) {
        for (String name : COOLDOWNS) setIntField(boss, name, name.equals(enabled) ? 0 : 10000);
    }

    private static String state(Fixture f) {
        var boss = f.boss();
        var target = f.target();
        StringBuilder cooldowns = new StringBuilder();
        for (String name : COOLDOWNS) cooldowns.append(name).append('=').append(privateField(boss, name)).append(',');
        return "cast=" + boss.cast() + ", burner=" + boss.burner() + ", enraged=" + boss.enraged() + ", healthRatio=" + boss.getHealth() / boss.getMaxHealth() + ", recover=" + privateField(boss, "recover") + ", cryoRemaining=" + boss.getPersistentData().getIntOr("thermal_shock_reforged:ice_bomb_remaining", 0) + ", target=" + boss.getTarget() + ", targetAlive=" + target.isAlive() + ", targetInLevel=" + (boss.level().getEntity(target.getUUID()) == target) + ", distance=" + boss.distanceTo(target) + ", sight=" + boss.hasLineOfSight(target) + ", boss=" + boss.position() + ", victim=" + target.position() + ", cooldowns=" + cooldowns;
    }

    private static void setIntField(Object owner, String name, int value) {
        try {
            Field field = owner.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.setInt(owner, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static List<?> privateList(Object owner, String name) {
        try {
            Field field = owner.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (List<?>) field.get(owner);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static Object privateField(Object owner, String name) {
        try {
            Field field = owner.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(owner);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static void cleanup(Fixture f) {
        f.boss().discard();
        f.target().discard();
    }

    private static void assertHits(GameTestHelper h, ProbeCow target, double expected, String message) {
        List<Double> hits = target.hits();
        double thermal = 40 * BastionCombatConfig.THERMAL_MULTIPLIER.get();
        h.assertTrue(hits.stream().anyMatch(amount -> Math.abs(amount - expected) < .05)
                && hits.stream().allMatch(amount -> Math.abs(amount - expected) < .05 || Math.abs(amount - thermal) < .05), message + "; Bastion damage events=" + hits);
    }
    private static final class ProbeCow extends Cow {
        private final List<Hit> hits = new ArrayList<>();
        private ProbeCow(ServerLevel level) { super(EntityType.COW, level); }
        @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { hits.add(new Hit(source.getEntity(), amount)); return super.hurtServer(level, source, amount); }
        private List<Double> hits() { return hits.stream().filter(hit -> hit.source() instanceof BastionEntity).map(Hit::amount).toList(); }
    }
    private static final class TrackingPlayer extends ServerPlayer {
        private final List<Hit> hits = new ArrayList<>();
        private TrackingPlayer(ServerLevel level) { super(level.getServer(), level, new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "bastion-skill"), net.minecraft.server.level.ClientInformation.createDefault()); }
        @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { hits.add(new Hit(source.getEntity(), amount)); return super.hurtServer(level, source, amount); }
    }
    private record Hit(net.minecraft.world.entity.Entity source, double amount) { }
    private record Fixture(BastionEntity boss, ProbeCow target) { }

    private CurrentBastionSkillServerTests() { }
}

