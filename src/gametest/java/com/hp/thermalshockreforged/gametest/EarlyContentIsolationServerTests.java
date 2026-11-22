package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.ShalePotBlock;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import com.hp.thermalshockreforged.entity.pots.SkullscorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
public final class EarlyContentIsolationServerTests {
    public static void register() {
        TsrGameTests.add("history_early_content_isolation_server_tests_unsupported_icicle_removes_itself_on_placement", EarlyContentIsolationServerTests::unsupportedIcicleRemovesItselfOnPlacement, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_supported_icicle_remains_after_placement", EarlyContentIsolationServerTests::supportedIcicleRemainsAfterPlacement, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_non_air_fluid_above_icicle_counts_as_support", EarlyContentIsolationServerTests::nonAirFluidAboveIcicleCountsAsSupport, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_removing_icicle_support_removes_the_icicle", EarlyContentIsolationServerTests::removingIcicleSupportRemovesTheIcicle, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_entity_contact_consumes_icicle_and_deals_one_stalagmite_damage", EarlyContentIsolationServerTests::entityContactConsumesIcicleAndDealsOneStalagmiteDamage, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_glitterdrop_icicle_has_the_same_contact_break_and_damage_behavior", EarlyContentIsolationServerTests::glitterdropIcicleHasTheSameContactBreakAndDamageBehavior, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_successful_shale_pot_player_break_removes_the_block", EarlyContentIsolationServerTests::successfulShalePotPlayerBreakRemovesTheBlock, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_stale_shale_pot_player_break_cannot_spawn_skullscor_again", EarlyContentIsolationServerTests::staleShalePotPlayerBreakCannotSpawnSkullscorAgain, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_shale_pot_exposes_nine_slots_through_every_automation_face", EarlyContentIsolationServerTests::shalePotExposesNineSlotsThroughEveryAutomationFace, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_shale_pot_inventory_remains_owned_by_its_block_entity", EarlyContentIsolationServerTests::shalePotInventoryRemainsOwnedByItsBlockEntity, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_shale_pot_inventory_insertion_and_extraction_remain_open_on_every_side", EarlyContentIsolationServerTests::shalePotInventoryInsertionAndExtractionRemainOpenOnEverySide, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_shale_pot_player_break_drops_its_stored_inventory", EarlyContentIsolationServerTests::shalePotPlayerBreakDropsItsStoredInventory, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_every_shale_pot_variant_retains_the_player_break_path", EarlyContentIsolationServerTests::everyShalePotVariantRetainsThePlayerBreakPath, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_shale_pot_player_break_uses_the_original_two_point_five_percent_spawn_chance", EarlyContentIsolationServerTests::shalePotPlayerBreakUsesTheOriginalTwoPointFivePercentSpawnChance, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_shale_pot_explosion_does_not_use_player_break_spawn_hook", EarlyContentIsolationServerTests::shalePotExplosionDoesNotUsePlayerBreakSpawnHook, 50, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_pesticide_completion_consumes_one_survival_item_and_starts_cooldown", EarlyContentIsolationServerTests::pesticideCompletionConsumesOneSurvivalItemAndStartsCooldown, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_pesticide_completion_retains_original_creative_consumption", EarlyContentIsolationServerTests::pesticideCompletionRetainsOriginalCreativeConsumption, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_uncompleted_pesticide_use_has_no_consumption_or_cooldown", EarlyContentIsolationServerTests::uncompletedPesticideUseHasNoConsumptionOrCooldown, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_pesticide_completion_consumes_the_using_offhand_stack", EarlyContentIsolationServerTests::pesticideCompletionConsumesTheUsingOffhandStack, 40, "supernova_server");
        TsrGameTests.add("history_early_content_isolation_server_tests_rejected_non_player_pesticide_use_does_not_consume_or_cool_down", EarlyContentIsolationServerTests::rejectedNonPlayerPesticideUseDoesNotConsumeOrCoolDown, 40, "supernova_server");
    }
    public static void unsupportedIcicleRemovesItselfOnPlacement(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(pos).isAir(), "An icicle placed without a solid block above must remove itself immediately");
        helper.succeed();
    }
    public static void supportedIcicleRemainsAfterPlacement(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(pos).is(NativeDecorContent.ICICLES.get()), "An icicle attached to a solid block must remain in place");
        helper.succeed();
    }
    public static void nonAirFluidAboveIcicleCountsAsSupport(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos.above(), Blocks.WATER.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(pos).is(NativeDecorContent.ICICLES.get()), "Attachment logic must treat non-air fluid as a support state");
        helper.succeed();
    }
    public static void removingIcicleSupportRemovesTheIcicle(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(pos).isAir(), "Removing the supporting block must remove the attached icicle");
        helper.succeed();
    }
    public static void entityContactConsumesIcicleAndDealsOneStalagmiteDamage(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.ICICLES.get().defaultBlockState(), 3);
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, 5.5f, 2.0f, 8.5f);
        float before = cow.getHealth();
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(level.getBlockState(pos).isAir(), "Entity contact must consume the icicle exactly once");
            helper.assertTrue(cow.getHealth() == before - 1.0F, "Entity contact must apply one point of stalagmite damage");
            helper.succeed();
        });
    }
    public static void glitterdropIcicleHasTheSameContactBreakAndDamageBehavior(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(pos, NativeDecorContent.GLITTERDROP_ICICLE.get().defaultBlockState(), 3);
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, 5.5f, 2.0f, 8.5f);
        float before = cow.getHealth();
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(level.getBlockState(pos).isAir(), "Glitterdrop contact must consume the icicle");
            helper.assertTrue(cow.getHealth() == before - 1.0F, "Glitterdrop contact must apply one point of stalagmite damage");
            helper.succeed();
        });
    }
    public static void successfulShalePotPlayerBreakRemovesTheBlock(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var state = level.getBlockState(pos);
        helper.assertTrue(state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "A valid player break must be accepted");
        helper.assertTrue(level.getBlockState(pos).isAir(), "A successful player break must remove the shale pot before its spawn roll");
        helper.succeed();
    }
    public static void staleShalePotPlayerBreakCannotSpawnSkullscorAgain(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var state = level.getBlockState(pos);
        helper.assertTrue(state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "The first player break must be accepted");
        int spawnedAfterBreak = level.getEntitiesOfClass(SkullscorEntity.class, new AABB(pos).inflate(2)).size();
        helper.assertTrue(!state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "Repeating a stale player break after removal must fail");
        helper.assertTrue(level.getEntitiesOfClass(SkullscorEntity.class, new AABB(pos).inflate(2)).size() == spawnedAfterBreak, "A stale repeated callback must not create another Skullscor");
        helper.succeed();
    }
    public static void shalePotExposesNineSlotsThroughEveryAutomationFace(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) level.getBlockEntity(pos);
        helper.assertTrue(entity != null && entity.getContainerSize() == 9, "Shale Pot must expose its original nine-slot inventory");
        for (var face : net.minecraft.core.Direction.values()) {
            int[] slots = entity.getSlotsForFace(face);
            helper.assertTrue(slots.length == 9, "Every side must expose all nine Shale Pot slots");
            for (int slot = 0; slot < 9; slot++) helper.assertTrue(slots[slot] == slot, "Shale Pot side slot ordering must remain stable");
        }
        helper.succeed();
    }
    public static void shalePotInventoryRemainsOwnedByItsBlockEntity(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) level.getBlockEntity(pos);
        var stack = new ItemStack(net.minecraft.world.item.Items.DIAMOND, 7);
        entity.setItem(4, stack);
        helper.assertTrue(entity.getItem(4).is(net.minecraft.world.item.Items.DIAMOND) && entity.getItem(4).getCount() == 7, "The pot must retain actual item identity and count in its saved inventory");
        helper.assertTrue(entity.getItem(3).isEmpty() && entity.getItem(5).isEmpty(), "Writing one slot must not alter its neighbors");
        helper.succeed();
    }
    public static void shalePotInventoryInsertionAndExtractionRemainOpenOnEverySide(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) level.getBlockEntity(pos);
        for (var face : net.minecraft.core.Direction.values()) {
            for (int slot : entity.getSlotsForFace(face)) {
                helper.assertTrue(entity.canPlaceItemThroughFace(slot, new ItemStack(net.minecraft.world.item.Items.STONE), face), "Every Shale Pot slot must accept automation input from every face");
                helper.assertTrue(entity.canTakeItemThroughFace(slot, new ItemStack(net.minecraft.world.item.Items.STONE), face), "Every Shale Pot slot must allow automation extraction from every face");
            }
        }
        helper.succeed();
    }
    public static void shalePotPlayerBreakDropsItsStoredInventory(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        var entity = (GeoDecorBlockEntity) level.getBlockEntity(pos);
        entity.setItem(4, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND, 7));
        var state = level.getBlockState(pos);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "The inventory pot must accept a player break");
        int dropped = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(pos).inflate(3)).stream()
                .filter(item -> item.getItem().is(net.minecraft.world.item.Items.DIAMOND))
                .mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertTrue(dropped == 7, "Breaking a shale pot must release every stored diamond exactly once");
        helper.succeed();
    }
    public static void everyShalePotVariantRetainsThePlayerBreakPath(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = GeoDecorContent.block(GeoDecorKind.SHALE_POT);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int variant = 0; variant < 3; variant++) {
            level.setBlock(pos, block.defaultBlockState().setValue(ShalePotBlock.VARIANT, variant), 3);
            var state = level.getBlockState(pos);
            helper.assertTrue(state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "Every pot variant must accept the ordinary player break: " + variant);
            helper.assertTrue(level.getBlockState(pos).isAir(), "Every pot variant must be removed after the successful break: " + variant);
        }
        helper.succeed();
    }
    public static void shalePotPlayerBreakUsesTheOriginalTwoPointFivePercentSpawnChance(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = GeoDecorContent.block(GeoDecorKind.SHALE_POT);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        level.getRandom().setSeed(240L);
        int spawned = 0;
        for (int attempt = 0; attempt < 512; attempt++) {
            level.setBlock(pos, block.defaultBlockState(), 3);
            var state = level.getBlockState(pos);
            helper.assertTrue(state.onDestroyedByPlayer(level, pos, player, ItemStack.EMPTY, true, state.getFluidState()), "Each valid trial must run the player break callback");
            var nearby = level.getEntitiesOfClass(SkullscorEntity.class, new AABB(pos).inflate(3));
            spawned += nearby.size();
            nearby.forEach(SkullscorEntity::discard);
            level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(pos).inflate(3)).forEach(net.minecraft.world.entity.item.ItemEntity::discard);
        }
        helper.assertTrue(spawned >= 2 && spawned <= 30, "Five hundred twelve independent player breaks must produce a plausible count for the retained 2.5 percent chance: " + spawned);
        helper.succeed();
    }
    public static void shalePotExplosionDoesNotUsePlayerBreakSpawnHook(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(pos, GeoDecorContent.block(GeoDecorKind.SHALE_POT).defaultBlockState(), 3);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0F, false, ExplosionInteraction.BLOCK);
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(level.getBlockState(pos).isAir(), "The block explosion must destroy the ordinary shale pot");
            helper.assertTrue(level.getEntitiesOfClass(com.hp.thermalshockreforged.entity.pots.SkullscorEntity.class, new AABB(pos).inflate(2)).isEmpty(), "Explosion destruction must not invoke the player-only Skullscor chance");
            helper.succeed();
        });
    }
    public static void pesticideCompletionConsumesOneSurvivalItemAndStartsCooldown(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getAbilities().instabuild = false;
        var item = UtilityContent.PESTICIDE.get();
        var stack = new ItemStack(item, 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        helper.assertTrue(item.getUseDuration(stack, player) == 20, "Pesticide must retain the twenty-tick drinking duration");
        helper.assertTrue(item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND).consumesAction(), "Pesticide use must start through the real item use entry");
        item.finishUsingItem(stack, helper.getLevel(), player);
        helper.assertTrue(stack.getCount() == 2 && player.getMainHandItem().getCount() == 2, "Completing one survival use must consume exactly one item from the using hand");
        helper.assertTrue(player.getCooldowns().isOnCooldown(stack), "Completing pesticide use must start its item cooldown");
        for (int tick = 0; tick < 79; tick++) player.getCooldowns().tick();
        helper.assertTrue(player.getCooldowns().isOnCooldown(stack), "Pesticide cooldown must remain active through tick seventy-nine");
        player.getCooldowns().tick();
        helper.assertTrue(!player.getCooldowns().isOnCooldown(stack), "Pesticide cooldown must expire at tick eighty");
        helper.succeed();
    }
    public static void pesticideCompletionRetainsOriginalCreativeConsumption(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;
        var item = UtilityContent.PESTICIDE.get();
        var stack = new ItemStack(item, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        item.finishUsingItem(stack, helper.getLevel(), player);
        helper.assertTrue(stack.getCount() == 1 && player.getMainHandItem().getCount() == 1, "Creative completion must preserve the original one-item consumption behavior");
        helper.succeed();
    }
    public static void uncompletedPesticideUseHasNoConsumptionOrCooldown(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getAbilities().instabuild = false;
        var item = UtilityContent.PESTICIDE.get();
        var stack = new ItemStack(item, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.isUsingItem() && stack.getCount() == 2, "Starting the drink must not consume the pesticide before completion");
        helper.assertTrue(!player.getCooldowns().isOnCooldown(stack), "An interrupted, unfinished drink must not start cooldown");
        helper.succeed();
    }
    public static void pesticideCompletionConsumesTheUsingOffhandStack(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getAbilities().instabuild = false;
        var item = UtilityContent.PESTICIDE.get();
        var stack = new ItemStack(item, 2);
        player.setItemInHand(InteractionHand.OFF_HAND, stack);
        item.finishUsingItem(stack, helper.getLevel(), player);
        helper.assertTrue(player.getOffhandItem().getCount() == 1 && player.getMainHandItem().isEmpty(), "Completing an offhand use must consume one item from that hand only");
        helper.succeed();
    }
    public static void rejectedNonPlayerPesticideUseDoesNotConsumeOrCoolDown(GameTestHelper helper) {
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, 5, 2, 8);
        var item = UtilityContent.PESTICIDE.get();
        var stack = new ItemStack(item, 2);
        item.finishUsingItem(stack, helper.getLevel(), cow);
        helper.assertTrue(stack.getCount() == 2, "A non-player consumer must not consume the pesticide stack");
        helper.succeed();
    }

    private EarlyContentIsolationServerTests() { }
}
