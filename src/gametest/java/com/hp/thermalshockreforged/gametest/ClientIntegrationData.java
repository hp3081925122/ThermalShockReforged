package com.hp.thermalshockreforged.gametest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import com.mojang.serialization.JsonOps;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public final class ClientIntegrationData {
    public static final List<String> SCENARIOS = List.of("login_sync", "unlock_sync", "centrifuge", "fabricator", "desk_photo", "laut_quest", "kofoni_navigation");
    private static List<String> catalogueScenarios;

    private ClientIntegrationData() {
    }

    public static boolean enabled() {
        return Boolean.getBoolean("tsr.clientIntegration");
    }

    public static List<String> scenarios() {
        if (Boolean.getBoolean("tsr.clientIntegrationFeatures") && !System.getProperty("tsr.clientIntegrationFeatureFilter", "").isBlank()) return List.of("login_sync");
        if (!Boolean.getBoolean("tsr.clientIntegrationCatalogue")) return SCENARIOS;
        if (catalogueScenarios == null) {
            catalogueScenarios = ClientMenuCatalogue.scenarios();
            require(!catalogueScenarios.isEmpty(), "Registered menu catalogue must not be empty");
        }
        return catalogueScenarios;
    }

    public static Path directory() {
        Path root = Path.of("D:/AI临时文件").toAbsolutePath().normalize();
        Path directory = Path.of(System.getProperty("tsr.clientIntegrationDir", "D:/AI临时文件/tsr2612-client-integration/round35")).toAbsolutePath().normalize();
        if (!directory.startsWith(root) || directory.equals(root)) throw new IllegalArgumentException("Client integration output must be inside the temporary workspace");
        return directory;
    }

    public static JsonObject read(String name) {
        try {
            return JsonParser.parseString(Files.readString(directory().resolve(name), StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception failure) {
            throw new IllegalStateException("Cannot read client integration evidence: " + name, failure);
        }
    }

    public static void write(String name, JsonObject value) {
        try {
            Files.createDirectories(directory());
            Path target = directory().resolve(name);
            Path temporary = directory().resolve(name + ".tmp");
            Files.writeString(temporary, new GsonBuilder().setPrettyPrinting().create().toJson(value), StandardCharsets.UTF_8);
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception failure) {
            throw new IllegalStateException("Cannot write client integration evidence: " + name, failure);
        }
    }

    public static JsonObject recipes(Level level) {
        var counts = new TreeMap<String, Integer>();
        var entries = new TreeMap<String, String>();
        var ops = level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        for (var holder : RecipeContent.recipes(level).values()) {
            String type = BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType()).toString();
            if (!type.startsWith(ThermalShockReforged.MOD_ID + ":")) continue;
            counts.merge(type, 1, Integer::sum);
            entries.put(holder.id().identifier().toString(), Recipe.CODEC.encodeStart(ops, holder.value()).getOrThrow().toString());
        }
        var result = new JsonObject();
        var types = new JsonObject();
        counts.forEach(types::addProperty);
        result.add("types", types);
        result.addProperty("total", entries.size());
        try {
            String body = entries.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(java.util.stream.Collectors.joining("\n"));
            result.addProperty("sha256", HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception failure) {
            throw new IllegalStateException("Cannot hash synchronized recipes", failure);
        }
        return result;
    }

    public static JsonObject inventory(Player player) {
        Map<String, Integer> counts = new TreeMap<>();
        for (var stack : player.getInventory().getNonEquipmentItems()) {
            if (!stack.isEmpty()) counts.merge(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), stack.getCount(), Integer::sum);
        }
        var result = new JsonObject();
        counts.forEach(result::addProperty);
        return result;
    }

    public static String menu(Player player) {
        return player.containerMenu == player.inventoryMenu ? "none" : BuiltInRegistries.MENU.getKey(player.containerMenu.getType()).toString();
    }

    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
