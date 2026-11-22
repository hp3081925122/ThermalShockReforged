package com.hp.thermalshockreforged.gametest;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sootle.SootleContent;
import com.hp.thermalshockreforged.entity.sootle.SootleEntity;
import com.hp.thermalshockreforged.entity.sootle.SootleIdleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.gametest.framework.GameTestHelper;
public final class SootleServerBehaviorTests {
    public static void repeatedDamageKeepsOneSevenTickConversion(GameTestHelper helper) {
        SootleIdleEntity idle = helper.spawnWithNoFreeWill(SootleContent.SOOTLE_IDLE.get(), 2, 2, 2);
        var source = helper.getLevel().damageSources().generic();
        helper.assertTrue(idle.hurtServer((ServerLevel)idle.level(), source, 1), "The first ordinary damage hit must be accepted");
        CompoundTag firstHit = save(idle);
        helper.assertTrue(firstHit.getBooleanOr("ConversionClaimed", false) && firstHit.getIntOr("ConversionRemaining", 0) == 7, "The first hit must claim a single seven-tick conversion");
        idle.hurtServer((ServerLevel)idle.level(), source, 1);
        CompoundTag repeatedHit = save(idle);
        helper.assertTrue(repeatedHit.getIntOr("ConversionRemaining", 0) == firstHit.getIntOr("ConversionRemaining", 0), "A repeated hit must not restart the conversion timer");
        helper.runAfterDelay(6, () -> {
            helper.assertTrue(!idle.isRemoved(), "The idle Sootle must remain until the seven-tick conversion expires");
            AABB area = new AABB(idle.blockPosition()).inflate(4);
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(SootleEntity.class, area).isEmpty(), "No active Sootle may appear before the conversion expires");
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(idle.isRemoved(), "The idle Sootle must be removed when its conversion completes");
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(SootleEntity.class, area).size() == 1, "One idle Sootle conversion must create exactly one active Sootle");
                helper.succeed();
            });
        });
    }

    private SootleServerBehaviorTests() { }
    public static void register() {
        TsrGameTests.add("history_sootle_server_behavior_tests_repeated_damage_keeps_one_seven_tick_conversion", SootleServerBehaviorTests::repeatedDamageKeepsOneSevenTickConversion, 30, "supernova_server");
    }

    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static void putUuid(CompoundTag tag,String key,java.util.UUID value){tag.put(key,net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElseThrow());}
}
