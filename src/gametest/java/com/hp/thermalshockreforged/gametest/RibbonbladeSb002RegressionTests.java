package com.hp.thermalshockreforged.gametest;
import net.minecraft.world.entity.EntitySpawnReason;

import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeActionEntity;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeContent;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeGalvaniteEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class RibbonbladeSb002RegressionTests {
    public static void stationaryTargetAheadIsHitByDirectionalSweep(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8);
        var target = cow(h, blade.position().add(0, 0, 2));
        float before = target.getHealth();
        submit(h, blade, target);
        h.runAfterDelay(20, () -> { h.assertTrue(target.getHealth() < before, "A stationary target two blocks ahead must be hit by the directional sweep"); h.succeed(); });
    }

    public static void positiveXUsesAnIsolatedDirectionalSweep(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8);
        var target = cow(h, blade.position().add(1 * 2, 0, 0 * 2));
        float before = target.getHealth();
        submit(h, blade, target);
        h.runAfterDelay(20, () -> {
            h.assertTrue(Math.abs(before - target.getHealth() - 8) < 1.0E-3, "An isolated directional strike must deal exactly eight damage");
            h.succeed();
        });
    }

    public static void negativeXUsesAnIsolatedDirectionalSweep(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8);
        var target = cow(h, blade.position().add(-1 * 2, 0, 0 * 2));
        float before = target.getHealth();
        submit(h, blade, target);
        h.runAfterDelay(20, () -> {
            String state = "before=" + before + ", after=" + target.getHealth() + ", target=" + target.position() + ", blade=" + blade.position() + ", direction=negative_x";
            System.out.println("RIBBONBLADE_SWEEP " + state);
            h.assertTrue(Math.abs(before - target.getHealth() - 8) < 1.0E-3, "An isolated directional strike must deal exactly eight damage: " + state);
            h.succeed();
        });
    }

    public static void positiveZUsesAnIsolatedDirectionalSweep(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8);
        var target = cow(h, blade.position().add(0 * 2, 0, 1 * 2));
        float before = target.getHealth();
        submit(h, blade, target);
        h.runAfterDelay(20, () -> {
            h.assertTrue(Math.abs(before - target.getHealth() - 8) < 1.0E-3, "An isolated directional strike must deal exactly eight damage");
            h.succeed();
        });
    }

    public static void negativeZUsesAnIsolatedDirectionalSweep(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8);
        var target = cow(h, blade.position().add(0 * 2, 0, -1 * 2));
        float before = target.getHealth();
        submit(h, blade, target);
        h.runAfterDelay(20, () -> {
            String state = "before=" + before + ", after=" + target.getHealth() + ", target=" + target.position() + ", blade=" + blade.position() + ", direction=negative_z";
            System.out.println("RIBBONBLADE_SWEEP " + state);
            h.assertTrue(Math.abs(before - target.getHealth() - 8) < 1.0E-3, "An isolated directional strike must deal exactly eight damage: " + state);
            h.succeed();
        });
    }

    public static void diagonalUsesAnIsolatedDirectionalSweep(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8);
        var target = cow(h, blade.position().add(1 * 2, 0, 1 * 2));
        float before = target.getHealth();
        submit(h, blade, target);
        h.runAfterDelay(20, () -> {
            h.assertTrue(Math.abs(before - target.getHealth() - 8) < 1.0E-3, "An isolated directional strike must deal exactly eight damage");
            h.succeed();
        });
    }

    public static void originalSideRangeStillHitsWithinInflatedBox(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8); var target = cow(h, blade.position().add(1.4, 0, .5)); float before = target.getHealth(); submit(h, blade, target);
        h.runAfterDelay(20, () -> { h.assertTrue(target.getHealth() < before, "The original 1.5 block side range must remain active"); h.succeed(); });
    }

    public static void rangeOutsideDirectionalBoxDoesNotHit(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8); var target = cow(h, blade.position().add(2.8, 0, 2.8)); float before = target.getHealth(); submit(h, blade, target);
        h.runAfterDelay(20, () -> { h.assertTrue(target.getHealth() == before, "A target outside both the inflated side range and sweep extension must not be hit"); h.succeed(); });
    }

    public static void casterIsExcludedFromItsOwnSweep(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8); var target = cow(h, blade.position().add(0, 0, 2)); float before = blade.getHealth(); submit(h, blade, target);
        h.runAfterDelay(20, () -> { h.assertTrue(blade.getHealth() == before, "The caster must remain excluded from its own strike bounds"); h.succeed(); });
    }

    public static void armorAndKnockbackResistanceRemainNative(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8); var target = cow(h, blade.position().add(0, 0, 2)); target.getAttribute(Attributes.ARMOR).setBaseValue(10); target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(.5); float before = target.getHealth(); submit(h, blade, target);
        h.runAfterDelay(20, () -> { double damage = before - target.getHealth(); h.assertTrue(damage > 2.5 && damage < 5.5, "Armor must reduce the strike through the native damage path"); h.assertTrue(target.getDeltaMovement().lengthSqr() > 0, "The strike must apply knockback after native resistance scaling"); h.succeed(); });
    }

    public static void strikeSettlesOnceAndRemovesItsFiniteTask(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8); var target = cow(h, blade.position().add(0, 0, 2)); float before = target.getHealth(); submit(h, blade, target);
        h.runAfterDelay(20, () -> { float after = target.getHealth(); h.assertTrue(after < before && tasks(h, blade) == 0, "The strike must resolve once and remove its task"); h.runAfterDelay(15, () -> { h.assertTrue(target.getHealth() == after, "A settled task must not resolve a second time"); h.succeed(); }); });
    }

    public static void savedStrikeReloadKeepsTargetAndDirectionalHit(GameTestHelper h) {
        var blade = blade(h, 8, 2, 8); var target = cow(h, blade.position().add(0, 0, 2)); float before = target.getHealth(); var task = submit(h, blade, target); CompoundTag tag = snapshot(task); task.discard(); tag.put("UUID",net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,UUID.randomUUID()).result().orElseThrow()); var restored = RibbonbladeContent.ACTION.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND); h.assertTrue(restored != null, "Action entity type must create a reload fixture"); restore(h, restored, tag); h.assertTrue(h.getLevel().addFreshEntity(restored), "Reloaded action task must join the server world");
        h.runAfterDelay(20, () -> { h.assertTrue(target.getHealth() < before, "A reloaded strike must retain its target and directional sweep"); h.succeed(); });
    }

    public static void originalBeginAttackHitsChosenStationaryTarget(GameTestHelper h) {
        h.getLevel().setBlock(h.absolutePos(new BlockPos(5, 1, 5)), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
        h.getLevel().setBlock(h.absolutePos(new BlockPos(5, 1, 7)), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
        var blade = blade(h, 5, 2, 3);
        var target = cow(h, h.absolutePos(new BlockPos(5, 2, 5)).getBottomCenter());
        target.setYRot(0);
        target.setXRot(0);
        target.setDeltaMovement(Vec3.ZERO);
        float before = target.getHealth();
        Vec3 expectedDestination = target.position().add(0, 0, 2);
        blade.beginAttack(target);
        h.assertTrue(blade.position().distanceToSqr(expectedDestination) < 1.0E-6, "The real attack entry must teleport two blocks ahead of the chosen target");
        h.assertTrue(snapshot(blade).getIntOr("RibbonCooldown", 0) == 20, "A committed real attack must start the configured cooldown");
        h.assertTrue(tasks(h, blade) == 1, "One real attack must create one local finite task");
        h.runAfterDelay(20, () -> {
            h.assertTrue(Math.abs(before - target.getHealth() - 8) < 1.0E-3, "The original beginAttack failure must now deal exactly eight damage");
            h.assertTrue(tasks(h, blade) == 0, "The actual local task must be gone after settlement");
            h.succeed();
        });
    }

    private static RibbonbladeGalvaniteEntity blade(GameTestHelper h, int x, int y, int z) { var blade = h.spawnWithNoFreeWill(RibbonbladeContent.RIBBONBLADE.get(), x, y, z); blade.setNoGravity(true); blade.setDeltaMovement(Vec3.ZERO); return blade; }
    private static Cow cow(GameTestHelper h, Vec3 position) { var cow = EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND); h.assertTrue(cow != null, "Cow fixture must be created"); cow.setNoAi(true); cow.setNoGravity(true); cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100); cow.setHealth(100); cow.setPos(position); h.assertTrue(h.getLevel().addFreshEntity(cow), "Cow fixture must join the server world"); return cow; }
    private static RibbonbladeActionEntity submit(GameTestHelper h, RibbonbladeGalvaniteEntity blade, Cow target) { var task = RibbonbladeActionEntity.submitStrike(h.getLevel(), blade, target, blade.position()); h.assertTrue(task != null, "Strike task must join through the native submit path"); return task; }
    private static int tasks(GameTestHelper h, RibbonbladeGalvaniteEntity blade) { return (int) h.getLevel().getEntitiesOfClass(RibbonbladeActionEntity.class, blade.getBoundingBox().inflate(16)).stream().filter(task -> { var tag = snapshot(task); return uuid(tag, "Caster") != null && uuid(tag, "Caster").equals(blade.getUUID()); }).count(); }
    private RibbonbladeSb002RegressionTests() { }


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.LENIENT_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_stationary_target_ahead_is_hit_by_directional_sweep", RibbonbladeSb002RegressionTests::stationaryTargetAheadIsHitByDirectionalSweep, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_positive_xuses_an_isolated_directional_sweep", RibbonbladeSb002RegressionTests::positiveXUsesAnIsolatedDirectionalSweep, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_negative_xuses_an_isolated_directional_sweep", RibbonbladeSb002RegressionTests::negativeXUsesAnIsolatedDirectionalSweep, 35, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_positive_zuses_an_isolated_directional_sweep", RibbonbladeSb002RegressionTests::positiveZUsesAnIsolatedDirectionalSweep, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_negative_zuses_an_isolated_directional_sweep", RibbonbladeSb002RegressionTests::negativeZUsesAnIsolatedDirectionalSweep, 35, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_diagonal_uses_an_isolated_directional_sweep", RibbonbladeSb002RegressionTests::diagonalUsesAnIsolatedDirectionalSweep, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_original_side_range_still_hits_within_inflated_box", RibbonbladeSb002RegressionTests::originalSideRangeStillHitsWithinInflatedBox, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_range_outside_directional_box_does_not_hit", RibbonbladeSb002RegressionTests::rangeOutsideDirectionalBoxDoesNotHit, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_caster_is_excluded_from_its_own_sweep", RibbonbladeSb002RegressionTests::casterIsExcludedFromItsOwnSweep, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_armor_and_knockback_resistance_remain_native", RibbonbladeSb002RegressionTests::armorAndKnockbackResistanceRemainNative, 35, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_strike_settles_once_and_removes_its_finite_task", RibbonbladeSb002RegressionTests::strikeSettlesOnceAndRemovesItsFiniteTask, 50, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_saved_strike_reload_keeps_target_and_directional_hit", RibbonbladeSb002RegressionTests::savedStrikeReloadKeepsTargetAndDirectionalHit, 55, "migration_empty");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_ribbonblade_sb002_regression_tests_original_begin_attack_hits_chosen_stationary_target", RibbonbladeSb002RegressionTests::originalBeginAttackHitsChosenStationaryTarget, 45, "migration_empty");
    }
}
