package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
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

public final class ForecastFixtureAuditServerTests {
    private static final String TEMPLATE = "forecast_test";

    public static void warningFiltersItemsButKeepsOrdinaryEntities(GameTestHelper h) {
        var caster = forecast(h, 3, 2, 3);
        var friend = cow(h, new Vec3(5, 2, 5));
        friend.setHealth(100);
        Vec3 itemOrigin = absolutePosition(h, new Vec3(5, 2, 5));
        var item = new ItemEntity(h.getLevel(), itemOrigin.x, itemOrigin.y, itemOrigin.z, new ItemStack(Items.DIAMOND));
        item.setNoGravity(true);
        item.setDeltaMovement(Vec3.ZERO);
        h.getLevel().addFreshEntity(item);
        var warning = zap(h, caster, new Vec3(5, 2, 5), 1);
        h.assertTrue(item.position().distanceToSqr(warning.position()) < 1.0E-6, "The item filter fixture must start at the actual warning origin");
        h.runAfterDelay(24, () -> {
            h.assertTrue(Math.abs(friend.getHealth() - 90) < 1.0E-6, "Ordinary friendly entities must remain eligible for the unfiltered lightning area");
            h.assertTrue(!item.isRemoved() && item.getItem().getCount() == 1, "ItemEntity must be excluded from warning damage");
            h.succeed();
        });
    }

    public static void internalSlowHasTwoPulsesAndFortyTickPhases(GameTestHelper h) {
        var entity = ModEntities.FORECAST.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        entity.setPos(absolutePosition(h, new Vec3(8, 4, 8)));
        entity.setNoGravity(true);
        entity.setNoAi(true);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.tick();
        entity.endTick(h.getLevel());
        var first = com.hp.thermalshockreforged.gametest.TsrGameTests.roundTrip(h, entity);
        Vec3 firstVelocity = entity.getDeltaMovement();
        h.assertTrue(first.getIntOr("DriftCooldown", -1) == 0 && first.getBooleanOr("DriftSlowPending", false), "The first controlled server entity tick must commit one pulse with slow pending");
        h.assertTrue(firstVelocity.lengthSqr() > 1.0E-10, "The first pulse must create actual motion");
        entity.setDeltaMovement(Vec3.ZERO);
        entity.tick();
        entity.endTick(h.getLevel());
        var second = com.hp.thermalshockreforged.gametest.TsrGameTests.roundTrip(h, entity);
        Vec3 secondVelocity = entity.getDeltaMovement();
        h.assertTrue(second.getIntOr("DriftCooldown", -1) == 40 && second.getBooleanOr("DriftSlowPending", false), "The second tick must commit the second pulse and start forty slow ticks");
        h.assertTrue(secondVelocity.lengthSqr() > 1.0E-10 && firstVelocity.distanceToSqr(secondVelocity) > 1.0E-14, "The second pulse must create a second numeric motion value after velocity was cleared");
        entity.setDeltaMovement(Vec3.ZERO);
        entity.tick();
        entity.endTick(h.getLevel());
        var third = com.hp.thermalshockreforged.gametest.TsrGameTests.roundTrip(h, entity);
        h.assertTrue(third.getIntOr("DriftCooldown", -1) == 40 && !third.getBooleanOr("DriftSlowPending", true), "The third tick must refresh the slow phase without a third pulse");
        h.assertTrue(entity.getDeltaMovement().lengthSqr() < 1.0E-12, "The third tick must not create new random motion");
        for (int tick = 0; tick < 39; tick++) {
            entity.setDeltaMovement(Vec3.ZERO);
            entity.tick();
            entity.endTick(h.getLevel());
            h.assertTrue(entity.getDeltaMovement().lengthSqr() < 1.0E-12, "No pulse may occur during the remaining thirty-nine slow ticks");
        }
        var lastSlow = com.hp.thermalshockreforged.gametest.TsrGameTests.roundTrip(h, entity);
        h.assertTrue(lastSlow.getIntOr("DriftCooldown", -1) == 1 && !lastSlow.getBooleanOr("DriftSlowPending", true), "The slow phase must still hold one tick before expiry");
        h.assertTrue(Math.abs(entity.getAttributeValue(Attributes.MOVEMENT_SPEED) - .165) < 1.0E-6, "The slow phase must retain the original speed factor");
        entity.setDeltaMovement(Vec3.ZERO);
        entity.tick();
        entity.endTick(h.getLevel());
        var expiry = com.hp.thermalshockreforged.gametest.TsrGameTests.roundTrip(h, entity);
        h.assertTrue(expiry.getIntOr("DriftCooldown", -1) == 0 && expiry.getBooleanOr("DriftSlowPending", false), "The next pulse must start exactly when forty slow ticks expire");
        h.assertTrue(entity.getDeltaMovement().lengthSqr() > 1.0E-10, "The expiry pulse must create actual motion");
        entity.discard();
        h.succeed();
    }

    private static ForecastEntity forecast(GameTestHelper h, int x, int y, int z) {
        var entity = ModEntities.FORECAST.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        entity.setPos(h.absolutePos(new BlockPos(x, y, z)).getCenter());
        entity.setNoGravity(true);
        h.getLevel().addFreshEntity(entity);
        return entity;
    }

    private static net.minecraft.world.entity.LivingEntity cow(GameTestHelper h, Vec3 position) {
        var entity = EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        entity.setPos(absolutePosition(h, position));
        entity.setNoAi(true);
        entity.setNoGravity(true);
        entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        entity.getAttribute(Attributes.ARMOR).setBaseValue(0);
        entity.setHealth(100);
        h.getLevel().addFreshEntity(entity);
        return entity;
    }

    private static ZapWarningEntity zap(GameTestHelper h, ForecastEntity caster, Vec3 origin, double multiplier) {
        var warning = ModEntities.ZAP.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
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

    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_forecast_fixture_audit_server_tests_warning_filters_items_but_keeps_ordinary_entities", ForecastFixtureAuditServerTests::warningFiltersItemsButKeepsOrdinaryEntities, 40, "forecast_test");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_forecast_fixture_audit_server_tests_internal_slow_has_two_pulses_and_forty_tick_phases", ForecastFixtureAuditServerTests::internalSlowHasTwoPulsesAndFortyTickPhases, 20, "forecast_test");
    }
}
