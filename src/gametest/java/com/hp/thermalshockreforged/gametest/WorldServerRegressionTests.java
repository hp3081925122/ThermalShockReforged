package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.world.WorldProgressData;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.IOException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class WorldServerRegressionTests {
    private WorldServerRegressionTests() {
    }

    public static void worldProgressCodecAndStateTransitionsRoundTrip(GameTestHelper helper) {
        WorldProgressData progress = new WorldProgressData();
        helper.assertTrue(!progress.altarUnlocked(), "Fresh isolated world progress must keep altars locked");
        helper.assertTrue(progress.unlockAltars() && !progress.unlockAltars(), "Altar unlock must be a one-time transition");
        progress.advanceCrux(2.5);
        progress.advanceCrux(1.0);
        helper.assertTrue(progress.worldCrux() == 2.5, "World crux must only advance to a greater finite stage");
        helper.assertTrue(progress.beginPalacePlacement() && !progress.beginPalacePlacement(), "Palace placement must reject a concurrent duplicate");
        progress.finishPalacePlacement(false);
        helper.assertTrue(progress.beginPalacePlacement(), "Failed palace placement must release its reservation");
        progress.finishPalacePlacement(true);
        helper.assertTrue(!progress.beginPalacePlacement(), "Successfully placed palace must be idempotent");
        helper.assertTrue(progress.beginShalespringPlacement(), "Shalespring should reserve once");
        progress.finishShalespringPlacement(true);
        helper.assertTrue(!progress.beginShalespringPlacement(), "Shalespring existence must prevent duplicate placement");
        helper.assertTrue(progress.beginKeystonePlacement(), "Keystone should reserve once");
        Identifier noticeDimension = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "aqeron");
        net.minecraft.core.BlockPos anchor = new net.minecraft.core.BlockPos(37, 82, -19);
        progress.finishKeystonePlacement(true, noticeDimension, anchor);
        helper.assertTrue(!progress.beginKeystonePlacement(), "Keystone existence must prevent duplicate placement");
        helper.assertTrue(progress.keystoneNoticeDimension().equals(noticeDimension) && progress.keystoneNoticeAnchor().equals(anchor), "Keystone notification must retain its dimension and anchor");
        for (int tick = 1; tick < 200; tick++) helper.assertTrue(progress.advanceKeystoneNotice() == tick, "Keystone notice tick sequence changed at " + tick);

        ServerLevel overworld = helper.getLevel().getServer().overworld();
        progress.initializeOreChoices(overworld);
        int oreOne = progress.oreTier1();
        int oreTwo = progress.oreTier2();
        helper.assertTrue((oreOne == 1 || oreOne == 2) && (oreTwo == 1 || oreTwo == 2), "Ore choices must initialize to supported variants");
        progress.initializeOreChoices(overworld);
        helper.assertTrue(progress.oreTier1() == oreOne && progress.oreTier2() == oreTwo, "Ore choices must remain stable after initialization");

        try {
            Field field = WorldProgressData.class.getDeclaredField("FACTORY");
            field.setAccessible(true);
            SavedDataType<?> type = (SavedDataType<?>) field.get(null);
            Method codecMethod = java.util.Arrays.stream(type.getClass().getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 0 && Codec.class.isAssignableFrom(method.getReturnType()))
                    .findFirst().orElseThrow(() -> new IllegalStateException("SavedDataType codec accessor was not found"));
            codecMethod.setAccessible(true);
            Codec<?> rawCodec = (Codec<?>) codecMethod.invoke(type);
            @SuppressWarnings("unchecked")
            Codec<WorldProgressData> codec = (Codec<WorldProgressData>) rawCodec;
            net.minecraft.nbt.Tag encoded = codec.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, progress)
                    .getOrThrow(error -> new IllegalStateException("World progress codec encode failed: " + error));
            WorldProgressData restored = codec.parse(net.minecraft.nbt.NbtOps.INSTANCE, encoded)
                    .getOrThrow(error -> new IllegalStateException("World progress codec decode failed: " + error));
            helper.assertTrue(restored.altarUnlocked() && restored.worldCrux() == 2.5, "Codec round trip lost altar or world crux progress");
            helper.assertTrue(restored.oreTier1() == oreOne && restored.oreTier2() == oreTwo, "Codec round trip changed chosen ores");
            helper.assertTrue(restored.keystoneExists() && restored.keystoneNoticeDimension().equals(noticeDimension)
                    && restored.keystoneNoticeAnchor().equals(anchor), "Codec round trip lost keystone notification anchor");
            helper.assertTrue(restored.advanceKeystoneNotice() == 200, "Keystone notification must end at tick 200");
            helper.assertTrue(restored.keystoneNoticeComplete(), "Keystone notification should complete after 200 ticks");
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not access the production world progress codec", exception);
        }
        helper.succeed();
    }

    public static void customDimensionDefinitionsAndWorldgenDecode(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        LevelStem loqendia = decodeDimension(server.getResourceManager(), ops, "loqendia");
        LevelStem aqeron = decodeDimension(server.getResourceManager(), ops, "aqeron");

        helper.assertTrue(!loqendia.type().value().hasCeiling() && loqendia.type().value().minY() == -64
                && loqendia.type().value().height() == 384, "Loqendia dimension type must retain has_ceiling=false and its -64..319 build range");
        helper.assertTrue(!aqeron.type().value().hasCeiling() && aqeron.type().value().minY() == 0
                && aqeron.type().value().height() == 256, "Aqeron dimension type must retain has_ceiling=false and its 0..255 build range");
        helper.assertTrue(loqendia.generator() instanceof NoiseBasedChunkGenerator, "Loqendia dimension must decode a noise chunk generator");
        helper.assertTrue(aqeron.generator() instanceof NoiseBasedChunkGenerator, "Aqeron dimension must decode a noise chunk generator");
        helper.assertTrue(helper.getLevel().getGameRules().get(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES.get()), "Special structure generation gamerule must be registered with its default enabled");
        helper.assertTrue(helper.getLevel().getGameRules().get(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION.get()), "Progression enforcement gamerule must be registered with its default enabled");

        var phobotiteFeature = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("thermal_shock_reforged", "phobotite_outcrop_generation"));
        var benzorStructure = ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath("thermal_shock_reforged", "benzor_home"));
        helper.assertTrue(helper.getLevel().registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(phobotiteFeature).isPresent(), "Phobotite worldgen feature must load into the server registry");
        helper.assertTrue(helper.getLevel().registryAccess().lookupOrThrow(Registries.STRUCTURE).get(benzorStructure).isPresent(), "Benzor structure must load into the server registry");
        helper.succeed();
    }

    private static LevelStem decodeDimension(net.minecraft.server.packs.resources.ResourceManager resourceManager,
                                            com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> ops, String name) {
        Identifier resourceId = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "dimension/" + name + ".json");
        var resource = resourceManager.getResource(resourceId)
                .orElseThrow(() -> new IllegalStateException("Missing dimension definition: " + resourceId));
        try (var reader = resource.openAsReader()) {
            var json = JsonParser.parseReader(reader);
            return LevelStem.CODEC.decode(ops, json).getOrThrow(error -> new IllegalStateException(resourceId + ": " + error)).getFirst();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read dimension definition: " + resourceId, exception);
        }
    }
}
