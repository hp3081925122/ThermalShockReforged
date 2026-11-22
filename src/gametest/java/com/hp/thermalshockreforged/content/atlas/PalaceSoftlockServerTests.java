package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import com.hp.thermalshockreforged.world.PalaceGeneration;
import com.hp.thermalshockreforged.world.WorldProgressData;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class PalaceSoftlockServerTests {
    private PalaceSoftlockServerTests() { }

    public static void register() {
        TsrGameTests.addDedicated("palace_softlock_preflight_and_round_trip", PalaceSoftlockServerTests::preflightAndRoundTrip, 300, "supernova_server");
        TsrGameTests.addDedicated("palace_softlock_existing_advancement_recovers_on_reentry", PalaceSoftlockServerTests::existingAdvancementRecoversOnReentry, 300, "supernova_server");
        TsrGameTests.addDedicated("palace_softlock_hidden_advancement_generates_structure", PalaceSoftlockServerTests::hiddenAdvancementGeneratesStructure, 300, "supernova_server");
    }

    public static void preflightAndRoundTrip(GameTestHelper helper) {
        var source = helper.getLevel();
        var target = source.getServer().getLevel(AtlasContent.PALACE);
        helper.assertTrue(source.dimension().equals(Level.OVERWORLD) && target != null, "The dedicated fixture must start in the overworld with Aqeron loaded");
        var progress = WorldProgressData.get(target);
        helper.assertTrue(!progress.palaceExists(), "This test requires a fresh world without committed Palace generation");
        boolean oldRule = target.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get());
        BlockPos origin = helper.absolutePos(new BlockPos(3, 2, 3));
        var atlas = GeoDecorContent.block(GeoDecorKind.ATLAS);
        var previous = source.getBlockState(origin);
        ServerPlayer player = null;
        try {
            source.setBlock(origin, atlas.defaultBlockState(), 3);
            target.getChunkAt(PalaceGeneration.RETURN_ATLAS);
            target.setBlock(PalaceGeneration.RETURN_ATLAS, Blocks.AIR.defaultBlockState(), 3);
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), false, source.getServer());
            player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 3.5, 2, 3.5);
            awardArrival(player);
            var outbound = new AtlasMenu(71, player.getInventory(), origin);
            player.containerMenu = outbound;
            Vec3 before = player.position();
            helper.assertTrue(!outbound.clickMenuButton(player, 2), "An Atlas without an available return route must reject travel while structure generation is disabled");
            helper.assertTrue(player.level() == source && player.position().equals(before) && player.containerMenu == outbound, "Rejected travel must leave the player and usable menu in the source dimension");
            helper.assertTrue(!progress.palaceExists() && AtlasCoordinates.get(player, Level.OVERWORLD).equals(Vec3.ZERO), "Rejected travel must not commit a structure or overwrite return coordinates");
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), true, source.getServer());
            helper.assertTrue(outbound.clickMenuButton(player, 2), "The same menu must allow retry after the destination becomes available");
            helper.assertTrue(player.level() == target && progress.palaceExists(), "The real Atlas button must prepare the missing arrival structure even when its advancement was already earned");
            helper.assertTrue(target.getBlockState(PalaceGeneration.RETURN_ATLAS).is(atlas), "The full template must provide its original return Atlas at the arrival point");
            helper.assertTrue(target.getBlockState(PalaceGeneration.RETURN_ATLAS.below()).is(com.hp.thermalshockreforged.content.MaterialContent.blocks().get("atlas_base_centre").get()), "The original pedestal must be placed with the full structure");
            helper.assertTrue(AtlasCoordinates.get(player, Level.OVERWORLD).equals(new Vec3(origin.getX(), origin.getY(), origin.getZ())), "Successful travel must preserve the actual overworld return Atlas");
            BlockPos sample = new BlockPos(-74, 178, -81);
            target.setBlock(sample, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            var returnPos = PalaceGeneration.RETURN_ATLAS;
            player.connection.teleport(returnPos.getX() + 0.5, returnPos.getY(), returnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
            var inbound = new AtlasMenu(72, player.getInventory(), PalaceGeneration.RETURN_ATLAS);
            player.containerMenu = inbound;
            helper.assertTrue(inbound.clickMenuButton(player, 1) && player.level() == source, "The generated Atlas must support a real return trip to the overworld");
            var repeated = new AtlasMenu(73, player.getInventory(), origin);
            player.containerMenu = repeated;
            helper.assertTrue(repeated.clickMenuButton(player, 2) && player.level() == target, "An existing return Atlas must allow a later visit");
            helper.assertTrue(target.getBlockState(sample).is(Blocks.GOLD_BLOCK), "A repeat visit must not regenerate the structure over later changes");
            var backAgain = new AtlasMenu(74, player.getInventory(), PalaceGeneration.RETURN_ATLAS);
            player.containerMenu = backAgain;
            helper.assertTrue(backAgain.clickMenuButton(player, 1), "The second return trip must succeed");
            target.setBlock(PalaceGeneration.RETURN_ATLAS, Blocks.AIR.defaultBlockState(), 3);
            var brokenReturn = new AtlasMenu(75, player.getInventory(), origin);
            player.containerMenu = brokenReturn;
            helper.assertTrue(!brokenReturn.clickMenuButton(player, 2) && player.level() == source, "A destroyed return Atlas must not strand a later visitor");
            helper.assertTrue(target.getBlockState(sample).is(Blocks.GOLD_BLOCK), "Missing return furniture must not trigger destructive whole-structure regeneration");
            helper.succeed();
        } finally {
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), oldRule, source.getServer());
            source.setBlock(origin, previous, 3);
            if (player != null) PlayerInteractionTests.release(helper, player);
        }
    }

    public static void existingAdvancementRecoversOnReentry(GameTestHelper helper) {
        var source = helper.getLevel();
        var target = source.getServer().getLevel(AtlasContent.PALACE);
        helper.assertTrue(target != null && !WorldProgressData.get(target).palaceExists(), "This test requires a fresh world without Palace");
        boolean oldRule = target.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get());
        ServerPlayer player = null;
        try {
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), false, source.getServer());
            player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 3.5, 2, 3.5);
            helper.assertTrue(player.teleportTo(target, 0, 194, 0, Set.of(), 0, 0, true), "The initial native dimension transfer must succeed");
            var advancement = source.getServer().getAdvancements().get(TsrGameTests.id("palace_get"));
            helper.assertTrue(advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone(), "The first visit must earn the arrival advancement while generation is disabled");
            helper.assertTrue(!WorldProgressData.get(target).palaceExists(), "Disabled generation must not be bypassed by the new arrival hook");
            helper.assertTrue(player.teleportTo(source, 0, 120, 0, Set.of(), 0, 0, true), "The fixture must return through the native transfer path");
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), true, source.getServer());
            helper.assertTrue(player.teleportTo(target, 0, 194, 0, Set.of(), 0, 0, true), "The same player must be able to revisit with an already completed advancement");
            helper.assertTrue(WorldProgressData.get(target).palaceExists() && target.getBlockState(PalaceGeneration.RETURN_ATLAS).is(GeoDecorContent.block(GeoDecorKind.ATLAS)), "Reentry must retry the missing full structure without requiring a fresh advancement");
            helper.succeed();
        } finally {
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), oldRule, source.getServer());
            if (player != null) PlayerInteractionTests.release(helper, player);
        }
    }

    public static void hiddenAdvancementGeneratesStructure(GameTestHelper helper) {
        var source = helper.getLevel();
        var target = source.getServer().getLevel(AtlasContent.PALACE);
        helper.assertTrue(target != null && !WorldProgressData.get(target).palaceExists(), "This test requires a fresh Palace world");
        boolean oldRule = target.getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get());
        ServerPlayer player = null;
        try {
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), false, source.getServer());
            player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 3.5, 2, 3.5);
            helper.assertTrue(player.teleportTo(target, 0, 194, 0, Set.of(), 0, 0, true), "The fixture must enter Palace before the isolated advancement test");
            var advancement = source.getServer().getAdvancements().get(TsrGameTests.id("palace_get"));
            helper.assertTrue(advancement != null && advancement.value().display().isEmpty(), "The production Palace advancement must remain display-free");
            for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getCompletedCriteria()) player.getAdvancements().revoke(advancement, criterion);
            helper.assertTrue(!WorldProgressData.get(target).palaceExists(), "The disabled-rule arrival must not have generated the Palace");
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), true, source.getServer());
            awardArrival(player);
            helper.assertTrue(WorldProgressData.get(target).palaceExists() && target.getBlockState(PalaceGeneration.RETURN_ATLAS).is(GeoDecorContent.block(GeoDecorKind.ATLAS)), "Granting the hidden advancement while already in Palace must generate its full template without another dimension-change or login event");
            helper.succeed();
        } finally {
            target.getGameRules().set(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get(), oldRule, source.getServer());
            if (player != null) PlayerInteractionTests.release(helper, player);
        }
    }

    private static void awardArrival(ServerPlayer player) {
        var advancement = player.level().getServer().getAdvancements().get(TsrGameTests.id("palace_get"));
        if (advancement == null) throw new IllegalStateException("Missing Aqeron arrival advancement");
        for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
    }
}
