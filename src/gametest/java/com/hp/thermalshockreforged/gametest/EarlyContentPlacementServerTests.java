package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.farming.CropKind;
import com.hp.thermalshockreforged.content.farming.FarmingContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
public final class EarlyContentPlacementServerTests {
    public static void register() {
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_ancient_skorio_on_planter_pot", EarlyContentPlacementServerTests::geoCropItemPlacesAncient_skorioOnPlanterPot, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_ancient_barbubble_on_planter_box", EarlyContentPlacementServerTests::geoCropItemPlacesAncient_barbubbleOnPlanterBox, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_dewleaf_on_planter_pot", EarlyContentPlacementServerTests::geoCropItemPlacesDewleafOnPlanterPot, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_hearthfly_branch_on_planter_box", EarlyContentPlacementServerTests::geoCropItemPlacesHearthfly_branchOnPlanterBox, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_frost_lotus_on_planter_pot", EarlyContentPlacementServerTests::geoCropItemPlacesFrost_lotusOnPlanterPot, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_neoflorm_on_planter_box", EarlyContentPlacementServerTests::geoCropItemPlacesNeoflormOnPlanterBox, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_volatine_on_planter_pot", EarlyContentPlacementServerTests::geoCropItemPlacesVolatineOnPlanterPot, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_ancient_skorio_on_planter_box", EarlyContentPlacementServerTests::geoCropItemPlacesAncient_skorioOnPlanterBox, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_ancient_barbubble_on_planter_pot", EarlyContentPlacementServerTests::geoCropItemPlacesAncient_barbubbleOnPlanterPot, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_dewleaf_on_planter_box", EarlyContentPlacementServerTests::geoCropItemPlacesDewleafOnPlanterBox, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_hearthfly_branch_on_planter_pot", EarlyContentPlacementServerTests::geoCropItemPlacesHearthfly_branchOnPlanterPot, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_frost_lotus_on_planter_box", EarlyContentPlacementServerTests::geoCropItemPlacesFrost_lotusOnPlanterBox, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_neoflorm_on_planter_pot", EarlyContentPlacementServerTests::geoCropItemPlacesNeoflormOnPlanterPot, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_geo_crop_item_places_volatine_on_planter_box", EarlyContentPlacementServerTests::geoCropItemPlacesVolatineOnPlanterBox, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_skorio_on_planter", EarlyContentPlacementServerTests::seedItemPlantsSkorioOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_barbubble_on_planter", EarlyContentPlacementServerTests::seedItemPlantsBarbubbleOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_ancient_skorio_on_planter", EarlyContentPlacementServerTests::seedItemPlantsAncient_skorioOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_ancient_barbubble_on_planter", EarlyContentPlacementServerTests::seedItemPlantsAncient_barbubbleOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_dewleaf_on_planter", EarlyContentPlacementServerTests::seedItemPlantsDewleafOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_hearthfly_branch_on_planter", EarlyContentPlacementServerTests::seedItemPlantsHearthfly_branchOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_frost_lotus_on_planter", EarlyContentPlacementServerTests::seedItemPlantsFrost_lotusOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_neoflorm_on_planter", EarlyContentPlacementServerTests::seedItemPlantsNeoflormOnPlanter, 40, "migration_empty");
        TsrGameTests.add("history_early_content_placement_server_tests_seed_item_plants_volatine_on_planter", EarlyContentPlacementServerTests::seedItemPlantsVolatineOnPlanter, 40, "migration_empty");
    }

    public static void geoCropItemPlacesAncient_skorioOnPlanterPot(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.ANCIENT_SKORIO), 2), CropKind.ANCIENT_SKORIO, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesAncient_barbubbleOnPlanterBox(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.ANCIENT_BARBUBBLE), 2), CropKind.ANCIENT_BARBUBBLE, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesDewleafOnPlanterPot(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.DEWLEAF), 2), CropKind.DEWLEAF, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesHearthfly_branchOnPlanterBox(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.HEARTHFLY_BRANCH), 2), CropKind.HEARTHFLY_BRANCH, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesFrost_lotusOnPlanterPot(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.FROST_LOTUS), 2), CropKind.FROST_LOTUS, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesNeoflormOnPlanterBox(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.NEOFLORM), 2), CropKind.NEOFLORM, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesVolatineOnPlanterPot(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.VOLATINE), 2), CropKind.VOLATINE, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesAncient_skorioOnPlanterBox(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.ANCIENT_SKORIO), 2), CropKind.ANCIENT_SKORIO, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesAncient_barbubbleOnPlanterPot(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.ANCIENT_BARBUBBLE), 2), CropKind.ANCIENT_BARBUBBLE, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesDewleafOnPlanterBox(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.DEWLEAF), 2), CropKind.DEWLEAF, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesHearthfly_branchOnPlanterPot(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.HEARTHFLY_BRANCH), 2), CropKind.HEARTHFLY_BRANCH, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesFrost_lotusOnPlanterBox(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.FROST_LOTUS), 2), CropKind.FROST_LOTUS, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesNeoflormOnPlanterPot(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.NEOFLORM), 2), CropKind.NEOFLORM, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void geoCropItemPlacesVolatineOnPlanterBox(GameTestHelper helper) {
        placeCropItemOnSoil(helper, new ItemStack(FarmingContent.geoItem(CropKind.VOLATINE), 2), CropKind.VOLATINE, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void seedItemPlantsSkorioOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.SKORIO).asItem(), 2), CropKind.SKORIO, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void seedItemPlantsBarbubbleOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.BARBUBBLE).asItem(), 2), CropKind.BARBUBBLE, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void seedItemPlantsAncient_skorioOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.ANCIENT_SKORIO).asItem(), 2), CropKind.ANCIENT_SKORIO, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void seedItemPlantsAncient_barbubbleOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.ANCIENT_BARBUBBLE).asItem(), 2), CropKind.ANCIENT_BARBUBBLE, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void seedItemPlantsDewleafOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.DEWLEAF).asItem(), 2), CropKind.DEWLEAF, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void seedItemPlantsHearthfly_branchOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.HEARTHFLY_BRANCH).asItem(), 2), CropKind.HEARTHFLY_BRANCH, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void seedItemPlantsFrost_lotusOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.FROST_LOTUS).asItem(), 2), CropKind.FROST_LOTUS, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }
    public static void seedItemPlantsNeoflormOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.NEOFLORM).asItem(), 2), CropKind.NEOFLORM, FarmingContent.PLANTER_BOX.get());
        helper.succeed();
    }
    public static void seedItemPlantsVolatineOnPlanter(GameTestHelper helper) {
        placeSeedItemOnPlanter(helper, new ItemStack(FarmingContent.seed(CropKind.VOLATINE).asItem(), 2), CropKind.VOLATINE, FarmingContent.PLANTER_POT.get());
        helper.succeed();
    }

    private static void placeCropItemOnSoil(GameTestHelper helper, ItemStack stack, CropKind kind, Block soil) {
        var level = helper.getLevel();
        BlockPos support = helper.absolutePos(new BlockPos(1, 1, 2));
        BlockPos position = support.above();
        level.setBlock(support, soil.defaultBlockState(), 3);
        var player = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "early-test"));
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        var hit = new BlockHitResult(Vec3.atCenterOf(support), Direction.UP, support, false);
        var result = stack.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        helper.assertTrue(result.consumesAction() && stack.getCount() == 1, "The GeoCrop item must place and consume exactly one item: " + kind.id + ", result=" + result);
        helper.assertTrue(level.getBlockState(position).is(FarmingContent.crop(kind)) && level.getBlockEntity(position) instanceof com.hp.thermalshockreforged.content.farming.GeoCropBlockEntity, "The GeoCrop item must place its matching mature block and block entity: " + kind.id);
    }

    private static void placeSeedItemOnPlanter(GameTestHelper helper, ItemStack stack, CropKind kind, Block soil) {
        var level = helper.getLevel();
        BlockPos support = helper.absolutePos(new BlockPos(1, 1, 2));
        BlockPos position = support.above();
        level.setBlock(support, soil.defaultBlockState(), 3);
        var player = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "early-test"));
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        var hit = new BlockHitResult(Vec3.atCenterOf(support), Direction.UP, support, false);
        var result = stack.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        helper.assertTrue(result.consumesAction() && stack.getCount() == 1, "The seed item must place and consume exactly one item: " + kind.id + ", result=" + result);
        helper.assertTrue(level.getBlockState(position).is(FarmingContent.seed(kind)) && level.getBlockEntity(position) instanceof com.hp.thermalshockreforged.content.farming.SeedBlockEntity, "The seed item must place its matching seed block and inventory: " + kind.id);
    }
}
