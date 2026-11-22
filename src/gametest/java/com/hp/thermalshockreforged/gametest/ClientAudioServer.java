package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.weatherutility.WeatherUtilityContent;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.List;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class ClientAudioServer {
    private static final List<String> SCENARIOS = List.of("rain_drum", "angry_rain_drum");
    private static final int DRUM_Y = 66;

    private ClientAudioServer() {
    }

    public static List<String> scenarios() {
        return SCENARIOS;
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationAudioProbe");
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!enabled()) return;
        event.getDispatcher().register(Commands.literal("tsr_audio")
                .then(Commands.literal("prepare")
                        .then(Commands.argument("scene", StringArgumentType.word())
                                .executes(context -> prepare(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scene")))))
                .then(Commands.literal("trigger")
                        .then(Commands.argument("scene", StringArgumentType.word())
                                .executes(context -> trigger(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scene"))))));
    }

    private static int prepare(ServerPlayer player, String scene) {
        JsonObject evidence = new JsonObject();
        evidence.addProperty("scene", scene);
        try {
            ClientIntegrationData.require(enabled(), "Audio integration probe is not enabled");
            ClientIntegrationData.require(SCENARIOS.contains(scene), "Unsupported rain drum scene: " + scene);
            Session session = currentSession();
            evidence.addProperty("session", session.id());
            evidence.addProperty("serverTick", session.tick());
            ServerLevel level = player.level().getServer().getLevel(Level.OVERWORLD);
            ClientIntegrationData.require(level != null, "Overworld is not loaded");
            ClientIntegrationData.require(player.level() == level, "Rain drum fixture must be prepared in the Overworld");
            ClientIntegrationData.require(player.connection != null && !player.connection.getConnection().isMemoryConnection(), "Audio probe requires a real TCP player");
            ClientIntegrationData.require(player.level().getServer().getPlayerList().getPlayer(player.getUUID()) == player, "Command source is not a connected server player");

            BlockPos playerBlock = player.blockPosition();
            BlockPos drum = new BlockPos(playerBlock.getX() + 12, DRUM_Y, playerBlock.getZ() + 12);
            BlockPos lever = drum.east();
            preparePlatform(level, drum, lever);
            level.setBlock(drum.below(), scene.equals("angry_rain_drum") ? NativeDecorContent.THERMITE.get().defaultBlockState() : Blocks.STONE.defaultBlockState(), 3);
            level.setBlock(drum, WeatherUtilityContent.RAIN_DRUM.get().defaultBlockState(), 3);
            var leverState = Blocks.LEVER.defaultBlockState()
                    .setValue(LeverBlock.FACE, AttachFace.FLOOR)
                    .setValue(LeverBlock.FACING, net.minecraft.core.Direction.NORTH)
                    .setValue(LeverBlock.POWERED, false);
            level.setBlock(lever, leverState, 3);
            player.level().getServer().setWeatherParameters(0, 1000000, true, false);
            ClientIntegrationData.require(player.teleportTo(level, drum.getX() + 0.5D, drum.getY(), drum.getZ() + 4.5D, java.util.Set.of(), 180.0F, 10.0F, true), "Cannot position connected client near the rain drum");
            player.setYRot(180.0F);
            player.setXRot(10.0F);

            ClientIntegrationData.require(player.level().getServer().getWeatherData().isRaining(), "Server weather data did not accept the requested rain state");
            ClientIntegrationData.require(level.getBlockState(drum).is(WeatherUtilityContent.RAIN_DRUM.get()), "Rain drum block was not placed");
            ClientIntegrationData.require(level.isEmptyBlock(drum.above()), "Rain drum opening is obstructed");
            ClientIntegrationData.require(!level.getBlockState(lever).getValue(LeverBlock.POWERED), "Lever must begin unpowered");
            ClientIntegrationData.require(level.getBestNeighborSignal(drum) == 0, "Rain drum must begin with no redstone signal");

            evidence.addProperty("status", "passed");
            evidence.addProperty("dimension", level.dimension().identifier().toString());
            evidence.addProperty("drumX", drum.getX());
            evidence.addProperty("drumY", drum.getY());
            evidence.addProperty("drumZ", drum.getZ());
            evidence.addProperty("leverX", lever.getX());
            evidence.addProperty("leverY", lever.getY());
            evidence.addProperty("leverZ", lever.getZ());
            evidence.addProperty("belowBlock", scene.equals("angry_rain_drum") ? "thermal_shock_reforged:thermite" : "minecraft:stone");
            evidence.addProperty("aboveAir", true);
            evidence.addProperty("leverPowered", false);
            evidence.addProperty("neighborSignal", 0);
            evidence.addProperty("raining", level.isRaining());
            evidence.addProperty("rainRequested", true);
            evidence.addProperty("rainIntensity", level.getRainLevel(1.0F));
            evidence.addProperty("awaitRainTransition", true);
            ThermalShockReforged.LOGGER.debug("Audio fixture requested rain and awaits the native intensity transition before triggering: scene={}, initialIntensity={}", scene, level.getRainLevel(1.0F));
            evidence.addProperty("expectedSound", soundId(scene));
            evidence.addProperty("fixture", "Server-controlled isolated open-sky platform in the dedicated TCP test world");
            evidence.addProperty("soundPath", "LeverBlock.pull toggles the adjacent lever and runs native neighbor updates; RainDrumBlock.neighborChanged selects the registered random sound pool");
            evidence.addProperty("limitation", "RainDrumBlock.animateTick can also emit the same ambient sound randomly during rain; the test does not attribute a client sound callback to one server packet");
        } catch (Throwable failure) {
            evidence.addProperty("status", "failed");
            evidence.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client audio preparation failed: scene={}", scene, failure);
        }
        try {
            ClientIntegrationData.write("audio-" + scene + "-prepared.json", evidence);
        } catch (Throwable failure) {
            ThermalShockReforged.LOGGER.error("Client audio preparation evidence publication failed: scene={}", scene, failure);
            return 0;
        }
        return "passed".equals(evidence.get("status").getAsString()) ? 1 : 0;
    }

    private static int trigger(ServerPlayer player, String scene) {
        JsonObject evidence = new JsonObject();
        evidence.addProperty("scene", scene);
        try {
            ClientIntegrationData.require(enabled(), "Audio integration probe is not enabled");
            ClientIntegrationData.require(SCENARIOS.contains(scene), "Unsupported rain drum scene: " + scene);
            Session session = currentSession();
            evidence.addProperty("session", session.id());
            evidence.addProperty("serverTick", session.tick());
            JsonObject prepared = ClientIntegrationData.read("audio-" + scene + "-prepared.json");
            ClientIntegrationData.require("passed".equals(prepared.get("status").getAsString()), "Rain drum preparation did not pass");
            ClientIntegrationData.require(session.id().equals(prepared.get("session").getAsString()), "Prepared scene belongs to another test session");
            ClientIntegrationData.require(scene.equals(prepared.get("scene").getAsString()), "Prepared scene identity does not match command");
            ServerLevel level = player.level().getServer().getLevel(Level.OVERWORLD);
            ClientIntegrationData.require(level != null && player.level() == level, "Trigger player must be in the Overworld");
            ClientIntegrationData.require(player.connection != null && !player.connection.getConnection().isMemoryConnection(), "Audio probe requires a real TCP player");
            ClientIntegrationData.require(player.level().getServer().getPlayerList().getPlayer(player.getUUID()) == player, "Command source is not a connected server player");
            int drumX = prepared.get("drumX").getAsInt();
            int drumY = prepared.get("drumY").getAsInt();
            int drumZ = prepared.get("drumZ").getAsInt();
            int leverX = prepared.get("leverX").getAsInt();
            int leverY = prepared.get("leverY").getAsInt();
            int leverZ = prepared.get("leverZ").getAsInt();
            BlockPos drum = new BlockPos(drumX, drumY, drumZ);
            BlockPos lever = new BlockPos(leverX, leverY, leverZ);
            level.getChunk(drum.getX() >> 4, drum.getZ() >> 4, ChunkStatus.FULL, true);
            ClientIntegrationData.require(level.isRaining(), "Rain is no longer active");
            ClientIntegrationData.require(level.getBlockState(drum).is(WeatherUtilityContent.RAIN_DRUM.get()), "Prepared rain drum is missing");
            ClientIntegrationData.require(level.isEmptyBlock(drum.above()), "Rain drum opening is obstructed");
            ClientIntegrationData.require(level.getBestNeighborSignal(drum) == 0, "Rain drum already has a redstone signal");
            ClientIntegrationData.require(level.getBlockState(lever).getBlock() instanceof LeverBlock, "Prepared lever is missing");
            var leverState = level.getBlockState(lever);
            ClientIntegrationData.require(!leverState.getValue(LeverBlock.POWERED), "Prepared lever must still be off");
            ClientIntegrationData.require(soundId(scene).equals(prepared.get("expectedSound").getAsString()), "Prepared sound identity does not match scene");

            ((LeverBlock) leverState.getBlock()).pull(leverState, level, lever, player);
            var triggeredLever = level.getBlockState(lever);
            int neighborSignal = level.getBestNeighborSignal(drum);
            boolean aboveAir = level.isEmptyBlock(drum.above());
            boolean raining = level.isRaining();
            ClientIntegrationData.require(triggeredLever.getValue(LeverBlock.POWERED), "Native lever interaction did not power the lever");
            ClientIntegrationData.require(neighborSignal > 0, "Lever neighbor update did not power the rain drum");
            ClientIntegrationData.require(raining && aboveAir, "Rain drum sound preconditions changed during trigger");

            evidence.addProperty("status", "passed");
            evidence.addProperty("dimension", level.dimension().identifier().toString());
            evidence.addProperty("drumX", drumX);
            evidence.addProperty("drumY", drumY);
            evidence.addProperty("drumZ", drumZ);
            evidence.addProperty("leverX", leverX);
            evidence.addProperty("leverY", leverY);
            evidence.addProperty("leverZ", leverZ);
            evidence.addProperty("leverPowered", triggeredLever.getValue(LeverBlock.POWERED));
            evidence.addProperty("neighborSignal", neighborSignal);
            evidence.addProperty("raining", raining);
            evidence.addProperty("aboveAir", aboveAir);
            evidence.addProperty("expectedSound", soundId(scene));
            evidence.addProperty("soundPath", "LeverBlock.pull toggled the real server block and synchronously invoked the RainDrumBlock neighbor update path");
            evidence.addProperty("limitation", "RainDrumBlock.animateTick can independently emit the same ambient random sound during rain; this evidence confirms native server trigger preconditions and redstone state, not the origin of any observed client callback");
        } catch (Throwable failure) {
            evidence.addProperty("status", "failed");
            evidence.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client audio trigger failed: scene={}", scene, failure);
        }
        try {
            ClientIntegrationData.write("audio-" + scene + "-triggered.json", evidence);
        } catch (Throwable failure) {
            ThermalShockReforged.LOGGER.error("Client audio trigger evidence publication failed: scene={}", scene, failure);
            return 0;
        }
        return "passed".equals(evidence.get("status").getAsString()) ? 1 : 0;
    }

    private static void preparePlatform(ServerLevel level, BlockPos drum, BlockPos lever) {
        for (int chunkX = (drum.getX() >> 4) - 1; chunkX <= (drum.getX() >> 4) + 1; chunkX++) {
            for (int chunkZ = (drum.getZ() >> 4) - 1; chunkZ <= (drum.getZ() >> 4) + 1; chunkZ++) {
                level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
            }
        }
        for (int x = drum.getX() - 2; x <= drum.getX() + 2; x++) {
            for (int z = drum.getZ() - 2; z <= drum.getZ() + 5; z++) {
                level.setBlock(new BlockPos(x, drum.getY() - 1, z), Blocks.STONE.defaultBlockState(), 3);
                for (int y = drum.getY(); y <= drum.getY() + 4; y++) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        for (int y = drum.getY() + 1; y < level.getMaxY(); y++) {
            level.setBlock(new BlockPos(drum.getX(), y, drum.getZ()), Blocks.AIR.defaultBlockState(), 3);
        }
        level.setBlock(new BlockPos(lever.getX(), drum.getY() - 1, lever.getZ()), Blocks.STONE.defaultBlockState(), 3);
    }

    private static Session currentSession() {
        JsonObject ready = ClientIntegrationData.read("server-ready.json");
        JsonObject clock = ClientIntegrationData.read("server-clock.json");
        String session = ready.get("session").getAsString();
        ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()), "TCP integration server is not ready");
        ClientIntegrationData.require(session.equals(clock.get("session").getAsString()), "Server clock evidence belongs to another session");
        return new Session(session, clock.get("serverTick").getAsLong());
    }

    private static String soundId(String scene) {
        return "thermal_shock_reforged:" + (scene.equals("angry_rain_drum") ? "angry_rain_drum_random" : "rain_drum_random");
    }

    private record Session(String id, long tick) {
    }
}


