package com.hp.thermalshockreforged.gametest;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ambient.AmbientContent;
import com.hp.thermalshockreforged.entity.ambient.SootlingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.gametest.framework.GameTestHelper;
public final class SootlingServerBehaviorTests {
    public static void removedSkillTargetClearsTaskAndKeepsCooldown(GameTestHelper helper) {
        SootlingEntity sootling = helper.spawnWithNoFreeWill(AmbientContent.SOOTLING.get(), 2, 2, 2);
        var firstTarget = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        sootling.beginSkill(firstTarget);
        CompoundTag started = save(sootling);
        helper.assertTrue(started.getIntOr("SkillTick", 0) == 0, "Beginning a skill must save its initial skill tick");
        helper.assertTrue(started.getIntOr("SkillCooldown", 0) == 80, "Beginning a skill must start the 80-tick cooldown");

        firstTarget.discard();
        helper.runAfterDelay(1, () -> {
            CompoundTag canceled = save(sootling);
            int cooldown = canceled.getIntOr("SkillCooldown", 0);
            helper.assertTrue(canceled.getIntOr("SkillTick", 0) == -1, "A removed target must cancel the pending skill");
            helper.assertTrue(cooldown > 0 && cooldown < 80, "Canceling a skill must preserve its remaining cooldown");

            var secondTarget = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 3);
            sootling.beginSkill(secondTarget);
            CompoundTag rejected = save(sootling);
            helper.assertTrue(rejected.getIntOr("SkillTick", 0) == -1, "The active cooldown must reject a new target skill");
            helper.assertTrue(rejected.getIntOr("SkillCooldown", 0) == cooldown, "A rejected skill must not reset its cooldown");
            helper.succeed();
        });
    }

    private SootlingServerBehaviorTests() { }
    public static void register() {
        TsrGameTests.add("history_sootling_server_behavior_tests_removed_skill_target_clears_task_and_keeps_cooldown", SootlingServerBehaviorTests::removedSkillTargetClearsTaskAndKeepsCooldown, 30, "supernova_server");
    }

    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static void putUuid(CompoundTag tag,String key,java.util.UUID value){tag.put(key,net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElseThrow());}
}
