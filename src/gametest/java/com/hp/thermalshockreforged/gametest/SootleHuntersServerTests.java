package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.entity.ambient.AmbientContent;
import com.hp.thermalshockreforged.entity.ambient.SootlingEntity;
import com.hp.thermalshockreforged.entity.hunters.HuntersContent;
import com.hp.thermalshockreforged.entity.hunters.FrostilyteEntity;
import com.hp.thermalshockreforged.entity.hunters.RazorwingEntity;
import com.hp.thermalshockreforged.entity.sootle.SootleContent;
import com.hp.thermalshockreforged.entity.sootle.SootleEntity;
import com.hp.thermalshockreforged.entity.sootle.SootleIdleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.UUID;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.gametest.framework.GameTestHelper;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
public final class SootleHuntersServerTests {
    public static void idleMeleeEntryStartsTenTickConversionWithoutDamage(GameTestHelper helper) {
        SootleIdleEntity idle = helper.spawnWithNoFreeWill(SootleContent.SOOTLE_IDLE.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        float before = target.getHealth();
        helper.assertTrue(!idle.doHurtTarget(helper.getLevel(), target), "Idle Sootle melee entry must defer rather than deal immediate melee damage");
        ValueInput state = TsrGameTests.roundTrip(helper, idle);
        helper.assertTrue(state.getBooleanOr("ConversionClaimed", false) && state.getIntOr("ConversionRemaining", -1) == 10, "Melee entry must claim its ten-tick conversion path");
        helper.assertTrue(target.getHealth() == before, "Melee entry must not apply direct damage");
        helper.runAfterDelay(11, () -> {
            helper.assertTrue(idle.isRemoved(), "Idle Sootle must be removed when the melee conversion expires");
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(SootleEntity.class, new AABB(idle.blockPosition()).inflate(4)).size() == 1, "Melee conversion must produce one active Sootle");
            helper.succeed();
        });
    }
    public static void pendingIdleConversionResumesAfterEntityNbtReload(GameTestHelper helper) {
        SootleIdleEntity original = helper.spawnWithNoFreeWill(SootleContent.SOOTLE_IDLE.get(), 2, 2, 2);
        original.beginConversion(7);
        helper.runAfterDelay(2, () -> {
            CompoundTag saved = saveTag(helper, original);
            int remaining = saved.getIntOr("ConversionRemaining", -1);
            var restoredPosition = original.position();
            original.discard();
            SootleIdleEntity restored = SootleContent.SOOTLE_IDLE.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
            restored.load(readTag(helper, saved));
            restored.setPos(restoredPosition);
            helper.getLevel().addFreshEntity(restored);
            helper.assertTrue(remaining == 5 && TsrGameTests.roundTrip(helper, restored).getIntOr("ConversionRemaining", -1) == remaining, "Entity NBT reload must preserve the remaining conversion ticks");
            helper.runAfterDelay(4, () -> helper.assertTrue(!restored.isRemoved(), "Restored conversion must remain pending before its final tick"));
            helper.runAfterDelay(7, () -> {
                helper.assertTrue(restored.isRemoved(), "Restored conversion must finish on the saved remaining-tick schedule");
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(SootleEntity.class, new AABB(restored.blockPosition()).inflate(4)).size() == 1, "Reloaded conversion must create exactly one active Sootle");
                helper.succeed();
            });
        });
    }
    public static void lethalIdleDamageDoesNotLeaveAConversionSpawn(GameTestHelper helper) {
        SootleIdleEntity idle = helper.spawnWithNoFreeWill(SootleContent.SOOTLE_IDLE.get(), 2, 2, 2);
        idle.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
        helper.runAfterDelay(9, () -> {
            helper.assertTrue(!idle.isAlive(), "Lethal damage must kill the idle Sootle");
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(SootleEntity.class, new AABB(idle.blockPosition()).inflate(4)).isEmpty(), "A dead idle Sootle must not complete its pending conversion");
            helper.succeed();
        });
    }
    public static void activeSootleAttackAppliesTheSelectedDelayedDamage(GameTestHelper helper) {
        SootleEntity sootle = helper.spawnWithNoFreeWill(SootleContent.SOOTLE.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        target.setNoAi(true);
        float before = target.getHealth();
        helper.assertTrue(!sootle.doHurtTarget(helper.getLevel(), target), "Active Sootle melee entry must schedule its skill without immediate damage");
        ValueInput started = TsrGameTests.roundTrip(helper, sootle);
        helper.assertTrue(started.getIntOr("SkillAge", -1) == 0 && started.getIntOr("ActionLock", -1) == 18, "Attack entry must lock movement and start the skill age");
        helper.assertTrue(target.getHealth() == before, "Delayed attack must not damage on its entry tick");
        helper.runAfterDelay(12, () -> {
            ValueInput ended = TsrGameTests.roundTrip(helper, sootle);
            float expectedDamage = started.getBooleanOr("SingleAttack", false) ? 4.0F : 6.0F;
            helper.assertTrue(ended.getIntOr("SkillAge", -1) == -1 && ended.getIntOr("SkillCooldown", -1) > 0, "Attack must finish its selected strike phases and retain cooldown");
            helper.assertTrue(Math.abs((before - target.getHealth()) - expectedDamage) < 0.01F, "Selected single or double attack must deal its source-defined total damage");
            helper.succeed();
        });
    }
    public static void activeSootleCancelsWhenItsCommittedTargetDisappears(GameTestHelper helper) {
        SootleEntity sootle = helper.spawnWithNoFreeWill(SootleContent.SOOTLE.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        sootle.beginAttack(target);
        target.discard();
        helper.runAfterDelay(2, () -> {
            ValueInput state = TsrGameTests.roundTrip(helper, sootle);
            helper.assertTrue(state.getIntOr("SkillAge", -1) == -1 && state.getIntOr("ActionLock", -1) == 0, "A removed target must clear the committed attack and movement lock");
            helper.assertTrue(state.getIntOr("SkillCooldown", -1) > 0, "Canceling a committed attack must preserve the cooldown");
            helper.succeed();
        });
    }
    public static void activeSootleStrikeRejectsTargetBeyondThreeBlockReach(GameTestHelper helper) {
        SootleEntity sootle = helper.spawnWithNoFreeWill(SootleContent.SOOTLE.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 2);
        target.setNoAi(true);
        sootle.beginAttack(target);
        target.setPos(sootle.position().add(4, 0, 0));
        float before = target.getHealth();
        helper.runAfterDelay(12, () -> {
            helper.assertTrue(target.getHealth() == before, "A target moved beyond the three-block strike range must receive no delayed hit");
            helper.assertTrue(TsrGameTests.roundTrip(helper, sootle).getIntOr("SkillAge", -1) == -1, "Out-of-range attack must still consume its scheduled strike phases");
            helper.succeed();
        });
    }
    public static void sootlingSkillHitsAtItsFirstServerStrikePhase(GameTestHelper helper) {
        SootlingEntity sootling = helper.spawnWithNoFreeWill(AmbientContent.SOOTLING.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        target.setNoAi(true);
        float before = target.getHealth();
        helper.assertTrue(!sootling.doHurtTarget(helper.getLevel(), target), "Sootling melee entry must start the deferred skill, not apply ordinary damage");
        helper.assertTrue(TsrGameTests.roundTrip(helper, sootling).getIntOr("SkillTick", -1) == 0, "Real melee callback must start skill phase zero");
        helper.runAfterDelay(19, () -> helper.assertTrue(target.getHealth() == before, "Sootling must not hit before its tick-20 strike phase"));
        helper.runAfterDelay(20, () -> {
            helper.assertTrue(Math.abs((before - target.getHealth()) - 1.0F) < 0.01F, "Tick-20 skill strike must deal one third of the base three attack damage");
            helper.assertTrue(TsrGameTests.roundTrip(helper, sootling).getIntOr("SkillTick", -1) == 20, "First real strike must be recorded at skill tick 20");
            helper.succeed();
        });
    }
    public static void sootlingDoesNotHitTargetOutsideItsTwoBlockStrikeRange(GameTestHelper helper) {
        SootlingEntity sootling = helper.spawnWithNoFreeWill(AmbientContent.SOOTLING.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        target.setNoAi(true);
        sootling.beginSkill(target);
        target.setPos(sootling.position().add(8, 0, 0));
        float before = target.getHealth();
        helper.runAfterDelay(22, () -> {
            helper.assertTrue(target.getHealth() == before, "Sootling skill must not damage a target beyond two blocks at its strike phase");
            helper.assertTrue(TsrGameTests.roundTrip(helper, sootling).getIntOr("SkillTick", -1) == 22, "Out-of-range target must not cancel the live skill state");
            helper.succeed();
        });
    }
    public static void sootlingActiveSkillAndCooldownSurviveEntityNbtReload(GameTestHelper helper) {
        SootlingEntity original = helper.spawnWithNoFreeWill(AmbientContent.SOOTLING.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        original.beginSkill(target);
        helper.runAfterDelay(5, () -> {
            CompoundTag saved = saveTag(helper, original);
            int age = saved.getIntOr("SkillTick", -1);
            int cooldown = saved.getIntOr("SkillCooldown", -1);
            var restoredPosition = original.position();
            original.discard();
            SootlingEntity restored = AmbientContent.SOOTLING.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
            restored.load(readTag(helper, saved));
            restored.setPos(restoredPosition);
            restored.setNoAi(true);
            helper.getLevel().addFreshEntity(restored);
            ValueInput loaded = TsrGameTests.roundTrip(helper, restored);
            helper.assertTrue(loaded.getIntOr("SkillTick", -1) == age && loaded.getIntOr("SkillCooldown", -1) == cooldown, "NBT reload must preserve current Sootling phase and cooldown values");
            helper.runAfterDelay(1, () -> {
                ValueInput advanced = TsrGameTests.roundTrip(helper, restored);
                helper.assertTrue(advanced.getIntOr("SkillTick", -1) == age + 1 && advanced.getIntOr("SkillCooldown", -1) == cooldown - 1, "Reloaded skill and cooldown must advance by one server tick");
                helper.succeed();
            });
        });
    }
    public static void sootlingRejectsDeadTargetWithoutStartingCooldown(GameTestHelper helper) {
        SootlingEntity sootling = helper.spawnWithNoFreeWill(AmbientContent.SOOTLING.get(), 2, 2, 2);
        var dead = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        dead.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
        helper.assertTrue(!dead.isAlive(), "Fixture target must be dead before the skill request");
        sootling.beginSkill(dead);
        ValueInput rejected = TsrGameTests.roundTrip(helper, sootling);
        helper.assertTrue(rejected.getIntOr("SkillTick", -1) == -1 && rejected.getIntOr("SkillCooldown", -1) == 0, "Dead targets must not start a Sootling skill or cooldown");
        helper.succeed();
    }
    public static void frostilyteRealIncomingAttackDefersDamageToItsEleventhTick(GameTestHelper helper) {
        FrostilyteEntity frostilyte = helper.spawnWithNoFreeWill(HuntersContent.FROSTILYTE.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        target.setNoAi(true);
        float before = target.getHealth();
        helper.assertTrue(!target.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(frostilyte), 2), "Frostilyte-origin attack event must cancel the raw hit");
        ValueInput started = TsrGameTests.roundTrip(helper, frostilyte);
        helper.assertTrue(started.getIntOr("AttackRemaining", -1) == 11 && started.getIntOr("AttackCooldown", -1) == 12, "Canceled incoming hit must commit the eleven-tick attack and twelve-tick cooldown");
        helper.assertTrue(target.getHealth() == before, "Attack event must not apply its raw incoming damage");
        helper.runAfterDelay(10, () -> helper.assertTrue(target.getHealth() == before, "Frostilyte must not deal delayed damage before its eleventh tick"));
        helper.runAfterDelay(12, () -> {
            helper.assertTrue(Math.abs((before - target.getHealth()) - 6.0F) < 0.01F, "Frostilyte strike must deal 1.5 times its four-point attack attribute");
            helper.assertTrue(TsrGameTests.roundTrip(helper, frostilyte).getIntOr("AttackRemaining", -1) == -1, "Resolved attack must clear the pending target");
            helper.succeed();
        });
    }
    public static void frostilyteDelayedAttackMissesTargetOutsideOneAndHalfBlocks(GameTestHelper helper) {
        FrostilyteEntity frostilyte = helper.spawnWithNoFreeWill(HuntersContent.FROSTILYTE.get(), 2, 2, 2);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 3, 2, 2);
        target.setNoAi(true);
        target.hurtServer(helper.getLevel(), helper.getLevel().damageSources().mobAttack(frostilyte), 1);
        target.setPos(frostilyte.position().add(3, 0, 0));
        float before = target.getHealth();
        helper.runAfterDelay(13, () -> {
            helper.assertTrue(target.getHealth() == before, "Frostilyte must recheck the committed target's one-and-a-half-block range at impact");
            helper.assertTrue(TsrGameTests.roundTrip(helper, frostilyte).getIntOr("AttackRemaining", -1) == -1, "Out-of-range impact must consume and clear the pending attack");
            helper.succeed();
        });
    }
    public static void frostilyteRejectsDrowningDamage(GameTestHelper helper) {
        FrostilyteEntity frostilyte = helper.spawnWithNoFreeWill(HuntersContent.FROSTILYTE.get(), 2, 2, 2);
        float before = frostilyte.getHealth();
        helper.assertTrue(!frostilyte.hurtServer(helper.getLevel(), helper.getLevel().damageSources().source(DamageTypes.DROWN), 5), "Drowning damage must be rejected by Frostilyte");
        helper.assertTrue(frostilyte.getHealth() == before, "Rejected drowning damage must not lower Frostilyte health");
        helper.succeed();
    }
    public static void frostilyteCustomDropHookHonorsEntityDropsGameRule(GameTestHelper helper) {
        FrostilyteEntity frostilyte = helper.spawnWithNoFreeWill(HuntersContent.FROSTILYTE.get(), 2, 2, 2);
        helper.getLevel().getGameRules().set(net.minecraft.world.level.gamerules.GameRules.ENTITY_DROPS, false, helper.getLevel().getServer());
        frostilyte.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(1, 1, 1, 6, 6, 6)).isEmpty(), "Frostilyte custom drops must be suppressed when doEntityDrops is false");
            helper.succeed();
        });
    }
    public static void razorwingFoodRulesSeparateNormalAndArmouredVariants(GameTestHelper helper) {
        RazorwingEntity normal = HuntersContent.RAZORWING.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        RazorwingEntity ruby = HuntersContent.RUBY_ARMOURED_RAZORWING.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        helper.assertTrue(normal.isFood(new ItemStack(Items.COD)) && normal.isFood(new ItemStack(Items.SALMON)), "Normal Razorwing must accept cod and salmon");
        helper.assertTrue(normal.isFood(new ItemStack(com.hp.thermalshockreforged.content.MaterialContent.items().get("raw_crystal_koi").get())), "Normal Razorwing must accept its registered mod fish food");
        helper.assertTrue(ruby.isFood(new ItemStack(Items.COD)) && !ruby.isFood(new ItemStack(Items.SALMON)), "Armoured Razorwing must accept cod while rejecting salmon");
        helper.assertTrue(!ruby.isFood(new ItemStack(com.hp.thermalshockreforged.content.MaterialContent.items().get("raw_crystal_koi").get())), "Armoured Razorwing must reject normal-variant mod fish food");
        helper.succeed();
    }
    public static void ownerArmorInteractionConvertsRazorwingAndConsumesOneArmor(GameTestHelper helper) {
        RazorwingEntity original = helper.spawnWithNoFreeWill(HuntersContent.RAZORWING.get(), 2, 2, 2);
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 2.5, 2, 3.5);
        original.tame(player);
        original.setHealth(7);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(com.hp.thermalshockreforged.content.MaterialContent.items().get("ruby_razorwing_armor").get(), 2));
        helper.assertTrue(original.mobInteract(player, InteractionHand.MAIN_HAND).consumesAction(), "Owner armor interaction must commit the Razorwing conversion");
        var replacements = helper.getLevel().getEntitiesOfClass(RazorwingEntity.class, new AABB(original.blockPosition()).inflate(4), entity -> entity.getType() == HuntersContent.RUBY_ARMOURED_RAZORWING.get());
        helper.assertTrue(original.isRemoved() && replacements.size() == 1, "Successful conversion must replace the source by exactly one ruby-armoured variant");
        RazorwingEntity replacement = replacements.getFirst();
        helper.assertTrue(player.getUUID().equals(replacement.getOwnerReference().getUUID()) && replacement.getHealth() == 7, "Conversion must preserve tame owner and current health");
        helper.assertTrue(player.getMainHandItem().getCount() == 1 && replacement.getAttributeValue(Attributes.ARMOR) == 10, "Survival conversion must consume exactly one armor and apply ten armor points");
        PlayerInteractionTests.release(helper, player);
        helper.succeed();
    }
    public static void armouredRazorwingInteractionMakesPlayerItsControllingPassenger(GameTestHelper helper) {
        RazorwingEntity armored = helper.spawnWithNoFreeWill(HuntersContent.BLOOD_ARMOURED_RAZORWING.get(), 2, 2, 2);
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 2.5, 2, 3.5);
        armored.mobInteract(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.getVehicle() == armored && armored.getControllingPassenger() == player, "Armoured Razorwing interaction must mount the player as controlling passenger");
        PlayerInteractionTests.release(helper, player);
        helper.succeed();
    }
    private static void setOverworldClock(ServerLevel level, long ticks) {
        level.clockManager().setTotalTicks(level.registryAccess().getOrThrow(net.minecraft.world.clock.WorldClocks.OVERWORLD), ticks);
    }
    public static void razorwingDaySpawnAcceptsBothShalesAndRejectsStone(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = new BlockPos(helper.absolutePos(new BlockPos(2, 2, 2)));
        setOverworldClock(level, 6000);
        helper.runAfterDelay(2, () -> {
            level.setBlock(pos.below(), TerrainContent.GRASSY_SHALE.get().defaultBlockState(), 3);
            boolean grassy = SpawnPlacements.checkSpawnRules(HuntersContent.RAZORWING.get(), level, EntitySpawnReason.NATURAL, pos, level.getRandom());
            level.setBlock(pos.below(), TerrainContent.SNOWY_SHALE.get().defaultBlockState(), 3);
            boolean snowy = SpawnPlacements.checkSpawnRules(HuntersContent.RAZORWING.get(), level, EntitySpawnReason.NATURAL, pos, level.getRandom());
            level.setBlock(pos.below(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
            boolean stone = SpawnPlacements.checkSpawnRules(HuntersContent.RAZORWING.get(), level, EntitySpawnReason.NATURAL, pos, level.getRandom());
            helper.assertTrue(grassy && snowy, "Daytime Razorwing natural spawn must accept grassy and snowy shale under open sky");
            helper.assertTrue(!stone, "Razorwing natural spawn must reject stone substrate");
            helper.succeed();
        });
    }
    public static void razorwingNaturalSpawnRejectsNightAfterWorldTimeAdvances(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = new BlockPos(helper.absolutePos(new BlockPos(2, 2, 2)));
        level.setBlock(pos.below(), TerrainContent.SNOWY_SHALE.get().defaultBlockState(), 3);
        setOverworldClock(level, 18000);
        helper.runAfterDelay(2, () -> {
            boolean night = SpawnPlacements.checkSpawnRules(HuntersContent.RAZORWING.get(), level, EntitySpawnReason.NATURAL, pos, level.getRandom());
            helper.assertTrue(!night, "Razorwing natural spawn must reject snowy shale at night after the level updates its daylight state");
            helper.succeed();
        });
    }
    public static void normalRazorwingDeathDropsConfiguredChickenAndFeatherRanges(GameTestHelper helper) {
        RazorwingEntity razorwing = helper.spawnWithNoFreeWill(HuntersContent.RAZORWING.get(), 2, 2, 2);
        razorwing.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
        helper.runAfterDelay(2, () -> {
            var drops = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(razorwing.blockPosition()).inflate(4));
            int chicken = drops.stream().filter(item -> item.getItem().is(Items.CHICKEN)).mapToInt(item -> item.getItem().getCount()).sum();
            int feathers = drops.stream().filter(item -> item.getItem().is(Items.FEATHER)).mapToInt(item -> item.getItem().getCount()).sum();
            helper.assertTrue(chicken >= 2 && chicken <= 4 && feathers >= 3 && feathers <= 5, "Normal Razorwing death must yield two to four chicken and three to five feathers");
            helper.succeed();
        });
    }
    public static void frostilyteIceDropRollUsesTheRegisteredIceTagAndDeterministicSeed(GameTestHelper helper) {
        FrostilyteEntity frostilyte = helper.spawnWithNoFreeWill(HuntersContent.FROSTILYTE.get(), 2, 2, 2);
        long seed = 0;
        for (; seed < 100000; seed++) {
            var random = net.minecraft.util.RandomSource.create(seed);
            if (random.nextFloat() >= 0.1F && random.nextFloat() < 0.7F) break;
        }
        helper.assertTrue(seed < 100000, "A deterministic roll must be found for the Frostilyte ice-drop branch");
        long usedSeed = seed;
        helper.getLevel().getServer().getRandomSequence(net.minecraft.resources.Identifier.fromNamespaceAndPath("thermal_shock_reforged", "entities/frostilyte")).setSeed(usedSeed);
        frostilyte.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
        helper.runAfterDelay(2, () -> {
            var drops = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(frostilyte.blockPosition()).inflate(4));
            int phobotite = drops.stream().filter(item -> item.getItem().is(com.hp.thermalshockreforged.content.MaterialContent.items().get("phobotite").get())).mapToInt(item -> item.getItem().getCount()).sum();
            int neuralChips = drops.stream().filter(item -> item.getItem().is(com.hp.thermalshockreforged.content.MaterialContent.items().get("neural_chip").get())).mapToInt(item -> item.getItem().getCount()).sum();
            helper.assertTrue(phobotite == 1 && neuralChips == 0, "A seed outside the neural-chip roll but inside the 70-percent ice roll must yield one phobotite only");
            helper.succeed();
        });
    }
    public static void rubyAndBloodArmouredRazorwingDeathsProduceNoUnconfiguredDrops(GameTestHelper helper) {
        RazorwingEntity ruby = helper.spawnWithNoFreeWill(HuntersContent.RUBY_ARMOURED_RAZORWING.get(), 1, 2, 2);
        ruby.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
        helper.runAfterDelay(2, () -> {
            var rubyDrops = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(ruby.blockPosition()).inflate(3));
            helper.assertTrue(rubyDrops.isEmpty(), "Ruby-armoured Razorwing currently resolves no entity-specific loot table and must emit no item drops");
            RazorwingEntity blood = helper.spawnWithNoFreeWill(HuntersContent.BLOOD_ARMOURED_RAZORWING.get(), 3, 2, 2);
            blood.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
            helper.runAfterDelay(2, () -> {
                var bloodDrops = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(blood.blockPosition()).inflate(3));
                helper.assertTrue(bloodDrops.isEmpty(), "Blood-armoured Razorwing currently resolves no entity-specific loot table and must emit no item drops");
                helper.succeed();
            });
        });
    }
    private static CompoundTag saveTag(GameTestHelper helper, Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        entity.saveWithoutId(output);
        return output.buildResult();
    }

    private static ValueInput readTag(GameTestHelper helper, CompoundTag tag) {
        return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag);
    }

    public static void register() {
        TsrGameTests.add("history_sootle_hunters_server_tests_idle_melee_entry_starts_ten_tick_conversion_without_damage", SootleHuntersServerTests::idleMeleeEntryStartsTenTickConversionWithoutDamage, 35, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_pending_idle_conversion_resumes_after_entity_nbt_reload", SootleHuntersServerTests::pendingIdleConversionResumesAfterEntityNbtReload, 25, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_lethal_idle_damage_does_not_leave_a_conversion_spawn", SootleHuntersServerTests::lethalIdleDamageDoesNotLeaveAConversionSpawn, 20, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_active_sootle_attack_applies_the_selected_delayed_damage", SootleHuntersServerTests::activeSootleAttackAppliesTheSelectedDelayedDamage, 25, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_active_sootle_cancels_when_its_committed_target_disappears", SootleHuntersServerTests::activeSootleCancelsWhenItsCommittedTargetDisappears, 20, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_active_sootle_strike_rejects_target_beyond_three_block_reach", SootleHuntersServerTests::activeSootleStrikeRejectsTargetBeyondThreeBlockReach, 20, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_sootling_skill_hits_at_its_first_server_strike_phase", SootleHuntersServerTests::sootlingSkillHitsAtItsFirstServerStrikePhase, 45, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_sootling_does_not_hit_target_outside_its_two_block_strike_range", SootleHuntersServerTests::sootlingDoesNotHitTargetOutsideItsTwoBlockStrikeRange, 45, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_sootling_active_skill_and_cooldown_survive_entity_nbt_reload", SootleHuntersServerTests::sootlingActiveSkillAndCooldownSurviveEntityNbtReload, 30, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_sootling_rejects_dead_target_without_starting_cooldown", SootleHuntersServerTests::sootlingRejectsDeadTargetWithoutStartingCooldown, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_frostilyte_real_incoming_attack_defers_damage_to_its_eleventh_tick", SootleHuntersServerTests::frostilyteRealIncomingAttackDefersDamageToItsEleventhTick, 25, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_frostilyte_delayed_attack_misses_target_outside_one_and_half_blocks", SootleHuntersServerTests::frostilyteDelayedAttackMissesTargetOutsideOneAndHalfBlocks, 25, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_frostilyte_rejects_drowning_damage", SootleHuntersServerTests::frostilyteRejectsDrowningDamage, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_frostilyte_custom_drop_hook_honors_entity_drops_game_rule", SootleHuntersServerTests::frostilyteCustomDropHookHonorsEntityDropsGameRule, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_razorwing_food_rules_separate_normal_and_armoured_variants", SootleHuntersServerTests::razorwingFoodRulesSeparateNormalAndArmouredVariants, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_owner_armor_interaction_converts_razorwing_and_consumes_one_armor", SootleHuntersServerTests::ownerArmorInteractionConvertsRazorwingAndConsumesOneArmor, 20, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_armoured_razorwing_interaction_makes_player_its_controlling_passenger", SootleHuntersServerTests::armouredRazorwingInteractionMakesPlayerItsControllingPassenger, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_razorwing_day_spawn_accepts_both_shales_and_rejects_stone", SootleHuntersServerTests::razorwingDaySpawnAcceptsBothShalesAndRejectsStone, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_razorwing_natural_spawn_rejects_night_after_world_time_advances", SootleHuntersServerTests::razorwingNaturalSpawnRejectsNightAfterWorldTimeAdvances, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_normal_razorwing_death_drops_configured_chicken_and_feather_ranges", SootleHuntersServerTests::normalRazorwingDeathDropsConfiguredChickenAndFeatherRanges, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_frostilyte_ice_drop_roll_uses_the_registered_ice_tag_and_deterministic_seed", SootleHuntersServerTests::frostilyteIceDropRollUsesTheRegisteredIceTagAndDeterministicSeed, 15, "supernova_server");
        TsrGameTests.add("history_sootle_hunters_server_tests_ruby_and_blood_armoured_razorwing_deaths_produce_no_unconfigured_drops", SootleHuntersServerTests::rubyAndBloodArmouredRazorwingDeathsProduceNoUnconfiguredDrops, 20, "supernova_server");
    }
    private SootleHuntersServerTests() { }
}












