package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.clock.WorldClocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class ClientWorldServer {
    private static final List<String> SCENARIOS = createScenarios();
    private static final int CENTER_X = 8;
    private static final int CENTER_Z = 8;
    private static final int PATCH_RADIUS = 16;
    private static final String LOQ = "thermal_shock_reforged:loqendia";
    private static final String AQERON = "thermal_shock_reforged:aqeron";

    private ClientWorldServer() {
    }

    public static List<String> scenarios() {
        return SCENARIOS;
    }

    private static List<String> createScenarios() {
        var values = new ArrayList<String>();
        values.add("weather_overworld_borean_snow_radius10");
        values.add("weather_loq_borean_snow_radius4");
        values.add("loq_sunrise");
        values.add("loq_noon");
        values.add("loq_sunset");
        values.add("loq_night");
        values.add("luminous_sea_rain");
        values.add("lunar_tundra_clear");
        for (int phase = 0; phase < 8; phase++) values.add("aqeron_moon_" + phase);
        return List.copyOf(values);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!ClientIntegrationData.enabled() || !Boolean.getBoolean("tsr.clientIntegrationWorld")) return;
        event.getDispatcher().register(Commands.literal("tsr_world")
                .then(Commands.literal("prepare")
                        .then(Commands.argument("scene", StringArgumentType.word())
                                .executes(context -> prepare(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scene"))))));
    }

    private static int prepare(ServerPlayer player, String scene) {
        JsonObject evidence = new JsonObject();
        evidence.addProperty("scene", scene);
        try {
            ClientIntegrationData.require(SCENARIOS.contains(scene), "Unsupported world scene: " + scene);
            JsonObject ready = ClientIntegrationData.read("server-ready.json");
            JsonObject clockEvidence = ClientIntegrationData.read("server-clock.json");
            String session = ready.get("session").getAsString();
            ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()), "TCP integration server is not ready");
            ClientIntegrationData.require(session.equals(clockEvidence.get("session").getAsString()), "World clock evidence belongs to another session");
            evidence.addProperty("session", session);
            evidence.addProperty("serverTick", clockEvidence.get("serverTick").getAsLong());
            MinecraftServer server = player.level().getServer();
            Scene target = describe(scene);
            ServerLevel level = server.getLevel(target.dimension());
            ClientIntegrationData.require(level != null, "Target dimension is not loaded: " + target.dimension().identifier());
            ClientIntegrationData.require(server.getPlayerList().getPlayer(player.getUUID()) == player, "Command player is not a connected server player");
            ClientIntegrationData.require(!player.connection.getConnection().isMemoryConnection(), "World preparation requires the real TCP client");
            ensureChunkSquare(level);
            int highestTerrain = level.getSeaLevel() + 8;
            for (int x = CENTER_X - PATCH_RADIUS; x <= CENTER_X + PATCH_RADIUS; x++) {
                for (int z = CENTER_Z - PATCH_RADIUS; z <= CENTER_Z + PATCH_RADIUS; z++) {
                    highestTerrain = Math.max(highestTerrain, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z));
                }
            }
            int groundY = Math.max(level.getMinY() + 8, Math.min(highestTerrain, level.getMaxY() - 16));
            if (target.kind().equals("rain")) groundY = Math.max(level.getMinY() + 8, Math.min(level.getSeaLevel() + 8, level.getMaxY() - 16));
            int playerY = groundY + 1;
            buildCameraPlatform(level, groundY);
            if (target.kind().equals("rain")) {
                for (int x = CENTER_X - 4; x <= CENTER_X + 4; x++) {
                    for (int z = CENTER_Z - 4; z <= CENTER_Z + 4; z++) {
                        for (int y = groundY + 9; y < level.getMaxY(); y++) {
                            BlockPos opening = new BlockPos(x, y, z);
                            if (!level.getBlockState(opening).isAir()) level.setBlock(opening, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
            BlockPos from = new BlockPos(CENTER_X - PATCH_RADIUS, floorToFour(playerY - 8), CENTER_Z - PATCH_RADIUS);
            BlockPos to = new BlockPos(CENTER_X + PATCH_RADIUS - 1, floorToFour(playerY + 7), CENTER_Z + PATCH_RADIUS - 1);
            Holder<Biome> biome = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(target.biome());
            boolean cameraBiomeAlreadyMatches = level.getBiome(new BlockPos(CENTER_X, playerY + 1, CENTER_Z)).is(target.biome());
            Either<Integer, com.mojang.brigadier.exceptions.CommandSyntaxException> fill = FillBiomeCommand.fill(level, from, to, biome);
            ClientIntegrationData.require(fill.left().isPresent(), "Biome patch failed: " + fill.right().map(Throwable::getMessage).orElse("unknown fill error"));
            int changedQuartCells = fill.left().get();
            ClientIntegrationData.require(changedQuartCells > 0 || cameraBiomeAlreadyMatches, "Biome patch changed no cells and the camera biome does not match at " + from + ".." + to);
            BlockPos eyeBlock = new BlockPos(CENTER_X, playerY + 1, CENTER_Z);
            Biome.Precipitation cameraPrecipitation = level.getBiome(eyeBlock).value().getPrecipitationAt(eyeBlock, level.getSeaLevel());
            evidence.addProperty("seaLevel", level.getSeaLevel());
            evidence.addProperty("cameraBiomePrecipitation", cameraPrecipitation.toString());
            evidence.addProperty("cameraBiomeBaseTemperature", level.getBiome(eyeBlock).value().getBaseTemperature());
            if (target.kind().equals("rain")) ClientIntegrationData.require(cameraPrecipitation == Biome.Precipitation.RAIN, "Rain fixture camera must be below the native snow-height threshold");
            if (target.clockTicks() != null) {
                var clock = server.registryAccess().getOrThrow(WorldClocks.OVERWORLD);
                level.clockManager().setTotalTicks(clock, target.clockTicks());
                evidence.addProperty("expectedClockTicks", target.clockTicks());
            }
            server.setWeatherParameters(target.raining() ? 0 : 1000000, 1000000, target.raining(), false);
            player.teleportTo(level, CENTER_X + 0.5D, playerY, CENTER_Z + 0.5D, java.util.Set.of(), target.yaw(), target.pitch(), true);
            player.setYRot(target.yaw());
            player.setXRot(target.pitch());
            evidence.addProperty("status", "passed");
            evidence.addProperty("dimension", level.dimension().identifier().toString());
            evidence.addProperty("biome", target.biome().identifier().toString());
            evidence.addProperty("sceneKind", target.kind());
            evidence.addProperty("cameraX", CENTER_X + 0.5D);
            evidence.addProperty("cameraY", playerY);
            evidence.addProperty("cameraZ", CENTER_Z + 0.5D);
            evidence.addProperty("cameraYaw", target.yaw());
            evidence.addProperty("cameraPitch", target.pitch());
            evidence.addProperty("fixturePlatformY", groundY);
            evidence.addProperty("weatherRequested", target.raining() ? "rain_or_snow_by_biome" : "clear");
            evidence.addProperty("biomePatchMethod", "FillBiomeCommand.fill on loaded chunks; controlled local fixture, not natural world generation");
            BlockPos effectiveFrom = quantize(from);
            BlockPos effectiveTo = quantize(to);
            evidence.addProperty("biomePatchRequestedBlockBounds", from.toShortString() + ".." + to.toShortString());
            evidence.addProperty("biomePatchEffectiveBlockBounds", effectiveFrom.toShortString() + ".." + effectiveTo.toShortString());
            evidence.addProperty("biomePatchEffectiveBlockVolume", (long)(effectiveTo.getX() - effectiveFrom.getX() + 1) * (effectiveTo.getY() - effectiveFrom.getY() + 1) * (effectiveTo.getZ() - effectiveFrom.getZ() + 1));
            evidence.addProperty("cameraPlatformBounds", (CENTER_X - PATCH_RADIUS) + "," + groundY + "," + (CENTER_Z - PATCH_RADIUS) + ".." + (CENTER_X + PATCH_RADIUS) + "," + (groundY + 8) + "," + (CENTER_Z + PATCH_RADIUS));
            evidence.addProperty("cameraPlatformRequestedVolume", 33 * 9 * 33);
            evidence.addProperty("cameraPlatformHeightSelection", target.kind().equals("rain") ? "Sea level +8, below the native height-temperature snow threshold, with a9x9 open-sky weather column shaft" : "At or above the highest MOTION_BLOCKING terrain in the bounded33x33 area; clamped16 blocks below dimension top");
            ThermalShockReforged.LOGGER.debug("World fixture camera precipitation preflight: scene={}, eyeBlockY={}, seaLevel={}, precipitation={}", scene, eyeBlock.getY(), level.getSeaLevel(), cameraPrecipitation);
            evidence.addProperty("biomePatchSafetyMargin", "32-block horizontal biome fill includes every neighboring quart-cell corner consulted by BiomeManager.getBiome for the21x21 weather column area");
            evidence.addProperty("biomePatchChangedQuartCells", changedQuartCells);
            evidence.addProperty("chunkLoadArea", "9 FULL chunks centered at chunk 0,0; terrain generation may occur inside this bounded fixture area");
            evidence.addProperty("expectedCloudPass", target.dimension().identifier().toString().equals(AQERON) ? "skipped_when_cloud_color_alpha_is_zero" : "normal_option_and_color_gate");
            evidence.addProperty("expectedMoonPhase", target.moonPhase() == null ? "" : target.moonPhase());
            evidence.addProperty("expectedSunAngleDegrees", target.sunAngleDegrees());
            evidence.addProperty("expectedStarBrightness", target.starBrightness() == null ? -1.0F : target.starBrightness());
        } catch (Throwable failure) {
            evidence.addProperty("status", "failed");
            evidence.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client world preparation failed: scene={}", scene, failure);
        }
        try {
            ClientIntegrationData.write("world-" + scene + "-prepared.json", evidence);
        } catch (Throwable failure) {
            ThermalShockReforged.LOGGER.error("Client world evidence publication failed: scene={}", scene, failure);
            return 0;
        }
        return "passed".equals(evidence.get("status").getAsString()) ? 1 : 0;
    }

    private static void ensureChunkSquare(ServerLevel level) {
        for (int chunkX = -1; chunkX <= 1; chunkX++) {
            for (int chunkZ = -1; chunkZ <= 1; chunkZ++) {
                level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
            }
        }
    }

    private static void buildCameraPlatform(ServerLevel level, int groundY) {
        var stone = Blocks.STONE.defaultBlockState();
        for (int x = CENTER_X - PATCH_RADIUS; x <= CENTER_X + PATCH_RADIUS; x++) {
            for (int z = CENTER_Z - PATCH_RADIUS; z <= CENTER_Z + PATCH_RADIUS; z++) {
                BlockPos floor = new BlockPos(x, groundY, z);
                if (!level.getBlockState(floor).equals(stone)) level.setBlock(floor, stone, 3);
                for (int y = groundY + 1; y <= groundY + 8; y++) {
                    BlockPos air = new BlockPos(x, y, z);
                    if (!level.getBlockState(air).isAir()) level.setBlock(air, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static int floorToFour(int value) {
        return Math.floorDiv(value, 4) * 4;
    }

    private static BlockPos quantize(BlockPos block) {
        return new BlockPos(QuartPos.toBlock(QuartPos.fromBlock(block.getX())), QuartPos.toBlock(QuartPos.fromBlock(block.getY())), QuartPos.toBlock(QuartPos.fromBlock(block.getZ())));
    }

    private static Scene describe(String name) {
        ResourceKey<Level> overworld = Level.OVERWORLD;
        ResourceKey<Level> loq = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("thermal_shock_reforged", "loqendia"));
        ResourceKey<Level> aqeron = AtlasContent.PALACE;
        if (name.startsWith("aqeron_moon_")) {
            int phase = Integer.parseInt(name.substring("aqeron_moon_".length()));
            String[] phases = {"full_moon", "waning_gibbous", "third_quarter", "waning_crescent", "new_moon", "waxing_crescent", "first_quarter", "waxing_gibbous"};
            return new Scene(aqeron, biome("borean_tundra"), "aqeron_moon", 180.0F, 0.5F, phases[phase], 18000L + phase * 24000L, false, 0.0F, -80.0F);
        }
        if (name.equals("weather_overworld_borean_snow_radius10")) return new Scene(overworld, biome("borean_tundra"), "weather_snow_radius_10", null, null, null, null, true, 0.0F, 0.0F);
        if (name.equals("weather_loq_borean_snow_radius4")) return new Scene(loq, biome("borean_tundra"), "weather_snow_radius_4", null, null, null, null, true, 0.0F, 0.0F);
        if (name.equals("loq_sunrise")) return new Scene(loq, biome("borean_tundra"), "sunrise", null, null, null, 600L, false, -90.0F, -20.0F);
        if (name.equals("loq_noon")) return new Scene(loq, biome("borean_tundra"), "noon", null, null, null, 6000L, false, 0.0F, -80.0F);
        if (name.equals("loq_sunset")) return new Scene(loq, biome("borean_tundra"), "sunset", null, null, null, 11600L, false, 90.0F, -20.0F);
        if (name.equals("loq_night")) return new Scene(loq, biome("borean_tundra"), "night", null, null, null, 18000L, false, 0.0F, -80.0F);
        if (name.equals("luminous_sea_rain")) return new Scene(loq, biome("luminous_sea"), "rain", null, null, null, null, true, 0.0F, 0.0F);
        if (name.equals("lunar_tundra_clear")) return new Scene(loq, biome("lunar_tundra"), "no_precipitation", null, null, null, null, true, 0.0F, 0.0F);
        throw new IllegalArgumentException("No scene descriptor for " + name);
    }

    private static ResourceKey<Biome> biome(String path) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path));
    }

    private record Scene(ResourceKey<Level> dimension, ResourceKey<Biome> biome, String kind, Float sunAngleDegrees, Float starBrightness, String moonPhase, Long clockTicks, boolean raining, float yaw, float pitch) {
    }
}

