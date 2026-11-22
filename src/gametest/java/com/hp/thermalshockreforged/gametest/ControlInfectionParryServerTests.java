package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.gravers.GraverContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public final class ControlInfectionParryServerTests {
    public static void register() {
        TsrGameTests.add("history_control_infection_parry_server_tests_stunned_scales_real_entity_movement_without_damage_or_invulnerability", ControlInfectionParryServerTests::stunnedScalesRealEntityMovementWithoutDamageOrInvulnerability, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_tremors_adds_only_finite_bounded_motion_on_every_axis", ControlInfectionParryServerTests::tremorsAddsOnlyFiniteBoundedMotionOnEveryAxis, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_control_effects_resist_milk_and_honey_but_are_cured_by_totem", ControlInfectionParryServerTests::controlEffectsResistMilkAndHoneyButAreCuredByTotem, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_tagged_attacker_infects_through_the_real_living_hurt_mixin", ControlInfectionParryServerTests::taggedAttackerInfectsThroughTheRealLivingHurtMixin, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_ordinary_attacker_does_not_invent_an_infection", ControlInfectionParryServerTests::ordinaryAttackerDoesNotInventAnInfection, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_graver_patient_retains_its_infection_attack_exception", ControlInfectionParryServerTests::graverPatientRetainsItsInfectionAttackException, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_milk_removal_schedules_one_bounded_task_per_patient_tick", ControlInfectionParryServerTests::milkRemovalSchedulesOneBoundedTaskPerPatientTick, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_purified_patient_suppresses_the_real_infection_removal_task", ControlInfectionParryServerTests::purifiedPatientSuppressesTheRealInfectionRemovalTask, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_npc_infection_recovery_waits_twenty_ticks_then_bursts_once", ControlInfectionParryServerTests::npcInfectionRecoveryWaitsTwentyTicksThenBurstsOnce, 40, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_knights_edge_uses_difficulty_window_and_one_hundred_tick_cooldown", ControlInfectionParryServerTests::knightsEdgeUsesDifficultyWindowAndOneHundredTickCooldown, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_living_parry_consumes_once_charges_the_sword_and_respects_stun_radius", ControlInfectionParryServerTests::livingParryConsumesOnceChargesTheSwordAndRespectsStunRadius, 100, "supernova_server");
        TsrGameTests.add("history_control_infection_parry_server_tests_player_parry_mixin_and_full_resistance_suppress_recoil_without_a_free_sword_charge", ControlInfectionParryServerTests::playerParryMixinAndFullResistanceSuppressRecoilWithoutAFreeSwordCharge, 100, "supernova_server");
    }
    private static final String TASKS = "thermal_shock_reforged:infection_tasks";
    private static final Identifier LOCK = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "infection_movement_lock");

    public static void stunnedScalesRealEntityMovementWithoutDamageOrInvulnerability(GameTestHelper helper) {
        Cow cow = cow(helper, 5, 8);
        Vec3 start = cow.position();
        float health = cow.getHealth();
        helper.assertTrue(StatusContent.STUNNED.value().applyEffectTick(helper.getLevel(), cow, 0), "Stunned must remain active after applying its tick");
        cow.move(MoverType.SELF, new Vec3(1, 0, 1));
        helper.assertTrue(cow.position().subtract(start).distanceTo(new Vec3(.25, 0, .25)) < 1.0E-6 && cow.getHealth() == health && !cow.isInvulnerable(), "Stunned must scale native movement without replacing control with damage or invulnerability");
        helper.succeed();
    }

    public static void tremorsAddsOnlyFiniteBoundedMotionOnEveryAxis(GameTestHelper helper) {
        Cow cow = cow(helper, 5, 8);
        Vec3 start = new Vec3(.3, .2, .5);
        cow.setDeltaMovement(start);
        helper.assertTrue(StatusContent.TREMORS.value().applyEffectTick(helper.getLevel(), cow, 9), "Tremors must remain active after applying its tick");
        Vec3 delta = cow.getDeltaMovement().subtract(start);
        for (double axis : new double[]{delta.x, delta.y, delta.z}) helper.assertTrue(Double.isFinite(axis) && axis >= -.10000001 && axis < .10000001, "Each tremor axis must add a finite original-range random offset");
        helper.succeed();
    }

    public static void controlEffectsResistMilkAndHoneyButAreCuredByTotem(GameTestHelper helper) {
        Cow cow = cow(helper, 5, 8);
        cow.addEffect(new MobEffectInstance(StatusContent.STUNNED, 200, 0));
        cow.addEffect(new MobEffectInstance(StatusContent.TREMORS, 200, 0));
        var milk = Items.MILK_BUCKET.getDefaultInstance();
        milk.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), cow, milk);
        var honey = Items.HONEY_BOTTLE.getDefaultInstance();
        honey.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), cow, honey);
        helper.assertTrue(cow.hasEffect(StatusContent.STUNNED) && cow.hasEffect(StatusContent.TREMORS), "Milk and honey must not cure either control effect");
        DeathProtection.TOTEM_OF_UNDYING.applyEffects(Items.TOTEM_OF_UNDYING.getDefaultInstance(), cow);
        helper.assertTrue(!cow.hasEffect(StatusContent.STUNNED) && !cow.hasEffect(StatusContent.TREMORS), "Totem must cure both control effects");
        helper.succeed();
    }

    public static void taggedAttackerInfectsThroughTheRealLivingHurtMixin(GameTestHelper helper) {
        Cow patient = cow(helper, 5, 8);
        var attacker = GraverContent.ASSIMILATOR.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(patient.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(attacker), 1), "The test's ordinary hit must be accepted");
        var effect = patient.getEffect(StatusContent.INFECTED);
        helper.assertTrue(effect != null && effect.getDuration() == 200 && effect.getAmplifier() == 1, "The tagged damage source must apply the full infection through the real hurt hook");
        helper.succeed();
    }

    public static void ordinaryAttackerDoesNotInventAnInfection(GameTestHelper helper) {
        Cow patient = cow(helper, 5, 8);
        Cow attacker = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(patient.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(attacker), 1) && !patient.hasEffect(StatusContent.INFECTED), "An untagged attacker must cause normal damage without infection");
        helper.succeed();
    }

    public static void graverPatientRetainsItsInfectionAttackException(GameTestHelper helper) {
        var patient = GraverContent.GRAVER.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        var attacker = GraverContent.ASSIMILATOR.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(patient.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(attacker), 1), "The Graver test hit must reach the real damage path");
        helper.assertTrue(!patient.hasEffect(StatusContent.INFECTED), "The original Graver patient exception must survive tagged attacks");
        helper.succeed();
    }

    public static void milkRemovalSchedulesOneBoundedTaskPerPatientTick(GameTestHelper helper) {
        Cow patient = cow(helper, 5, 8);
        for (int attempt = 0; attempt < 2; attempt++) {
            patient.addEffect(new MobEffectInstance(StatusContent.INFECTED, 200, 1));
            var milk = Items.MILK_BUCKET.getDefaultInstance();
            milk.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), patient, milk);
        }
        var state = patient.getPersistentData().getCompoundOrEmpty(TASKS);
        var tasks = state.getListOrEmpty("Tasks");
        helper.assertTrue(!patient.hasEffect(StatusContent.INFECTED) && tasks.size() == 1 && tasks.getCompoundOrEmpty(0).getIntOr("Remaining", 0) == 20 && tasks.getCompoundOrEmpty(0).getBooleanOr("Burst", false) && state.getIntOr("LockRemaining", 0) == 30, "Real milk removal must deduplicate the original delayed NPC recovery task within one server tick");
        helper.assertTrue(patient.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(LOCK) && patient.getAttributeValue(Attributes.MOVEMENT_SPEED) == 0, "NPC movement locking must use the internal attribute state");
        helper.succeed();
    }

    public static void purifiedPatientSuppressesTheRealInfectionRemovalTask(GameTestHelper helper) {
        Cow patient = cow(helper, 5, 8);
        patient.addEffect(new MobEffectInstance(StatusContent.PURIFIED, 200, 0));
        patient.addEffect(new MobEffectInstance(StatusContent.INFECTED, 200, 1));
        patient.removeEffect(StatusContent.INFECTED);
        helper.assertTrue(patient.hasEffect(StatusContent.PURIFIED) && !patient.hasEffect(StatusContent.INFECTED) && !patient.getPersistentData().contains(TASKS) && !patient.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(LOCK), "Purified must suppress re-infection scheduling when it remains active during the removal hook");
        helper.succeed();
    }

    public static void npcInfectionRecoveryWaitsTwentyTicksThenBurstsOnce(GameTestHelper helper) {
        Cow patient = cow(helper, 5, 8);
        Cow nearby = cow(helper, 6, 8);
        Cow outside = cow(helper, 9, 8);
        float nearbyHealth = nearby.getHealth();
        float outsideHealth = outside.getHealth();
        patient.addEffect(new MobEffectInstance(StatusContent.INFECTED, 200, 1));
        patient.removeEffect(StatusContent.INFECTED);
        helper.runAfterDelay(18, () -> helper.assertTrue(!patient.hasEffect(StatusContent.INFECTED) && nearby.getHealth() == nearbyHealth, "Recovery and its burst must not execute early"));
        helper.runAfterDelay(22, () -> {
            helper.assertTrue(patient.hasEffect(StatusContent.INFECTED) && nearby.hasEffect(StatusContent.INFECTED) && nearby.getHealth() == nearbyHealth - 5 && !outside.hasEffect(StatusContent.INFECTED) && outside.getHealth() == outsideHealth, "The finite recovery must re-infect the patient and deal one five-point nearby burst while preserving outside targets");
            helper.assertTrue(patient.getPersistentData().getCompoundOrEmpty(TASKS).getListOrEmpty("Tasks").isEmpty(), "The consumed recovery task must be removed before any later expiry");
            helper.succeed();
        });
    }

    public static void knightsEdgeUsesDifficultyWindowAndOneHundredTickCooldown(GameTestHelper helper) {
        ServerPlayer player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 8);
        var item = WeaponContent.KNIGHTS_EDGE.get();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));
        player.setDeltaMovement(.3, .2, .1);
        item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        int expected = switch (helper.getLevel().getDifficulty()) { case NORMAL -> 18; case HARD -> 12; default -> 25; };
        helper.assertTrue(player.getEffect(StatusContent.PARRYING).getDuration() == expected && player.getCooldowns().isOnCooldown(player.getMainHandItem()) && player.getDeltaMovement().equals(Vec3.ZERO), "Knight's Edge must stop motion and open the original difficulty-dependent parry window");
        player.removeEffect(StatusContent.PARRYING);
        player.setDeltaMovement(.3, .2, .1);
        for (int tick = 0; tick < 99; tick++) player.getCooldowns().tick();
        item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(!player.hasEffect(StatusContent.PARRYING) && player.getCooldowns().isOnCooldown(player.getMainHandItem()) && player.getDeltaMovement().distanceTo(new Vec3(.3, .2, .1)) < 1.0E-8, "Cooldown retries must not reopen parry or stop movement on tick ninety-nine");
        player.getCooldowns().tick();
        item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.hasEffect(StatusContent.PARRYING) && player.getCooldowns().isOnCooldown(player.getMainHandItem()), "The hundredth cooldown tick must permit the next legal parry");
        PlayerInteractionTests.release(helper, player);
        helper.succeed();
    }

    public static void livingParryConsumesOnceChargesTheSwordAndRespectsStunRadius(GameTestHelper helper) {
        Cow patient = cow(helper, 5, 8);
        Cow nearby = cow(helper, 6, 8);
        Cow outside = cow(helper, 9, 8);
        patient.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(WeaponContent.KNIGHTS_EDGE.get()));
        patient.addEffect(new MobEffectInstance(StatusContent.PARRYING, 18, 0));
        patient.setYRot(0);
        patient.setXRot(0);
        patient.setDeltaMovement(Vec3.ZERO);
        patient.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 1);
        helper.assertTrue(!patient.hasEffect(StatusContent.PARRYING) && patient.getEffect(StatusContent.KNIGHTS_EDGE_CHARGE).getDuration() == 40 && patient.getEffect(MobEffects.REGENERATION).getDuration() == 20 && patient.getEffect(MobEffects.REGENERATION).getAmplifier() == 9 && patient.getEffect(MobEffects.SLOWNESS).getAmplifier() == 1, "The real damage path must consume parry and apply the original charged-sword aftermath");
        helper.assertTrue(nearby.getEffect(StatusContent.STUNNED).getDuration() == 20 && nearby.getEffect(StatusContent.STUNNED).getAmplifier() == 9 && !outside.hasEffect(StatusContent.STUNNED) && !patient.hasEffect(StatusContent.STUNNED), "The parry stun must affect only other nearby living entities");
        helper.assertTrue(patient.getDeltaMovement().distanceTo(new Vec3(0, 0, -.6)) < 1.0E-6, "Zero-resistance parry recoil must retain its bounded original magnitude");
        patient.removeEffect(StatusContent.KNIGHTS_EDGE_CHARGE);
        patient.invulnerableTime = 0;
        patient.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 1);
        helper.assertTrue(!patient.hasEffect(StatusContent.KNIGHTS_EDGE_CHARGE), "A second hit without a new parry window must not recreate sword charge");
        helper.succeed();
    }

    public static void playerParryMixinAndFullResistanceSuppressRecoilWithoutAFreeSwordCharge(GameTestHelper helper) {
        ServerPlayer player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 8);
        player.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        player.setDeltaMovement(Vec3.ZERO);
        player.addEffect(new MobEffectInstance(StatusContent.PARRYING, 18, 0));
        player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 1);
        helper.assertTrue(!player.hasEffect(StatusContent.PARRYING) && player.hasEffect(MobEffects.REGENERATION) && !player.hasEffect(StatusContent.KNIGHTS_EDGE_CHARGE) && player.getDeltaMovement().lengthSqr() == 0, "The separate Player hurt hook must consume parry, honor full knockback resistance and avoid charging an absent sword");
        PlayerInteractionTests.release(helper, player);
        helper.succeed();
    }

    private static Cow cow(GameTestHelper helper, int x, int z) {
        Cow cow = helper.spawnWithNoFreeWill(EntityType.COW, x, 3, z);
        cow.setNoGravity(true);
        return cow;
    }

    private ControlInfectionParryServerTests() { }
}






