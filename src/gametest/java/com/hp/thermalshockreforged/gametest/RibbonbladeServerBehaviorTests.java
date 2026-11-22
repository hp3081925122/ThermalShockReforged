package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeActionEntity;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeContent;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeConfig;
import com.hp.thermalshockreforged.entity.ribbonblade.RibbonbladeGalvaniteEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.gametest.framework.GameTestHelper;
public final class RibbonbladeServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_ribbonblade_server_behavior_tests_uncharged_damage_schedules_electrified_after_delay", RibbonbladeServerBehaviorTests::unchargedDamageSchedulesElectrifiedAfterDelay, 40, "supernova_server");
        TsrGameTests.add("history_ribbonblade_server_behavior_tests_electrified_damage_blocks_and_grants_configured_regeneration", RibbonbladeServerBehaviorTests::electrifiedDamageBlocksAndGrantsConfiguredRegeneration, 30, "supernova_server");
        TsrGameTests.add("history_ribbonblade_server_behavior_tests_delayed_strike_hits_the_entity_that_started_the_attack", RibbonbladeServerBehaviorTests::delayedStrikeHitsTheEntityThatStartedTheAttack, 45, "supernova_server");
    }

    public static void unchargedDamageSchedulesElectrifiedAfterDelay(GameTestHelper helper) {
        RibbonbladeGalvaniteEntity blade = helper.spawnWithNoFreeWill(RibbonbladeContent.RIBBONBLADE.get(), 4, 2, 4);
        Cow attacker = addCow(helper, 7, 2, 4);
        helper.assertTrue(blade.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(attacker), 1), "An ordinary damaging hit must reach Ribbonblade");
        helper.assertTrue(!blade.hasEffect(StatusContent.ELECTRIFIED), "An uncharged hit must not apply Electrified synchronously");
        helper.runAfterDelay(8, () -> {
            MobEffectInstance electrified = blade.getEffect(StatusContent.ELECTRIFIED);
            helper.assertTrue(electrified != null && electrified.getAmplifier() == 0 && electrified.getDuration() > 20, "The five-END charge delay must grant 30 ticks of level-zero Electrified");
            helper.succeed();
        });
    }
    public static void electrifiedDamageBlocksAndGrantsConfiguredRegeneration(GameTestHelper helper) {
        RibbonbladeGalvaniteEntity blade = helper.spawnWithNoFreeWill(RibbonbladeContent.RIBBONBLADE.get(), 4, 2, 4);
        Cow attacker = addCow(helper, 7, 2, 4);
        blade.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 100, 0, false, true));
        float healthBefore = blade.getHealth();
        helper.assertTrue(blade.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(attacker), 1), "The incoming hit event must be accepted for the block reaction");
        MobEffectInstance regeneration = blade.getEffect(MobEffects.REGENERATION);
        var saved = TsrGameTests.roundTrip(helper, blade);
        helper.assertTrue(regeneration != null && regeneration.getAmplifier() == 5 && regeneration.getDuration() == 40, "An Electrified hit must grant the original high-amplifier regeneration");
        helper.assertTrue(saved.getIntOr("RibbonCooldown", 0) == 40 && saved.getBooleanOr("RibbonCooldownVisible", false), "A successful block reaction must start the visible configured cooldown");
        helper.assertTrue(blade.getHealth() < healthBefore, "The block effect must not silently replace normal accepted damage");
        helper.succeed();
    }
    public static void delayedStrikeHitsTheEntityThatStartedTheAttack(GameTestHelper helper) {
        helper.getLevel().setBlock(helper.absolutePos(new BlockPos(1, 1, 2)), Blocks.STONE.defaultBlockState(), 3);
        helper.getLevel().setBlock(helper.absolutePos(new BlockPos(1, 1, 4)), Blocks.STONE.defaultBlockState(), 3);
        RibbonbladeGalvaniteEntity blade = helper.spawnWithNoFreeWill(RibbonbladeContent.RIBBONBLADE.get(), 1, 2, 0);
        Cow target = addCow(helper, 1, 2, 2);
        target.setYRot(0);
        target.setXRot(0);
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);
        float healthBefore = target.getHealth();
        blade.beginAttack(target);
        var saved = TsrGameTests.roundTrip(helper, blade);
        helper.assertTrue(saved.getIntOr("RibbonCooldown", 0) == 20, "A committed strike must start the configured cooldown");
        helper.assertTrue(countTasks(helper, blade) == 1, "A committed strike must create one finite action task");
        helper.runAfterDelay(20, () -> {
            double expectedDamage = blade.getAttributeValue(Attributes.ATTACK_DAMAGE) * RibbonbladeConfig.MULTIPLIER.get() - target.getArmorValue() * .4;
            double actualDamage = healthBefore - target.getHealth();
            helper.assertTrue(Math.abs(actualDamage - expectedDamage) < 1.0E-3, "The delayed strike must hit the chosen target for base attack damage times the configured multiplier");
            helper.assertTrue(countTasks(helper, blade) == 0, "The completed strike task must remove itself");
            helper.succeed();
        });
    }

    private static Cow addCow(GameTestHelper helper, int x, int y, int z) {
        Cow cow = EntityType.COW.create(helper.getLevel(), net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (cow == null) throw new IllegalStateException("Cow entity type returned null");
        cow.setNoAi(true);
        cow.snapTo(helper.absolutePos(new BlockPos(x, y, z)), 0, 0);
        if (!helper.getLevel().addFreshEntity(cow)) throw new IllegalStateException("GameTest server rejected cow entity");
        return cow;
    }

    private static int countTasks(GameTestHelper helper, RibbonbladeGalvaniteEntity blade) {
        return helper.getLevel().getEntitiesOfClass(RibbonbladeActionEntity.class, new AABB(blade.blockPosition()).inflate(16)).size();
    }
}
