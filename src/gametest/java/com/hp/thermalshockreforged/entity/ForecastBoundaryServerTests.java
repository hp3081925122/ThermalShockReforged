package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public final class ForecastBoundaryServerTests {
    public static void register() {
        TsrGameTests.add("history_forecast_boundary_server_tests_forecast_cooldown_nbt_clamps_loaded_range", ForecastBoundaryServerTests::forecastCooldownNbtClampsLoadedRange, 100, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_forecast_rejects_target_in_another_dimension", ForecastBoundaryServerTests::forecastRejectsTargetInAnotherDimension, 100, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_legacy_warning_nbt_defaults_to_twenty_end_ticks", ForecastBoundaryServerTests::legacyWarningNbtDefaultsToTwentyEndTicks, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_committed_nbt_does_not_replay_damage", ForecastBoundaryServerTests::committedNbtDoesNotReplayDamage, 10, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_invalid_saved_origin_cancels_warning", ForecastBoundaryServerTests::invalidSavedOriginCancelsWarning, 10, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_warning_from_other_dimension_is_discarded", ForecastBoundaryServerTests::warningFromOtherDimensionIsDiscarded, 10, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_expired_remaining_ticks_detonate_without_delay", ForecastBoundaryServerTests::expiredRemainingTicksDetonateWithoutDelay, 12, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_over_limit_remaining_ticks_clamp_to_twenty", ForecastBoundaryServerTests::overLimitRemainingTicksClampToTwenty, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_infinite_saved_damage_clamps_to_zero", ForecastBoundaryServerTests::infiniteSavedDamageClampsToZero, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_nan_saved_damage_clamps_to_zero", ForecastBoundaryServerTests::nanSavedDamageClampsToZero, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_negative_saved_damage_clamps_to_zero", ForecastBoundaryServerTests::negativeSavedDamageClampsToZero, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_zero_damage_warning_still_consumes_once", ForecastBoundaryServerTests::zeroDamageWarningStillConsumesOnce, 30, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_configured_owner_can_remain_eligible", ForecastBoundaryServerTests::configuredOwnerCanRemainEligible, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_configured_owner_can_be_excluded", ForecastBoundaryServerTests::configuredOwnerCanBeExcluded, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_warning_excludes_entity_outside_outer_area", ForecastBoundaryServerTests::warningExcludesEntityOutsideOuterArea, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_warning_includes_entity_inside_outer_area", ForecastBoundaryServerTests::warningIncludesEntityInsideOuterArea, 40, "supernova_server");
        TsrGameTests.add("history_forecast_boundary_server_tests_armed_damage_uses_caster_attack_snapshot", ForecastBoundaryServerTests::armedDamageUsesCasterAttackSnapshot, 40, "supernova_server");
    }
    private static final String TEMPLATE = "forecast_test";

    public static void armedDamageUsesCasterAttackSnapshot(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var warning = warning(h, caster, new Vec3(5, 2, 5), 1);
        caster.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(22);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Armed damage must retain the caster's attack snapshot");
            h.assertTrue(warning.isRemoved(), "Snapshot warning must be consumed once");
            h.succeed();
        });
    }

    public static void warningIncludesEntityInsideOuterArea(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(6.2, 2, 5));
        target.setHealth(100);
        warning(h, caster, new Vec3(5, 2, 5), 1);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Target intersecting the 1.5 block area must be hit");
            h.succeed();
        });
    }

    public static void warningExcludesEntityOutsideOuterArea(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(7.2, 2, 5));
        target.setHealth(100);
        warning(h, caster, new Vec3(5, 2, 5), 1);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "Target outside the 1.5 block area must remain untouched");
            h.succeed();
        });
    }

    public static void configuredOwnerCanBeExcluded(GameTestHelper h) {
        var owner = cow(h, new Vec3(5, 2, 5));
        var other = cow(h, new Vec3(5.8, 2, 5));
        owner.setHealth(100);
        other.setHealth(100);
        spawn(h, new Vec3(5, 2, 5), owner.getUUID(), 10, true);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(owner.getHealth() - 100) < 1.0E-6, "Configured owner must be excluded when requested");
            h.assertTrue(Math.abs(other.getHealth() - 90) < 1.0E-6, "Other ordinary entities must remain eligible");
            h.succeed();
        });
    }

    public static void configuredOwnerCanRemainEligible(GameTestHelper h) {
        var owner = cow(h, new Vec3(5, 2, 5));
        ((net.minecraft.world.entity.Mob) owner).setPersistenceRequired();
        owner.setHealth(100);
        var warning = spawn(h, new Vec3(5, 2, 5), owner.getUUID(), 10, false);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(owner.getHealth() - 90) < 1.0E-6, "Owner must remain eligible when exclusion is disabled; health=" + owner.getHealth() + ", removed=" + owner.isRemoved() + ", warningRemoved=" + warning.isRemoved());
            h.succeed();
        });
    }

    public static void zeroDamageWarningStillConsumesOnce(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var warning = warning(h, caster, new Vec3(5, 2, 5), 0);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "Zero damage must not affect targets");
            h.assertTrue(warning.isRemoved(), "Zero damage warning must still be consumed");
            h.succeed();
        });
    }

    public static void negativeSavedDamageClampsToZero(GameTestHelper h) {
        assertMalformedDamageClamps(h, -12);
    }

    public static void nanSavedDamageClampsToZero(GameTestHelper h) {
        assertMalformedDamageClamps(h, Double.NaN);
    }

    public static void infiniteSavedDamageClampsToZero(GameTestHelper h) {
        assertMalformedDamageClamps(h, Double.POSITIVE_INFINITY);
    }

    public static void overLimitRemainingTicksClampToTwenty(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var saved = saveWarning(h, caster);
        saved.putInt("RemainingTicks", 1000);
        var restored = restoreWarning(h, saved, new Vec3(12, 2, 12));
        CompoundTag roundTrip = new CompoundTag();
        roundTrip = save(restored);
        h.assertTrue(remainingEndTicks(h, restored) == 20, "Loaded remaining time must clamp to twenty actual END ticks");
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Clamped warning must eventually detonate");
            h.succeed();
        });
    }

    public static void expiredRemainingTicksDetonateWithoutDelay(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var saved = saveWarning(h, caster);
        saved.putInt("RemainingTicks", -8);
        var restored = restoreWarning(h, saved, new Vec3(12, 2, 12));
        h.assertTrue(remainingEndTicks(h, restored) == 1, "A negative saved delay must clamp to one executable END tick");
        h.runAfterDelay(4, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Zero-clamped warning must detonate on its first eligible END tick; health=" + target.getHealth() + ", removed=" + restored.isRemoved());
            h.assertTrue(restored.isRemoved(), "Expired warning must be consumed");
            h.succeed();
        });
    }

    public static void warningFromOtherDimensionIsDiscarded(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var saved = saveWarning(h, caster);
        saved.putString("WarningDimension", "minecraft:the_nether");
        var restored = restoreWarning(h, saved, new Vec3(12, 2, 12));
        h.runAfterDelay(2, () -> {
            h.assertTrue(restored.isRemoved(), "A warning whose saved dimension differs must be discarded");
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "A mismatched-dimension warning must not damage entities");
            h.succeed();
        });
    }

    public static void invalidSavedOriginCancelsWarning(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var saved = saveWarning(h, caster);
        saved.putDouble("WarningOriginX", Double.NaN);
        var restored = restoreWarning(h, saved, new Vec3(12, 2, 12));
        h.runAfterDelay(2, () -> {
            h.assertTrue(restored.isRemoved(), "A non-finite saved origin must cancel the carrier");
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "Invalid origin must not cause damage");
            h.succeed();
        });
    }

    public static void committedNbtDoesNotReplayDamage(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var saved = saveWarning(h, caster);
        saved.putBoolean("WarningCommitted", true);
        var restored = restoreWarning(h, saved, new Vec3(12, 2, 12));
        h.runAfterDelay(2, () -> {
            h.assertTrue(restored.isRemoved(), "Committed carrier must remove itself after reload");
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "Committed NBT must prevent a second damage pulse");
            h.succeed();
        });
    }

    public static void legacyWarningNbtDefaultsToTwentyEndTicks(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var saved = saveWarning(h, caster);
        saved.remove("RemainingTicks");
        var restored = restoreWarning(h, saved, new Vec3(12, 2, 12));
        CompoundTag roundTrip = new CompoundTag();
        roundTrip = save(restored);
        h.assertTrue(remainingEndTicks(h, restored) == 20, "Legacy warning NBT must retain the current twenty END tick default");
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Legacy warning NBT must still detonate");
            h.succeed();
        });
    }

    public static void forecastRejectsTargetInAnotherDimension(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var nether = h.getLevel().getServer().getLevel(net.minecraft.world.level.Level.NETHER);
        var target = EntityType.COW.create(nether, EntitySpawnReason.COMMAND);
        h.assertTrue(target != null, "Nether target fixture must be constructible");
        h.assertTrue(!caster.doHurtTarget(h.getLevel(), target), "Forecast must reject targets from another dimension");
        h.succeed();
    }

    public static void forecastCooldownNbtClampsLoadedRange(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        CompoundTag saved = new CompoundTag();
        saved = save(forecast);
        saved.putInt("ZapCooldown", 90000);
        saved.putInt("DriftCooldown", -5);
        var restored = ModEntities.FORECAST.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        restored.load(input(h, saved));
        CompoundTag roundTrip = new CompoundTag();
        roundTrip = save(restored);
        h.assertTrue(intValue(roundTrip, "ZapCooldown") == 72000, "Loaded attack cooldown must clamp to its maximum");
        h.assertTrue(intValue(roundTrip, "DriftCooldown") == 0, "Loaded drift cooldown must clamp to zero");
        h.succeed();
    }

    private static void assertMalformedDamageClamps(GameTestHelper h, double value) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var saved = saveWarning(h, caster);
        saved.putDouble("ZapDamage", value);
        saved.putBoolean("ZapDamageConfigured", true);
        var restored = restoreWarning(h, saved, new Vec3(12, 2, 12));
        CompoundTag roundTrip = new CompoundTag();
        roundTrip = save(restored);
        h.assertTrue(doubleValue(roundTrip, "ZapDamage") == 0, "Malformed damage must load as zero");
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "Malformed damage must not affect targets");
            h.assertTrue(restored.isRemoved(), "Malformed warning must still consume its task");
            h.succeed();
        });
    }

    private static ForecastEntity forecast(GameTestHelper h, int x, int y, int z) {
        var entity = ModEntities.FORECAST.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        entity.setPos(h.absolutePos(new BlockPos(x, y, z)).getCenter());
        entity.setNoGravity(true);
        entity.setPersistenceRequired();
        h.getLevel().addFreshEntity(entity);
        return entity;
    }

    private static net.minecraft.world.entity.LivingEntity cow(GameTestHelper h, Vec3 position) {
        var entity = EntityType.COW.create(h.getLevel(), EntitySpawnReason.COMMAND);
        entity.setPos(absolutePosition(h, position));
        entity.setNoAi(true);
        entity.setNoGravity(true);
        entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        entity.getAttribute(Attributes.ARMOR).setBaseValue(0);
        ((net.minecraft.world.entity.Mob) entity).setPersistenceRequired();
        entity.setHealth(100);
        h.getLevel().addFreshEntity(entity);
        return entity;
    }

    private static ZapWarningEntity warning(GameTestHelper h, ForecastEntity caster, Vec3 origin, double multiplier) {
        var warning = ModEntities.ZAP.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        warning.snapTo(absolutePosition(h, origin));
        warning.arm(caster, multiplier);
        h.getLevel().addFreshEntity(warning);
        return warning;
    }

    private static ZapWarningEntity spawn(GameTestHelper h, Vec3 origin, java.util.UUID owner, double damage, boolean ignoreOwner) {
        BlockPos position = h.absolutePos(BlockPos.containing(origin));
        var warning = ZapWarningEntity.spawn(h.getLevel(), position, owner, damage, ignoreOwner);
        h.assertTrue(warning != null, "Configured warning spawn must be accepted");
        return warning;
    }


    private static CompoundTag saveWarning(GameTestHelper h, ForecastEntity caster) {
        var warning = warning(h, caster, new Vec3(5, 2, 5), 1);
        int remaining = remainingEndTicks(h, warning);
        CompoundTag saved = save(warning);
        saved.remove("ZapDetonationTask");
        saved.putInt("RemainingTicks", remaining);
        warning.discard();
        return saved;
    }

    private static int remainingEndTicks(GameTestHelper h, ZapWarningEntity warning) {
        CompoundTag tag = save(warning);
        var taskId = tag.getString("ZapDetonationTask").map(java.util.UUID::fromString).orElseThrow();
        var task = h.getLevel().getEntity(taskId);
        return intValue(save(task), "RemainingEndTicks");
    }

    private static ZapWarningEntity restoreWarning(GameTestHelper h, CompoundTag saved, Vec3 position) {
        var warning = ModEntities.ZAP.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        warning.load(input(h, saved));
        warning.setPos(absolutePosition(h, position));
        h.assertTrue(h.getLevel().addFreshEntity(warning), "Restored warning must be accepted by its level");
        return warning;
    }

    private static Vec3 absolutePosition(GameTestHelper h, Vec3 localPosition) {
        BlockPos localBlock = BlockPos.containing(localPosition);
        Vec3 fractionalOffset = localPosition.subtract(localBlock.getX(), localBlock.getY(), localBlock.getZ());
        return h.absolutePos(localBlock).getCenter().add(fractionalOffset.x - 0.5, fractionalOffset.y - 0.5, fractionalOffset.z - 0.5);
    }

    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper h, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag); }
    private static int intValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag)tag.get(key)).intValue(); }
    private static double doubleValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag)tag.get(key)).doubleValue(); }
    private static CompoundTag save(Entity e) { TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, e.registryAccess()); e.saveWithoutId(output); return output.buildResult(); }
}
