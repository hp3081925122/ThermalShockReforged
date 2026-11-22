package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.status.InfectionStatus;
import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class StatusServerRegressionTests {
    private StatusServerRegressionTests() { }

    public static void frostbiteBlocksHealingAndRemovalRestoresIt(GameTestHelper helper) {
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 3);
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        cow.setHealth(10);
        cow.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 200));
        cow.heal(5);
        helper.assertTrue(cow.getHealth() == 10, "Frostbite must block the real LivingEntity heal entry point");
        cow.removeEffect(StatusContent.FROSTBITE);
        cow.heal(5);
        helper.assertTrue(cow.getHealth() == 15, "Removing frostbite must allow healing again");
        helper.succeed();
    }

    public static void electrifiedDamageChainDoesNotRecurse(GameTestHelper helper) {
        var first = helper.spawnWithNoFreeWill(EntityType.COW, 2, 2, 2);
        var second = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        var third = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 2);
        for (var cow : java.util.List.of(first, second, third)) {
            cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
            cow.setHealth(100);
            cow.addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 200));
        }
        helper.assertTrue(first.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 8), "Initial electrified hit must be accepted");
        helper.assertTrue(first.getHealth() == 92, "Electrified chain must not hit its source recursively");
        helper.assertTrue(second.getHealth() == 96 && third.getHealth() == 96, "Electrified neighbors must receive exactly one half-damage hit");
        helper.succeed();
    }

    public static void infectionRemovalSchedulesAndResolvesBurst(GameTestHelper helper) {
        var patient = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 3);
        patient.setNoGravity(true);
        var nearby = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 3);
        nearby.setNoGravity(true);
        patient.addEffect(new MobEffectInstance(StatusContent.INFECTED, 200, 1));
        patient.removeEffect(StatusContent.INFECTED);
        helper.assertTrue(patient.getAttributeValue(Attributes.MOVEMENT_SPEED) == 0, "Removing infection from a mob must apply its temporary movement lock");
        helper.runAfterDelay(22, () -> {
            helper.assertTrue(patient.hasEffect(StatusContent.INFECTED), "Infection removal must reapply infection when its committed 20-tick burst resolves");
            helper.assertTrue(nearby.hasEffect(StatusContent.INFECTED) && nearby.getHealth() < nearby.getMaxHealth(), "Committed infection burst must damage and infect nearby mobs");
            InfectionStatus.cancel(patient);
            helper.assertTrue(patient.getAttributeValue(Attributes.MOVEMENT_SPEED) > 0, "Cancelling infection tasks must release the movement lock");
            helper.succeed();
        });
    }

    public static void purifiedPreventsInfectionRemovalRecurrence(GameTestHelper helper) {
        var patient = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 3);
        patient.setNoGravity(true);
        patient.addEffect(new MobEffectInstance(StatusContent.PURIFIED, 200));
        patient.addEffect(new MobEffectInstance(StatusContent.INFECTED, 200, 1));
        patient.removeEffect(StatusContent.INFECTED);
        helper.assertTrue(patient.getAttributeValue(Attributes.MOVEMENT_SPEED) > 0, "Purified mob must not receive infection's movement lock");
        helper.runAfterDelay(22, () -> {
            helper.assertTrue(!patient.hasEffect(StatusContent.INFECTED), "Purified mob must not have a delayed infection recurrence");
            helper.succeed();
        });
    }
}
