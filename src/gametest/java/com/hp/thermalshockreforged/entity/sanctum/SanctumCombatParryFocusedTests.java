package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.effect.MobEffects;

public final class SanctumCombatParryFocusedTests {
    public static void register() {
        TsrGameTests.add("history_sanctum_combat_parry_focused_tests_errant_parry_schedules_heal_before_damage_and_consumes_the_window", SanctumCombatParryFocusedTests::errantParrySchedulesHealBeforeDamageAndConsumesTheWindow, 10, "supernova_server");
    }

    public static void errantParrySchedulesHealBeforeDamageAndConsumesTheWindow(GameTestHelper h) {
        ErrantEntity owner = h.spawnWithNoFreeWill(SanctumContent.ERRANT.get(), 2, 2, 2);
        Cow target = EntityType.COW.create(h.getLevel(), EntitySpawnReason.COMMAND);
        target.setNoAi(true);
        target.setNoGravity(true);
        target.snapTo(h.absolutePos(new BlockPos(4, 2, 2)), 0, 0);
        if (!h.getLevel().addFreshEntity(target)) throw new IllegalStateException("GameTest server rejected cow entity");
        long seed = 0;
        for (; seed < 100000; seed++) {
            RandomSource random = RandomSource.create(seed);
            if (random.nextDouble() < .7 && random.nextDouble() < .6) break;
        }
        h.assertTrue(seed < 100000, "A deterministic parry seed must exist");
        owner.getRandom().setSeed(seed);
        owner.beginAttack(target);
        owner.getAttribute(Attributes.ARMOR).setBaseValue(0);
        owner.setHealth(50);
        boolean accepted = owner.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 10);
        h.assertTrue(accepted && Math.abs(owner.getHealth() - 40) < 1.0E-5 && owner.hasEffect(MobEffects.INSTANT_HEALTH), "Errant must queue the one-tick heal effect before incoming damage and the parry mixin must consume PARRYING");
        h.assertTrue(!owner.hasEffect(StatusContent.PARRYING), "The real before-armor path must consume the parry window");
        h.succeed();
    }

    private SanctumCombatParryFocusedTests() { }
}
