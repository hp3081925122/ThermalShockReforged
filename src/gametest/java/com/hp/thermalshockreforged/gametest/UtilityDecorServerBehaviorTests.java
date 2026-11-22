package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.geo.ShalePillarBlock;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class UtilityDecorServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_utility_decor_server_behavior_tests_bottled_loque_milk_retains_food_container_and_original_non_curing_behavior", UtilityDecorServerBehaviorTests::bottledLoqueMilkRetainsFoodContainerAndOriginalNonCuringBehavior, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_bucket_loque_milk_retains_nutrition_and_returns_one_bucket", UtilityDecorServerBehaviorTests::bucketLoqueMilkRetainsNutritionAndReturnsOneBucket, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_non_player_milk_consumption_returns_the_original_containers", UtilityDecorServerBehaviorTests::nonPlayerMilkConsumptionReturnsTheOriginalContainers, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_spice_direct_use_has_no_invented_consumption_or_status_effect", UtilityDecorServerBehaviorTests::spiceDirectUseHasNoInventedConsumptionOrStatusEffect, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_chisel_item_callback_alone_does_not_change_unrelated_blocks_or_consume_the_tool", UtilityDecorServerBehaviorTests::chiselItemCallbackAloneDoesNotChangeUnrelatedBlocksOrConsumeTheTool, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_all_eight_pillar_styles_retain_facing_water_and_inventory_without_duplicate_drops", UtilityDecorServerBehaviorTests::allEightPillarStylesRetainFacingWaterAndInventoryWithoutDuplicateDrops, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_crystalleye_cycles_all_three_styles_and_rejects_other_tools", UtilityDecorServerBehaviorTests::crystalleyeCyclesAllThreeStylesAndRejectsOtherTools, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_icicle_chisel_cycles_three_states_and_gold_rejects_non_base_style", UtilityDecorServerBehaviorTests::icicleChiselCyclesThreeStatesAndGoldRejectsNonBaseStyle, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_base_icicle_gold_conversion_consumes_exactly_one_nugget", UtilityDecorServerBehaviorTests::baseIcicleGoldConversionConsumesExactlyOneNugget, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_constellation_block_must_have_runtime_block_and_item_registration", UtilityDecorServerBehaviorTests::constellationBlockMustHaveRuntimeBlockAndItemRegistration, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_reinforced_window_must_have_runtime_block_and_item_registration", UtilityDecorServerBehaviorTests::reinforcedWindowMustHaveRuntimeBlockAndItemRegistration, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_trident_lamp_must_have_runtime_block_and_item_registration", UtilityDecorServerBehaviorTests::tridentLampMustHaveRuntimeBlockAndItemRegistration, 100, "supernova_server");
        TsrGameTests.add("history_utility_decor_server_behavior_tests_brewing_stand_filter_regression", UtilityDecorServerBehaviorTests::brewingStandFilterRegression, 100, "supernova_server");
    }

    public static void bottledLoqueMilkRetainsFoodContainerAndOriginalNonCuringBehavior(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(0);
        player.getFoodData().setSaturation(0);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
        player.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 200, 0));
        var item = UtilityContent.LOQUE_MILK_BOTTLE.get();
        var stack = new ItemStack(item);
        helper.assertTrue(item.getUseDuration(stack, player) == 20 && item.getUseAnimation(stack) == ItemUseAnimation.DRINK && stack.getMaxStackSize() == 1, "Bottled milk must retain its drinking time and single-item stack");
        var result = stack.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(result.is(Items.GLASS_BOTTLE) && result.getCount() == 1 && player.getFoodData().getFoodLevel() == 1 && player.getFoodData().getSaturationLevel() == 1, "Bottled milk must give its original food and return one bottle");
        helper.assertTrue(player.hasEffect(MobEffects.POISON) && player.hasEffect(StatusContent.FROSTBITE), "Loque milk must preserve the original non-curing behavior rather than invent vanilla milk cleansing");
        helper.succeed();
    }

    public static void bucketLoqueMilkRetainsNutritionAndReturnsOneBucket(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(0);
        player.getFoodData().setSaturation(0);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
        var item = UtilityContent.LOQUE_MILK.get();
        var stack = new ItemStack(item);
        helper.assertTrue(item.getUseDuration(stack, player) == 45 && item.getUseAnimation(stack) == ItemUseAnimation.DRINK && stack.getMaxStackSize() == 1, "Bucket milk must retain its original drinking duration and stack limit");
        var result = stack.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(result.is(Items.BUCKET) && result.getCount() == 1 && player.getFoodData().getFoodLevel() == 5 && player.getFoodData().getSaturationLevel() == 5 && player.hasEffect(MobEffects.POISON), "Milk must give five food, cap saturation to the food level and return exactly one bucket without cleansing");
        helper.succeed();
    }

    public static void nonPlayerMilkConsumptionReturnsTheOriginalContainers(GameTestHelper helper) {
        var cow = EntityType.COW.create(helper.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        Item[] items = {UtilityContent.LOQUE_MILK_BOTTLE.get(), UtilityContent.LOQUE_MILK.get()};
        Item[] containers = {Items.GLASS_BOTTLE, Items.BUCKET};
        for (int index = 0; index < items.length; index++) {
            var result = items[index].finishUsingItem(new ItemStack(items[index]), helper.getLevel(), cow);
            helper.assertTrue(result.is(containers[index]) && result.getCount() == 1, "Non-player milk consumption must return one matching container");
        }
        helper.succeed();
    }

    public static void spiceDirectUseHasNoInventedConsumptionOrStatusEffect(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var item = UtilityContent.ADAMANTINE_SPICE.get();
        var stack = new ItemStack(item, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        var started = item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(started == InteractionResult.CONSUME && player.isUsingItem() && item.getUseDuration(stack, player) == 10, "Spice must retain its original ten-tick direct-use action");
        var result = item.finishUsingItem(stack, helper.getLevel(), player);
        helper.assertTrue(result == stack && stack.getCount() == 4 && player.getActiveEffects().isEmpty(), "Direct spice completion must not invent food, effects or costs belonging to the separate feeding interaction");
        player.stopUsingItem();
        helper.succeed();
    }

    public static void chiselItemCallbackAloneDoesNotChangeUnrelatedBlocksOrConsumeTheTool(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position, Blocks.STONE.defaultBlockState(), 3);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var item = UtilityContent.CHISEL.get();
        var stack = new ItemStack(item);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        var result = item.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit(position)));
        helper.assertTrue(result == InteractionResult.SUCCESS && level.getBlockState(position).is(Blocks.STONE) && stack.getCount() == 1 && stack.getDamageValue() == 0, "The original sound-only tool callback must preserve unrelated blocks and the tool stack");
        helper.succeed();
    }

    public static void allEightPillarStylesRetainFacingWaterAndInventoryWithoutDuplicateDrops(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        var styles = new GeoDecorKind[]{GeoDecorKind.SHALE_PILLAR, GeoDecorKind.SHALE_PILLAR_VAR_2, GeoDecorKind.SHALE_PILLAR_VAR_3, GeoDecorKind.SHALE_PILLAR_VAR_4, GeoDecorKind.SHALE_PILLAR_VAR_5, GeoDecorKind.SHALE_PILLAR_VAR_6, GeoDecorKind.SHALE_PILLAR_VAR_7, GeoDecorKind.SHALE_PILLAR_VAR_8};
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var tool = new ItemStack(UtilityContent.CHISEL.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        level.setBlock(position, GeoDecorContent.block(styles[0]).defaultBlockState().setValue(GeoDecorBlock.Directional.FACING, Direction.EAST).setValue(ShalePillarBlock.WATERLOGGED, true), 3);
        var inventory = (GeoDecorBlockEntity) level.getBlockEntity(position);
        inventory.setItem(0, new ItemStack(Items.DIAMOND, 7));
        inventory.setItem(8, new ItemStack(Items.GOLD_INGOT, 11));
        for (int index = 0; index < styles.length; index++) {
            var state = level.getBlockState(position);
            helper.assertTrue(state.useItemOn(tool, level, player, InteractionHand.MAIN_HAND, hit(position)) == InteractionResult.SUCCESS, "Each pillar style must accept the chisel");
            var next = level.getBlockState(position);
            helper.assertTrue(next.is(GeoDecorContent.block(styles[(index + 1) % styles.length])) && next.getValue(GeoDecorBlock.Directional.FACING) == Direction.EAST && next.getValue(ShalePillarBlock.WATERLOGGED), "Style cycling must preserve facing and water state through the full wrap");
            var current = (GeoDecorBlockEntity) level.getBlockEntity(position);
            helper.assertTrue(current.getItem(0).is(Items.DIAMOND) && current.getItem(0).getCount() == 7 && current.getItem(8).is(Items.GOLD_INGOT) && current.getItem(8).getCount() == 11, "Replacing a pillar style must migrate the actual first and last inventory slots");
        }
        helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(3)).isEmpty() && tool.getCount() == 1, "A full style cycle must not duplicate inventory into drops or consume the chisel");
        helper.succeed();
    }

    public static void crystalleyeCyclesAllThreeStylesAndRejectsOtherTools(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        var blocks = new net.minecraft.world.level.block.Block[]{NativeDecorContent.CHISELED_MANTLESTONE.get(), NativeDecorContent.RED_CRYSTALLEYE_LAMP.get(), NativeDecorContent.BLUE_CRYSTALLEYE_LAMP.get()};
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var tool = new ItemStack(UtilityContent.CHISEL.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        level.setBlock(position, blocks[0].defaultBlockState(), 3);
        for (int index = 0; index < blocks.length; index++) {
            var result = level.getBlockState(position).useItemOn(tool, level, player, InteractionHand.MAIN_HAND, hit(position));
            helper.assertTrue(result == InteractionResult.SUCCESS && level.getBlockState(position).is(blocks[(index + 1) % blocks.length]), "Crystalleye must retain its three-style cycle");
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
        var state = level.getBlockState(position);
        helper.assertTrue(state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit(position)) == InteractionResult.TRY_WITH_EMPTY_HAND && level.getBlockState(position).equals(state), "A non-chisel must not cycle Crystalleye");
        helper.succeed();
    }

    public static void icicleChiselCyclesThreeStatesAndGoldRejectsNonBaseStyle(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(position, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        var property = (IntegerProperty) level.getBlockState(position).getBlock().getStateDefinition().getProperty("blockstate");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var tool = new ItemStack(UtilityContent.CHISEL.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        for (int expected : new int[]{1, 2, 0}) {
            level.getBlockState(position).useItemOn(tool, level, player, InteractionHand.MAIN_HAND, hit(position));
            helper.assertTrue(level.getBlockState(position).is(NativeDecorContent.ICICLES.get()) && level.getBlockState(position).getValue(property) == expected, "The icicle chisel must retain all three styles and wrap to zero");
        }
        level.setBlock(position, level.getBlockState(position).setValue(property, 1), 3);
        var gold = new ItemStack(Items.GOLD_NUGGET, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, gold);
        level.getBlockState(position).useItemOn(gold, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(level.getBlockState(position).is(NativeDecorContent.ICICLES.get()) && level.getBlockState(position).getValue(property) == 1 && gold.getCount() == 2, "Gold must not convert or be consumed by a non-base icicle style");
        helper.succeed();
    }

    public static void baseIcicleGoldConversionConsumesExactlyOneNugget(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(position, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var gold = new ItemStack(Items.GOLD_NUGGET, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, gold);
        level.getBlockState(position).useItemOn(gold, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(level.getBlockState(position).is(NativeDecorContent.GLITTERDROP_ICICLE.get()) && gold.getCount() == 1, "The base icicle must convert to Glitterdrop by spending exactly one gold nugget");
        helper.succeed();
    }

    public static void constellationBlockMustHaveRuntimeBlockAndItemRegistration(GameTestHelper helper) { requireRegistration(helper, "constellation_block", "SB-015"); }

    public static void reinforcedWindowMustHaveRuntimeBlockAndItemRegistration(GameTestHelper helper) { requireRegistration(helper, "reinforced_window", "SB-016"); }

    public static void tridentLampMustHaveRuntimeBlockAndItemRegistration(GameTestHelper helper) { requireRegistration(helper, "trident_lamp", "SB-017"); }

    public static void brewingStandFilterRegression(GameTestHelper helper) { NativeBrewingServerBehaviorTests.standAcceptsCustomInputsAndRetainsNativeSidedFilters(helper); }

    private static void requireRegistration(GameTestHelper helper, String id, String issue) {
        var key = TsrGameTests.id(id);
        helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(key) && BuiltInRegistries.ITEM.containsKey(key), issue + ": The migrated original block and its item must exist in the live server registries: " + key);
        helper.succeed();
    }

    private static BlockHitResult hit(BlockPos position) { return new BlockHitResult(Vec3.atCenterOf(position), Direction.NORTH, position, false); }
    private UtilityDecorServerBehaviorTests() { }
}







