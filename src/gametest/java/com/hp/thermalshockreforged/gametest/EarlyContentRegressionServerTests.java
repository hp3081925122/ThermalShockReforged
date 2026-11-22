package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.farming.CropKind;
import com.hp.thermalshockreforged.content.farming.FarmingContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.geo.ShalePotBlock;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.pots.SkullscorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
public final class EarlyContentRegressionServerTests {
    public static void register() {
        TsrGameTests.add("history_early_content_regression_server_tests_unsupported_glitterdrop_icicle_removes_itself_on_placement", EarlyContentRegressionServerTests::unsupportedGlitterdropIcicleRemovesItselfOnPlacement, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_glitterdrop_icicle_accepts_water_as_non_air_support", EarlyContentRegressionServerTests::glitterdropIcicleAcceptsWaterAsNonAirSupport, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_removing_glitterdrop_support_removes_the_block", EarlyContentRegressionServerTests::removingGlitterdropSupportRemovesTheBlock, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_plain_icicle_contact_uses_stalagmite_damage_source", EarlyContentRegressionServerTests::plainIcicleContactUsesStalagmiteDamageSource, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_one_icicle_cannot_damage_two_entities_after_its_first_contact", EarlyContentRegressionServerTests::oneIcicleCannotDamageTwoEntitiesAfterItsFirstContact, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_inventory_survives_block_entity_save_load", EarlyContentRegressionServerTests::shalePotInventorySurvivesBlockEntitySaveLoad, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_item_handler_exposes_all_nine_slots_on_each_face", EarlyContentRegressionServerTests::shalePotItemHandlerExposesAllNineSlotsOnEachFace, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_rejects_null_sided_item_handler", EarlyContentRegressionServerTests::shalePotRejectsNullSidedItemHandler, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_sided_handlers_share_the_same_inventory", EarlyContentRegressionServerTests::shalePotSidedHandlersShareTheSameInventory, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_sided_handler_rejects_out_of_range_slots", EarlyContentRegressionServerTests::shalePotSidedHandlerRejectsOutOfRangeSlots, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_sided_handler_splits_input_at_the_vanilla_stack_limit", EarlyContentRegressionServerTests::shalePotSidedHandlerSplitsInputAtTheVanillaStackLimit, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_variant_change_retains_its_block_entity_inventory", EarlyContentRegressionServerTests::shalePotVariantChangeRetainsItsBlockEntityInventory, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_all_variant_states_preserve_independent_inventory", EarlyContentRegressionServerTests::shalePotAllVariantStatesPreserveIndependentInventory, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_explosion_releases_inventory_without_player_spawn_roll", EarlyContentRegressionServerTests::shalePotExplosionReleasesInventoryWithoutPlayerSpawnRoll, 50, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_creative_shale_pot_break_releases_stored_inventory", EarlyContentRegressionServerTests::creativeShalePotBreakReleasesStoredInventory, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_stale_shale_pot_break_does_not_duplicate_stored_inventory", EarlyContentRegressionServerTests::staleShalePotBreakDoesNotDuplicateStoredInventory, 40, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_shale_pot_spawn_roll_still_runs_for_variant_two", EarlyContentRegressionServerTests::shalePotSpawnRollStillRunsForVariantTwo, 50, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_stale_sickle_callbacks_do_not_harvest_any_single_block_crop_again", EarlyContentRegressionServerTests::staleSickleCallbacksDoNotHarvestAnySingleBlockCropAgain, 50, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_every_geo_crop_remains_on_its_allowed_planter_soil", EarlyContentRegressionServerTests::everyGeoCropRemainsOnItsAllowedPlanterSoil, 50, "supernova_server");
        TsrGameTests.add("history_early_content_regression_server_tests_stale_upper_double_plant_callback_does_not_add_a_second_harvest", EarlyContentRegressionServerTests::staleUpperDoublePlantCallbackDoesNotAddASecondHarvest, 50, "supernova_server");
    }
    public static void unsupportedGlitterdropIcicleRemovesItselfOnPlacement(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, NativeDecorContent.GLITTERDROP_ICICLE.get().defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(pos).isAir(), "An unsupported Glitterdrop icicle must remove itself immediately");
        helper.succeed();
    }
    public static void glitterdropIcicleAcceptsWaterAsNonAirSupport(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos.above(), Blocks.WATER.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.GLITTERDROP_ICICLE.get().defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(pos).is(NativeDecorContent.GLITTERDROP_ICICLE.get()), "Water above a Glitterdrop icicle must count as non-air support");
        helper.succeed();
    }
    public static void removingGlitterdropSupportRemovesTheBlock(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.GLITTERDROP_ICICLE.get().defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(pos).isAir(), "Removing Glitterdrop support must remove the icicle");
        helper.succeed();
    }
    public static void plainIcicleContactUsesStalagmiteDamageSource(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(1, 3, 2));
        level.setBlock(pos.below(2), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        Cow cow = helper.spawnWithNoFreeWill(EntityType.COW, 1.5f, 2.0f, 2.5f);
        float before = cow.getHealth();
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(cow.getHealth() == before - 1.0F && cow.getLastDamageSource() != null && cow.getLastDamageSource().is(net.minecraft.world.damagesource.DamageTypes.STALAGMITE), "Icicle damage mismatch: before=" + before + ", after=" + cow.getHealth() + ", lastSource=" + cow.getLastDamageSource());
            helper.succeed();
        });
    }
    public static void oneIcicleCannotDamageTwoEntitiesAfterItsFirstContact(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(1, 3, 2));
        level.setBlock(pos.below(2), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        Cow first = helper.spawnWithNoFreeWill(EntityType.COW, 1.5f, 2.0f, 2.5f);
        Cow second = helper.spawnWithNoFreeWill(EntityType.COW, 1.5f, 2.0f, 2.5f);
        float firstHealth = first.getHealth();
        float secondHealth = second.getHealth();
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(level.getBlockState(pos).isAir(), "The first contact must consume the icicle");
            float totalLoss = firstHealth - first.getHealth() + secondHealth - second.getHealth();
            helper.assertTrue(totalLoss == 1.0F, "A consumed icicle must deal exactly one total point across both entities: first=" + first.getHealth() + ", second=" + second.getHealth());
            helper.succeed();
        });
    }
    public static void shalePotInventorySurvivesBlockEntitySaveLoad(GameTestHelper helper) {
        var state = GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(pos, state, 3);
        var original = (GeoDecorBlockEntity) helper.getLevel().getBlockEntity(pos);
        original.setItem(0, new ItemStack(Items.DIAMOND, 7));
        original.setItem(8, new ItemStack(Items.EMERALD, 11));
        var saved = original.saveWithoutMetadata(helper.getLevel().registryAccess());
        var restored = new GeoDecorBlockEntity(GeoDecorKind.SHALE_POT, pos, state);
        restored.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), saved));
        helper.assertTrue(restored.getItem(0).is(Items.DIAMOND) && restored.getItem(0).getCount() == 7 && restored.getItem(8).is(Items.EMERALD) && restored.getItem(8).getCount() == 11, "Both ends of the Shale Pot inventory must survive NBT save and load");
        helper.succeed();
    }
    public static void shalePotItemHandlerExposesAllNineSlotsOnEachFace(GameTestHelper helper) {
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) helper.getLevel().getBlockEntity(pos);
        for (Direction side : Direction.values()) helper.assertTrue(entity.itemHandler(side).size() == 9, "Each sided item capability must expose nine slots");
        helper.succeed();
    }
    public static void shalePotRejectsNullSidedItemHandler(GameTestHelper helper) {
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(entity.itemHandler(null) == null, "The Shale Pot capability must not expose an unsided handler");
        helper.succeed();
    }
    public static void shalePotSidedHandlersShareTheSameInventory(GameTestHelper helper) {
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) helper.getLevel().getBlockEntity(pos);
        var simulatedInsert = insert(entity.itemHandler(Direction.NORTH), 0, new ItemStack(Items.DIAMOND, 7), false);
        helper.assertTrue(simulatedInsert.isEmpty() && entity.isEmpty(), "An uncommitted ResourceHandler insertion must roll back without changing the inventory");
        var inserted = insert(entity.itemHandler(Direction.NORTH), 0, new ItemStack(Items.DIAMOND, 7), true);
        var simulatedExtract = extract(entity.itemHandler(Direction.SOUTH), 0, 4, false);
        helper.assertTrue(inserted.isEmpty() && simulatedExtract.is(Items.DIAMOND) && simulatedExtract.getCount() == 4 && entity.getItem(0).getCount() == 7, "A rolled-back extraction must preview the requested amount while preserving the committed shared inventory");
        var extracted = extract(entity.itemHandler(Direction.SOUTH), 0, 4, true);
        helper.assertTrue(extracted.is(Items.DIAMOND) && extracted.getCount() == 4, "Insertions and extractions through different sides must operate on the same inventory");
        helper.assertTrue(entity.getItem(0).is(Items.DIAMOND) && entity.getItem(0).getCount() == 3, "The shared inventory must retain the exact unextracted remainder");
        helper.succeed();
    }
    public static void shalePotSidedHandlerRejectsOutOfRangeSlots(GameTestHelper helper) {
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) helper.getLevel().getBlockEntity(pos);
        var attempted = new ItemStack(Items.DIAMOND);
        boolean rejected = false;
        try { insert(entity.itemHandler(Direction.UP), 9, attempted, true); } catch (IndexOutOfBoundsException exception) { rejected = true; }
        helper.assertTrue(rejected && entity.isEmpty(), "An out-of-range item capability slot must reject input without changing the inventory");
        helper.succeed();
    }
    public static void shalePotSidedHandlerSplitsInputAtTheVanillaStackLimit(GameTestHelper helper) {
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) helper.getLevel().getBlockEntity(pos);
        var remainder = insert(entity.itemHandler(Direction.EAST), 0, new ItemStack(Items.STONE, 70), true);
        helper.assertTrue(entity.getItem(0).getCount() == 64 && remainder.getCount() == 6, "Sided insertion must respect the vanilla stack limit and return the uninserted remainder");
        helper.succeed();
    }
    public static void shalePotVariantChangeRetainsItsBlockEntityInventory(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = GeoDecorContent.block(GeoDecorKind.SHALE_POT);
        var state = block.defaultBlockState();
        level.setBlock(pos, state, 3);
        var entity = (GeoDecorBlockEntity) level.getBlockEntity(pos);
        entity.setItem(2, new ItemStack(Items.DIAMOND, 5));
        level.setBlock(pos, state.setValue(ShalePotBlock.VARIANT, 2), 3);
        helper.assertTrue(level.getBlockEntity(pos) == entity && entity.getItem(2).is(Items.DIAMOND) && entity.getItem(2).getCount() == 5, "Changing the Shale Pot visual variant must retain its existing inventory");
        helper.succeed();
    }
    public static void shalePotAllVariantStatesPreserveIndependentInventory(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = GeoDecorContent.block(GeoDecorKind.SHALE_POT);
        for (int variant = 0; variant < 3; variant++) {
            var state = block.defaultBlockState().setValue(ShalePotBlock.VARIANT, variant);
            level.setBlock(pos, state, 3);
            var entity = (GeoDecorBlockEntity) level.getBlockEntity(pos);
            entity.setItem(variant, new ItemStack(Items.DIAMOND, variant + 1));
            helper.assertTrue(entity.kind == GeoDecorKind.SHALE_POT && level.getBlockState(pos).getValue(ShalePotBlock.VARIANT) == variant, "Every variant must keep its own registered pot entity and state");
            helper.assertTrue(entity.getItem(variant).is(Items.DIAMOND) && entity.getItem(variant).getCount() == variant + 1, "Each variant must retain its assigned inventory slot");
        }
        helper.succeed();
    }
    public static void shalePotExplosionReleasesInventoryWithoutPlayerSpawnRoll(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        ((GeoDecorBlockEntity) level.getBlockEntity(pos)).setItem(4, new ItemStack(Items.DIAMOND, 7));
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0F, false, ExplosionInteraction.BLOCK);
        helper.runAfterDelay(2, () -> {
            int diamonds = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).stream().filter(item -> item.getItem().is(Items.DIAMOND)).mapToInt(item -> item.getItem().getCount()).sum();
            helper.assertTrue(level.getBlockState(pos).isAir() && diamonds == 7, "An explosion must destroy the pot and release its full inventory");
            helper.assertTrue(level.getEntitiesOfClass(SkullscorEntity.class, new AABB(pos).inflate(3)).isEmpty(), "An explosion must not invoke the player-only Skullscor chance");
            helper.succeed();
        });
    }
    public static void creativeShalePotBreakReleasesStoredInventory(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        ((GeoDecorBlockEntity) level.getBlockEntity(pos)).setItem(4, new ItemStack(Items.DIAMOND, 7));
        var player = helper.makeMockPlayer(GameType.CREATIVE);
        var state = level.getBlockState(pos);
        helper.assertTrue(state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "Creative-mode pot destruction must succeed");
        int dropped = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).stream().filter(item -> item.getItem().is(Items.DIAMOND)).mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertTrue(dropped == 7, "Creative-mode destruction must release every stored item exactly once");
        helper.succeed();
    }
    public static void staleShalePotBreakDoesNotDuplicateStoredInventory(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        ((GeoDecorBlockEntity) level.getBlockEntity(pos)).setItem(4, new ItemStack(Items.DIAMOND, 7));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var oldState = level.getBlockState(pos);
        helper.assertTrue(oldState.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, oldState.getFluidState()), "The first inventory pot break must succeed");
        int before = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).stream().filter(item -> item.getItem().is(Items.DIAMOND)).mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertTrue(!oldState.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, oldState.getFluidState()), "A stale pot break after removal must fail");
        int after = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).stream().filter(item -> item.getItem().is(Items.DIAMOND)).mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertTrue(before == 7 && after == before, "A stale pot callback must not duplicate its stored inventory");
        helper.succeed();
    }
    public static void shalePotSpawnRollStillRunsForVariantTwo(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = GeoDecorContent.block(GeoDecorKind.SHALE_POT);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        level.getRandom().setSeed(221L);
        int spawned = 0;
        for (int attempt = 0; attempt < 512; attempt++) {
            level.setBlock(pos, block.defaultBlockState().setValue(ShalePotBlock.VARIANT, 2), 3);
            var state = level.getBlockState(pos);
            helper.assertTrue(state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "Every variant-two trial must run the player break callback");
            var nearby = level.getEntitiesOfClass(SkullscorEntity.class, new AABB(pos).inflate(3));
            spawned += nearby.size();
            nearby.forEach(SkullscorEntity::discard);
            level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).forEach(ItemEntity::discard);
        }
        helper.assertTrue(spawned >= 2 && spawned <= 30, "Variant two must retain the same approximate two-point-five-percent player spawn chance: " + spawned);
        helper.succeed();
    }
    public static void staleSickleCallbacksDoNotHarvestAnySingleBlockCropAgain(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(WeaponContent.RUBY_SICKLE.get()));
        for (CropKind kind : CropKind.values()) {
            if (kind == CropKind.BARBUBBLE) continue;
            level.setBlock(pos.below(), FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
            level.setBlock(pos, FarmingContent.crop(kind).defaultBlockState(), 3);
            var stale = level.getBlockState(pos);
            helper.assertTrue(stale.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, stale.getFluidState()), "The first sickle callback must succeed for " + kind.id);
            int before = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).size();
            helper.assertTrue(!stale.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, stale.getFluidState()), "A stale callback must fail for " + kind.id);
            int after = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).size();
            helper.assertTrue(after == before, "A stale callback must not add harvest entities for " + kind.id);
            level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).forEach(ItemEntity::discard);
        }
        helper.succeed();
    }
    public static void everyGeoCropRemainsOnItsAllowedPlanterSoil(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        for (CropKind kind : CropKind.values()) {
            if (!kind.geo) continue;
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.setBlock(pos.below(), FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
            level.setBlock(pos, FarmingContent.crop(kind).defaultBlockState(), 3);
            helper.assertTrue(level.getBlockState(pos).is(FarmingContent.crop(kind)), "An allowed planter pot must support " + kind.id);
        }
        helper.succeed();
    }
    public static void staleUpperDoublePlantCallbackDoesNotAddASecondHarvest(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = (DoublePlantBlock) FarmingContent.crop(CropKind.BARBUBBLE);
        var lower = block.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        var upper = lower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
        level.setBlock(pos.below(), Blocks.DIRT.defaultBlockState(), 3);
        DoublePlantBlock.placeAt(level, lower, pos, 3);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(WeaponContent.RUBY_SICKLE.get()));
        helper.assertTrue(lower.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, lower.getFluidState()), "The initial lower-half break must succeed");
        int before = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).size();
        helper.assertTrue(!upper.onDestroyedByPlayer(level, pos.above(), player, ItemStack.EMPTY, true, upper.getFluidState()), "A stale upper-half callback must fail after the pair was removed");
        int after = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3)).size();
        helper.assertTrue(after == before, "The stale upper-half callback must not add a second harvest");
        helper.succeed();
    }

    private static ItemStack insert(ResourceHandler<ItemResource> handler, int slot, ItemStack stack, boolean commit) {
        int inserted;
        try (var transaction = Transaction.openRoot()) {
            inserted = handler.insert(slot, ItemResource.of(stack), stack.getCount(), transaction);
            if (commit) transaction.commit();
        }
        int remainder = stack.getCount() - inserted;
        return remainder == 0 ? ItemStack.EMPTY : stack.copyWithCount(remainder);
    }
    private static ItemStack extract(ResourceHandler<ItemResource> handler, int slot, int amount, boolean commit) {
        ItemResource resource = handler.getResource(slot);
        if (resource.isEmpty()) return ItemStack.EMPTY;
        int extracted;
        try (var transaction = Transaction.openRoot()) {
            extracted = handler.extract(slot, resource, amount, transaction);
            if (commit) transaction.commit();
        }
        return extracted == 0 ? ItemStack.EMPTY : resource.toStack(extracted);
    }
    private EarlyContentRegressionServerTests() { }
}
