package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.compat.jei.MachineCategory;
import com.hp.thermalshockreforged.compat.jei.MachineEntry;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class ClientJeiProbe implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "client_integration_probe");
    private static volatile IJeiRuntime runtime;
    private static volatile List<IRecipeType<?>> recipeTypes;

    @Override
    public Identifier getPluginUid() { return UID; }

    @Override
    public void onRuntimeAvailable(IJeiRuntime availableRuntime) {
        if (!enabled()) return;
        Minecraft.getInstance().execute(() -> {
            try {
                List<IRecipeType<?>> found = availableRuntime.getRecipeManager().createRecipeCategoryLookup().includeHidden().get()
                        .<IRecipeType<?>>map(category -> category.getRecipeType())
                        .filter(type -> type.getUid().getNamespace().equals(ThermalShockReforged.MOD_ID))
                        .distinct()
                        .sorted((left, right) -> left.getUid().toString().compareTo(right.getUid().toString()))
                        .toList();
                runtime = availableRuntime;
                recipeTypes = found;
                writeState("ready");
                ThermalShockReforged.LOGGER.info("Client JEI probe runtime is ready with {} registered TSR recipe types", found.size());
            } catch (Throwable failure) {
                runtime = availableRuntime;
                recipeTypes = List.of();
                writeFailure(failure);
                ThermalShockReforged.LOGGER.error("Client JEI probe could not inspect recipe categories", failure);
            }
        });
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
        recipeTypes = null;
    }

    public static boolean ready() { return enabled() && runtime != null && recipeTypes != null; }

    public static List<String> types() {
        if (!ready()) return List.of();
        return recipeTypes.stream().map(type -> type.getUid().toString()).toList();
    }

    public static JsonArray counts() {
        JsonArray result = new JsonArray();
        if (!ready()) return result;
        for (IRecipeType<?> type : recipeTypes) {
            JsonObject entry = new JsonObject();
            entry.addProperty("type", type.getUid().toString());
            entry.addProperty("visible", count(runtime, type, false));
            entry.addProperty("includeHidden", count(runtime, type, true));
            result.add(entry);
        }
        return result;
    }

    public static List<MachineEntry> fabricatorEntries() {
        if (!ready()) return List.of();
        return runtime.getRecipeManager().createRecipeLookup(MachineCategory.FABRICATOR).includeHidden().get().toList();
    }

    public static String currentType() {
        var screen = Minecraft.getInstance().screen;
        if (!(screen instanceof RecipesGui recipesGui)) return null;
        try {
            var logicField = RecipesGui.class.getDeclaredField("logic");
            logicField.setAccessible(true);
            var category = ((IRecipeGuiLogic) logicField.get(recipesGui)).getSelectedRecipeCategory();
            return category == null ? null : category.getRecipeType().getUid().toString();
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot read the selected JEI recipe type from the current JEI GUI", failure);
        }
    }

    public static boolean showType(int index) {
        if (!ready() || Minecraft.getInstance().getConnection() == null || index < 0 || index >= recipeTypes.size()) return false;
        IRecipeType<?> type = recipeTypes.get(index);
        runtime.getRecipesGui().showTypes(List.of(type));
        writeState("show_type");
        return true;
    }

    public static JsonObject result() {
        JsonObject result = new JsonObject();
        result.addProperty("status", ready() ? "ready" : "pending");
        result.addProperty("actualTypeCount", ready() ? recipeTypes.size() : 0);
        JsonArray ids = new JsonArray();
        for (String id : types()) ids.add(id);
        result.add("typeIds", ids);
        result.add("counts", counts());
        result.addProperty("currentType", currentType());
        var screen = Minecraft.getInstance().screen;
        result.addProperty("currentScreen", screen == null ? "none" : screen.getClass().getName());
        return result;
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationJeiProbe");
    }

    private static int count(IJeiRuntime currentRuntime, IRecipeType<?> type, boolean includeHidden) {
        return countCaptured(currentRuntime, type, includeHidden);
    }

    private static <T> int countCaptured(IJeiRuntime currentRuntime, IRecipeType<T> type, boolean includeHidden) {
        var lookup = currentRuntime.getRecipeManager().createRecipeLookup(type);
        if (includeHidden) lookup = lookup.includeHidden();
        return Math.toIntExact(lookup.get().count());
    }

    private static void writeState(String phase) {
        JsonObject output = result();
        output.addProperty("phase", phase);
        if (phase.equals("show_type")) output.addProperty("shownType", currentType());
        ClientIntegrationData.write("jei-probe.json", output);
    }

    private static void writeFailure(Throwable failure) {
        JsonObject output = new JsonObject();
        output.addProperty("status", "failed");
        output.addProperty("error", failure.toString());
        ClientIntegrationData.write("jei-probe.json", output);
    }
}
