package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.ModEffects;
import com.hp.thermalshockreforged.content.PondlerEggBlockEntity;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import com.hp.thermalshockreforged.entity.pots.PotContent;
import com.hp.thermalshockreforged.entity.snobol.SnobolContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
public final class EarlyContentServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_early_content_server_behavior_tests_pondler_egg_incubation_threshold_survives_save_load", EarlyContentServerBehaviorTests::pondlerEggIncubationThresholdSurvivesSaveLoad, 100, "migration_empty");
        TsrGameTests.add("history_early_content_server_behavior_tests_pondler_egg_nine_slots_and_all_sides_retain_inventory", EarlyContentServerBehaviorTests::pondlerEggNineSlotsAndAllSidesRetainInventory, 100, "migration_empty");
        TsrGameTests.add("history_early_content_server_behavior_tests_cocoa_completion_cures_frostbite_and_returns_bottle", EarlyContentServerBehaviorTests::cocoaCompletionCuresFrostbiteAndReturnsBottle, 100, "migration_empty");
        TsrGameTests.add("history_early_content_server_behavior_tests_panacea_completion_and_crafting_do_not_consume_the_item", EarlyContentServerBehaviorTests::panaceaCompletionAndCraftingDoNotConsumeTheItem, 100, "migration_empty");
        TsrGameTests.add("history_early_content_server_behavior_tests_antitoxin_completion_grants_long_effect_and_returns_flask", EarlyContentServerBehaviorTests::antitoxinCompletionGrantsLongEffectAndReturnsFlask, 100, "migration_empty");
        TsrGameTests.add("history_early_content_server_behavior_tests_pesticide_damages_only_nearby_skullscor", EarlyContentServerBehaviorTests::pesticideDamagesOnlyNearbySkullscor, 100, "supernova_server");
        TsrGameTests.add("history_early_content_server_behavior_tests_breather_sponge_retains_straw_costs_and_exact_cooldowns", EarlyContentServerBehaviorTests::breatherSpongeRetainsStrawCostsAndExactCooldowns, 100, "migration_empty");
        TsrGameTests.add("history_early_content_server_behavior_tests_heart_crystal_stacks_effect_and_consumes_one_per_use", EarlyContentServerBehaviorTests::heartCrystalStacksEffectAndConsumesOnePerUse, 100, "migration_empty");
    }

    public static void pondlerEggIncubationThresholdSurvivesSaveLoad(GameTestHelper helper) {
        var state = MaterialContent.blocks().get("pondler_egg").get().defaultBlockState();
        var egg = new PondlerEggBlockEntity(helper.absolutePos(new BlockPos(3, 2, 3)), state);
        for (int index = 0; index < 300; index++) helper.assertTrue(!egg.advanceIncubation(), "The first three hundred incubation calls must not hatch");
        CompoundTag saved = egg.saveWithoutMetadata(helper.getLevel().registryAccess());
        helper.assertTrue(saved.getIntOr("IncubationAge", 0) == 300, "The incubation age must be persisted");
        var restored = new PondlerEggBlockEntity(egg.getBlockPos(), state);
        restored.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), saved));
        for (int index = 300; index < 600; index++) helper.assertTrue(!restored.advanceIncubation(), "The restored egg must retain the original six-hundred-call threshold");
        helper.assertTrue(restored.advanceIncubation() && restored.advanceIncubation(), "Call six hundred and one and subsequent ready checks must return true");
        helper.succeed();
    }
    public static void pondlerEggNineSlotsAndAllSidesRetainInventory(GameTestHelper helper) {
        var state = MaterialContent.blocks().get("pondler_egg").get().defaultBlockState();
        var egg = new PondlerEggBlockEntity(helper.absolutePos(new BlockPos(3, 2, 3)), state);
        helper.assertTrue(egg.getContainerSize() == 9, "The egg inventory must retain all nine slots");
        for (Direction face : Direction.values()) {
            int[] slots = egg.getSlotsForFace(face);
            helper.assertTrue(slots.length == 9, "Every automation face must expose nine slots");
            for (int slot = 0; slot < 9; slot++) {
                helper.assertTrue(slots[slot] == slot && egg.canPlaceItemThroughFace(slot, new ItemStack(Items.STONE), face) && egg.canTakeItemThroughFace(slot, new ItemStack(Items.STONE), face), "Every slot must allow both automation directions");
            }
            slots[0] = 100;
            helper.assertTrue(egg.getSlotsForFace(face)[0] == 0, "Callers must not mutate the shared slot mapping");
        }
        egg.setItem(0, new ItemStack(Items.DIAMOND, 7));
        egg.setItem(8, new ItemStack(Items.STONE, 64));
        var restored = new PondlerEggBlockEntity(egg.getBlockPos(), state);
        restored.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), egg.saveWithoutMetadata(helper.getLevel().registryAccess())));
        helper.assertTrue(restored.getItem(0).is(Items.DIAMOND) && restored.getItem(0).getCount() == 7 && restored.getItem(8).is(Items.STONE) && restored.getItem(8).getCount() == 64, "Both inventory boundaries must retain item identities and counts across NBT");
        helper.succeed();
    }
    public static void cocoaCompletionCuresFrostbiteAndReturnsBottle(GameTestHelper helper) {
        var player = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "early-test"));
        player.getAbilities().instabuild = false;
        player.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 100, 0));
        var stack = new ItemStack(StatusContent.COCOA.get());
        var result = stack.finishUsingItem(helper.getLevel(), player);
        var regeneration = player.getEffect(MobEffects.REGENERATION);
        var warmth = player.getEffect(StatusContent.COCOA_EFFECT);
        helper.assertTrue(stack.isEmpty() && result.is(Items.GLASS_BOTTLE) && result.getCount() == 1, "Survival cocoa completion must consume one drink and return one bottle");
        helper.assertTrue(!player.hasEffect(StatusContent.FROSTBITE), "Cocoa must remove Frostbite");
        helper.assertTrue(regeneration != null && regeneration.getDuration() == 120 && regeneration.getAmplifier() == 1, "Cocoa regeneration duration and amplifier must match the original");
        helper.assertTrue(warmth != null && warmth.getDuration() == 1000 && warmth.getAmplifier() == 1, "Cocoa warmth duration and amplifier must match the original");
        helper.succeed();
    }
    public static void panaceaCompletionAndCraftingDoNotConsumeTheItem(GameTestHelper helper) {
        var consumer = EntityType.COW.create(helper.getLevel(), net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        helper.assertTrue(consumer != null, "The living consumer fixture must be created");
        var item = StatusContent.PANACEA.get();
        var stack = new ItemStack(item);
        var result = item.finishUsingItem(stack, helper.getLevel(), consumer);
        var purified = consumer.getEffect(StatusContent.PURIFIED);
        helper.assertTrue(result.is(item) && result.getCount() == 1 && stack.getCount() == 1, "Panacea must remain available after completed use");
        helper.assertTrue(purified != null && purified.getDuration() == 200 && purified.getAmplifier() == 0, "Panacea must apply the original purification duration");
        helper.assertTrue(item.getUseDuration(stack, consumer) == 60 && item.getCraftingRemainder(stack) != null && item.getCraftingRemainder(stack).is(item), "Use duration and reusable crafting remainder must be retained");
        helper.succeed();
    }
    public static void antitoxinCompletionGrantsLongEffectAndReturnsFlask(GameTestHelper helper) {
        var player = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "early-test"));
        player.getAbilities().instabuild = false;
        var stack = new ItemStack(SnobolContent.ANTITOXIN.get());
        var result = stack.finishUsingItem(helper.getLevel(), player);
        var antitoxin = player.getEffect(StatusContent.ANTITOXIN_EFFECT);
        helper.assertTrue(stack.isEmpty() && result.is(MaterialContent.items().get("corrial_flask").get()) && result.getCount() == 1, "The completed antitoxin must consume one drink and return its original flask");
        helper.assertTrue(antitoxin != null && antitoxin.getDuration() == 120000 && antitoxin.getAmplifier() == 1, "Antitoxin must retain its long original effect");
        helper.succeed();
    }
    public static void pesticideDamagesOnlyNearbySkullscor(GameTestHelper helper) {
        var level = helper.getLevel();
        Cow consumer = helper.spawnWithNoFreeWill(EntityType.COW, 5, 3, 8);
        var near = helper.spawnWithNoFreeWill(PotContent.SKULLSCOR.get(), 6, 3, 8);
        var far = helper.spawnWithNoFreeWill(PotContent.SKULLSCOR.get(), 9, 3, 8);
        Cow other = helper.spawnWithNoFreeWill(EntityType.COW, 5, 3, 9);
        float nearHealth = near.getHealth();
        float farHealth = far.getHealth();
        float consumerHealth = consumer.getHealth();
        float otherHealth = other.getHealth();
        var stack = new ItemStack(UtilityContent.PESTICIDE.get(), 2);
        stack.getItem().finishUsingItem(stack, level, consumer);
        helper.assertTrue(near.getHealth() < nearHealth, "The nearby Skullscor must receive pesticide damage");
        helper.assertTrue(far.getHealth() == farHealth && other.getHealth() == otherHealth && consumer.getHealth() == consumerHealth, "The outer Skullscor and all non-Skullscor entities must remain unharmed");
        helper.assertTrue(stack.getCount() == 2, "The original non-player completion path must not consume the item");
        helper.succeed();
    }
    public static void breatherSpongeRetainsStrawCostsAndExactCooldowns(GameTestHelper helper) {
        var player = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "early-test"));
        player.getAbilities().instabuild = false;
        var position = helper.absolutePos(new BlockPos(3, 2, 3));
        var state = TerrainContent.BREATHER_SPONGE.get().defaultBlockState();
        var hit = new BlockHitResult(Vec3.atCenterOf(position), Direction.UP, position, false);
        var ordinary = new ItemStack(MaterialContent.items().get("drinking_straw").get());
        player.setItemInHand(InteractionHand.MAIN_HAND, ordinary);
        player.setAirSupply(200);
        helper.assertTrue(state.useItemOn(ordinary, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit) == InteractionResult.SUCCESS, "An available ordinary straw must activate the sponge");
        helper.assertTrue(player.getAirSupply() == 260 && ordinary.getDamageValue() == 1, "An ordinary straw must restore sixty air and spend one durability");
        helper.assertTrue(state.useItemOn(ordinary, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit) == InteractionResult.TRY_WITH_EMPTY_HAND && player.getAirSupply() == 260 && ordinary.getDamageValue() == 1, "A cooldown retry must not restore air or spend durability");
        for (int tick = 0; tick < 19; tick++) player.getCooldowns().tick();
        helper.assertTrue(player.getCooldowns().isOnCooldown(ordinary), "The ordinary straw must still be cooling down after nineteen ticks");
        player.getCooldowns().tick();
        helper.assertTrue(!player.getCooldowns().isOnCooldown(ordinary), "The ordinary straw must become available at twenty ticks");
        state.useItemOn(ordinary, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
        helper.assertTrue(player.getAirSupply() == 300 && ordinary.getDamageValue() == 2, "Repeated legal drinking must cap air at three hundred");
        var silly = new ItemStack(MaterialContent.items().get("silly_straw").get());
        player.setItemInHand(InteractionHand.MAIN_HAND, silly);
        player.setAirSupply(1);
        helper.assertTrue(state.useItemOn(silly, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit) == InteractionResult.SUCCESS && player.getAirSupply() == 300 && silly.getDamageValue() == 1, "The silly straw must fill air completely for one durability");
        for (int tick = 0; tick < 59; tick++) player.getCooldowns().tick();
        helper.assertTrue(player.getCooldowns().isOnCooldown(silly), "The silly straw must still be cooling down after fifty-nine ticks");
        player.getCooldowns().tick();
        helper.assertTrue(!player.getCooldowns().isOnCooldown(silly), "The silly straw must become available at sixty ticks");
        helper.succeed();
    }
    public static void heartCrystalStacksEffectAndConsumesOnePerUse(GameTestHelper helper) {
        var player = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "early-test"));
        player.getAbilities().instabuild = false;
        var item = MaterialContent.items().get("heart_crystal").get();
        for (int use = 1; use <= 2; use++) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));
            item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            var boost = player.getEffect(ModEffects.HEART_BOOST);
            var regeneration = player.getEffect(MobEffects.REGENERATION);
            helper.assertTrue(player.getMainHandItem().isEmpty(), "Every completed crystal use must consume exactly one crystal");
            helper.assertTrue(boost != null && boost.getAmplifier() == use && boost.getDuration() == 5000, "Consecutive crystals must advance the original heart-boost amplifier");
            helper.assertTrue(regeneration != null && regeneration.getDuration() == 20 && regeneration.getAmplifier() == 9, "Crystal use must retain its short high-amplifier regeneration");
        }
        helper.succeed();
    }

    private EarlyContentServerBehaviorTests() { }
}

