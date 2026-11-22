package com.hp.thermalshockreforged.entity.weaponcore;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.gametest.framework.GameTestHelper;
public final class PrismaticSupernovaServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_prismatic_supernova_server_behavior_tests_delayed_blast_survives_source_removal_and_damages_once", PrismaticSupernovaServerBehaviorTests::delayedBlastSurvivesSourceRemovalAndDamagesOnce, 60, "supernova_server");
        TsrGameTests.add("history_prismatic_supernova_server_behavior_tests_fatal_damage_does_not_cancel_the_committed_blast", PrismaticSupernovaServerBehaviorTests::fatalDamageDoesNotCancelTheCommittedBlast, 60, "supernova_server");
        TsrGameTests.add("history_prismatic_supernova_server_behavior_tests_saved_blast_keeps_its_remaining_delay_and_follows_source", PrismaticSupernovaServerBehaviorTests::savedBlastKeepsItsRemainingDelayAndFollowsSource, 65, "supernova_server");
        TsrGameTests.add("history_prismatic_supernova_server_behavior_tests_malformed_blast_data_is_bounded_and_unowned_tasks_expire", PrismaticSupernovaServerBehaviorTests::malformedBlastDataIsBoundedAndUnownedTasksExpire, 10, "supernova_server");
    }

    public static void delayedBlastSurvivesSourceRemovalAndDamagesOnce(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(5, 3, 8));
        PrismaticSupernovaEntity source = WeaponContent.PRISMATIC_SUPERNOVA.get().spawn(level, spawnPos, EntitySpawnReason.MOB_SUMMONED);
        helper.assertTrue(source != null, "MOB_SUMMONED spawn must create the Prismatic Supernova");
        Vec3 origin = source.position();
        Cow inside = addCow(level, origin.add(2, 0, 0));
        Cow outside = addCow(level, origin.add(6, 0, 0));
        float insideHealth = inside.getHealth();
        float outsideHealth = outside.getHealth();
        BlockPos protectedBlock = BlockPos.containing(origin.add(0, 0, 2));
        level.setBlock(protectedBlock, Blocks.STONE.defaultBlockState(), 3);
        helper.assertTrue(taskCount(level, origin) == 1, "Adding the summoned entity must register exactly one delayed blast task");
        source.discard();
        helper.assertTrue(source.isRemoved(), "The test must remove the casting entity before the delayed blast");

        helper.runAfterDelay(38, () -> {
            helper.assertTrue(inside.getHealth() == insideHealth, "The blast must not damage targets before its 40-tick delay");
            helper.assertTrue(outside.getHealth() == outsideHealth, "The outside target must remain unharmed before detonation");
            helper.assertTrue(taskCount(level, origin) == 1, "The independent blast task must survive source removal until detonation");
        });
        helper.runAfterDelay(42, () -> {
            helper.assertTrue(Math.abs(inside.getHealth() - (insideHealth - 9)) < 1.0E-4F, "The delayed blast must deal exactly nine damage inside its radius after its source is gone");
            helper.assertTrue(outside.getHealth() == outsideHealth, "The delayed blast must not damage targets outside its radius");
            helper.assertTrue(level.getBlockState(protectedBlock).is(Blocks.STONE), "The ownerless explosion damage must not break blocks");
            helper.assertTrue(taskCount(level, origin) == 0, "The delayed blast task must remove itself after one detonation");
        });
        helper.runAfterDelay(46, () -> {
            helper.assertTrue(Math.abs(inside.getHealth() - (insideHealth - 9)) < 1.0E-4F, "The completed task must not apply its damage twice");
            helper.assertTrue(outside.getHealth() == outsideHealth, "The outside target must remain unharmed after the completed task");
            helper.succeed();
        });
    }

    public static void fatalDamageDoesNotCancelTheCommittedBlast(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        var source = WeaponContent.PRISMATIC_SUPERNOVA.get().spawn(level, helper.absolutePos(new BlockPos(5, 3, 8)), EntitySpawnReason.MOB_SUMMONED);
        helper.assertTrue(source != null, "A native spawn must create the casting entity");
        Vec3 origin = source.position();
        Cow target = addCow(level, origin.add(2, 0, 0));
        float initialHealth = target.getHealth();
        helper.assertTrue(!source.hurtServer(level, level.damageSources().fall(), 1000) && source.getHealth() == 900, "The original fall immunity must reject damage");
        helper.assertTrue(source.hurtServer(level, level.damageSources().genericKill(), 1000) && source.isDeadOrDying(), "Accepted fatal damage must enter the death sequence");
        helper.runAfterDelay(13, () -> {
            helper.assertTrue(source.isRemoved(), "The dead casting entity must be removed after ten death ticks");
            helper.assertTrue(target.getHealth() == initialHealth && taskCount(level, origin) == 1, "Source death must leave the independent blast pending without early damage");
        });
        helper.runAfterDelay(43, () -> {
            helper.assertTrue(target.getHealth() == initialHealth - 9 && taskCount(level, origin) == 0, "The committed task must blast once after normal source death");
            helper.succeed();
        });
    }

    public static void savedBlastKeepsItsRemainingDelayAndFollowsSource(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        var source = WeaponContent.PRISMATIC_SUPERNOVA.get().spawn(level, helper.absolutePos(new BlockPos(5, 3, 8)), EntitySpawnReason.MOB_SUMMONED);
        helper.assertTrue(source != null, "A native spawn must create the casting entity");
        source.setPersistenceRequired();
        source.setNoAi(true);
        Vec3 origin = source.position();
        Cow target = addCow(level, origin.add(7, 0, 0));
        float initialHealth = target.getHealth();
        helper.runAfterDelay(10, () -> {
            var tasks = level.getEntitiesOfClass(SupernovaBlastEntity.class, new AABB(origin, origin).inflate(5));
            helper.assertTrue(tasks.size() == 1, "The initial native spawn must own one task");
            var previous = tasks.getFirst();
            ValueInput saved = TsrGameTests.roundTrip(helper, previous);
            int remaining = saved.getIntOr("Remaining", -1);
            helper.assertTrue(remaining >= 28 && remaining <= 31 && saved.getBooleanOr("Armed", false), "The saved task must retain its elapsed countdown");
            previous.discard();
            var loaded = new SupernovaBlastEntity(WeaponContent.SUPERNOVA_BLAST.get(), level);
            loaded.load(saved);
            helper.assertTrue(level.addFreshEntity(loaded), "The saved task must be accepted by the server");
            helper.assertTrue(TsrGameTests.roundTrip(helper, loaded).getIntOr("Remaining", -1) == remaining, "Loading must not reset the countdown to forty");
            source.setPos(origin.add(5, 0, 0));
        });
        helper.runAfterDelay(35, () -> helper.assertTrue(target.getHealth() == initialHealth, "Reloading must not cause premature damage"));
        helper.runAfterDelay(44, () -> {
            var pending = level.getEntitiesOfClass(SupernovaBlastEntity.class, new AABB(origin, origin).inflate(128));
            String pendingState = pending.stream().map(task -> task.position() + "/" + TsrGameTests.roundTrip(helper, task).getIntOr("Remaining", -1)).toList().toString();
            helper.assertTrue(target.getHealth() == initialHealth - 9 && source.isRemoved(), "The reloaded task must follow its live source and detonate at the original deadline: health=" + target.getHealth() + ", sourceRemoved=" + source.isRemoved() + ", sourcePos=" + source.position() + ", pending=" + pendingState);
            helper.assertTrue(taskCount(level, origin.add(5, 0, 0)) == 0, "The reloaded task must be consumed once");
            helper.succeed();
        });
    }

    public static void malformedBlastDataIsBoundedAndUnownedTasksExpire(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        CompoundTag saved = new CompoundTag();
        saved.putBoolean("Armed", true);
        saved.putInt("Remaining", Integer.MAX_VALUE);
        var unowned = new SupernovaBlastEntity(WeaponContent.SUPERNOVA_BLAST.get(), level);
        unowned.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), saved));
        ValueInput normalized = TsrGameTests.roundTrip(helper, unowned);
        helper.assertTrue(!normalized.getBooleanOr("Armed", false) && normalized.getIntOr("Remaining", -1) == 40, "Unowned task data must disarm and clamp an excessive delay");
        unowned.snapTo(Vec3.atCenterOf(helper.absolutePos(new BlockPos(5, 3, 8))));
        helper.assertTrue(level.addFreshEntity(unowned), "The malformed task fixture must be added for cleanup verification");
        saved.putString("Source", java.util.UUID.randomUUID().toString());
        saved.putInt("Remaining", -100);
        var bounded = new SupernovaBlastEntity(WeaponContent.SUPERNOVA_BLAST.get(), level);
        bounded.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), saved));
        helper.assertTrue(TsrGameTests.roundTrip(helper, bounded).getIntOr("Remaining", -1) == 0, "Negative remaining time must clamp to zero");
        helper.runAfterDelay(3, () -> {
            helper.assertTrue(unowned.isRemoved(), "A disarmed task must remove itself instead of remaining loaded indefinitely");
            helper.succeed();
        });
    }

    private static Cow addCow(ServerLevel level, Vec3 position) {
        Cow cow = new Cow(EntityType.COW, level) {
            @Override
            public boolean hurtServer(ServerLevel server, net.minecraft.world.damagesource.DamageSource damage, float amount) {
                ThermalShockReforged.LOGGER.info("Supernova fixture damage: uuid={}, type={}, amount={}, position={}, noGravity={}", getUUID(), damage.type().msgId(), amount, position(), isNoGravity());
                return super.hurtServer(server, damage, amount);
            }
        };
        cow.setNoAi(true);
        cow.setNoGravity(true);
        cow.snapTo(position, 0, 0);
        if (!level.addFreshEntity(cow)) throw new IllegalStateException("GameTest server rejected cow entity");
        return cow;
    }

    private static int taskCount(ServerLevel level, Vec3 origin) {
        return level.getEntitiesOfClass(SupernovaBlastEntity.class, new AABB(origin, origin).inflate(5)).size();
    }

    private PrismaticSupernovaServerBehaviorTests() { }
}
