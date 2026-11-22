package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.entity.beetles.BeetleAttackEntity;
import com.hp.thermalshockreforged.entity.beetles.BeetleCombatEntity;
import com.hp.thermalshockreforged.entity.beetles.BeetleConfig;
import com.hp.thermalshockreforged.entity.beetles.BeetleContent;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.Level;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;
public final class BeetleServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_beetle_server_behavior_tests_beetle_rejects_self_dead_and_cross_dimension_targets_without_cooldown", BeetleServerBehaviorTests::beetleRejectsSelfDeadAndCrossDimensionTargetsWithoutCooldown, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_real_beetle_attack_event_cancels_raw_damage_and_deduplicates_cooldown_retries", BeetleServerBehaviorTests::realBeetleAttackEventCancelsRawDamageAndDeduplicatesCooldownRetries, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_crasher_heavy_strike_uses_the_actual_seventh_server_end_tick", BeetleServerBehaviorTests::crasherHeavyStrikeUsesTheActualSeventhServerEndTick, 25, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_heavy_strike_rejects_the_exact_four_block_boundary", BeetleServerBehaviorTests::heavyStrikeRejectsTheExactFourBlockBoundary, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_heavy_strike_snapshots_base_damage_and_scales_knockback_resistance", BeetleServerBehaviorTests::heavyStrikeSnapshotsBaseDamageAndScalesKnockbackResistance, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_crasher_dash_preserves_sixteen_and_twenty_three_end_phases", BeetleServerBehaviorTests::crasherDashPreservesSixteenAndTwentyThreeEndPhases, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_thrasher_triple_attempts_three_hits_while_keeping_native_invulnerability_frames", BeetleServerBehaviorTests::thrasherTripleAttemptsThreeHitsWhileKeepingNativeInvulnerabilityFrames, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_thrasher_dodge_keeps_both_strikes_and_its_final_parry", BeetleServerBehaviorTests::thrasherDodgeKeepsBothStrikesAndItsFinalParry, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_removed_victim_does_not_redirect_damage_to_abystander", BeetleServerBehaviorTests::removedVictimDoesNotRedirectDamageToABystander, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_removed_caster_retains_its_already_committed_heavy_strike", BeetleServerBehaviorTests::removedCasterRetainsItsAlreadyCommittedHeavyStrike, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_reloaded_cooldown_and_movement_lock_reject_another_attack", BeetleServerBehaviorTests::reloadedCooldownAndMovementLockRejectAnotherAttack, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_non_player_target_task_reload_resumes_only_its_remaining_end_phases", BeetleServerBehaviorTests::nonPlayerTargetTaskReloadResumesOnlyItsRemainingEndPhases, 100, "supernova_server");
        TsrGameTests.add("history_beetle_server_behavior_tests_malformed_task_snapshots_disarm_instead_of_damaging_entities", BeetleServerBehaviorTests::malformedTaskSnapshotsDisarmInsteadOfDamagingEntities, 100, "supernova_server");
    }
    public static void beetleRejectsSelfDeadAndCrossDimensionTargetsWithoutCooldown(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var dead = cow(helper, 7);
        dead.setHealth(0);
        owner.beginAttack(owner);
        owner.beginAttack(dead);
        var otherLevel = helper.getLevel().getServer().getLevel(Level.NETHER);
        helper.assertTrue(otherLevel != null, "The test server must expose its native Nether level");
        var foreign = EntityType.COW.create(otherLevel, EntitySpawnReason.COMMAND);
        owner.beginAttack(foreign);
        helper.assertTrue(tasks(helper, owner).isEmpty() && save(owner).getIntOr("BeetleCooldown", 0) == 0, "Illegal targets must not create a task or spend cooldown");
        helper.succeed();
    }
    public static void realBeetleAttackEventCancelsRawDamageAndDeduplicatesCooldownRetries(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        owner.getRandom().setSeed(seed(false, true));
        boolean hit = target.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(!hit && target.getHealth() == 100 && save(onlyTask(helper, owner)).getIntOr("BeetleSkill", 0) == 0, "The real attack-attempt hook must replace raw beetle damage with its selected skill");
        int cooldown = save(owner).getIntOr("BeetleCooldown", 0);
        for (int attempt = 0; attempt < 5; attempt++) target.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(tasks(helper, owner).size() == 1 && save(owner).getIntOr("BeetleCooldown", 0) == cooldown && target.getHealth() == 100, "Cooldown hits must remain canceled without creating recursive or duplicate tasks");
        helper.succeed();
    }
    public static void crasherHeavyStrikeUsesTheActualSeventhServerEndTick(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        owner.getRandom().setSeed(seed(false, true));
        owner.beginAttack(target);
        var task = onlyTask(helper, owner);
        double damage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * BeetleConfig.CRASHER_STRIKE.get();
        helper.runAfterDelay(5, () -> helper.assertTrue(target.getHealth() == 100 && !task.isRemoved(), "The heavy strike must not hit before its seventh END phase"));
        helper.runAfterDelay(9, () -> {
            helper.assertTrue(Math.abs(target.getHealth() - (100 - damage)) < 1.0E-4 && task.isRemoved(), "The registered server END scheduler must apply one configured heavy strike and remove its task");
            helper.succeed();
        });
    }
    public static void heavyStrikeRejectsTheExactFourBlockBoundary(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        target.snapTo(owner.position().add(4, 0, 0));
        var task = begin(helper, owner, target, true);
        ends(helper, task, 7);
        helper.assertTrue(target.getHealth() == 100 && task.isRemoved(), "Crasher reach must remain strictly less than four blocks");
        helper.succeed();
    }
    public static void heavyStrikeSnapshotsBaseDamageAndScalesKnockbackResistance(GameTestHelper helper) {
        for (double resistance : new double[]{0, .5, 1}) {
            var owner = beetle(helper, false);
            owner.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(16);
            var target = cow(helper, 7);
            target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(resistance);
            Vec3 previous = new Vec3(.2, .1, .3);
            target.setDeltaMovement(previous);
            var task = begin(helper, owner, target, true);
            owner.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1);
            ends(helper, task, 7);
            double expectedDamage = 16 * BeetleConfig.CRASHER_STRIKE.get();
            Vec3 expectedMotion = resistance == 1 ? previous : owner.getLookAngle().scale(BeetleConfig.STRIKE_KNOCKBACK.get() * (1 - resistance));
            helper.assertTrue(Math.abs(target.getHealth() - (100 - expectedDamage)) < 1.0E-4 && target.getDeltaMovement().distanceTo(expectedMotion) < 1.0E-6, "Heavy damage must use the committed attack snapshot and recoil must scale to each target's resistance");
        }
        helper.succeed();
    }
    public static void crasherDashPreservesSixteenAndTwentyThreeEndPhases(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 9);
        var task = begin(helper, owner, target, false);
        ends(helper, task, 15);
        helper.assertTrue(!owner.hasEffect(AuraContent.DAMAGING_AURA) && !task.isRemoved(), "Dash must not activate its aura during windup");
        ends(helper, task, 1);
        var aura = owner.getEffect(AuraContent.DAMAGING_AURA);
        var look = owner.getLookAngle();
        helper.assertTrue(aura != null && aura.getDuration() == 10 && aura.getAmplifier() == 12 && owner.getDeltaMovement().distanceTo(new Vec3(look.x * 1.8, .2, look.z * 1.8)) < 1.0E-6, "The sixteenth END phase must apply the complete original aura and dash motion");
        ends(helper, task, 6);
        helper.assertTrue(!task.isRemoved(), "Dash must retain its final fog phase until END twenty-three");
        ends(helper, task, 1);
        helper.assertTrue(task.isRemoved(), "Dash must finish once after its twenty-third END phase");
        helper.succeed();
    }
    public static void thrasherTripleAttemptsThreeHitsWhileKeepingNativeInvulnerabilityFrames(GameTestHelper helper) {
        var owner = beetle(helper, true);
        var target = cow(helper, 6);
        var task = begin(helper, owner, target, true);
        ends(helper, task, 6);
        helper.assertTrue(target.hurtCalls == 0, "Triple strike must not deal early damage");
        ends(helper, task, 1);
        helper.assertTrue(target.hurtCalls == 1, "The seventh END phase must attempt the first strike");
        ends(helper, task, 2);
        helper.assertTrue(target.hurtCalls == 2 && !task.isRemoved(), "The ninth END phase must attempt the second strike");
        ends(helper, task, 2);
        double damage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * BeetleConfig.THRASHER_STRIKE.get();
        helper.assertTrue(target.hurtCalls == 3 && Math.abs(target.getHealth() - (100 - damage)) < 1.0E-4 && task.isRemoved(), "Three attempts must remain subject to native damage frames and end at phase eleven");
        helper.succeed();
    }
    public static void thrasherDodgeKeepsBothStrikesAndItsFinalParry(GameTestHelper helper) {
        var owner = beetle(helper, true);
        var target = cow(helper, 6);
        var task = begin(helper, owner, target, false);
        ends(helper, task, 5);
        helper.assertTrue(target.hurtCalls == 0, "Dodge must not strike during its initial windup");
        ends(helper, task, 1);
        helper.assertTrue(target.hurtCalls == 1, "Dodge must strike at END six");
        ends(helper, task, 6);
        helper.assertTrue(target.hurtCalls == 2, "Dodge must strike again at END twelve");
        ends(helper, task, 13);
        helper.assertTrue(!owner.hasEffect(StatusContent.PARRYING) && !task.isRemoved(), "Dodge must preserve its final parry delay");
        ends(helper, task, 1);
        var parry = owner.getEffect(StatusContent.PARRYING);
        var look = owner.getLookAngle();
        helper.assertTrue(task.isRemoved() && parry != null && parry.getDuration() == 5 && parry.getAmplifier() == 1 && parry.isAmbient() && parry.isVisible() && owner.getDeltaMovement().distanceTo(new Vec3(-look.z, .5, -look.x)) < 1.0E-6, "The final dodge phase must retain its original visible parry and retreat motion");
        helper.succeed();
    }
    public static void removedVictimDoesNotRedirectDamageToABystander(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        var bystander = cow(helper, 6);
        var task = begin(helper, owner, target, true);
        target.discard();
        ends(helper, task, 7);
        helper.assertTrue(bystander.getHealth() == 100 && bystander.hurtCalls == 0 && task.isRemoved(), "A missing original victim must not redirect the finite strike to nearby entities");
        helper.succeed();
    }
    public static void removedCasterRetainsItsAlreadyCommittedHeavyStrike(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        var task = begin(helper, owner, target, true);
        double damage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * BeetleConfig.CRASHER_STRIKE.get();
        owner.discard();
        ends(helper, task, 7);
        helper.assertTrue(Math.abs(target.getHealth() - (100 - damage)) < 1.0E-4 && task.isRemoved(), "The independent task must retain its already committed strike after the caster is removed");
        helper.succeed();
    }
    public static void reloadedCooldownAndMovementLockRejectAnotherAttack(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        begin(helper, owner, target, true);
        var data = save(owner);
        helper.assertTrue(data.getIntOr("BeetleCooldown", 0) == BeetleConfig.CRASHER_STRIKE_COOLDOWN.get() && data.getIntOr("BeetleMovementLock", 0) == 15, "A committed heavy strike must store its exact cooldown and movement lock");
        var restored = BeetleContent.CRASHER.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        restored.load(input(helper, data));
        restored.beginAttack(target);
        helper.assertTrue(save(restored).getIntOr("BeetleCooldown", 0) == data.getIntOr("BeetleCooldown", 0) && save(restored).getIntOr("BeetleMovementLock", 0) == 15 && tasks(helper, owner).size() == 1, "Restoring the caster must not reset cooldown or submit another attack");
        helper.succeed();
    }
    public static void nonPlayerTargetTaskReloadResumesOnlyItsRemainingEndPhases(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        var original = begin(helper, owner, target, true);
        ends(helper, original, 3);
        var data = save(original);
        original.discard();
        var restored = BeetleContent.ATTACK.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        restored.load(input(helper, data));
        restored.setUUID(UUID.randomUUID());
        helper.assertTrue(helper.getLevel().addFreshEntity(restored), "The isolated restored task must be accepted");
        ends(helper, restored, 3);
        helper.assertTrue(target.getHealth() == 100 && !restored.isRemoved(), "A saved task must not restart or execute its seventh phase early");
        ends(helper, restored, 1);
        double damage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * BeetleConfig.CRASHER_STRIKE.get();
        helper.assertTrue(Math.abs(target.getHealth() - (100 - damage)) < 1.0E-4 && target.hurtCalls == 1 && restored.isRemoved(), "The saved non-player target task must execute exactly its remaining strike");
        helper.succeed();
    }
    public static void malformedTaskSnapshotsDisarmInsteadOfDamagingEntities(GameTestHelper helper) {
        var owner = beetle(helper, false);
        var target = cow(helper, 7);
        var original = begin(helper, owner, target, true);
        var data = save(original);
        original.discard();
        for (int variant = 0; variant < 2; variant++) {
            var bad = data.copy();
            if (variant == 0) bad.putDouble("BeetleLookX", Double.NaN);
            else bad.putString("BeetleTarget", owner.getUUID().toString());
            var restored = BeetleContent.ATTACK.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
            restored.load(input(helper, bad));
            restored.setUUID(UUID.randomUUID());
            helper.getLevel().addFreshEntity(restored);
            restored.tick();
            helper.assertTrue(restored.isRemoved() && target.getHealth() == 100, "Invalid vectors or self-target snapshots must disarm without executing any hit");
        }
        helper.succeed();
    }

    private static BeetleCombatEntity beetle(GameTestHelper helper, boolean thrasher) {
        BeetleCombatEntity result = thrasher ? BeetleContent.THRASHER.get().create(helper.getLevel(), EntitySpawnReason.COMMAND) : BeetleContent.CRASHER.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        result.snapTo(helper.absolutePos(new BlockPos(5, 3, 8)), 0, 0);
        result.setNoAi(true);
        result.setNoGravity(true);
        if (result == null || !helper.getLevel().addFreshEntity(result)) throw new IllegalStateException("Test beetle insertion failed");
        return result;
    }

    private static CountingCow cow(GameTestHelper helper, int x) {
        var cow = new CountingCow(helper.getLevel());
        cow.snapTo(helper.absolutePos(new BlockPos(x, 3, 8)), 0, 0);
        cow.setNoAi(true);
        cow.setNoGravity(true);
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        cow.setHealth(100);
        if (!helper.getLevel().addFreshEntity(cow)) throw new IllegalStateException("Test target insertion failed");
        return cow;
    }

    private static BeetleAttackEntity begin(GameTestHelper helper, BeetleCombatEntity owner, Cow target, boolean first) {
        owner.getRandom().setSeed(seed(owner.thrasher(), first));
        owner.beginAttack(target);
        var task = onlyTask(helper, owner);
        int expected = owner.thrasher() ? (first ? 2 : 3) : (first ? 0 : 1);
        helper.assertTrue(save(task).getIntOr("BeetleSkill", 0) == expected, "The deterministic seed must select the requested skill");
        return task;
    }

    private static long seed(boolean thrasher, boolean first) {
        double chance = thrasher ? BeetleConfig.THRASHER_TRIPLE_CHANCE.get() : BeetleConfig.CRASHER_HEAVY_CHANCE.get();
        for (long seed = 0; seed < 100000; seed++) if ((RandomSource.create(seed).nextDouble() < chance) == first) return seed;
        throw new IllegalStateException("No deterministic beetle skill seed found");
    }

    private static List<BeetleAttackEntity> tasks(GameTestHelper helper, BeetleCombatEntity owner) {
        return helper.getLevel().getEntitiesOfClass(BeetleAttackEntity.class, helper.getBounds().inflate(2), task -> {
            var data = save(task);
            return owner.getUUID().toString().equals(data.getStringOr("BeetleCaster", ""));
        });
    }

    private static BeetleAttackEntity onlyTask(GameTestHelper helper, BeetleCombatEntity owner) {
        var tasks = tasks(helper, owner);
        if (tasks.size() != 1) throw new IllegalStateException("Expected exactly one owned beetle task, got " + tasks.size());
        return tasks.getFirst();
    }

    private static CompoundTag save(net.minecraft.world.entity.Entity entity) { var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper helper, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag); }
    private static void ends(GameTestHelper helper, BeetleAttackEntity task, int count) { for (int phase = 0; phase < count; phase++) task.endTick(helper.getLevel()); }

    private static final class CountingCow extends Cow {
        private int hurtCalls;
        private CountingCow(Level level) { super(EntityType.COW, level); }
        @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { hurtCalls++; return super.hurtServer(level, source, amount); }
    }

    private BeetleServerBehaviorTests() { }
}
