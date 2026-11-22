package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class NativeBrewingServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_native_brewing_server_behavior_tests_all_ten_recipes_use_the_real_server_brewing_table", NativeBrewingServerBehaviorTests::allTenRecipesUseTheRealServerBrewingTable, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_custom_recipes_reject_stacked_lower_inputs", NativeBrewingServerBehaviorTests::customRecipesRejectStackedLowerInputs, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_invalid_ingredients_leave_all_ten_inputs_unchanged", NativeBrewingServerBehaviorTests::invalidIngredientsLeaveAllTenInputsUnchanged, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_stand_accepts_custom_inputs_and_retains_native_sided_filters", NativeBrewingServerBehaviorTests::standAcceptsCustomInputsAndRetainsNativeSidedFilters, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_one_ingredient_brews_three_lower_slots_at_the_four_hundred_tick_boundary", NativeBrewingServerBehaviorTests::oneIngredientBrewsThreeLowerSlotsAtTheFourHundredTickBoundary, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_shared_starfruit_brews_bottle_and_snowcone_without_changing_unmatched_potion", NativeBrewingServerBehaviorTests::sharedStarfruitBrewsBottleAndSnowconeWithoutChangingUnmatchedPotion, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_milk_ingredient_returns_exactly_one_bucket_after_three_bottle_conversions", NativeBrewingServerBehaviorTests::milkIngredientReturnsExactlyOneBucketAfterThreeBottleConversions, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_no_fuel_prevents_brewing_and_leaves_every_input_untouched", NativeBrewingServerBehaviorTests::noFuelPreventsBrewingAndLeavesEveryInputUntouched, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_replacing_ingredient_aborts_the_current_cycle_without_brewing", NativeBrewingServerBehaviorTests::replacingIngredientAbortsTheCurrentCycleWithoutBrewing, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_in_progress_nbt_reload_resumes_with_the_original_deadline_and_fuel", NativeBrewingServerBehaviorTests::inProgressNbtReloadResumesWithTheOriginalDeadlineAndFuel, 100, "supernova_server");
        TsrGameTests.add("history_native_brewing_server_behavior_tests_subsequent_cycle_uses_stored_fuel_without_another_powder", NativeBrewingServerBehaviorTests::subsequentCycleUsesStoredFuelWithoutAnotherPowder, 100, "supernova_server");
        TsrGameTests.addOptional("history_native_brewing_server_behavior_tests_cancelled_unchanged_brew_must_not_publish_a_success_post_event", NativeBrewingServerBehaviorTests::cancelledUnchangedBrewMustNotPublishASuccessPostEvent, 100, "supernova_server");
    }

    public static void allTenRecipesUseTheRealServerBrewingTable(GameTestHelper helper) {
        var brewing = helper.getLevel().potionBrewing();
        for (Recipe recipe : recipes()) {
            ItemStack input = recipe.input.copy();
            ItemStack ingredient = recipe.ingredient.copy();
            helper.assertTrue(brewing.isInput(input) && brewing.isIngredient(ingredient) && brewing.hasMix(input, ingredient), "Every migrated recipe must be registered in the actual server brewing table");
            ItemStack result = brewing.mix(ingredient, input);
            helper.assertTrue(ItemStack.matches(result, recipe.output), "Every recipe must produce its exact item, count and potion components");
            helper.assertTrue(ItemStack.matches(input, recipe.input) && ItemStack.matches(ingredient, recipe.ingredient), "Table lookup must not consume or mutate the caller's input stacks");
        }
        helper.succeed();
    }

    public static void customRecipesRejectStackedLowerInputs(GameTestHelper helper) {
        var brewing = helper.getLevel().potionBrewing();
        Recipe[] recipes = recipes();
        for (int index : new int[]{0, 1, 2, 3, 4, 5, 6, 9}) {
            Recipe recipe = recipes[index];
            ItemStack stacked = recipe.input.copyWithCount(2);
            helper.assertTrue(!brewing.hasMix(stacked, recipe.ingredient) && ItemStack.matches(brewing.mix(recipe.ingredient, stacked), stacked), "NeoForge custom recipes must reject a lower stack count other than one without losing it");
        }
        helper.succeed();
    }

    public static void invalidIngredientsLeaveAllTenInputsUnchanged(GameTestHelper helper) {
        var brewing = helper.getLevel().potionBrewing();
        var invalid = new ItemStack(Items.DIAMOND);
        helper.assertTrue(!brewing.isIngredient(invalid), "Diamond must not become a brewing ingredient");
        for (Recipe recipe : recipes()) {
            var input = recipe.input.copy();
            helper.assertTrue(!brewing.hasMix(input, invalid) && ItemStack.matches(brewing.mix(invalid, input), recipe.input), "An unmatched ingredient must preserve all input components and counts");
        }
        helper.succeed();
    }

    public static void standAcceptsCustomInputsAndRetainsNativeSidedFilters(GameTestHelper helper) {
        var stand = stand(helper);
        for (Recipe recipe : recipes()) {
            for (int slot = 0; slot < 3; slot++) helper.assertTrue(stand.canPlaceItem(slot, recipe.input), "Every migrated input must be accepted by the three native brewing slots");
            helper.assertTrue(stand.canPlaceItem(3, recipe.ingredient), "Every migrated ingredient must be accepted by the native ingredient slot");
        }
        helper.assertTrue(stand.getContainerSize() == 5, "The stand must retain all five native slots");
        helper.assertTrue(stand.canPlaceItem(4, new ItemStack(Items.BLAZE_POWDER)) && !stand.canPlaceItem(4, new ItemStack(Items.STONE)), "Only blaze powder must pass the native fuel filter");
        helper.assertTrue(stand.canPlaceItem(3, new ItemStack(Items.STONE)) && !stand.canPlaceItem(3, new ItemStack(Items.DIAMOND)), "The ingredient filter must accept native Infested stone and reject diamond");
        helper.assertTrue(java.util.Arrays.equals(stand.getSlotsForFace(Direction.UP), new int[]{3}) && java.util.Arrays.equals(stand.getSlotsForFace(Direction.DOWN), new int[]{0, 1, 2, 3}), "Top and bottom automation must retain their exact native slots");
        for (Direction side : Direction.Plane.HORIZONTAL) helper.assertTrue(java.util.Arrays.equals(stand.getSlotsForFace(side), new int[]{0, 1, 2, 4}), "All horizontal sides must expose bottles and fuel");
        helper.assertTrue(!stand.canTakeItemThroughFace(3, new ItemStack(UtilityContent.LOQUE_MILK.get()), Direction.DOWN) && stand.canTakeItemThroughFace(3, new ItemStack(Items.GLASS_BOTTLE), Direction.DOWN), "The native ingredient extraction filter must remain intact");
        helper.succeed();
    }

    public static void oneIngredientBrewsThreeLowerSlotsAtTheFourHundredTickBoundary(GameTestHelper helper) {
        var stand = stand(helper);
        for (int slot = 0; slot < 3; slot++) stand.setItem(slot, new ItemStack(material("dew_bottle")));
        stand.setItem(3, new ItemStack(material("hearth_honey"), 2));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));
        tick(helper, stand, 1);
        helper.assertTrue(brewTime(helper, stand) == 400 && fuel(helper, stand) == 19 && stand.getItem(4).isEmpty() && stand.getItem(3).getCount() == 2, "Starting must spend one fuel charge and preserve the ingredient until completion");
        tick(helper, stand, 399);
        helper.assertTrue(brewTime(helper, stand) == 1 && stand.getItem(0).is(material("dew_bottle")) && stand.getItem(3).getCount() == 2, "The last incomplete tick must not transform inputs or consume ingredient");
        tick(helper, stand, 1);
        var expected = PotionContents.createItemStack(Items.POTION, Potions.REGENERATION);
        for (int slot = 0; slot < 3; slot++) helper.assertTrue(ItemStack.matches(stand.getItem(slot), expected), "One ingredient must brew all three lower inputs");
        helper.assertTrue(stand.getItem(3).getCount() == 1 && fuel(helper, stand) == 19 && brewTime(helper, stand) == 0, "A three-slot brew must consume exactly one ingredient and one fuel charge");
        helper.succeed();
    }

    public static void sharedStarfruitBrewsBottleAndSnowconeWithoutChangingUnmatchedPotion(GameTestHelper helper) {
        var stand = stand(helper);
        stand.setItem(0, new ItemStack(Items.GLASS_BOTTLE));
        stand.setItem(1, new ItemStack(material("icy_snowcone")));
        var unmatched = PotionContents.createItemStack(Items.POTION, Potions.AWKWARD);
        stand.setItem(2, unmatched.copy());
        stand.setItem(3, new ItemStack(material("starfruit_item")));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));
        tick(helper, stand, 401);
        helper.assertTrue(stand.getItem(0).is(GeoDecorContent.item(GeoDecorKind.BOTTLE_OF_STARLIGHT)) && stand.getItem(1).is(material("starry_snowcone")) && ItemStack.matches(stand.getItem(2), unmatched), "One ingredient must independently convert both matching item kinds while preserving an unmatched slot");
        helper.assertTrue(stand.getItem(0).getCount() == 1 && stand.getItem(1).getCount() == 1 && stand.getItem(3).isEmpty(), "A mixed brew must not duplicate products or ingredient");
        helper.succeed();
    }

    public static void milkIngredientReturnsExactlyOneBucketAfterThreeBottleConversions(GameTestHelper helper) {
        var stand = stand(helper);
        for (int slot = 0; slot < 3; slot++) stand.setItem(slot, new ItemStack(Items.GLASS_BOTTLE));
        stand.setItem(3, new ItemStack(UtilityContent.LOQUE_MILK.get()));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));
        tick(helper, stand, 401);
        for (int slot = 0; slot < 3; slot++) helper.assertTrue(stand.getItem(slot).is(UtilityContent.LOQUE_MILK_BOTTLE.get()) && stand.getItem(slot).getCount() == 1, "Milk must fill each of the three lower bottle slots");
        helper.assertTrue(stand.getItem(3).is(Items.BUCKET) && stand.getItem(3).getCount() == 1 && fuel(helper, stand) == 19, "The consumed milk ingredient must leave exactly one bucket, independent of output count");
        helper.succeed();
    }

    public static void noFuelPreventsBrewingAndLeavesEveryInputUntouched(GameTestHelper helper) {
        var stand = stand(helper);
        stand.setItem(0, new ItemStack(material("icy_snowcone")));
        stand.setItem(3, new ItemStack(material("starfruit_item"), 2));
        tick(helper, stand, 401);
        helper.assertTrue(stand.getItem(0).is(material("icy_snowcone")) && stand.getItem(3).getCount() == 2 && brewTime(helper, stand) == 0 && fuel(helper, stand) == 0, "A valid recipe without fuel must not progress or consume anything");
        helper.succeed();
    }

    public static void replacingIngredientAbortsTheCurrentCycleWithoutBrewing(GameTestHelper helper) {
        var stand = stand(helper);
        stand.setItem(0, new ItemStack(material("icy_snowcone")));
        stand.setItem(3, new ItemStack(material("starfruit_item")));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));
        tick(helper, stand, 1);
        stand.setItem(3, new ItemStack(Items.DIAMOND, 2));
        tick(helper, stand, 1);
        helper.assertTrue(brewTime(helper, stand) == 0 && fuel(helper, stand) == 19 && stand.getItem(0).is(material("icy_snowcone")) && stand.getItem(3).is(Items.DIAMOND) && stand.getItem(3).getCount() == 2, "An ingredient swap must abort without transforming input, consuming the replacement or refunding the already spent charge");
        helper.succeed();
    }

    public static void inProgressNbtReloadResumesWithTheOriginalDeadlineAndFuel(GameTestHelper helper) {
        var stand = stand(helper);
        stand.setItem(0, PotionContents.createItemStack(Items.POTION, Potions.AWKWARD));
        stand.setItem(3, new ItemStack(material("bioelectric_cell")));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));
        tick(helper, stand, 124);
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        stand.saveWithoutMetadata(output);
        var saved = output.buildResult();
        helper.assertTrue(saved.getShortOr("BrewTime", (short) 0) == 277 && saved.getByteOr("Fuel", (byte) 0) == 19, "Mid-cycle NBT must retain the exact remaining time and fuel");
        var position = stand.getBlockPos();
        helper.getLevel().setBlock(position, Blocks.AIR.defaultBlockState(), 3);
        helper.getLevel().setBlock(position, Blocks.BREWING_STAND.defaultBlockState(), 3);
        var restored = (BrewingStandBlockEntity) helper.getLevel().getBlockEntity(position);
        restored.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), saved));
        tick(helper, restored, 276);
        helper.assertTrue(brewTime(helper, restored) == 1 && ItemStack.matches(restored.getItem(0), PotionContents.createItemStack(Items.POTION, Potions.AWKWARD)), "Reload must not complete the brew early");
        tick(helper, restored, 1);
        helper.assertTrue(ItemStack.matches(restored.getItem(0), PotionContents.createItemStack(Items.POTION, StatusContent.ELECTRIFYING)) && restored.getItem(3).isEmpty() && fuel(helper, restored) == 19, "Reloaded native stand must finish once using the saved fuel and restored ingredient identity");
        helper.succeed();
    }

    public static void subsequentCycleUsesStoredFuelWithoutAnotherPowder(GameTestHelper helper) {
        var stand = stand(helper);
        stand.setItem(0, new ItemStack(material("dew_bottle")));
        stand.setItem(3, new ItemStack(material("hearth_honey"), 2));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));
        tick(helper, stand, 401);
        stand.setItem(0, new ItemStack(material("dew_bottle")));
        tick(helper, stand, 401);
        helper.assertTrue(ItemStack.matches(stand.getItem(0), PotionContents.createItemStack(Items.POTION, Potions.REGENERATION)) && stand.getItem(3).isEmpty() && stand.getItem(4).isEmpty() && fuel(helper, stand) == 18, "A second cycle must spend one stored charge without requiring or creating another blaze powder");
        helper.succeed();
    }

    public static void cancelledUnchangedBrewMustNotPublishASuccessPostEvent(GameTestHelper helper) {
        var stand = stand(helper);
        stand.setItem(0, new ItemStack(material("dew_bottle")));
        stand.setItem(3, new ItemStack(material("hearth_honey")));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));
        var preCount = new AtomicInteger();
        var postCount = new AtomicInteger();
        Consumer<PotionBrewEvent.Pre> pre = event -> {
            if (event.getItem(0).is(material("dew_bottle")) && event.getItem(3).is(material("hearth_honey"))) {
                preCount.incrementAndGet();
                event.setCanceled(true);
            }
        };
        Consumer<PotionBrewEvent.Post> post = event -> {
            if (event.getItem(0).is(material("dew_bottle")) && event.getItem(3).is(material("hearth_honey"))) postCount.incrementAndGet();
        };
        NeoForge.EVENT_BUS.addListener(PotionBrewEvent.Pre.class, pre);
        NeoForge.EVENT_BUS.addListener(PotionBrewEvent.Post.class, post);
        try {
            tick(helper, stand, 401);
        } finally {
            NeoForge.EVENT_BUS.unregister(pre);
            NeoForge.EVENT_BUS.unregister(post);
        }
        helper.assertTrue(preCount.get() == 1 && stand.getItem(0).is(material("dew_bottle")) && stand.getItem(3).is(material("hearth_honey")) && stand.getItem(3).getCount() == 1 && brewTime(helper, stand) == 0 && fuel(helper, stand) == 19, "Canceling Pre must preserve input and ingredient, while retaining the already spent fuel charge");
        System.out.println("SB011_CANCELLED_BREW pre=" + preCount.get() + " post=" + postCount.get());
        helper.assertTrue(postCount.get() == 0, "SB-011: A canceled Pre with unchanged inventory must not publish a successful Post event");
        helper.succeed();
    }

    private static BrewingStandBlockEntity stand(GameTestHelper helper) {
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        helper.getLevel().setBlock(position, Blocks.BREWING_STAND.defaultBlockState(), 3);
        return (BrewingStandBlockEntity) helper.getLevel().getBlockEntity(position);
    }

    private static void tick(GameTestHelper helper, BrewingStandBlockEntity stand, int count) {
        for (int tick = 0; tick < count; tick++) BrewingStandBlockEntity.serverTick(helper.getLevel(), stand.getBlockPos(), stand.getBlockState(), stand);
    }

    private static int brewTime(GameTestHelper helper, BrewingStandBlockEntity stand) { return savedStand(stand, helper).getShortOr("BrewTime", (short) 0); }
    private static int fuel(GameTestHelper helper, BrewingStandBlockEntity stand) { return savedStand(stand, helper).getByteOr("Fuel", (byte) 0); }
    private static net.minecraft.nbt.CompoundTag savedStand(BrewingStandBlockEntity stand, GameTestHelper helper) {
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        stand.saveWithoutMetadata(output);
        return output.buildResult();
    }

    private static Item material(String id) { return MaterialContent.items().get(id).get(); }

    private static Recipe[] recipes() {
        return new Recipe[]{
            new Recipe(new ItemStack(UtilityContent.LOQUE_MILK_BOTTLE.get()), new ItemStack(Items.COCOA_BEANS), new ItemStack(StatusContent.COCOA.get())),
            new Recipe(new ItemStack(Items.GLASS_BOTTLE), new ItemStack(UtilityContent.LOQUE_MILK.get()), new ItemStack(UtilityContent.LOQUE_MILK_BOTTLE.get())),
            new Recipe(new ItemStack(Items.GLASS_BOTTLE), new ItemStack(material("starfruit_item")), new ItemStack(GeoDecorContent.item(GeoDecorKind.BOTTLE_OF_STARLIGHT))),
            new Recipe(new ItemStack(Items.GLASS_BOTTLE), new ItemStack(material("lunalemon_item")), new ItemStack(GeoDecorContent.item(GeoDecorKind.BOTTLE_OF_LUNARLIGHT))),
            new Recipe(new ItemStack(material("icy_snowcone")), new ItemStack(material("starfruit_item")), new ItemStack(material("starry_snowcone"))),
            new Recipe(new ItemStack(material("icy_snowcone")), new ItemStack(material("lunalemon_item")), new ItemStack(material("lunar_snowcone"))),
            new Recipe(new ItemStack(material("icy_snowcone")), new ItemStack(material("barbubble_melon_slice")), new ItemStack(material("fizzy_snowcone"))),
            new Recipe(PotionContents.createItemStack(Items.POTION, Potions.AWKWARD), new ItemStack(TerrainContent.BREATHER_SPONGE.get().asItem()), PotionContents.createItemStack(Items.POTION, Potions.WATER_BREATHING)),
            new Recipe(PotionContents.createItemStack(Items.POTION, Potions.AWKWARD), new ItemStack(material("bioelectric_cell")), PotionContents.createItemStack(Items.POTION, StatusContent.ELECTRIFYING)),
            new Recipe(new ItemStack(material("dew_bottle")), new ItemStack(material("hearth_honey")), PotionContents.createItemStack(Items.POTION, Potions.REGENERATION))
        };
    }

    private record Recipe(ItemStack input, ItemStack ingredient, ItemStack output) { }
    private NativeBrewingServerBehaviorTests() { }
}






