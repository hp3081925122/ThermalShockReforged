package com.hp.thermalshockreforged.gametest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.hp.thermalshockreforged.world.WorldProgressData;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.registration.ChannelAttributes;
import net.neoforged.neoforge.network.registration.NetworkChannel;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class DedicatedServerRegression {
    private static final AtomicBoolean RAN = new AtomicBoolean();
    private static final double SEED_CRUX = 9.25;

    private DedicatedServerRegression() {
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        String mode = System.getProperty("tsr.dedicatedRegression", "");
        if ((!mode.equals("seed") && !mode.equals("verify")) || !RAN.compareAndSet(false, true)) return;
        JsonObject result = new JsonObject();
        Path resultFile = resultPath();
        result.addProperty("mode", mode);
        result.addProperty("resultFile", resultFile.toString());
        try {
            run(event.getServer(), mode, result);
            result.addProperty("status", "passed");
            writeResult(result, resultFile);
            ThermalShockReforged.LOGGER.info("Dedicated server regression passed: mode={}, result={}", mode, resultFile);
        } catch (Throwable failure) {
            result.addProperty("status", "failed");
            result.addProperty("failure", failure.toString());
            try {
                writeResult(result, resultFile);
            } catch (Throwable writeFailure) {
                failure.addSuppressed(writeFailure);
            }
            ThermalShockReforged.LOGGER.error("Dedicated server regression failed: mode={}", mode, failure);
        } finally {
            event.getServer().halt(false);
        }
    }

    private static void run(MinecraftServer server, String mode, JsonObject result) throws IOException {
        ServerLevel overworld = server.overworld();
        ServerLevel loqendia = requireLevel(server, LoqendiaContent.DIMENSION, "loqendia");
        ServerLevel aqeron = requireLevel(server, com.hp.thermalshockreforged.content.atlas.AtlasContent.PALACE, "aqeron");
        result.addProperty("dimensions", 2);
        result.addProperty("loqendiaKey", loqendia.dimension().identifier().toString());
        result.addProperty("aqeronKey", aqeron.dimension().identifier().toString());
        int chunks = validateDimension(server, loqendia, "loqendia", result);
        chunks += validateDimension(server, aqeron, "aqeron", result);
        result.addProperty("generatedCustomChunks", chunks);

        WorldProgressData progress = WorldProgressData.get(overworld);
        if (WorldProgressData.get(loqendia) != progress || WorldProgressData.get(aqeron) != progress
                || WorldProgressData.forGeneration(loqendia) != progress || WorldProgressData.forGeneration(aqeron) != progress) {
            throw new IllegalStateException("Custom dimensions do not share the overworld WorldProgressData instance");
        }
        result.addProperty("sharedWorldProgress", true);

        if (mode.equals("seed")) {
            if (progress.altarUnlocked() || progress.worldCrux() != 0 || progress.oreTier1() != 0 || progress.oreTier2() != 0) throw new IllegalStateException("Seed mode requires a fresh world progress state");
            testOutcropGate(loqendia, aqeron, progress);
            result.addProperty("outcropHarvestChecks", 8);
            progress.advanceCrux(SEED_CRUX);
            progress.initializeOreChoices(overworld);
            if (!progress.altarUnlocked() && !progress.unlockAltars()) throw new IllegalStateException("Could not seed altar unlock state");
            result.addProperty("expectedWorldCrux", SEED_CRUX);
            result.addProperty("expectedAltarUnlocked", true);
            result.addProperty("expectedOreTier1", progress.oreTier1());
            result.addProperty("expectedOreTier2", progress.oreTier2());
        } else {
            Path resultFile = resultPath();
            if (!Files.exists(resultFile)) throw new IllegalStateException("Seed result file is missing: " + resultFile);
            var expected = com.google.gson.JsonParser.parseString(Files.readString(resultFile, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!expected.has("status") || !expected.get("status").getAsString().equals("passed")
                    || !expected.has("mode") || !expected.get("mode").getAsString().equals("seed")) {
                throw new IllegalStateException("Dedicated server seed result is not marked passed");
            }
            if (!progress.altarUnlocked() || progress.worldCrux() != expected.get("expectedWorldCrux").getAsDouble()
                    || progress.oreTier1() != expected.get("expectedOreTier1").getAsInt()
                    || progress.oreTier2() != expected.get("expectedOreTier2").getAsInt()) {
                throw new IllegalStateException("World progress did not persist across dedicated server restart");
            }
            result.add("seedExpectations", expected.deepCopy());
            result.addProperty("persistedAltarUnlocked", progress.altarUnlocked());
            result.addProperty("persistedWorldCrux", progress.worldCrux());
            result.addProperty("persistedOreTier1", progress.oreTier1());
            result.addProperty("persistedOreTier2", progress.oreTier2());
        }

        if (!server.saveEverything(true, true, true)) throw new IllegalStateException("MinecraftServer.saveEverything returned false");
        result.addProperty("saveEverything", true);
    }

    private static ServerLevel requireLevel(MinecraftServer server, net.minecraft.resources.ResourceKey<Level> key, String name) {
        ServerLevel level = server.getLevel(key);
        if (level == null) throw new IllegalStateException("Dedicated server did not create custom dimension " + name + " (" + key.identifier() + ")");
        return level;
    }

    private static int validateDimension(MinecraftServer server, ServerLevel level, String name, JsonObject result) throws IOException {
        int nonAir = 0;
        String sampledBiome = null;
        int chunkCount = 0;
        Set<Identifier> expectedBiomes = dimensionBiomes(server, name);
        for (int chunkX = 0; chunkX < 2; chunkX++) {
            var chunk = level.getChunkSource().getChunk(chunkX, 0, ChunkStatus.FULL, true);
            if (chunk == null) throw new IllegalStateException(name + " failed to generate chunk " + chunkX + ",0");
            chunkCount++;
            int sampleX = chunkX * 16 + 8;
            int sampleZ = 8;
            for (int y = level.getMinY(); y < level.getMaxY(); y += 4) {
                var state = chunk.getBlockState(new BlockPos(sampleX, y, sampleZ));
                if (!state.isAir()) nonAir++;
            }
            var biome = level.getBiome(new BlockPos(sampleX, level.getMinY() + 80, sampleZ)).unwrapKey()
                    .orElseThrow(() -> new IllegalStateException(name + " returned an unregistered biome"));
            sampledBiome = biome.identifier().toString();
            if (!expectedBiomes.contains(biome.identifier())) {
                throw new IllegalStateException(name + " generated biome absent from its dimension definition: " + sampledBiome);
            }
        }
        if (nonAir == 0) throw new IllegalStateException(name + " generated no terrain blocks in the sampled columns");
        result.addProperty(name + "Chunks", chunkCount);
        result.addProperty(name + "NonAirSamples", nonAir);
        result.addProperty(name + "SampledBiome", sampledBiome);
        return chunkCount;
    }

    private static Set<Identifier> dimensionBiomes(MinecraftServer server, String name) throws IOException {
        Identifier resourceId = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "dimension/" + name + ".json");
        var resource = server.getResourceManager().getResource(resourceId)
                .orElseThrow(() -> new IllegalStateException("Missing dimension definition: " + resourceId));
        try (var reader = resource.openAsReader()) {
            var entries = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject()
                    .getAsJsonObject("generator").getAsJsonObject("biome_source").getAsJsonArray("biomes");
            Set<Identifier> ids = new java.util.HashSet<>();
            for (var entry : entries) ids.add(Identifier.parse(entry.getAsJsonObject().get("biome").getAsString()));
            if (ids.isEmpty()) throw new IllegalStateException("Dimension definition contains no biome entries: " + resourceId);
            return Set.copyOf(ids);
        }
    }

    private static void testOutcropGate(ServerLevel loqendia, ServerLevel aqeron, WorldProgressData progress) {
        if (progress.altarUnlocked()) throw new IllegalStateException("Outcrop gate check must start locked");
        checkOutcrops(loqendia);
        checkOutcrops(aqeron);
        if (!progress.unlockAltars()) throw new IllegalStateException("Could not unlock world altar progress for gate check");
        checkOutcrops(loqendia);
        checkOutcrops(aqeron);
    }

    private static void checkOutcrops(ServerLevel level) {
        var player = createPlayer(level);
        try {
            player.setGameMode(GameType.SURVIVAL);
            player.getInventory().setItem(0, Items.DIAMOND_PICKAXE.getDefaultInstance());
            GeoDecorKind[] kinds = {GeoDecorKind.PHOBOTITE_OUTCROP, GeoDecorKind.PHOBOTITE_MANTLE_OUTCROP};
            for (int index = 0; index < kinds.length; index++) {
                GeoDecorKind kind = kinds[index];
                BlockPos pos = new BlockPos(8, level.getMinY() + 100, 8 + index);
                level.setBlock(pos, GeoDecorContent.block(kind).defaultBlockState(), 3);
                boolean canHarvest = level.getBlockState(pos).canHarvestBlock(level, pos, player);
                boolean shouldHarvest = WorldProgressData.get(level).altarUnlocked();
                if (canHarvest != shouldHarvest) throw new IllegalStateException("Unexpected phobotite mining gate: dimension=" + level.dimension().identifier() + ", block=" + kind.id + ", unlocked=" + shouldHarvest + ", canHarvest=" + canHarvest);
            }
        } finally {
            level.getServer().getPlayerList().remove(player);
        }
    }

    private static ServerPlayer createPlayer(ServerLevel level) {
        MinecraftServer server = level.getServer();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "tsr-regress"), false);
        var player = new ServerPlayer(server, level, cookie.gameProfile(), cookie.clientInformation());
        var connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        negotiateChannels(connection);
        server.getPlayerList().placeNewPlayer(connection, player, cookie);
        player.connection.markClientLoaded();
        if (!player.teleportTo(level, 8.5, level.getMinY() + 101, 8.5, Set.of(), 0, 0, false) || player.level() != level) {
            throw new IllegalStateException("Test player did not remain in target dimension: " + level.dimension().identifier());
        }
        return player;
    }

    @SuppressWarnings("unchecked")
    private static void negotiateChannels(Connection connection) {
        try {
            var field = net.neoforged.neoforge.network.registration.NetworkRegistry.class.getDeclaredField("PAYLOAD_REGISTRATIONS");
            field.setAccessible(true);
            var registrations = (Map<ConnectionProtocol, Map<Identifier, net.neoforged.neoforge.network.registration.PayloadRegistration<?>>>) field.get(null);
            Map<Identifier, NetworkChannel> channels = new HashMap<>();
            registrations.getOrDefault(ConnectionProtocol.PLAY, Map.of()).forEach((id, registration) -> {
                if (!id.equals(net.neoforged.neoforge.network.payload.ConfigFilePayload.TYPE.id())) channels.put(id, new NetworkChannel(id, registration.version()));
            });
            ChannelAttributes.setPayloadSetup(connection, new NetworkPayloadSetup(Map.of(ConnectionProtocol.PLAY, channels)));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not initialize test player NeoForge channels", exception);
        }
    }

    private static Path resultPath() {
        String directory = System.getProperty("tsr.dedicatedRegressionResultDir", "").trim();
        return directory.isEmpty() ? Path.of("D:/AI临时文件/tsr-dedicated-regression.json") : Path.of(directory).resolve("tsr-dedicated-regression.json");
    }

    private static void writeResult(JsonObject result, Path resultFile) throws java.io.IOException {
        Files.createDirectories(resultFile.getParent());
        Files.writeString(resultFile, new GsonBuilder().setPrettyPrinting().create().toJson(result), StandardCharsets.UTF_8);
    }
}
