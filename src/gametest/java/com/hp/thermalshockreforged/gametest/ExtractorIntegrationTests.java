package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public final class ExtractorIntegrationTests {
    private ExtractorIntegrationTests() { }

    public static void register() {
        TsrGameTests.add("extractor_real_player_receives_output_and_advancement", ExtractorIntegrationTests::outputAndAdvancement, 100);
        TsrGameTests.add("extractor_rejected_output_preserves_input_and_advancement", ExtractorIntegrationTests::rejectedOutput, 100);
        TsrGameTests.add("extractor_seeded_random_outputs_cover_both_branches", ExtractorIntegrationTests::randomOutputs, 100);
    }

    public static void outputAndAdvancement(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 2.5, 5.5);
        var pos = prepare(helper);
        try {
            var advancement = helper.getLevel().getServer().getAdvancements().get(id("blasphemy_3"));
            helper.assertTrue(advancement != null && !player.getAdvancements().getOrStartProgress(advancement).isDone(), "Fresh player must not already own extraction advancement");
            var input = new ItemStack(item("infected_core_sample"), 2);
            use(helper, player, pos, input);
            var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2));
            helper.assertTrue(input.getCount() == 1 && drops.size() == 1 && drops.getFirst().getItem().is(item("primordial_blood")) && drops.getFirst().getItem().getCount() == 1, "Successful extraction must consume one sample and create exactly one primordial blood");
            helper.assertTrue(drops.getFirst().getAge() == 0 && drops.getFirst().hasPickUpDelay(), "Extraction drop must retain its initial age and pickup delay");
            helper.assertTrue(player.getAdvancements().getOrStartProgress(advancement).isDone(), "Successful extraction must award blasphemy_3 through real player progress");
        } finally {
            clear(helper, pos);
            PlayerInteractionTests.release(helper, player);
        }
        helper.succeed();
    }

    public static void rejectedOutput(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 2.5, 5.5);
        var pos = prepare(helper);
        Consumer<EntityJoinLevelEvent> reject = event -> {
            if (event.getLevel() == helper.getLevel() && event.getEntity() instanceof ItemEntity drop && drop.getItem().is(item("primordial_blood")) && drop.blockPosition().equals(pos)) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class, reject);
        try {
            var input = new ItemStack(item("infected_core_sample"), 2);
            use(helper, player, pos, input);
            var advancement = helper.getLevel().getServer().getAdvancements().get(id("blasphemy_3"));
            helper.assertTrue(input.getCount() == 2 && helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2)).isEmpty(), "Canceled output entity must restore the consumed sample without creating a reward");
            helper.assertTrue(advancement != null && !player.getAdvancements().getOrStartProgress(advancement).isDone() && player.experienceLevel == 0, "Rejected output must not grant advancement or experience levels");
        } finally {
            NeoForge.EVENT_BUS.unregister(reject);
            clear(helper, pos);
            PlayerInteractionTests.release(helper, player);
        }
        helper.succeed();
    }

    public static void randomOutputs(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 2.5, 5.5);
        var pos = prepare(helper);
        try {
            String[] inputs = {"herald_key", "neural_chip", "pesticide"};
            String[] outputs = {"helion_slag", "phobotite", "ferrous_nugget"};
            int[] maxCounts = {3, 6, 1};
            for (int index = 0; index < inputs.length; index++) {
                int present = 0;
                int absent = 0;
                for (int seed = 0; seed < 64; seed++) {
                    clear(helper, pos);
                    helper.getLevel().getRandom().setSeed(271828L + seed * 104729L);
                    int levels = player.experienceLevel;
                    var input = new ItemStack(item(inputs[index]), 2);
                    use(helper, player, pos, input);
                    var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2));
                    Item expected = item(outputs[index]);
                    helper.assertTrue(input.getCount() == 1 && drops.stream().allMatch(drop -> drop.getItem().is(expected) && drop.getItem().getCount() == 1), "Extraction must consume one input and emit only the configured item: " + inputs[index]);
                    helper.assertTrue(drops.size() <= maxCounts[index], "Random repeat count exceeded the configured bound: " + inputs[index]);
                    if (drops.isEmpty()) absent++; else present++;
                    int experience = helper.getLevel().getEntitiesOfClass(ExperienceOrb.class, new AABB(pos).inflate(2)).stream().mapToInt(ExperienceOrb::getValue).sum();
                    helper.assertTrue(index == 0 ? experience >= 15 && experience <= 40 && player.experienceLevel == levels + 10 : experience == (index == 1 ? 20 : 3) && player.experienceLevel == levels, "Extraction XP and level rewards changed: " + inputs[index]);
                }
                helper.assertTrue(present > 0 && absent > 0, "Fixed seed set must exercise successful and unsuccessful chance branches: " + inputs[index]);
                ThermalShockReforged.LOGGER.info("Extractor random output branches: input={}, emitted={}, absent={}", inputs[index], present, absent);
            }
        } finally {
            clear(helper, pos);
            PlayerInteractionTests.release(helper, player);
        }
        helper.succeed();
    }

    private static BlockPos prepare(GameTestHelper helper) {
        var pos = helper.absolutePos(new BlockPos(4, 2, 4));
        helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.EXTRACTOR).defaultBlockState(), 3);
        return pos;
    }

    private static void use(GameTestHelper helper, ServerPlayer player, BlockPos pos, ItemStack input) {
        player.setItemInHand(InteractionHand.MAIN_HAND, input);
        player.gameMode.useItemOn(player, helper.getLevel(), input, InteractionHand.MAIN_HAND, new BlockHitResult(pos.getCenter(), Direction.UP, pos, false));
    }

    private static void clear(GameTestHelper helper, BlockPos pos) {
        helper.getLevel().getEntitiesOfClass(Entity.class, new AABB(pos).inflate(2), entity -> entity instanceof ItemEntity || entity instanceof ExperienceOrb).forEach(Entity::discard);
    }

    private static Identifier id(String name) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name); }
    private static Item item(String name) { return BuiltInRegistries.ITEM.getValue(id(name)); }
}
