package com.hp.thermalshockreforged.gametest;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.huntress.HuntressBossEntity;
import com.hp.thermalshockreforged.entity.huntress.HuntressConfig;
import com.hp.thermalshockreforged.entity.huntress.HuntressContent;
import com.hp.thermalshockreforged.entity.huntress.HuntressTaskEntity;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.phys.AABB;
import net.minecraft.gametest.framework.GameTestHelper;
public final class HuntressServerBehaviorTests {
    public static void rollbackCancelsHitAndCooldownStillCancelsBossDamage(GameTestHelper helper) {
        HuntressBossEntity owner = helper.spawnWithNoFreeWill(HuntressContent.BOSS.get(), 4, 2, 4);
        Cow target = addCow(helper, 6, 2, 4);
        float initialHealth = target.getHealth();
        boolean firstHit = target.hurtServer((ServerLevel)target.level(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(!firstHit && target.getHealth() == initialHealth, "Close-range Huntress attack must be replaced by rollback skill");
        helper.assertTrue(Math.abs(owner.getDeltaMovement().y - 1) < 1.0E-6, "Rollback must preserve the configured upward impulse");
        CompoundTag ownerData = save(owner);
        helper.assertTrue(ownerData.getIntOr("HuntressCooldown", 0) > 0, "Rollback must start its cooldown");
        Cow cooldownTarget = addCow(helper, 8, 2, 4);
        float cooldownHealth = cooldownTarget.getHealth();
        boolean cooldownHit = cooldownTarget.hurtServer((ServerLevel)cooldownTarget.level(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(!cooldownHit && cooldownTarget.getHealth() == cooldownHealth, "Boss damage remains cancelled during cooldown");
        helper.assertTrue(tasksFor(helper, owner).isEmpty(), "Rollback and cooldown must not submit a delayed task for this boss");
        helper.succeed();
    }
    public static void tripleStrikeRunsDelayedAreaDamageAndRemovesTask(GameTestHelper helper) {
        HuntressBossEntity owner = helper.spawnWithNoFreeWill(HuntressContent.BOSS.get(), 4, 2, 4);
        owner.getRandom().setSeed(seedForTriple());
        Cow target = addCow(helper, 9, 2, 4);
        Cow nearby = addCow(helper, 6, 2, 4);
        float nearbyHealth = nearby.getHealth();
        boolean firstHit = target.hurtServer((ServerLevel)target.level(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(!firstHit && target.getHealth() == 10, "Distant Huntress attack must cancel the triggering hit");
        HuntressTaskEntity task = onlyTask(helper, owner);
        CompoundTag taskData = save(task);
        helper.assertTrue(taskData.getIntOr("HuntressSkill", 0) == 1, "The deterministic seed must select the two-strike skill");
        CompoundTag ownerData = save(owner);
        helper.assertTrue(ownerData.getIntOr("HuntressCooldown", 0) > 0 && ownerData.getIntOr("HuntressMovementLock", 0) > 0, "Committed triple strike must start cooldown and movement lock");
        helper.runAfterDelay(13, () -> {
            CompoundTag delayedData = save(task);
            helper.assertTrue(delayedData.getIntOr("HuntressElapsed", 0) >= 12, "The first area strike must wait for its delayed END tick");
            double expectedDamage = owner.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) * HuntressConfig.TRIPLE_MULTIPLIER.get();
            helper.assertTrue(Math.abs(nearby.getHealth() - (nearbyHealth - expectedDamage)) < 1.0E-4, "First strike must apply base attack times its configured multiplier inside the area");
        });
        helper.runAfterDelay(23, () -> {
            helper.assertTrue(task.isRemoved(), "The finite triple-strike task must remove itself after its final scheduled strike");
            helper.succeed();
        });
    }

    private static long seedForTriple() {
        for (long seed = 0; seed < 100000; seed++) if (RandomSource.create(seed).nextDouble() < .5) return seed;
        throw new IllegalStateException("No deterministic Huntress triple-strike seed found");
    }

    private static Cow addCow(GameTestHelper helper, int x, int y, int z) {
        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (cow == null) throw new IllegalStateException("Cow entity type returned null");
        cow.setNoAi(true);
        cow.snapTo(helper.absolutePos(new net.minecraft.core.BlockPos(x, y, z)), 0, 0);
        if (!helper.getLevel().addFreshEntity(cow)) throw new IllegalStateException("GameTest server rejected cow entity");
        return cow;
    }

    private static HuntressTaskEntity onlyTask(GameTestHelper helper, HuntressBossEntity owner) {
        List<HuntressTaskEntity> tasks = tasksFor(helper, owner);
        if (tasks.size() != 1) throw new IllegalStateException("Expected one Huntress task, found " + tasks.size());
        return tasks.getFirst();
    }

    private static List<HuntressTaskEntity> tasksFor(GameTestHelper helper, HuntressBossEntity owner) {
        return helper.getLevel().getEntitiesOfClass(HuntressTaskEntity.class, new AABB(owner.blockPosition()).inflate(64)).stream()
            .filter(task -> {
                CompoundTag data = save(task);
                return uuid(data, "HuntressCaster") != null && owner.getUUID().equals(uuid(data, "HuntressCaster"));
            })
            .toList();
    }
    public static void register() {
        TsrGameTests.add("history_huntress_server_behavior_tests_rollback_cancels_hit_and_cooldown_still_cancels_boss_damage", HuntressServerBehaviorTests::rollbackCancelsHitAndCooldownStillCancelsBossDamage, 50, "supernova_server");
        TsrGameTests.add("history_huntress_server_behavior_tests_triple_strike_runs_delayed_area_damage_and_removes_task", HuntressServerBehaviorTests::tripleStrikeRunsDelayedAreaDamageAndRemovesTask, 65, "supernova_server");
    }

    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static java.util.UUID uuid(CompoundTag tag,String key){return tag.getString(key).map(java.util.UUID::fromString).orElse(null);}
    private static void putUuid(CompoundTag tag,String key,java.util.UUID value){tag.put(key,net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElseThrow());}
}
