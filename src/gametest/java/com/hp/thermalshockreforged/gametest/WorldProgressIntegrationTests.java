package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.content.atlas.AtlasActivationEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.remainingutility.FixedStructureItem;
import com.hp.thermalshockreforged.content.remainingutility.RemainingUtilityContent;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.hp.thermalshockreforged.world.WorldProgressData;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class WorldProgressIntegrationTests {
    private static final BlockPos SHALESPRING_ORIGIN = new BlockPos(316, 53, 299);
    private static final BlockPos PALACE_ORIGIN = new BlockPos(-74, 178, -132);
    private static final BlockPos CHAMBER_ORIGIN = new BlockPos(1333, 133, 1333);
    private static final BlockPos[] SWITCHES = {new BlockPos(14, 0, 7), new BlockPos(0, 0, 7), new BlockPos(14, 0, -7), new BlockPos(0, 0, -7)};

    private WorldProgressIntegrationTests() { }

    public static void register() {
        TsrGameTests.addDedicated("world_progress_integration_advancement_structures_and_fixed_item", WorldProgressIntegrationTests::advancementStructuresAndFixedItem, 300, "supernova_server");
        TsrGameTests.addDedicated("world_progress_integration_keystone_wakeup_and_puzzle_reward", WorldProgressIntegrationTests::keystoneWakeupAndPuzzleReward, 180, "supernova_server");
        TsrGameTests.add("world_progress_integration_keystone_item_activates_atlas_base", WorldProgressIntegrationTests::keystoneItemActivatesAtlasBase, 100);
    }

    public static void advancementStructuresAndFixedItem(GameTestHelper helper) {
        var level = helper.getLevel();
        var server = level.getServer();
        var loqendia = server.getLevel(LoqendiaContent.DIMENSION);
        var palace = server.getLevel(AtlasContent.PALACE);
        helper.assertTrue(loqendia != null && palace != null, "Both production destination dimensions must be loaded");
        boolean oldLoqendiaRule = loqendia.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get());
        boolean oldPalaceRule = palace.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get());
        ServerPlayer disabled = null;
        ServerPlayer enabled = null;
        ServerPlayer repeated = null;
        ServerPlayer palacePlayer = null;
        ServerPlayer disabledPalace = null;
        ServerPlayer repeatedPalace = null;
        PalaceAdvancementObserver palaceObserver = new PalaceAdvancementObserver();
        try {
            var shale = block("shale");
            var shalespringSample = SHALESPRING_ORIGIN;
            loqendia.getChunkAt(shalespringSample);
            helper.assertTrue(loqendia.getStructureManager().get(id("shalespring_august")).isPresent(), "Shalespring template must load from production data");
            WorldProgressData progress = WorldProgressData.get(loqendia);
            helper.assertTrue(!savedBoolean(progress, "shalespringExists"), "Shalespring progress must begin uncommitted in the isolated dedicated world");
            loqendia.setBlock(shalespringSample, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            loqendia.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), false, server);
            disabled = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 3.0, 4.5);
            teleport(helper, disabled, loqendia, 316.5, 60.0, 299.5, "Disabled-rule player must enter Loqendia through the real dimension-transfer path");
            helper.assertTrue(advancementDone(disabled, "loqendia_get"), "A real Loqendia dimension change must earn the arrival advancement even when generation is disabled");
            helper.assertTrue(!loqendia.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get()), "Shalespring gamerule must remain disabled in its destination dimension");
            helper.assertTrue(!savedBoolean(progress, "shalespringExists") && loqendia.getBlockState(shalespringSample).is(Blocks.GOLD_BLOCK), "Disabled gamerule must preserve the sentinel block and leave Shalespring progress unset");

            loqendia.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), true, server);
            enabled = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 3.0, 4.5);
            teleport(helper, enabled, loqendia, 316.5, 60.0, 299.5, "Enabled-rule player must enter Loqendia through the real dimension-transfer path");
            helper.assertTrue(advancementDone(enabled, "loqendia_get") && savedBoolean(progress, "shalespringExists"), "The real arrival advancement must place Shalespring and commit its saved world-progress flag");
            helper.assertTrue(loqendia.getBlockState(shalespringSample).is(shale), "Shalespring template must place its recorded origin block at the production coordinates");
            loqendia.setBlock(shalespringSample, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            repeated = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 3.0, 4.5);
            teleport(helper, repeated, loqendia, 316.5, 60.0, 299.5, "Repeat player must enter Loqendia through the real dimension-transfer path");
            helper.assertTrue(loqendia.getBlockState(shalespringSample).is(Blocks.GOLD_BLOCK), "A second player's earned arrival advancement must not replace the already committed Shalespring");

            palace.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), false, server);
            helper.assertTrue(palace.getStructureManager().get(id("lavender_palace")).isPresent(), "Palace template must load from production data");
            WorldProgressData palaceProgress = WorldProgressData.get(palace);
            BlockPos palaceSample = PALACE_ORIGIN.offset(0, 0, 51);
            palace.getChunkAt(palaceSample);
            Block palaceMarker = block("flowing_dark");
            helper.assertTrue(!savedBoolean(palaceProgress, "palaceExists"), "Palace progress must begin uncommitted before its disabled-rule case");
            palace.setBlock(palaceSample, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            NeoForge.EVENT_BUS.register(palaceObserver);
            disabledPalace = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 3.0, 4.5);
            palaceObserver.watch(disabledPalace);
            teleport(helper, disabledPalace, palace, -70.5, 184.0, -130.5, "Disabled-rule player must enter the Palace destination through the real dimension-transfer path");
            helper.assertTrue(advancementDone(disabledPalace, "palace_get"), "A real Palace dimension change must earn the arrival advancement even when generation is disabled; observed=" + palaceObserver.events());
            helper.assertTrue(!palace.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get()), "Palace gamerule must remain disabled in its destination dimension");
            helper.assertTrue(!savedBoolean(palaceProgress, "palaceExists") && palace.getBlockState(palaceSample).is(Blocks.GOLD_BLOCK), "Disabled gamerule must preserve the Palace sentinel and leave progress unset");
            palace.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), true, server);
            palacePlayer = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 3.0, 4.5);
            palaceObserver.watch(palacePlayer);
            teleport(helper, palacePlayer, palace, -70.5, 184.0, -130.5, "Palace player must enter its destination through the real dimension-transfer path");
            helper.assertTrue(advancementDone(palacePlayer, "palace_get"), "The enabled Palace dimension transfer must earn palace_get; playerDimension=" + palacePlayer.level().dimension().identifier() + ", targetDimension=" + palace.dimension().identifier() + ", observed=" + palaceObserver.events());
            helper.assertTrue(palace.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get()), "Palace gamerule must be enabled in its destination dimension before generation");
            helper.assertTrue(savedBoolean(palaceProgress, "palaceExists"), "The enabled Palace arrival advancement must commit its saved progress flag; observed=" + palaceObserver.events());
            helper.assertTrue(palace.getBlockState(palaceSample).is(palaceMarker), "Palace template must place its recorded flowing-dark marker at the production coordinates; actual=" + palace.getBlockState(palaceSample));
            palace.setBlock(palaceSample, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            repeatedPalace = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 3.0, 4.5);
            palaceObserver.watch(repeatedPalace);
            teleport(helper, repeatedPalace, palace, -70.5, 184.0, -130.5, "Repeat player must enter the Palace through the real dimension-transfer path");
            helper.assertTrue(palace.getBlockState(palaceSample).is(Blocks.GOLD_BLOCK), "A second Palace advancement must not overwrite an already committed structure");

            var train = RemainingUtilityContent.MODEL_TRAIN.get();
            helper.assertTrue(loqendia.getStructureManager().get(id("shalespring_trainstation")).isPresent(), "Fixed structure item template must load from production data");
            enabled.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(train));
            InteractionResult successfulUse = enabled.gameMode.useItem(enabled, loqendia, enabled.getMainHandItem(), InteractionHand.MAIN_HAND);
            helper.assertTrue(successfulUse.consumesAction() && enabled.getMainHandItem().isEmpty() && loqendia.getBlockState(shalespringSample).is(shale), "Real fixed-structure item use must place the train station at its production origin and consume its stack");

            FixedStructureItem trainItem = (FixedStructureItem) train;
            Field templateField = FixedStructureItem.class.getDeclaredField("templateId");
            templateField.setAccessible(true);
            Identifier originalTemplate = (Identifier) templateField.get(trainItem);
            try {
                templateField.set(trainItem, id("tsr_gametest_missing_template"));
                ItemStack failedStack = new ItemStack(trainItem);
                enabled.setItemInHand(InteractionHand.MAIN_HAND, failedStack);
                InteractionResult failedUse = enabled.gameMode.useItem(enabled, loqendia, enabled.getMainHandItem(), InteractionHand.MAIN_HAND);
                helper.assertTrue(failedUse == InteractionResult.FAIL && enabled.getMainHandItem().getCount() == 1 && loqendia.getBlockState(shalespringSample).is(shale), "Missing-template use must fail, preserve the held item, and leave the placed structure untouched");
            } finally {
                templateField.set(trainItem, originalTemplate);
                if (!originalTemplate.equals(templateField.get(trainItem))) throw new IllegalStateException("The registered fixed-structure item template ID was not restored");
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not run the missing-template item case: " + exception + ", cause=" + exception.getCause(), exception);
        } finally {
            NeoForge.EVENT_BUS.unregister(palaceObserver);
            loqendia.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), oldLoqendiaRule, server);
            palace.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), oldPalaceRule, server);
            release(helper, repeatedPalace);
            release(helper, palacePlayer);
            release(helper, disabledPalace);
            release(helper, repeated);
            release(helper, enabled);
            release(helper, disabled);
        }
        helper.succeed();
    }

    public static void keystoneWakeupAndPuzzleReward(GameTestHelper helper) {
        helper.setTime(18000);
        helper.runAfterDelay(2, () -> runKeystoneWakeupAndPuzzleReward(helper));
    }

    private static void runKeystoneWakeupAndPuzzleReward(GameTestHelper helper) {
        var level = helper.getLevel();
        var server = level.getServer();
        boolean oldRule = level.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get());
        ServerPlayer player = null;
        ServerPlayer disabledPlayer = null;
        ServerPlayer repeated = null;
        boolean[] deferredCleanup = {false};
        try {
            level.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), false, server);
            var progress = WorldProgressData.get(level);
            helper.assertTrue(!progress.keystoneExists(), "Keystone chamber must begin uncommitted in a fresh GameTest world");
            var advancement = server.getAdvancements().get(TsrGameTests.id("loqendia_get"));
            helper.assertTrue(advancement != null, "Keystone arrival advancement must be loaded");
            player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 2.0, 4.5);
            helper.assertTrue(!player.getAdvancements().getOrStartProgress(advancement).isDone(), "The wake-up fixture must use a player who has not entered Loqendia");
            BlockPos bedFoot = helper.absolutePos(new BlockPos(3, 2, 3));
            installBed(level, bedFoot);
            var disabledSleep = player.startSleepInBed(bedFoot);
            helper.assertTrue(disabledSleep.right().isPresent() && player.isSleeping(), "The real server sleep path must accept a complete bed before its wake event; failure=" + disabledSleep.left().map(Object::toString).orElse("none") + ", bed=" + level.getBlockState(bedFoot) + ", head=" + level.getBlockState(bedFoot.relative(Direction.EAST)) + ", dark=" + level.isDarkOutside() + ", skyDarken=" + level.getSkyDarken());
            player.stopSleepInBed(true, true);
            helper.assertTrue(!player.isSleeping() && !progress.keystoneExists(), "The real wake-up event must respect the disabled structure gamerule");
            helper.assertTrue(!level.getStructureManager().get(id("keystonerubicon")).isEmpty(), "Keystone chamber template must load from production data");
            level.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), true, server);
            disabledPlayer = player;
            player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 2.0, 4.5);
            BlockPos enabledBed = helper.absolutePos(new BlockPos(6, 2, 3));
            installBed(level, enabledBed);
            var enabledSleep = player.startSleepInBed(enabledBed);
            helper.assertTrue(enabledSleep.right().isPresent() && player.isSleeping(), "The real server sleep path must accept a complete bed before enabled generation; failure=" + enabledSleep.left().map(Object::toString).orElse("none") + ", bed=" + level.getBlockState(enabledBed) + ", head=" + level.getBlockState(enabledBed.relative(Direction.EAST)) + ", dark=" + level.isDarkOutside() + ", skyDarken=" + level.getSkyDarken());
            BlockPos noticeAnchor = player.blockPosition().immutable();
            player.stopSleepInBed(true, true);
            helper.assertTrue(!player.isSleeping() && progress.keystoneExists(), "The real wake-up path must generate and commit the Keystone chamber");
            helper.assertTrue(level.getStructureManager().get(id("keystonerubicon")).isPresent(), "Keystone chamber template must load from production data");
            BlockPos chamberSample = CHAMBER_ORIGIN.offset(7, 0, 7);
            level.getChunkAt(chamberSample);
            helper.assertTrue(level.getBlockState(chamberSample).is(block("red_grid_corruption")), "Wake-up event must place the chamber marker at its production coordinates");
            helper.assertTrue(progress.keystoneNoticeDimension().equals(level.dimension().identifier()) && progress.keystoneNoticeAnchor().equals(noticeAnchor), "Wake-up generation must persist the actual player's dimension and notice anchor");
            BlockPos lever = CHAMBER_ORIGIN.offset(15, 13, 22);
            BlockPos pedestalPos = CHAMBER_ORIGIN.offset(22, 15, 22);
            BlockPos[] switches = {CHAMBER_ORIGIN.offset(15, 13, 15), CHAMBER_ORIGIN.offset(15, 13, 29), CHAMBER_ORIGIN.offset(29, 13, 15), CHAMBER_ORIGIN.offset(29, 13, 29)};
            helper.assertTrue(level.getBlockEntity(lever) instanceof GeoDecorBlockEntity leverEntity && leverEntity.kind == GeoDecorKind.RINTHIUM_LEVER, "The real chamber template must place its production lever at the configured puzzle coordinate");
            for (int index = 0; index < switches.length; index++) {
                BlockPos buttonPos = switches[index];
                helper.assertTrue(level.getBlockEntity(buttonPos) instanceof GeoDecorBlockEntity button && button.kind == GeoDecorKind.RINTHIUM_BUTTON, "The real chamber template must place production switch " + index + " at " + buttonPos);
            }
            for (int index = 0; index < switches.length - 1; index++) {
                InteractionResult press = useBlock(level, player, switches[index]);
                helper.assertTrue(press.consumesAction(), "The first three real template switches must accept physical interaction");
            }
            helper.assertTrue(useBlock(level, player, lever).consumesAction(), "The production lever must evaluate an incomplete four-switch puzzle");
            helper.assertTrue(!(level.getBlockEntity(pedestalPos) instanceof GeoDecorBlockEntity incomplete && incomplete.kind == GeoDecorKind.KEYSTONE_PEDESTAL) && count(player, AtlasContent.KEYSTONE.get()) == 0, "Three switches must not create a reward pedestal or give a Keystone");
            InteractionResult finalPress = useBlock(level, player, switches[3]);
            helper.assertTrue(finalPress.consumesAction(), "The fourth real template switch must accept physical interaction");
            InteractionResult unlock = useBlock(level, player, lever);
            helper.assertTrue(unlock.consumesAction(), "The production lever must unlock after all four real template switches are active");
            helper.assertTrue(level.getBlockEntity(pedestalPos) instanceof GeoDecorBlockEntity pedestal && pedestal.kind == GeoDecorKind.KEYSTONE_PEDESTAL && pedestal.getPersistentData().getBooleanOr("caninteract", false), "Four switches must awaken the production reward pedestal in its enabled state");
            InteractionResult collect = useBlock(level, player, pedestalPos);
            helper.assertTrue(collect.consumesAction() && count(player, AtlasContent.KEYSTONE.get()) == 1, "The actual pedestal interaction must award exactly one Keystone");
            useBlock(level, player, pedestalPos);
            var finalPedestal = (GeoDecorBlockEntity) level.getBlockEntity(pedestalPos);
            helper.assertTrue(count(player, AtlasContent.KEYSTONE.get()) == 1 && finalPedestal.getPersistentData().getBooleanOr("KeystoneClaimed", false) && !finalPedestal.getPersistentData().getBooleanOr("caninteract", true), "Repeated reward interaction must not create a second Keystone");
            level.setBlock(chamberSample, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            repeated = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 4.5, 2.0, 4.5);
            BlockPos repeatedBed = helper.absolutePos(new BlockPos(6, 2, 3));
            installBed(level, repeatedBed);
            var repeatedSleep = repeated.startSleepInBed(repeatedBed);
            helper.assertTrue(repeatedSleep.right().isPresent() && repeated.isSleeping(), "Second connected player must enter a real bed before the repeated wake event; failure=" + repeatedSleep.left().map(Object::toString).orElse("none") + ", bed=" + level.getBlockState(repeatedBed) + ", head=" + level.getBlockState(repeatedBed.relative(Direction.EAST)) + ", dark=" + level.isDarkOutside() + ", skyDarken=" + level.getSkyDarken());
            repeated.stopSleepInBed(true, true);
            helper.assertTrue(level.getBlockState(chamberSample).is(Blocks.GOLD_BLOCK), "A second genuine PlayerWakeUpEvent must not replace the committed Keystone chamber");
            ServerPlayer noticePlayer = player;
            ServerPlayer cleanupDisabled = disabledPlayer;
            ServerPlayer cleanupRepeated = repeated;
            systemChatKeys(noticePlayer);
            boolean[] monitoring = {true};
            int[] elapsed = {0};
            int[] giftCount = {0};
            int[] giftTick = {-1};
            int[] invitationCount = {0};
            int[] invitationTick = {-1};
            NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ServerTickEvent.Post event) -> {
                if (!monitoring[0] || event.getServer() != server) return;
                int tick = ++elapsed[0];
                for (String key : systemChatKeys(noticePlayer)) {
                    if (key.equals("message.thermal_shock_reforged.keystone.gift")) {
                        giftCount[0]++;
                        if (giftTick[0] < 0) giftTick[0] = tick;
                    }
                    if (key.equals("message.thermal_shock_reforged.keystone.invitation")) {
                        invitationCount[0]++;
                        if (invitationTick[0] < 0) invitationTick[0] = tick;
                    }
                }
                if (tick == 145) helper.runAfterDelay(1, () -> {
                    try {
                        helper.assertTrue(giftCount[0] == 1 && giftTick[0] == 100, "Connected server player must receive exactly one gift system packet at END tick 100; count=" + giftCount[0] + ", tick=" + giftTick[0]);
                        helper.assertTrue(invitationCount[0] == 1 && invitationTick[0] == 145, "Connected server player must receive exactly one invitation system packet at END tick 145; count=" + invitationCount[0] + ", tick=" + invitationTick[0]);
                    } finally {
                        monitoring[0] = false;
                        release(helper, cleanupRepeated);
                        release(helper, cleanupDisabled);
                        release(helper, noticePlayer);
                    }
                    helper.succeed();
                });
            });
            deferredCleanup[0] = true;
        } finally {
            level.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), oldRule, server);
            if (!deferredCleanup[0]) {
                release(helper, repeated);
                release(helper, disabledPlayer);
                release(helper, player);
            }
        }
    }

    private static List<String> systemChatKeys(ServerPlayer player) {
        var channel = player.connection.getConnection().channel();
        if (!(channel instanceof EmbeddedChannel embedded)) throw new IllegalStateException("GameTest server player must use an EmbeddedChannel connection");
        embedded.runPendingTasks();
        List<String> keys = new ArrayList<>();
        Object message;
        while ((message = embedded.readOutbound()) != null) {
            if (message instanceof ClientboundSystemChatPacket packet && packet.content().getContents() instanceof TranslatableContents translatable) keys.add(translatable.getKey());
            ReferenceCountUtil.release(message);
        }
        return keys;
    }

    public static void keystoneItemActivatesAtlasBase(GameTestHelper helper) {
        var level = helper.getLevel();
        var server = level.getServer();
        ServerPlayer player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 10.5, 2.0, 10.5);
        BlockPos clicked = helper.absolutePos(new BlockPos(6, 1, 6));
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) level.setBlock(clicked.offset(x, 0, z), Blocks.OBSIDIAN.defaultBlockState(), 3);
        level.setBlock(clicked.above(), Blocks.AIR.defaultBlockState(), 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(AtlasContent.KEYSTONE.get()));
        var stack = player.getMainHandItem();
        InteractionResult activation = player.gameMode.useItemOn(player, level, stack, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(clicked), Direction.UP, clicked, false));
        BlockPos baseCorner = clicked.offset(-1, 0, -1);
        Block marker = block("atlas_base_corner");
        List<AtlasActivationEntity> tasks = level.getEntitiesOfClass(AtlasActivationEntity.class, new net.minecraft.world.phys.AABB(clicked).inflate(4));
        helper.assertTrue(activation.consumesAction() && tasks.size() == 1 && player.getCooldowns().isOnCooldown(stack), "Real Keystone use on a valid obsidian platform must submit one Atlas task and start the native cooldown");
        helper.assertTrue(level.getBlockState(clicked.above()).is(GeoDecorContent.block(GeoDecorKind.ATLAS)), "Successful Keystone use must place the Atlas block above its clicked platform");
        helper.assertTrue(!level.getBlockState(baseCorner).is(marker), "Atlas base template must not be placed before its twenty end-tick delay");
        helper.runAfterDelay(19, () -> helper.assertTrue(!level.getBlockState(baseCorner).is(marker), "Atlas activation must preserve its twenty end-tick delay"));
        helper.runAfterDelay(21, () -> {
            helper.assertTrue(level.getBlockState(baseCorner).is(marker), "The actual Keystone activation must place the production atlasbase template after its delay");
            helper.assertTrue(!tasks.getFirst().isRemoved(), "Atlas activation task must remain armed for its production sixty-tick phase");
        });
        helper.runAfterDelay(82, () -> {
            helper.assertTrue(tasks.getFirst().isRemoved(), "Atlas activation task must finish its sixty-tick post-placement phase and discard");
            PlayerInteractionTests.release(helper, player);
            helper.succeed();
        });
    }

    private static boolean advancementDone(ServerPlayer player, String name) {
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(TsrGameTests.id(name));
        if (advancement == null) throw new IllegalStateException("Missing production advancement " + name);
        return player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    private static void teleport(GameTestHelper helper, ServerPlayer player, ServerLevel target, double x, double y, double z, String message) {
        helper.assertTrue(player.teleportTo(target, x, y, z, Set.of(), 0, 0, true), message);
    }

    private static boolean savedBoolean(WorldProgressData progress, String name) {
        try {
            Field field = WorldProgressData.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.getBoolean(progress);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Missing world progress field " + name, exception);
        }
    }

    private static final class PalaceAdvancementObserver {
        private final Set<UUID> watchedPlayers = new java.util.HashSet<>();
        private final List<String> observations = new ArrayList<>();

        private void watch(ServerPlayer player) {
            watchedPlayers.add(player.getUUID());
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
            if (!id("palace_get").equals(event.getAdvancement().id())) return;
            record(event.getEntity(), "earned", "");
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onAdvancementProgress(AdvancementEvent.AdvancementProgressEvent event) {
            if (!id("palace_get").equals(event.getAdvancement().id())) return;
            record(event.getEntity(), "progress=" + event.getProgressType(), ",criterion=" + event.getCriterionName() + ",done=" + event.getAdvancementProgress().isDone());
        }

        private void record(Player player, String eventType, String details) {
            if (!watchedPlayers.contains(player.getUUID())) return;
            String dimension = player.level().dimension().identifier().toString();
            boolean enabled = player.level() instanceof ServerLevel serverLevel
                    && serverLevel.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get());
            WorldProgressData progress = player.level() instanceof ServerLevel serverLevel
                    ? WorldProgressData.get(serverLevel)
                    : null;
            observations.add("event=" + eventType + details + ",player=" + player.getUUID() + ",dimension=" + dimension + ",rule=" + enabled
                    + ",placing=" + (progress != null && savedBoolean(progress, "palacePlacing"))
                    + ",exists=" + (progress != null && savedBoolean(progress, "palaceExists")));
        }

        private List<String> events() {
            return List.copyOf(observations);
        }
    }

    private static InteractionResult useBlock(ServerLevel level, ServerPlayer player, BlockPos pos) {
        player.snapTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0);
        return player.gameMode.useItemOn(player, level, player.getMainHandItem(), InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }

    private static int count(ServerPlayer player, Item item) {
        return player.getInventory().getNonEquipmentItems().stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }

    private static void installBed(ServerLevel level, BlockPos foot) {
        level.setBlock(foot.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(foot.relative(Direction.EAST).below(), Blocks.STONE.defaultBlockState(), 3);
        BlockState footState = Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.EAST).setValue(BedBlock.PART, BedPart.FOOT);
        BlockState headState = footState.setValue(BedBlock.PART, BedPart.HEAD);
        level.setBlock(foot.relative(Direction.EAST), headState, 3);
        level.setBlock(foot, footState, 3);
    }

    private static Block block(String path) {
        Block result = BuiltInRegistries.BLOCK.getValue(id(path));
        if (result == Blocks.AIR) throw new IllegalStateException("Missing registered block " + path);
        return result;
    }

    private static Identifier id(String path) {
        return TsrGameTests.id(path);
    }

    private static void release(GameTestHelper helper, ServerPlayer player) {
        if (player != null) PlayerInteractionTests.release(helper, player);
    }
}
