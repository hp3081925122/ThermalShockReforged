package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.ModEffects;
import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorContent;
import com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatContent;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DeathProtection;

public final class StatusSafetyServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_status_safety_server_behavior_tests_frostbite_cancels_healing_only_while_active", StatusSafetyServerBehaviorTests::frostbiteCancelsHealingOnlyWhileActive, 100, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_cocoa_warmth_continuously_removes_reapplied_frostbite", StatusSafetyServerBehaviorTests::cocoaWarmthContinuouslyRemovesReappliedFrostbite, 15, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_antitoxin_and_purified_cure_poison_without_removing_other_debuffs", StatusSafetyServerBehaviorTests::antitoxinAndPurifiedCurePoisonWithoutRemovingOtherDebuffs, 15, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_cure_sources_retain_the_original_milk_and_totem_rules", StatusSafetyServerBehaviorTests::cureSourcesRetainTheOriginalMilkAndTotemRules, 100, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_electrified_relay_halves_damage_without_returning_to_ancestors", StatusSafetyServerBehaviorTests::electrifiedRelayHalvesDamageWithoutReturningToAncestors, 100, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_electrified_triangle_deduplicates_hits_and_clears_its_propagation_context", StatusSafetyServerBehaviorTests::electrifiedTriangleDeduplicatesHitsAndClearsItsPropagationContext, 100, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_heart_boost_attribute_returns_to_baseline_on_removal_and_expiry", StatusSafetyServerBehaviorTests::heartBoostAttributeReturnsToBaselineOnRemovalAndExpiry, 15, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_antidote_and_warmth_keep_their_tick_behavior_after_entity_nbt_reload", StatusSafetyServerBehaviorTests::antidoteAndWarmthKeepTheirTickBehaviorAfterEntityNbtReload, 15, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_cryo_cooldown_is_only_cured_by_totem", StatusSafetyServerBehaviorTests::cryoCooldownIsOnlyCuredByTotem, 100, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_propulsion_boots_cooldown_resists_all_cure_sources", StatusSafetyServerBehaviorTests::propulsionBootsCooldownResistsAllCureSources, 100, "supernova_server");
        TsrGameTests.add("history_status_safety_server_behavior_tests_honey_cures_infection_and_damaging_aura", StatusSafetyServerBehaviorTests::honeyCuresInfectionAndDamagingAura, 100, "supernova_server");
    }
    public static void frostbiteCancelsHealingOnlyWhileActive(GameTestHelper helper) {
        Cow target = helper.spawnWithNoFreeWill(EntityType.COW, 5, 3, 8);
        target.setHealth(5);
        target.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 100));
        target.heal(2);
        helper.assertTrue(target.getHealth() == 5, "Frostbite must cancel native healing");
        target.removeEffect(StatusContent.FROSTBITE);
        target.heal(2);
        helper.assertTrue(target.getHealth() == 7, "Healing must resume after Frostbite is removed");
        helper.succeed();
    }

    public static void cocoaWarmthContinuouslyRemovesReappliedFrostbite(GameTestHelper helper) {
        Cow target = helper.spawnWithNoFreeWill(EntityType.COW, 5, 3, 8);
        target.setNoGravity(true);
        target.addEffect(new MobEffectInstance(StatusContent.COCOA_EFFECT, 50, 1));
        target.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 100));
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(!target.hasEffect(StatusContent.FROSTBITE) && target.hasEffect(StatusContent.COCOA_EFFECT), "Warmth must cure Frostbite during native entity ticking");
            target.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 100));
        });
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(!target.hasEffect(StatusContent.FROSTBITE) && target.hasEffect(StatusContent.COCOA_EFFECT), "Warmth must also cure a newly reapplied Frostbite");
            helper.succeed();
        });
    }

    public static void antitoxinAndPurifiedCurePoisonWithoutRemovingOtherDebuffs(GameTestHelper helper) {
        Cow antitoxin = helper.spawnWithNoFreeWill(EntityType.COW, 5, 3, 8);
        Cow purified = helper.spawnWithNoFreeWill(EntityType.COW, 9, 3, 8);
        antitoxin.setNoGravity(true);
        purified.setNoGravity(true);
        antitoxin.addEffect(new MobEffectInstance(StatusContent.ANTITOXIN_EFFECT, 100));
        purified.addEffect(new MobEffectInstance(StatusContent.PURIFIED, 100));
        for (Cow cow : new Cow[]{antitoxin, purified}) {
            cow.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
            cow.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100));
        }
        helper.runAfterDelay(3, () -> {
            for (Cow cow : new Cow[]{antitoxin, purified}) helper.assertTrue(!cow.hasEffect(MobEffects.POISON) && cow.hasEffect(MobEffects.SLOWNESS), "Both antidote effects must remove poison while preserving unrelated debuffs");
            helper.succeed();
        });
    }

    public static void cureSourcesRetainTheOriginalMilkAndTotemRules(GameTestHelper helper) {
        Cow target = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        target.addEffect(new MobEffectInstance(StatusContent.PARRYING, 100));
        target.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 100));
        target.addEffect(new MobEffectInstance(StatusContent.COCOA_EFFECT, 100));
        target.addEffect(new MobEffectInstance(StatusContent.ANTITOXIN_EFFECT, 100));
        target.addEffect(new MobEffectInstance(StatusContent.PURIFIED, 100));
        target.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 100));
        target.addEffect(new MobEffectInstance(StatusContent.KNIGHTS_EDGE_CHARGE, 100));
        var honey = Items.HONEY_BOTTLE.getDefaultInstance();
        honey.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), target, honey);
        helper.assertTrue(target.getActiveEffects().size() == 7, "Honey must not silently cure these original custom effects");
        var milk = Items.MILK_BUCKET.getDefaultInstance();
        milk.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), target, milk);
        helper.assertTrue(target.getActiveEffects().size() == 2 && target.hasEffect(StatusContent.FROSTBITE) && target.hasEffect(StatusContent.PARRYING), "Milk must remove only the five originally milk-curable effects");
        DeathProtection.TOTEM_OF_UNDYING.applyEffects(Items.TOTEM_OF_UNDYING.getDefaultInstance(), target);
        helper.assertTrue(!target.hasEffect(StatusContent.FROSTBITE) && !target.hasEffect(StatusContent.PARRYING), "Totem protection must cure the remaining original effects");
        helper.succeed();
    }


    public static void cryoCooldownIsOnlyCuredByTotem(GameTestHelper helper) {
        Cow cryo = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        cryo.addEffect(new MobEffectInstance(RemainingCombatContent.CRYO_VISUAL, 100));
        var milk = Items.MILK_BUCKET.getDefaultInstance();
        milk.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), cryo, milk);
        var honey = Items.HONEY_BOTTLE.getDefaultInstance();
        honey.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), cryo, honey);
        helper.assertTrue(cryo.hasEffect(RemainingCombatContent.CRYO_VISUAL), "Milk and honey must not cure the original cryo cooldown");
        DeathProtection.TOTEM_OF_UNDYING.applyEffects(Items.TOTEM_OF_UNDYING.getDefaultInstance(), cryo);
        helper.assertTrue(!cryo.hasEffect(RemainingCombatContent.CRYO_VISUAL), "Totem protection must cure the original cryo cooldown");
        helper.succeed();
    }

    public static void propulsionBootsCooldownResistsAllCureSources(GameTestHelper helper) {
        Cow propulsion = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        propulsion.addEffect(new MobEffectInstance(FabricatedArmorContent.COOLDOWN_VISUAL, 100));
        var milk = Items.MILK_BUCKET.getDefaultInstance();
        milk.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), propulsion, milk);
        var honey = Items.HONEY_BOTTLE.getDefaultInstance();
        honey.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), propulsion, honey);
        DeathProtection.TOTEM_OF_UNDYING.applyEffects(Items.TOTEM_OF_UNDYING.getDefaultInstance(), propulsion);
        helper.assertTrue(propulsion.hasEffect(FabricatedArmorContent.COOLDOWN_VISUAL), "Milk, honey, and totem must not clear the propulsion boots cooldown visual");
        helper.succeed();
    }

    public static void honeyCuresInfectionAndDamagingAura(GameTestHelper helper) {
        Cow infected = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        infected.addEffect(new MobEffectInstance(StatusContent.INFECTED, 100, 1));
        infected.addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 100, 2));
        var honey = Items.HONEY_BOTTLE.getDefaultInstance();
        honey.get(DataComponents.CONSUMABLE).onConsume(helper.getLevel(), infected, honey);
        helper.assertTrue(!infected.hasEffect(StatusContent.INFECTED) && !infected.hasEffect(AuraContent.DAMAGING_AURA), "Honey must cure both original infection and damaging aura effects");
        helper.succeed();
    }

    public static void electrifiedRelayHalvesDamageWithoutReturningToAncestors(GameTestHelper helper) {
        CountingCow root = addCountingCow(helper, 3, 8);
        CountingCow neighbor = addCountingCow(helper, 6, 8);
        CountingCow relay = addCountingCow(helper, 9, 8);
        CountingCow isolated = addCountingCow(helper, 15, 8);
        CountingCow ordinary = addCountingCow(helper, 4, 10);
        for (CountingCow cow : new CountingCow[]{root, neighbor, relay, isolated}) cow.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 100));
        root.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 8);
        helper.assertTrue(root.getHealth() == 92 && root.attempts == 1, "The initial target must receive its own damage once without recursive return damage");
        helper.assertTrue(neighbor.getHealth() == 96 && neighbor.attempts == 1, "The first relay must receive one half-damage hit");
        helper.assertTrue(relay.getHealth() == 98 && relay.attempts == 1, "The second relay must receive one quarter-damage hit");
        helper.assertTrue(isolated.getHealth() == 100 && isolated.attempts == 0 && ordinary.getHealth() == 100 && ordinary.attempts == 0, "Disconnected and non-electrified entities must stay unharmed");
        helper.succeed();
    }

    public static void electrifiedTriangleDeduplicatesHitsAndClearsItsPropagationContext(GameTestHelper helper) {
        CountingCow first = addCountingCow(helper, 4, 7);
        CountingCow second = addCountingCow(helper, 6, 7);
        CountingCow third = addCountingCow(helper, 5, 9);
        for (CountingCow cow : new CountingCow[]{first, second, third}) cow.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 100));
        first.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 8);
        helper.assertTrue(first.attempts == 1 && second.attempts == 1 && third.attempts == 1, "The triangular propagation graph must deliver exactly one hit attempt per entity");
        helper.assertTrue(first.getHealth() == 92 && second.getHealth() == 96 && third.getHealth() == 96, "Alternate paths must not amplify the same root damage event");
        for (CountingCow cow : new CountingCow[]{first, second, third}) cow.invulnerableTime = 0;
        second.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 6);
        helper.assertTrue(first.attempts == 2 && second.attempts == 2 && third.attempts == 2, "A subsequent root event must use a fresh propagation context");
        helper.assertTrue(first.getHealth() == 89 && second.getHealth() == 90 && third.getHealth() == 93, "A fresh event must preserve its new root and half-damage amounts");
        helper.succeed();
    }

    public static void heartBoostAttributeReturnsToBaselineOnRemovalAndExpiry(GameTestHelper helper) {
        Cow target = helper.spawnWithNoFreeWill(EntityType.COW, 5, 3, 8);
        target.setNoGravity(true);
        float baseline = target.getMaxHealth();
        target.addEffect(new MobEffectInstance(ModEffects.HEART_BOOST, 100, 2));
        helper.assertTrue(target.getMaxHealth() == baseline + 12, "Amplifier two must add three original four-health increments");
        target.removeEffect(ModEffects.HEART_BOOST);
        helper.assertTrue(target.getMaxHealth() == baseline, "Explicit effect removal must restore the unmodified attribute");
        target.addEffect(new MobEffectInstance(ModEffects.HEART_BOOST, 3, 1));
        helper.assertTrue(target.getMaxHealth() == baseline + 8, "A new effect must not accumulate the old modifier");
        helper.runAfterDelay(6, () -> {
            helper.assertTrue(!target.hasEffect(ModEffects.HEART_BOOST) && target.getMaxHealth() == baseline, "Natural expiry must restore the baseline without residual health modifiers");
            helper.succeed();
        });
    }

    public static void antidoteAndWarmthKeepTheirTickBehaviorAfterEntityNbtReload(GameTestHelper helper) {
        Cow original = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        original.addEffect(new MobEffectInstance(StatusContent.ANTITOXIN_EFFECT, 100, 1));
        original.addEffect(new MobEffectInstance(StatusContent.COCOA_EFFECT, 100, 1));
        TagValueOutput saved = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        original.saveWithoutId(saved);
        Cow restored = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.LOAD);
        restored.load(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), saved.buildResult()));
        restored.setNoAi(true);
        restored.setNoGravity(true);
        restored.snapTo(helper.absolutePos(new BlockPos(5, 3, 8)).getCenter());
        helper.assertTrue(helper.getLevel().addFreshEntity(restored), "The NBT-restored entity must be accepted");
        restored.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 100));
        restored.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
        helper.runAfterDelay(3, () -> {
            helper.assertTrue(restored.hasEffect(StatusContent.ANTITOXIN_EFFECT) && restored.hasEffect(StatusContent.COCOA_EFFECT) && !restored.hasEffect(StatusContent.FROSTBITE) && !restored.hasEffect(MobEffects.POISON), "Restored custom effects must continue their original purification ticks");
            helper.succeed();
        });
    }

    private static CountingCow addCountingCow(GameTestHelper helper, int x, int z) {
        CountingCow cow = new CountingCow(helper);
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        cow.setHealth(100);
        cow.setNoAi(true);
        cow.setNoGravity(true);
        cow.snapTo(helper.absolutePos(new BlockPos(x, 3, z)).getCenter());
        helper.assertTrue(helper.getLevel().addFreshEntity(cow), "The counting damage fixture must be accepted");
        return cow;
    }

    private static final class CountingCow extends Cow {
        private int attempts;
        private CountingCow(GameTestHelper helper) { super(EntityType.COW, helper.getLevel()); }
        @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource damage, float amount) { attempts++; return super.hurtServer(level, damage, amount); }
    }

    private StatusSafetyServerBehaviorTests() { }
}






