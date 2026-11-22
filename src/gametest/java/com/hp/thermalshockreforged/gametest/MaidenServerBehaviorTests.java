package com.hp.thermalshockreforged.gametest;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.maiden.MaidenConfig;
import com.hp.thermalshockreforged.entity.maiden.MaidenContent;
import com.hp.thermalshockreforged.entity.maiden.MaidenEntity;
import com.hp.thermalshockreforged.entity.maiden.MaidenVolleyEntity;
import com.hp.thermalshockreforged.entity.projectile.BloodBulletEntity;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.phys.AABB;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class MaidenServerBehaviorTests {
    private static final ConcurrentHashMap<UUID, CopyOnWriteArrayList<Double>> FIRED_DAMAGE = new ConcurrentHashMap<>();

    static {
        NeoForge.EVENT_BUS.addListener(MaidenServerBehaviorTests::onEntityJoin);
    }
    public static void singleRingAttackCancelsHitAndFiresEightScaledArrows(GameTestHelper helper) {
        MaidenEntity owner = helper.spawnWithNoFreeWill(MaidenContent.MAIDEN.get(), 4, 2, 4);
        FIRED_DAMAGE.put(owner.getUUID(), new CopyOnWriteArrayList<>());
        Cow firstTarget = addCow(helper, 6, 2, 4);
        owner.getRandom().setSeed(seedFor(true));
        float initialHealth = firstTarget.getHealth();
        boolean firstHit = firstTarget.hurtServer((ServerLevel)firstTarget.level(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(!firstHit && firstTarget.getHealth() == initialHealth, "Maiden hit must be replaced by its single-ring skill");
        MaidenVolleyEntity volley = onlyVolley(helper, owner);
        CompoundTag volleyData = save(volley);
        helper.assertTrue(volleyData.getBooleanOr("MaidenSingle", false), "The seeded attack must select the single ring");
        helper.assertTrue(Math.abs(volleyData.getDoubleOr("MaidenDamage", 0.0) - owner.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) * MaidenConfig.SINGLE_MULTIPLIER.get()) < 1.0E-6, "Single-ring arrow damage must use the configured attack multiplier");
        Cow cooldownTarget = addCow(helper, 7, 2, 6);
        float cooldownHealth = cooldownTarget.getHealth();
        boolean cooldownHit = cooldownTarget.hurtServer((ServerLevel)cooldownTarget.level(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(cooldownHit && cooldownTarget.getHealth() < cooldownHealth, "A hit during cooldown must deal ordinary damage");
        helper.runAfterDelay(10, () -> {
            helper.assertTrue(owner.hasEffect(StatusContent.STUNNED), "The single-ring windup must stun the Maiden");
            helper.assertTrue(countVolleys(helper, owner) == 1, "Cooldown must not create a second volley");
        });
        helper.runAfterDelay(32, () -> {
            List<Double> bullets = firedDamage(owner);
            helper.assertTrue(bullets.size() == 8, "Single-ring attack must fire exactly eight arrows");
            double expected = volleyData.getDoubleOr("MaidenDamage", 0.0) * 8;
            double actual = bullets.stream().mapToDouble(Double::doubleValue).sum();
            helper.assertTrue(Math.abs(actual - expected) < 1.0E-5, "All eight single-ring arrows must preserve the configured damage multiplier");
            helper.assertTrue(volley.isRemoved(), "Single-ring task must remove itself after the volley");
            FIRED_DAMAGE.remove(owner.getUUID());
            helper.succeed();
        });
    }
    public static void tripleRingAttackFiresThreeTimedWavesAndEnds(GameTestHelper helper) {
        MaidenEntity owner = helper.spawnWithNoFreeWill(MaidenContent.MAIDEN.get(), 4, 2, 4);
        FIRED_DAMAGE.put(owner.getUUID(), new CopyOnWriteArrayList<>());
        Cow target = addCow(helper, 6, 2, 4);
        owner.getRandom().setSeed(seedFor(false));
        float initialHealth = target.getHealth();
        boolean firstHit = target.hurtServer((ServerLevel)target.level(), helper.getLevel().damageSources().mobAttack(owner), 2);
        helper.assertTrue(!firstHit && target.getHealth() == initialHealth, "Maiden hit must be replaced by its triple-ring skill");
        MaidenVolleyEntity volley = onlyVolley(helper, owner);
        CompoundTag volleyData = save(volley);
        helper.assertTrue(!volleyData.getBooleanOr("MaidenSingle", false), "The seeded attack must select the triple ring");
        double damage = volleyData.getDoubleOr("MaidenDamage", 0.0);
        double lastDamage = volleyData.getDoubleOr("MaidenLastDamage", 0.0);
        helper.assertTrue(Math.abs(damage - owner.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) * MaidenConfig.TRIPLE_MULTIPLIER.get()) < 1.0E-6, "Triple-ring arrows must use the configured primary multiplier");
        helper.assertTrue(Math.abs(lastDamage - owner.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) * MaidenConfig.LAST_MULTIPLIER.get()) < 1.0E-6, "Each ring's last arrow must use its separate configured multiplier");
        helper.runAfterDelay(32, () -> {
            int count = firedDamage(owner).size();
            var taskData = save(volley);
            ThermalShockReforged.LOGGER.info("Maiden triple volley checkpoint 32: elapsed={}, ownerBullets={}", taskData.getIntOr("MaidenElapsed", 0), count);
            helper.assertTrue(count == 8, "First triple-ring wave must contain eight arrows; found " + count + " at elapsed " + taskData.getIntOr("MaidenElapsed", 0));
        });
        helper.runAfterDelay(37, () -> {
            int count = firedDamage(owner).size();
            var taskData = save(volley);
            ThermalShockReforged.LOGGER.info("Maiden triple volley checkpoint 37: elapsed={}, ownerBullets={}", taskData.getIntOr("MaidenElapsed", 0), count);
            helper.assertTrue(count == 16, "Second triple-ring wave must add eight arrows; found " + count + " at elapsed " + taskData.getIntOr("MaidenElapsed", 0));
        });
        helper.runAfterDelay(43, () -> {
            List<Double> bullets = firedDamage(owner);
            helper.assertTrue(bullets.size() == 24, "Triple-ring attack must fire exactly three eight-arrow waves");
            double expected = damage * 21 + lastDamage * 3;
            double actual = bullets.stream().mapToDouble(Double::doubleValue).sum();
            helper.assertTrue(Math.abs(actual - expected) < 1.0E-5, "All triple-ring arrows must preserve both configured damage multipliers");
            helper.assertTrue(volley.isRemoved(), "Triple-ring task must remove itself after the final wave");
            FIRED_DAMAGE.remove(owner.getUUID());
            helper.succeed();
        });
    }

    private static long seedFor(boolean single) {
        double chance = MaidenConfig.SINGLE_CHANCE.get();
        for (long seed = 0; seed < 100000; seed++) if ((RandomSource.create(seed).nextDouble() < chance) == single) return seed;
        throw new IllegalStateException("No deterministic Maiden random seed found");
    }

    private static Cow addCow(GameTestHelper helper, int x, int y, int z) {
        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (cow == null) throw new IllegalStateException("Cow entity type returned null");
        cow.setNoAi(true);
        cow.snapTo(helper.absolutePos(new net.minecraft.core.BlockPos(x, y, z)), 0, 0);
        if (!helper.getLevel().addFreshEntity(cow)) throw new IllegalStateException("GameTest server rejected cow entity");
        return cow;
    }

    private static MaidenVolleyEntity onlyVolley(GameTestHelper helper, MaidenEntity owner) {
        List<MaidenVolleyEntity> volleys = helper.getLevel().getEntitiesOfClass(MaidenVolleyEntity.class, new AABB(owner.blockPosition()).inflate(12));
        if (volleys.size() != 1) throw new IllegalStateException("Expected one Maiden volley task, found " + volleys.size());
        return volleys.getFirst();
    }

    private static int countVolleys(GameTestHelper helper, MaidenEntity owner) {
        return helper.getLevel().getEntitiesOfClass(MaidenVolleyEntity.class, new AABB(owner.blockPosition()).inflate(12)).size();
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof BloodBulletEntity bullet && bullet.getOwner() instanceof MaidenEntity owner) {
            CopyOnWriteArrayList<Double> fired = FIRED_DAMAGE.get(owner.getUUID());
            if (fired != null) fired.add(save(bullet).getDoubleOr("damage", 0.0));
        }
    }

    private static List<Double> firedDamage(MaidenEntity owner) {
        CopyOnWriteArrayList<Double> fired = FIRED_DAMAGE.get(owner.getUUID());
        return fired == null ? List.of() : List.copyOf(fired);
    }
    public static void register() {
        TsrGameTests.add("history_maiden_server_behavior_tests_single_ring_attack_cancels_hit_and_fires_eight_scaled_arrows", MaidenServerBehaviorTests::singleRingAttackCancelsHitAndFiresEightScaledArrows, 70, "supernova_server");
        TsrGameTests.add("history_maiden_server_behavior_tests_triple_ring_attack_fires_three_timed_waves_and_ends", MaidenServerBehaviorTests::tripleRingAttackFiresThreeTimedWavesAndEnds, 80, "supernova_server");
    }

    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static void putUuid(CompoundTag tag,String key,java.util.UUID value){tag.put(key,net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElseThrow());}
}
