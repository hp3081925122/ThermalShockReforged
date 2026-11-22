package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.CacheBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.farming.CropKind;
import com.hp.thermalshockreforged.content.farming.FarmingContent;
import com.hp.thermalshockreforged.content.farming.SeedBlockEntity;
import com.hp.thermalshockreforged.content.growth.GrowthContent;
import com.hp.thermalshockreforged.content.growth.VisiBulbBlock;
import com.hp.thermalshockreforged.content.machines.MachineContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class GrowthMachineServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_growth_machine_server_behavior_tests_all_six_transmutator_recipes_consume_one_and_produce_one", GrowthMachineServerBehaviorTests::allSixTransmutatorRecipesConsumeOneAndProduceOne, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_transmutator_rejects_invalid_input_without_output_or_consumption", GrowthMachineServerBehaviorTests::transmutatorRejectsInvalidInputWithoutOutputOrConsumption, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_extractor_invalid_input_has_no_cost_or_rewards", GrowthMachineServerBehaviorTests::extractorInvalidInputHasNoCostOrRewards, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_extractor_six_deterministic_inputs_retain_their_experience_values", GrowthMachineServerBehaviorTests::extractorSixDeterministicInputsRetainTheirExperienceValues, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_thirsty_seed_matures_on_third_watering_after_nbt_reload", GrowthMachineServerBehaviorTests::thirstySeedMaturesOnThirdWateringAfterNbtReload, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_non_thirsty_seed_rejects_repeated_watering_without_costs", GrowthMachineServerBehaviorTests::nonThirstySeedRejectsRepeatedWateringWithoutCosts, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_farm_seeds_require_planter_soil_and_drop_after_it_is_removed", GrowthMachineServerBehaviorTests::farmSeedsRequirePlanterSoilAndDropAfterItIsRemoved, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_blocked_barbubble_maturation_retains_thirst_and_progress", GrowthMachineServerBehaviorTests::blockedBarbubbleMaturationRetainsThirstAndProgress, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_visi_bulb_activates_once_and_does_not_refresh_night_vision", GrowthMachineServerBehaviorTests::visiBulbActivatesOnceAndDoesNotRefreshNightVision, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_visi_bulb_empty_hand_harvest_retains_its_fallback_drop", GrowthMachineServerBehaviorTests::visiBulbEmptyHandHarvestRetainsItsFallbackDrop, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_cryonic_cache_rejects_wrong_key_without_claiming_rewards", GrowthMachineServerBehaviorTests::cryonicCacheRejectsWrongKeyWithoutClaimingRewards, 100, "supernova_server");
        TsrGameTests.add("history_growth_machine_server_behavior_tests_enchanted_cache_rejects_an_invalid_pad_without_claiming_rewards", GrowthMachineServerBehaviorTests::enchantedCacheRejectsAnInvalidPadWithoutClaimingRewards, 100, "supernova_server");
    }

    public static void allSixTransmutatorRecipesConsumeOneAndProduceOne(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        BlockState state = MachineContent.TRANSMUTATOR.get().defaultBlockState();
        level.setBlock(position, state, 3);
        String[][] recipes = {{"rough_ruby", "rough_bloodstone"}, {"rough_bloodstone", "rough_ruby"}, {"ruby", "bloodstone"}, {"bloodstone", "ruby"}, {"malachite", "bismuth"}, {"bismuth", "malachite"}};
        for (String[] recipe : recipes) {
            ItemStack input = new ItemStack(MaterialContent.items().get(recipe[0]).get(), 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, input);
            InteractionResult result = state.useItemOn(input, level, player, InteractionHand.MAIN_HAND, hit(position));
            helper.assertTrue(result == InteractionResult.SUCCESS && input.getCount() == 1, "Every original transmutation must consume exactly one input");
            int output = level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(2), entity -> entity.getItem().is(MaterialContent.items().get(recipe[1]).get())).stream().mapToInt(entity -> entity.getItem().getCount()).sum();
            helper.assertTrue(output == 1, "Every original transmutation must create exactly one matching output");
        }
        helper.succeed();
    }

    public static void transmutatorRejectsInvalidInputWithoutOutputOrConsumption(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        var state = MachineContent.TRANSMUTATOR.get().defaultBlockState();
        level.setBlock(position, state, 3);
        ItemStack input = new ItemStack(Items.STONE, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, input);
        helper.assertTrue(state.useItemOn(input, level, player, InteractionHand.MAIN_HAND, hit(position)) == InteractionResult.TRY_WITH_EMPTY_HAND, "An unknown transmutation must fall back to default interaction");
        helper.assertTrue(input.getCount() == 2 && level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(2)).isEmpty(), "Invalid transmutation must preserve the input and create no output");
        helper.succeed();
    }

    public static void extractorInvalidInputHasNoCostOrRewards(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        var state = GeoDecorContent.block(GeoDecorKind.EXTRACTOR).defaultBlockState();
        level.setBlock(position, state, 3);
        ItemStack input = new ItemStack(Items.STONE, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, input);
        int experience = player.totalExperience;
        helper.assertTrue(state.useItemOn(input, level, player, InteractionHand.MAIN_HAND, hit(position)) == InteractionResult.SUCCESS, "The original extractor invalid-input interaction still consumes the action");
        helper.assertTrue(input.getCount() == 2 && player.totalExperience == experience && level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(2)).isEmpty() && level.getEntitiesOfClass(ExperienceOrb.class, new AABB(position).inflate(2)).isEmpty(), "Invalid extraction must not consume inputs or create rewards");
        helper.succeed();
    }

    public static void extractorSixDeterministicInputsRetainTheirExperienceValues(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        var state = GeoDecorContent.block(GeoDecorKind.EXTRACTOR).defaultBlockState();
        level.setBlock(position, state, 3);
        String[] inputs = {"infected_seeds", "blood_vessel", "ashen_bone", "beetle_claw", "infected_flesh", "tainted_energy"};
        int expected = 0;
        for (int index = 0; index < inputs.length; index++) {
            var input = new ItemStack(MaterialContent.items().get(inputs[index]).get(), 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, input);
            state.useItemOn(input, level, player, InteractionHand.MAIN_HAND, hit(position));
            expected += index < 3 ? 2 : 8;
            int actual = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(position).inflate(2)).stream().mapToInt(ExperienceOrb::getValue).sum();
            helper.assertTrue(input.getCount() == 1 && actual == expected, "Each fixed extraction must consume one item and add the exact original experience");
        }
        helper.succeed();
    }

    public static void thirstySeedMaturesOnThirdWateringAfterNbtReload(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.below(), FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
        var state = FarmingContent.seed(CropKind.SKORIO).defaultBlockState();
        level.setBlock(position, state, 3);
        var seed = (SeedBlockEntity) level.getBlockEntity(position);
        helper.assertTrue(seed != null, "A seed on planter soil must create its inventory");
        seed.getPersistentData().putBoolean("thirsty", true);
        seed.getPersistentData().putDouble("waterings", 2);
        var saved = save(seed, level);
        seed.getPersistentData().putDouble("waterings", 0);
        seed.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), saved));
        helper.assertTrue(seed.getPersistentData().getBooleanOr("thirsty", false) && seed.getPersistentData().getDoubleOr("waterings", 0.0) == 2, "Thirst and watering progress must survive BE NBT restoration");
        var can = new ItemStack(MaterialContent.items().get("watering_can").get());
        player.setItemInHand(InteractionHand.MAIN_HAND, can);
        state.useItemOn(can, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(level.getBlockState(position).is(FarmingContent.crop(CropKind.SKORIO)), "The third valid watering must replace the seed with its mature crop");
        helper.succeed();
    }

    public static void nonThirstySeedRejectsRepeatedWateringWithoutCosts(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.below(), FarmingContent.PLANTER_BOX.get().defaultBlockState(), 3);
        var state = FarmingContent.seed(CropKind.SKORIO).defaultBlockState();
        level.setBlock(position, state, 3);
        var seed = (SeedBlockEntity) level.getBlockEntity(position);
        seed.getPersistentData().putBoolean("thirsty", false);
        seed.getPersistentData().putDouble("waterings", 1);
        var can = new ItemStack(MaterialContent.items().get("watering_can").get());
        player.setItemInHand(InteractionHand.MAIN_HAND, can);
        for (int attempt = 0; attempt < 3; attempt++) state.useItemOn(can, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(level.getBlockState(position).is(state.getBlock()) && seed.getPersistentData().getDoubleOr("waterings", 0.0) == 1 && !seed.getPersistentData().getBooleanOr("thirsty", false) && can.getDamageValue() == 0 && can.getCount() == 1, "A non-thirsty seed must not gain progress or consume the watering can");
        helper.succeed();
    }

    public static void farmSeedsRequirePlanterSoilAndDropAfterItIsRemoved(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos invalid = helper.absolutePos(new BlockPos(4, 2, 8));
        BlockPos valid = helper.absolutePos(new BlockPos(9, 2, 8));
        var seed = FarmingContent.seed(CropKind.SKORIO);
        level.setBlock(invalid.below(), Blocks.DIRT.defaultBlockState(), 3);
        level.setBlock(invalid, seed.defaultBlockState(), 3);
        helper.assertTrue(!level.getBlockState(invalid).is(seed), "Ordinary dirt must not keep this planter-only seed");
        level.setBlock(valid.below(), FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
        level.setBlock(valid, seed.defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(valid).is(seed) && level.getBlockEntity(valid) instanceof SeedBlockEntity, "Planter soil must keep the seed and its inventory");
        level.setBlock(valid.below(), Blocks.DIRT.defaultBlockState(), 3);
        helper.assertTrue(!level.getBlockState(valid).is(seed), "Removing the valid planter soil must remove the unsupported seed");
        helper.succeed();
    }

    public static void blockedBarbubbleMaturationRetainsThirstAndProgress(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.below(), FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
        var state = FarmingContent.seed(CropKind.BARBUBBLE).defaultBlockState();
        level.setBlock(position, state, 3);
        level.setBlock(position.above(), Blocks.STONE.defaultBlockState(), 3);
        var seed = (SeedBlockEntity) level.getBlockEntity(position);
        seed.getPersistentData().putBoolean("thirsty", true);
        seed.getPersistentData().putDouble("waterings", 2);
        var can = new ItemStack(MaterialContent.items().get("watering_can").get());
        player.setItemInHand(InteractionHand.MAIN_HAND, can);
        state.useItemOn(can, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(level.getBlockState(position).is(state.getBlock()) && level.getBlockState(position.above()).is(Blocks.STONE) && seed.getPersistentData().getBooleanOr("thirsty", false) && seed.getPersistentData().getDoubleOr("waterings", 0.0) == 2, "Blocked double-height maturation must preserve the pending watering state and the obstacle");
        helper.succeed();
    }

    public static void visiBulbActivatesOnceAndDoesNotRefreshNightVision(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.below(), FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
        level.setBlock(position, GrowthContent.VISI_BULB.get().defaultBlockState(), 3);
        var state = level.getBlockState(position);
        state.useItemOn(ItemStack.EMPTY, level, player, InteractionHand.MAIN_HAND, hit(position));
        var vision = player.getEffect(MobEffects.NIGHT_VISION);
        helper.assertTrue(level.getBlockState(position).getValue(VisiBulbBlock.BLOCKSTATE) == 1 && vision != null && vision.getDuration() == 1300 && vision.getAmplifier() == 0, "Fresh Visi Bulb must activate its original night vision once");
        player.removeEffect(MobEffects.NIGHT_VISION);
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100));
        level.getBlockState(position).useItemOn(ItemStack.EMPTY, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(player.getEffect(MobEffects.NIGHT_VISION).getDuration() == 100, "The already activated bulb must not refresh the effect");
        helper.succeed();
    }

    public static void visiBulbEmptyHandHarvestRetainsItsFallbackDrop(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.below(), FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
        Block block = GrowthContent.VISI_BULB.get();
        level.setBlock(position, block.defaultBlockState(), 3);
        var state = level.getBlockState(position);
        var blockEntity = level.getBlockEntity(position);
        helper.assertTrue(block.onDestroyedByPlayer(state, level, position, player, ItemStack.EMPTY, true, state.getFluidState()), "The native player-destruction callback must accept harvesting");
        block.playerDestroy(level, player, position, state, blockEntity, ItemStack.EMPTY);
        var drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(2));
        helper.assertTrue(drops.size() == 1 && drops.getFirst().getItem().is(block.asItem()) && drops.getFirst().getItem().getCount() == 1, "Empty-hand Visi Bulb harvesting must preserve its one-item fallback without seeds");
        helper.succeed();
    }

    public static void cryonicCacheRejectsWrongKeyWithoutClaimingRewards(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.below(), Blocks.STONE.defaultBlockState(), 3);
        var block = GeoDecorContent.block(GeoDecorKind.CRYONIC_CACHE);
        level.setBlock(position, block.defaultBlockState(), 3);
        var cache = (CacheBlockEntity) level.getBlockEntity(position);
        var wrongKey = new ItemStack(Items.DIAMOND, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, wrongKey);
        level.getBlockState(position).useItemOn(wrongKey, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(level.getBlockState(position).is(block) && level.getBlockEntity(position) == cache && !save(cache, level).getBooleanOr("CacheConsumed", false), "A wrong key must keep the cache and leave its claim unused");
        helper.assertTrue(wrongKey.getCount() == 2 && level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(2)).isEmpty(), "A wrong key must have no cost or rewards");
        helper.succeed();
    }

    public static void enchantedCacheRejectsAnInvalidPadWithoutClaimingRewards(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.below(), GeoDecorContent.block(GeoDecorKind.CACHE_PAD).defaultBlockState(), 3);
        var block = GeoDecorContent.block(GeoDecorKind.ENCHANTED_CACHE);
        level.setBlock(position, block.defaultBlockState(), 3);
        var cache = (CacheBlockEntity) level.getBlockEntity(position);
        level.getBlockState(position).useItemOn(ItemStack.EMPTY, level, player, InteractionHand.MAIN_HAND, hit(position));
        helper.assertTrue(level.getBlockState(position).is(block) && level.getBlockEntity(position) == cache && !save(cache, level).getBooleanOr("CacheConsumed", false), "An invalid pad must preserve the enchanted cache and unused claim");
        helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(2)).isEmpty(), "An invalid pad must not create cache rewards");
        helper.succeed();
    }

    private static net.minecraft.nbt.CompoundTag save(BlockEntity entity, Level level) {
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        entity.saveWithoutMetadata(output);
        return output.buildResult();
    }

    private static BlockHitResult hit(BlockPos position) { return new BlockHitResult(Vec3.atCenterOf(position), Direction.UP, position, false); }
    private GrowthMachineServerBehaviorTests() { }
}








