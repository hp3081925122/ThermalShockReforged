package com.hp.thermalshockreforged.gametest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.gametest.framework.RetryOptions;
import net.minecraft.gametest.framework.StructureGridSpawner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class DedicatedGameTestRegression {
    private static MinecraftServer server;
    private static MultipleTestTracker tracker;
    private static List<GameTestInfo> tests;
    private static GameTestRunner runner;
    private static int elapsed;

    private DedicatedGameTestRegression() { }

    @SubscribeEvent
    public static void start(ServerStartedEvent event) {
        if (!System.getProperty("tsr.dedicatedRegression", "").equals("tests")) return;
        server = event.getServer();
        try {
            var level = server.overworld();
            String filter = System.getProperty("tsr.dedicatedRegressionFilter", "").trim();
            if (filter.isEmpty()) filter = "history_titan_server_behavior_tests_finding_caster_in_another_dimension_cancels_the_old_world_hit,history_dimension_player_identity_tests_actual_maiden_dimension_change_cancels_old_level_volley";
            List<String> filters = Arrays.stream(filter.split(",")).map(String::trim).filter(value -> !value.isEmpty()).toList();
            tests = level.registryAccess().lookupOrThrow(Registries.TEST_INSTANCE).listElements()
                    .filter(test -> test.key().identifier().getNamespace().equals(ThermalShockReforged.MOD_ID))
                    .filter(test -> !filters.isEmpty() || test.value().manualOnly())
                    .filter(test -> filters.isEmpty() || filters.stream().anyMatch(value -> {
                        String id = test.key().identifier().toString();
                        String path = test.key().identifier().getPath();
                        return value.endsWith("*") ? path.startsWith(value.substring(0, value.length() - 1)) : value.equals(id) || value.equals(path);
                    }))
                    .map(test -> new GameTestInfo(test, Rotation.NONE, level, RetryOptions.noRetries())).toList();
            if (tests.isEmpty()) throw new IllegalStateException("No dedicated regression tests were registered");
            runner = GameTestRunner.Builder.fromInfo(tests, level)
                    .newStructureSpawner(new StructureGridSpawner(new BlockPos(512, 80, 512), 4, false)).build();
            tracker = new MultipleTestTracker(runner.getTestInfos());
            runner.start();
            ThermalShockReforged.LOGGER.info("Dedicated GameTest regression started: tests={}", tests.size());
        } catch (Throwable failure) {
            finish(failure);
        }
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        if (tracker == null || event.getServer() != server) return;
        elapsed++;
        if (tracker.isDone()) finish(null);
        else if (elapsed >= 1800) finish(new IllegalStateException("Dedicated regression exceeded 1800 server ticks"));
    }

    private static void finish(Throwable failure) {
        var result = new JsonObject();
        Path resultFile = resultPath();
        boolean passed = failure == null && tracker != null && tracker.isDone() && !tracker.hasFailedRequired();
        result.addProperty("status", passed ? "passed" : "failed");
        result.addProperty("serverTicks", elapsed);
        result.addProperty("filter", System.getProperty("tsr.dedicatedRegressionFilter", ""));
        result.addProperty("total", tracker == null ? 0 : tracker.getTotalCount());
        result.addProperty("requiredFailures", tracker == null ? 0 : tracker.getFailedRequiredCount());
        if (failure != null) result.addProperty("failure", failure.toString());
        var rows = new JsonArray();
        if (tests != null) for (var test : tests) {
            var row = new JsonObject();
            row.addProperty("id", test.id().toString());
            row.addProperty("passed", test.hasSucceeded());
            if (test.getError() != null) row.addProperty("error", test.getError().toString());
            rows.add(row);
        }
        result.add("tests", rows);
        try {
            Files.createDirectories(resultFile.getParent());
            Files.writeString(resultFile, new GsonBuilder().setPrettyPrinting().create().toJson(result), StandardCharsets.UTF_8);
        } catch (Exception writeFailure) {
            ThermalShockReforged.LOGGER.error("Failed to write dedicated GameTest results", writeFailure);
        }
        if (runner != null) runner.stop();
        tracker = null;
        ThermalShockReforged.LOGGER.info("Dedicated GameTest regression completed: passed={}, result={}", passed, resultFile);
        server.halt(false);
    }

    private static Path resultPath() {
        String directory = System.getProperty("tsr.dedicatedGameTestResultDir", "").trim();
        return directory.isEmpty() ? Path.of("D:/AI临时文件/tsr-dedicated-tests-result.json") : Path.of(directory).resolve("tsr-dedicated-tests-result.json");
    }
}
