package com.hp.thermalshockreforged.content.creative;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreativeTabsContent {
    private static final String MANIFEST = "/assets/" + ThermalShockReforged.MOD_ID + "/creative_tabs.json";
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThermalShockReforged.MOD_ID);
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();
    private static final List<TabDefinition> DEFINITIONS = load();

    static {
        for (TabDefinition definition : DEFINITIONS) {
            TABS.register(definition.id().getPath(), () -> CreativeModeTab.builder()
                    .title(Component.translatable(definition.title()))
                    .icon(() -> icon(definition))
                    .withTabsBefore(definition.tabsBefore().toArray(ResourceLocation[]::new))
                    .displayItems((parameters, output) -> display(definition, parameters, output))
                    .build());
        }
    }

    private CreativeTabsContent() { }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

    private static ItemStack icon(TabDefinition definition) {
        var item = BuiltInRegistries.ITEM.getOptional(definition.icon());
        if (item.isPresent() && item.get() != Items.AIR) return new ItemStack(item.get());
        if (REPORTED.add("icon:" + definition.id())) {
            ThermalShockReforged.LOGGER.warn("Creative tab {} is awaiting migrated icon {}; showing barrier", definition.id(), definition.icon());
        }
        return new ItemStack(Items.BARRIER);
    }

    private static void display(TabDefinition definition, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        int missing = 0;
        for (ResourceLocation id : definition.items()) {
            var item = BuiltInRegistries.ITEM.getOptional(id);
            if (item.isEmpty() || item.get() == Items.AIR) {
                missing++;
            } else if (item.get().isEnabled(parameters.enabledFeatures())) {
                output.accept(item.get());
            }
        }
        if (missing > 0 && REPORTED.add("items:" + definition.id())) {
            ThermalShockReforged.LOGGER.warn("Creative tab {} has {} items awaiting migration; manifest order retained", definition.id(), missing);
        }
    }

    private static List<TabDefinition> load() {
        var stream = CreativeTabsContent.class.getResourceAsStream(MANIFEST);
        if (stream == null) throw new IllegalStateException("Missing creative tab manifest: " + MANIFEST);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject root = GsonHelper.parse(reader);
            List<TabDefinition> definitions = new ArrayList<>();
            Set<ResourceLocation> tabIds = new HashSet<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(root, "tabs")) {
                JsonObject tab = element.getAsJsonObject();
                ResourceLocation id = ResourceLocation.parse(GsonHelper.getAsString(tab, "id"));
                if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID) || !tabIds.add(id)) {
                    throw new IllegalStateException("Invalid or duplicate creative tab: " + id);
                }
                List<ResourceLocation> before = locations(tab, "tabs_before");
                List<ResourceLocation> items = locations(tab, "items");
                if (new HashSet<>(items).size() != items.size()) throw new IllegalStateException("Duplicate creative items: " + id);
                definitions.add(new TabDefinition(id, GsonHelper.getAsString(tab, "title"),
                        ResourceLocation.parse(GsonHelper.getAsString(tab, "icon")), before, items));
            }
            if (definitions.isEmpty()) throw new IllegalStateException("Empty creative tab manifest");
            for (TabDefinition definition : definitions) {
                for (ResourceLocation before : definition.tabsBefore()) {
                    if (before.equals(definition.id()) || !tabIds.contains(before)) {
                        throw new IllegalStateException("Invalid creative tab ordering: " + before);
                    }
                }
            }
            return List.copyOf(definitions);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Failed to load creative tab manifest: " + MANIFEST, exception);
        }
    }

    private static List<ResourceLocation> locations(JsonObject object, String key) {
        List<ResourceLocation> locations = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(object, key)) locations.add(ResourceLocation.parse(element.getAsString()));
        return List.copyOf(locations);
    }

    private record TabDefinition(ResourceLocation id, String title, ResourceLocation icon,
            List<ResourceLocation> tabsBefore, List<ResourceLocation> items) { }
}
