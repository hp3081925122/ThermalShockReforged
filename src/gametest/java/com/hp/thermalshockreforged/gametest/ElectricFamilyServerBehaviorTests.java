package com.hp.thermalshockreforged.gametest;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.electricsphere.ElectricSphereContent;
import com.hp.thermalshockreforged.entity.electricsphere.ElectricSphereEntity;
import com.hp.thermalshockreforged.entity.teslareed.TeslaPulseEntity;
import com.hp.thermalshockreforged.entity.teslareed.TeslaReedContent;
import com.hp.thermalshockreforged.entity.teslareed.TeslaReedEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.gametest.framework.GameTestHelper;
public final class ElectricFamilyServerBehaviorTests {
    public static void teslaPulseAffectsNearbyMonstersOnlyAndHonorsCooldown(GameTestHelper helper) {
        TeslaReedEntity reed = helper.spawnWithNoFreeWill(TeslaReedContent.TESLA_REED.get(), 4, 2, 4);
        Zombie nearby = addZombie(helper, 6, 2, 4);
        Zombie outside = addZombie(helper, 13, 2, 4);
        Cow nonMonster = addCow(helper, 7, 2, 5);
        reed.beginAttack(nearby);
        reed.beginAttack(nearby);
        helper.assertTrue(countPulses(helper, reed) == 1, "Tesla Reed cooldown must prevent duplicate pulse tasks");
        helper.assertTrue(save(reed).getIntOr("TeslaCooldown", 0) > 0, "Successful pulse submission must start the Tesla Reed cooldown");
        helper.runAfterDelay(15, () -> {
            MobEffectInstance electrified = nearby.getEffect(StatusContent.ELECTRIFIED);
            MobEffectInstance slow = nearby.getEffect(net.minecraft.world.effect.MobEffects.SLOWNESS);
            MobEffectInstance weakness = nearby.getEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
            helper.assertTrue(electrified != null && electrified.getAmplifier() == 0, "First Tesla pulse must electrify a nearby Monster");
            helper.assertTrue(slow != null && slow.getAmplifier() == 4, "Tesla pulse must preserve the configured high-amplifier slowdown");
            helper.assertTrue(weakness != null && weakness.getAmplifier() == 1, "Tesla pulse must preserve the configured weakness amplifier");
            helper.assertTrue(!outside.hasEffect(StatusContent.ELECTRIFIED) && !nonMonster.hasEffect(StatusContent.ELECTRIFIED), "Tesla pulse must ignore out-of-radius Monsters and non-Monsters");
        });
        helper.runAfterDelay(23, () -> {
            helper.assertTrue(countPulses(helper, reed) == 0, "The four-wave Tesla task must remove itself after its finite sequence");
            helper.succeed();
        });
    }
    public static void electricSphereRejectsFallDamageAndReboundsDamageByResistance(GameTestHelper helper) {
        ElectricSphereEntity sphere = helper.spawnWithNoFreeWill(ElectricSphereContent.ELECTRIC_SPHERE.get(), 4, 3, 4);
        float maxHealth = sphere.getMaxHealth();
        boolean fallDamage = sphere.hurtServer((ServerLevel)sphere.level(), helper.getLevel().damageSources().fall(), 4);
        helper.assertTrue(!fallDamage && sphere.getHealth() == maxHealth, "Electric Sphere must remain immune to fall damage");
        Cow attacker = addCow(helper, 7, 3, 4);
        attacker.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(.5);
        attacker.setDeltaMovement(.2, 0, .3);
        Vec3 resistedBefore = attacker.getDeltaMovement();
        boolean ordinaryDamage = sphere.hurtServer((ServerLevel)sphere.level(), helper.getLevel().damageSources().mobAttack(attacker), 10);
        helper.assertTrue(ordinaryDamage, "Ordinary damage must reach Electric Sphere");
        helper.assertTrue(Math.abs(sphere.getHealth() - (maxHealth - 10)) < 1.0E-4, "Rebound must not erase the incoming ordinary damage");
        ElectricSphereEntity unresistedSphere = helper.spawnWithNoFreeWill(ElectricSphereContent.ELECTRIC_SPHERE.get(), 11, 3, 4);
        Cow unresistedAttacker = addCow(helper, 14, 3, 4);
        unresistedAttacker.setDeltaMovement(.2, 0, .3);
        Vec3 unresistedBefore = unresistedAttacker.getDeltaMovement();
        boolean unresistedDamage = unresistedSphere.hurtServer((ServerLevel)unresistedSphere.level(), helper.getLevel().damageSources().mobAttack(unresistedAttacker), 10);
        helper.assertTrue(unresistedDamage, "Ordinary damage must trigger the zero-resistance comparison rebound");
        Vec3 resistedImpulse = attacker.getDeltaMovement().subtract(resistedBefore);
        Vec3 unresistedImpulse = unresistedAttacker.getDeltaMovement().subtract(unresistedBefore);
        helper.assertTrue(Math.abs(unresistedImpulse.x - resistedImpulse.x * 2) < 1.0E-4 && Math.abs(unresistedImpulse.z - resistedImpulse.z * 2) < 1.0E-4 && Math.abs(unresistedImpulse.y - resistedImpulse.y * 2) < 1.0E-4, "Half knockback resistance must halve horizontal and vertical rebound relative to an otherwise identical zero-resistance attacker");
        helper.succeed();
    }
    public static void electricSphereConstructorReplacesBlockWithEntity(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(4, 3, 4));
        helper.getLevel().setBlock(pos, ElectricSphereContent.CONSTRUCTOR.get().defaultBlockState(), 3);
        helper.assertTrue(helper.getLevel().getBlockState(pos).isAir(), "Electric Sphere constructor block must be consumed when placement succeeds");
        List<ElectricSphereEntity> spheres = helper.getLevel().getEntitiesOfClass(ElectricSphereEntity.class, new AABB(pos).inflate(2));
        helper.assertTrue(spheres.size() == 1, "Successful constructor placement must create one Electric Sphere");
        helper.succeed();
    }

    private static Zombie addZombie(GameTestHelper helper, int x, int y, int z) {
        Zombie zombie = EntityType.ZOMBIE.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (zombie == null) throw new IllegalStateException("Zombie entity type returned null");
        zombie.setNoAi(true);
        zombie.snapTo(helper.absolutePos(new BlockPos(x, y, z)), 0, 0);
        if (!helper.getLevel().addFreshEntity(zombie)) throw new IllegalStateException("GameTest server rejected zombie entity");
        return zombie;
    }

    private static Cow addCow(GameTestHelper helper, int x, int y, int z) {
        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (cow == null) throw new IllegalStateException("Cow entity type returned null");
        cow.setNoAi(true);
        cow.snapTo(helper.absolutePos(new BlockPos(x, y, z)), 0, 0);
        if (!helper.getLevel().addFreshEntity(cow)) throw new IllegalStateException("GameTest server rejected cow entity");
        return cow;
    }

    private static int countPulses(GameTestHelper helper, TeslaReedEntity reed) {
        return helper.getLevel().getEntitiesOfClass(TeslaPulseEntity.class, new AABB(reed.blockPosition()).inflate(24)).size();
    }
    public static void register() {
        TsrGameTests.add("history_electric_family_server_behavior_tests_tesla_pulse_affects_nearby_monsters_only_and_honors_cooldown", ElectricFamilyServerBehaviorTests::teslaPulseAffectsNearbyMonstersOnlyAndHonorsCooldown, 50, "supernova_server");
        TsrGameTests.add("history_electric_family_server_behavior_tests_electric_sphere_rejects_fall_damage_and_rebounds_damage_by_resistance", ElectricFamilyServerBehaviorTests::electricSphereRejectsFallDamageAndReboundsDamageByResistance, 50, "supernova_server");
        TsrGameTests.add("history_electric_family_server_behavior_tests_electric_sphere_constructor_replaces_block_with_entity", ElectricFamilyServerBehaviorTests::electricSphereConstructorReplacesBlockWithEntity, 30, "supernova_server");
    }

    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static void putUuid(CompoundTag tag,String key,java.util.UUID value){tag.put(key,net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElseThrow());}
}
