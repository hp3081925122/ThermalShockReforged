package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.entity.huntress.HuntressBossEntity;
import com.hp.thermalshockreforged.entity.huntress.HuntressContent;
import com.hp.thermalshockreforged.entity.huntress.HuntressTaskEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

public final class BossServerRegressionTests {
    private BossServerRegressionTests() {
    }

    public static void register() {
        TsrGameTests.add("huntress_triple_strike_uses_damage_snapshot", BossServerRegressionTests::huntressTripleStrikeUsesDamageSnapshot, 40);
        TsrGameTests.add("huntress_strike_respects_target_knockback_resistance", BossServerRegressionTests::huntressStrikeRespectsTargetKnockbackResistance, 30);
        TsrGameTests.add("huntress_committed_slam_finishes_after_caster_removal", BossServerRegressionTests::huntressCommittedSlamFinishesAfterCasterRemoval, 60);
        TsrGameTests.add("huntress_rollback_cooldown_blocks_repeat_task", BossServerRegressionTests::huntressRollbackCooldownBlocksRepeatTask, 20);
    }

    public static void huntressTripleStrikeUsesDamageSnapshot(GameTestHelper helper) {
        var boss = helper.spawnWithNoFreeWill(HuntressContent.BOSS.get(), 1, 2, 1);
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 2, 2, 1);
        prepareTarget(target);
        target.noPhysics = true;
        target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        HuntressTaskEntity task = submit(boss, target, 1);
        boss.discard();
        helper.runAfterDelay(22, () -> {
            double expected = 1000 - 20 * com.hp.thermalshockreforged.entity.huntress.HuntressConfig.TRIPLE_MULTIPLIER.get();
            helper.assertTrue(Math.abs(target.getHealth() - expected) < 0.01, "Triple strike must land twice using the caster damage snapshot and configured multiplier; health=" + target.getHealth() + ", taskRemoved=" + task.isRemoved());
            helper.assertTrue(task.isRemoved(), "Triple strike task must remove itself after its second hit");
            helper.succeed();
        });
    }

    public static void huntressStrikeRespectsTargetKnockbackResistance(GameTestHelper helper) {
        var boss = helper.spawnWithNoFreeWill(HuntressContent.BOSS.get(), 1, 2, 1);
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 2, 2, 1);
        prepareTarget(target);
        target.noPhysics = true;
        target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        HuntressTaskEntity task = submit(boss, target, 1);
        boss.discard();
        helper.runAfterDelay(14, () -> {
            double expected = 1000 - 10 * com.hp.thermalshockreforged.entity.huntress.HuntressConfig.TRIPLE_MULTIPLIER.get();
            helper.assertTrue(Math.abs(target.getHealth() - expected) < 0.01, "First triple strike must damage a fully knockback-resistant target; health=" + target.getHealth());
            helper.assertTrue(target.getDeltaMovement().horizontalDistanceSqr() < 1.0E-8, "Full knockback resistance must suppress horizontal skill knockback; velocity=" + target.getDeltaMovement());
            helper.assertTrue(!task.isRemoved(), "Triple strike task must remain armed until its second strike");
            helper.succeed();
        });
    }

    public static void huntressCommittedSlamFinishesAfterCasterRemoval(GameTestHelper helper) {
        var boss = helper.spawnWithNoFreeWill(HuntressContent.BOSS.get(), 1, 2, 1);
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 1);
        prepareTarget(target);
        HuntressTaskEntity task = submit(boss, target, 2);
        boss.discard();
        helper.runAfterDelay(47, () -> {
            double expected = 1000 - 10 * com.hp.thermalshockreforged.entity.huntress.HuntressConfig.SLAM_MULTIPLIER.get();
            helper.assertTrue(Math.abs(target.getHealth() - expected) < 0.01, "Committed slam must finish from its captured caster snapshot after caster removal; health=" + target.getHealth() + ", taskRemoved=" + task.isRemoved());
            helper.assertTrue(task.isRemoved(), "Slam task must remove itself after impact");
            helper.succeed();
        });
    }

    public static void huntressRollbackCooldownBlocksRepeatTask(GameTestHelper helper) {
        var boss = helper.spawnWithNoFreeWill(HuntressContent.BOSS.get(), 1, 2, 1);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 2, 2, 1);
        int tasksBefore = helper.getLevel().getEntitiesOfClass(HuntressTaskEntity.class, boss.getBoundingBox().inflate(16), entity -> true).size();
        boss.beginAttack(target);
        var distantTarget = helper.absolutePos(new BlockPos(8, 2, 1));
        target.snapTo(distantTarget.getX() + 0.5, distantTarget.getY(), distantTarget.getZ() + 0.5, target.getYRot(), target.getXRot());
        boss.beginAttack(target);
        int tasks = helper.getLevel().getEntitiesOfClass(HuntressTaskEntity.class, boss.getBoundingBox().inflate(16), entity -> true).size();
        helper.assertTrue(tasks == tasksBefore, "Rollback cooldown must reject a repeated attack after the target moves out of melee range; tasksBefore=" + tasksBefore + ", tasksAfter=" + tasks);
        helper.succeed();
    }

    private static void prepareTarget(net.minecraft.world.entity.LivingEntity target) {
        target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);
        target.setHealth(1000);
    }

    private static HuntressTaskEntity submit(HuntressBossEntity boss, net.minecraft.world.entity.LivingEntity target, int skill) {
        try {
            Method submit = HuntressTaskEntity.class.getDeclaredMethod("submit", net.minecraft.server.level.ServerLevel.class, HuntressBossEntity.class, net.minecraft.world.entity.LivingEntity.class, int.class);
            submit.setAccessible(true);
            return (HuntressTaskEntity) submit.invoke(null, (net.minecraft.server.level.ServerLevel) boss.level(), boss, target, skill);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("Could not start the Huntress regression skill", exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not access the Huntress skill submission entry point", exception);
        }
    }
}
