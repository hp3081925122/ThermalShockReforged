package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public final class ForecastServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_forecast_server_behavior_tests_real_slowness_is_not_multiplied_by_internal_drift_slow_twice", ForecastServerBehaviorTests::realSlownessIsNotMultipliedByInternalDriftSlowTwice, 50, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_internal_slow_has_two_pulses_and_forty_tick_phases", ForecastServerBehaviorTests::internalSlowHasTwoPulsesAndFortyTickPhases, 100, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_external_discard_cancels_warning_carrier", ForecastServerBehaviorTests::externalDiscardCancelsWarningCarrier, 30, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_warning_nbt_keeps_remaining_time_and_single_consumption", ForecastServerBehaviorTests::warningNbtKeepsRemainingTimeAndSingleConsumption, 40, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_warning_filters_items_but_keeps_ordinary_entities", ForecastServerBehaviorTests::warningFiltersItemsButKeepsOrdinaryEntities, 40, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_warning_uses_saved_origin_after_carrier_moves", ForecastServerBehaviorTests::warningUsesSavedOriginAfterCarrierMoves, 40, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_warning_damage_is_consumed_exactly_once", ForecastServerBehaviorTests::warningDamageIsConsumedExactlyOnce, 40, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_warning_expires_on_its_twentieth_end_tick", ForecastServerBehaviorTests::warningExpiresOnItsTwentiethEndTick, 40, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_experience_drops_after_twenty_real_death_ticks", ForecastServerBehaviorTests::experienceDropsAfterTwentyRealDeathTicks, 30, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_forecast_state_survives_nbt_round_trip", ForecastServerBehaviorTests::forecastStateSurvivesNbtRoundTrip, 100, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_no_ai_still_advances_internal_cooldown_and_drift_state", ForecastServerBehaviorTests::noAiStillAdvancesInternalCooldownAndDriftState, 100, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_drift_velocity_stays_inside_source_ranges", ForecastServerBehaviorTests::driftVelocityStaysInsideSourceRanges, 140, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_lightning_damage_does_not_reenter_attack_bridge", ForecastServerBehaviorTests::lightningDamageDoesNotReenterAttackBridge, 100, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_attack_attempt_bridge_starts_zap_and_cancels_melee_damage", ForecastServerBehaviorTests::attackAttemptBridgeStartsZapAndCancelsMeleeDamage, 50, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_real_attack_entry_rejects_invalid_targets", ForecastServerBehaviorTests::realAttackEntryRejectsInvalidTargets, 100, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_real_attack_entry_honors_ninety_tick_cooldown", ForecastServerBehaviorTests::realAttackEntryHonorsNinetyTickCooldown, 120, "supernova_server");
        TsrGameTests.add("history_forecast_server_behavior_tests_finalize_moves_up_exactly_once", ForecastServerBehaviorTests::finalizeMovesUpExactlyOnce, 100, "supernova_server");
    }
    private static final String TEMPLATE = "forecast_test";

    public static void finalizeMovesUpExactlyOnce(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        double y = forecast.getY();
        forecast.finalizeSpawn(h.getLevel(), h.getLevel().getCurrentDifficultyAt(forecast.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
        double once = forecast.getY();
        forecast.finalizeSpawn(h.getLevel(), h.getLevel().getCurrentDifficultyAt(forecast.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
        h.assertTrue(Math.abs(once - y - 1) < 1.0E-6 && Math.abs(forecast.getY() - once) < 1.0E-6, "Repeated finalizeSpawn must apply the one-block lift once");
        h.succeed();
    }

    public static void realAttackEntryHonorsNinetyTickCooldown(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        var first = cow(h, new Vec3(5, 2, 5));
        h.assertTrue(forecast.doHurtTarget(h.getLevel(), first), "The first real attack entry must commit");
        h.runAfterDelay(89, () -> {
            h.assertTrue(!forecast.doHurtTarget(h.getLevel(), first), "The attack entry must remain on cooldown before 90 world ticks");
            h.runAfterDelay(3, () -> {
                int remaining = intValue(save(forecast), "ZapCooldown");
                boolean committed = forecast.doHurtTarget(h.getLevel(), first);
                h.assertTrue(committed, "The attack entry must become available at the 90 tick boundary; remaining=" + remaining + ", casterAlive=" + forecast.isAlive() + ", targetAlive=" + first.isAlive() + ", sameLevel=" + (first.level() == h.getLevel()));
                h.succeed();
            });
        });
    }

    public static void realAttackEntryRejectsInvalidTargets(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        var removed = cow(h, new Vec3(5, 2, 5));
        removed.discard();
        h.assertTrue(!forecast.doHurtTarget(h.getLevel(), forecast), "Forecast must reject attacking itself");
        h.assertTrue(!forecast.doHurtTarget(h.getLevel(), removed), "Forecast must reject a removed target");
        h.succeed();
    }

    public static void attackAttemptBridgeStartsZapAndCancelsMeleeDamage(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        double health = target.getHealth();
        DamageSource source = h.getLevel().damageSources().mobAttack(forecast);
        target.hurtServer(h.getLevel(), source, 10);
        h.assertTrue(Math.abs(target.getHealth() - health) < 1.0E-6, "The attack-attempt bridge must cancel the direct melee damage");
        h.assertTrue(zaps(h, forecast) == 1, "The actual bridge must add one warning entity");
        h.runAfterDelay(22, () -> {
            h.assertTrue(Math.abs(target.getHealth() - health + 10) < 1.0E-6, "The actual bridge must start one configured Zap warning");
            h.succeed();
        });
    }

    public static void lightningDamageDoesNotReenterAttackBridge(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        h.assertTrue(target.hurtServer(h.getLevel(), h.getLevel().damageSources().lightningBolt(), 1), "Ordinary targets must receive lightning damage");
        h.assertTrue(!forecast.hurtServer(h.getLevel(), h.getLevel().damageSources().lightningBolt(), 1), "Forecast must retain its lightning immunity");
        h.assertTrue(zaps(h, forecast) == 0, "Lightning damage must not enter Forecast's melee attack bridge; zaps=" + zaps(h, forecast));
        h.succeed();
    }

    public static void driftVelocityStaysInsideSourceRanges(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        forecast.setNoAi(true);
        h.runAfterDelay(130, () -> {
            Vec3 velocity = forecast.getDeltaMovement();
            h.assertTrue(velocity.x >= -1 && velocity.x <= 1 && velocity.z >= -1 && velocity.z <= 1, "Horizontal random drift components must stay within [-1, 1]");
            h.assertTrue(velocity.y >= -.2 && velocity.y <= .2, "Vertical random drift component must stay within [-0.2, 0.2]");
            h.succeed();
        });
    }

    public static void noAiStillAdvancesInternalCooldownAndDriftState(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        forecast.setNoAi(true);
        h.assertTrue(forecast.doHurtTarget(h.getLevel(), target), "NoAI must not block an explicit skill entry");
        h.runAfterDelay(92, () -> {
            int remaining = intValue(save(forecast), "ZapCooldown");
            boolean committed = forecast.doHurtTarget(h.getLevel(), target);
            h.assertTrue(committed, "NoAI must not freeze the 90 world tick cooldown; remaining=" + remaining + ", casterAlive=" + forecast.isAlive() + ", targetAlive=" + target.isAlive() + ", sameLevel=" + (target.level() == h.getLevel()));
            h.succeed();
        });
    }

    public static void forecastStateSurvivesNbtRoundTrip(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        forecast.doHurtTarget(h.getLevel(), cow(h, new Vec3(5, 2, 5)));
        CompoundTag saved = new CompoundTag();
        saved = save(forecast);
        var restored = ModEntities.FORECAST.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        restored.load(input(h, saved));
        CompoundTag savedAgain = new CompoundTag();
        savedAgain = save(restored);
        for (String key : List.of("Texture", "ZapCooldown", "DriftCooldown", "DriftSlowPending", "ForecastSpawnInitialized", "ForecastExperiencePending")) {
            h.assertTrue(saved.contains(key) && saved.get(key).equals(savedAgain.get(key)), "Forecast NBT field must survive reload: " + key);
        }
        h.succeed();
    }

    public static void experienceDropsAfterTwentyRealDeathTicks(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(forecast.position());
        h.assertTrue(forecast.hurtServer(h.getLevel(), h.getLevel().damageSources().playerAttack(player), 1000), "Player damage must start Forecast death");
        h.runAfterDelay(19, () -> {
            h.assertTrue(orbs(h) == 0, "Forecast must not release XP before death tick 20");
            h.runAfterDelay(1, () -> {
            h.assertTrue(orbs(h) > 0, "Forecast must release XP on death tick 20; orbCount=" + orbs(h));
                h.succeed();
            });
        });
    }

    public static void warningExpiresOnItsTwentiethEndTick(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var warning = zap(h, caster, new Vec3(5, 2, 5), 1);
        h.runAfterDelay(19, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "Warning must not detonate before its final loaded world tick");
            h.runAfterDelay(2, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Warning must detonate after twenty actual END ticks");
                h.succeed();
            });
        });
    }

    public static void warningDamageIsConsumedExactlyOnce(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var warning = zap(h, caster, new Vec3(5, 2, 5), 1);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Committed warning must deal one damage pulse");
            h.assertTrue(warning.isRemoved(), "Warning must remove itself after committing damage");
            h.succeed();
        });
    }

    public static void warningUsesSavedOriginAfterCarrierMoves(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        Vec3 origin = new Vec3(5, 2, 5);
        var atOrigin = cow(h, origin);
        var atCarrier = cow(h, new Vec3(9, 2, 9));
        atOrigin.setHealth(100);
        atCarrier.setHealth(100);
        var warning = zap(h, caster, origin, 1);
        warning.setPos(absolutePosition(h, new Vec3(9, 2, 9)));
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(atOrigin.getHealth() - 90) < 1.0E-6, "Damage area must remain at the saved origin");
            h.assertTrue(Math.abs(atCarrier.getHealth() - 100) < 1.0E-6, "Moving the carrier must not move the damage area");
            h.succeed();
        });
    }

    public static void warningFiltersItemsButKeepsOrdinaryEntities(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var friend = cow(h, new Vec3(5, 2, 5));
        friend.setHealth(100);
        Vec3 itemOrigin = absolutePosition(h, new Vec3(5, 2, 5));
        var item = new ItemEntity(h.getLevel(), itemOrigin.x, itemOrigin.y, itemOrigin.z, new ItemStack(Items.DIAMOND));
        item.setNoGravity(true);
        item.setDeltaMovement(Vec3.ZERO);
        item.setNeverPickUp();
        h.assertTrue(h.getLevel().addFreshEntity(item) && h.getLevel().getEntity(item.getUUID()) == item, "The item exclusion target must enter the actual detonation area");
        var warning = zap(h, caster, new Vec3(5, 2, 5), 1);
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(friend.getHealth() - 90) < 1.0E-6, "Ordinary friendly entities must remain eligible for the unfiltered lightning area");
            h.assertTrue(!item.isRemoved() && item.getItem().getCount() == 1 && item.position().distanceToSqr(itemOrigin) < .01, "ItemEntity inside the real damage area must be excluded: removed=" + item.isRemoved() + ", reason=" + item.getRemovalReason() + ", stack=" + item.getItem() + ", position=" + item.position() + ", origin=" + itemOrigin);
            h.succeed();
        });
    }

    public static void warningNbtKeepsRemainingTimeAndSingleConsumption(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var warning = zap(h, caster, new Vec3(5, 2, 5), 1);
        CompoundTag saved = resumableSave(h, warning);
        h.assertTrue(intValue(saved, "RemainingTicks") == 20, "A newly armed warning must retain twenty actual END ticks in its resumable carrier snapshot");
        warning.discard();
        h.runAfterDelay(1, () -> {
            var restored = ModEntities.ZAP.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
            restored.load(input(h, saved));
            h.assertTrue(restored.getUUID().equals(warning.getUUID()), "The restored carrier must retain its saved UUID");
            restored.setPos(absolutePosition(h, new Vec3(12, 2, 12)));
            boolean inserted = h.getLevel().addFreshEntity(restored);
            var indexed = h.getLevel().getEntity(restored.getUUID());
            h.assertTrue(inserted && indexed == restored, "Reloaded warning must be the unique world entity for its saved UUID: inserted=" + inserted + ", position=" + restored.position() + ", indexed=" + indexed + ", removed=" + restored.isRemoved());
            var pending = h.getLevel().getEntitiesOfClass(ZapDetonationEntity.class, target.getBoundingBox().inflate(2), task -> !task.isRemoved() && save(task).getStringOr("Warning", "").equals(restored.getUUID().toString()));
            h.assertTrue(pending.size() == 1, "The restored carrier must own exactly one live detonation task");
            h.runAfterDelay(23, () -> {
                h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6, "Reloaded warning must consume its stored remaining time and damage once");
                h.assertTrue(restored.isRemoved(), "Reloaded warning must be removed after one commit");
                h.runAfterDelay(4, () -> {
                    h.assertTrue(Math.abs(target.getHealth() - 90) < 1.0E-6 && restored.isRemoved(), "Consumed warning must not replay its damage after removal");
                    h.succeed();
                });
            });
        });
    }

    public static void externalDiscardCancelsWarningCarrier(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var target = cow(h, new Vec3(5, 2, 5));
        target.setHealth(100);
        var warning = zap(h, caster, new Vec3(5, 2, 5), 1);
        warning.discard();
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(target.getHealth() - 100) < 1.0E-6, "External carrier removal must cancel an uncommitted warning");
            h.succeed();
        });
    }

    public static void internalSlowHasTwoPulsesAndFortyTickPhases(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        forecast.setNoAi(true);
        List<Vec3> velocities = new ArrayList<>();
        h.runAfterDelay(1, () -> velocities.add(forecast.getDeltaMovement()));
        h.runAfterDelay(2, () -> velocities.add(forecast.getDeltaMovement()));
        h.runAfterDelay(39, () -> velocities.add(forecast.getDeltaMovement()));
        h.runAfterDelay(40, () -> velocities.add(forecast.getDeltaMovement()));
        h.runAfterDelay(42, () -> {
            h.assertTrue(velocities.size() == 4 && velocities.get(0) != velocities.get(1), "Adjacent server ticks must retain the two initial random pulses");
            h.assertTrue(Math.abs(forecast.getAttributeValue(Attributes.MOVEMENT_SPEED) - .165) < .01, "Internal drift slow must apply its intended three-level speed factor; speed=" + forecast.getAttributeValue(Attributes.MOVEMENT_SPEED));
            h.succeed();
        });
    }

    public static void realSlownessIsNotMultipliedByInternalDriftSlowTwice(GameTestHelper h) {
        var forecast = forecast(h, 3, 2, 3);
        forecast.setNoAi(true);
        h.runAfterDelay(3, () -> {
            forecast.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, 200, 0, false, true));
            h.runAfterDelay(5, () -> {
                h.assertTrue(Math.abs(forecast.getAttributeValue(Attributes.MOVEMENT_SPEED) - .165) < .015, "External slowness and internal drift state must combine without double application; speed=" + forecast.getAttributeValue(Attributes.MOVEMENT_SPEED));
                h.succeed();
            });
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
        entity.setPersistenceRequired();
        entity.setHealth(100);
        h.getLevel().addFreshEntity(entity);
        return entity;
    }

    private static ZapWarningEntity zap(GameTestHelper h, ForecastEntity caster, Vec3 origin, double multiplier) {
        var warning = ModEntities.ZAP.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        warning.snapTo(absolutePosition(h, origin));
        warning.arm(caster, multiplier);
        h.getLevel().addFreshEntity(warning);
        return warning;
    }

    private static Vec3 absolutePosition(GameTestHelper h, Vec3 localPosition) {
        BlockPos localBlock = BlockPos.containing(localPosition);
        Vec3 fractionalOffset = localPosition.subtract(localBlock.getX(), localBlock.getY(), localBlock.getZ());
        return h.absolutePos(localBlock).getCenter().add(fractionalOffset.x - 0.5, fractionalOffset.y - 0.5, fractionalOffset.z - 0.5);
    }

    private static int zaps(GameTestHelper h, Entity owner) {
        return h.getLevel().getEntitiesOfClass(ZapWarningEntity.class, owner.getBoundingBox().inflate(3)).size();
    }

    private static int orbs(GameTestHelper h) {
        return h.getLevel().getEntitiesOfClass(ExperienceOrb.class, new net.minecraft.world.phys.AABB(h.absolutePos(BlockPos.ZERO).getCenter(), h.absolutePos(new BlockPos(20, 8, 16)).getCenter())).size();
    }

    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper h, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag); }
    private static int intValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag)tag.get(key)).intValue(); }
    private static CompoundTag resumableSave(GameTestHelper h, ZapWarningEntity warning) {
        int remaining = remainingEndTicks(h, warning);
        CompoundTag saved = save(warning);
        saved.remove("ZapDetonationTask");
        saved.putInt("RemainingTicks", remaining);
        return saved;
    }
    private static int remainingEndTicks(GameTestHelper h, ZapWarningEntity warning) {
        CompoundTag tag = save(warning);
        var taskId = tag.getString("ZapDetonationTask").map(java.util.UUID::fromString).orElseThrow();
        var task = h.getLevel().getEntity(taskId);
        return intValue(save(task), "RemainingEndTicks");
    }
    private static CompoundTag save(Entity e) { TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, e.registryAccess()); e.saveWithoutId(output); return output.buildResult(); }
}
