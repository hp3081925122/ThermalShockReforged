package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.compat.jei.NpcTradeCategory;
import com.hp.thermalshockreforged.compat.jei.NpcTradeEntry;
import com.hp.thermalshockreforged.compat.jei.NpcTradeListWidget;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import mezz.jei.api.runtime.IJeiKeyMapping;

@JeiPlugin
@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientJeiScrollProbe implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "client_integration_jei_scroll_probe");
    private static final String TYPE_ID = ThermalShockReforged.MOD_ID + ":npc_trade";
    private static final int VISIBLE_ROWS = NpcTradeListWidget.VISIBLE_ROWS;
    private static volatile IJeiRuntime runtime;
    private static volatile boolean runtimeArrived;
    private static boolean running;
    private static boolean finished;
    private static int state;
    private static int entryIndex;
    private static int expectedFirstRow;
    private static int maxScroll;
    private static long deadlineNanos;
    private static long nextFrameSequence;
    private static long observedFrameSequence;
    private static String session;
    private static String originalFilter;
    private static List<NpcTradeEntry> allEntries = List.of();
    private static List<NpcTradeEntry> entries = List.of();
    private static JsonArray focusChecks = new JsonArray();
    private static JsonArray searchChecks = new JsonArray();
    private static String stage;
    private static String captureName;
    private static String captureAfterAction;
    private static boolean captureRequested;
    private static boolean capturePending;
    private static boolean captureDone;
    private static boolean entryOpenRequested;
    private static boolean actionPending;
    private static boolean actionConsumed;
    private static int actionExpectedFirstRow;
    private static String actionKind;
    private static JsonObject current;
    private static JsonArray results = new JsonArray();
    private static JsonArray actions;
    private static Throwable failure;

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime availableRuntime) {
        if (!enabled()) return;
        runtime = availableRuntime;
        runtimeArrived = true;
        ThermalShockReforged.LOGGER.info("Client JEI scroll probe captured the actual JEI runtime");
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
        runtimeArrived = false;
    }

    public static boolean ready() {
        return enabled() && runtimeArrived && runtime != null;
    }

    public static boolean finished() {
        return finished;
    }

    public static JsonObject result() {
        JsonObject output = new JsonObject();
        output.addProperty("status", finished ? failure == null ? "passed" : "failed" : running ? "running" : ready() ? "ready" : "pending");
        output.addProperty("session", session);
        output.addProperty("runtimeCaptured", ready());
        output.addProperty("entryCount", entries.size());
        output.addProperty("allRegisteredEntries", allEntries.size());
        output.addProperty("completedEntries", results.size());
        output.addProperty("currentStage", stage);
        output.add("entries", results.deepCopy());
        if (failure != null) output.addProperty("error", failure.toString());
        return output;
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationJeiScroll");
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!enabled() || finished) return;
        Minecraft mc = Minecraft.getInstance();
        try {
            if (failure != null) throw new IllegalStateException("JEI scroll probe failed", failure);
            if (running && System.nanoTime() > deadlineNanos) throw new IllegalStateException("JEI scroll probe timed out: state=" + state + ", stage=" + stage + ", entry=" + entryIndex);
            if (!running) {
                if (!ready()) return;
                Path baselinePath = ClientIntegrationData.directory().resolve("client-results.json");
                if (!Files.exists(baselinePath)) return;
                JsonObject baseline = ClientIntegrationData.read("client-results.json");
                JsonObject ready = ClientIntegrationData.read("server-ready.json");
                if (!"passed".equals(baseline.get("status").getAsString()) || baseline.get("completedScenarios").getAsInt() != 7 || !"ready".equals(ready.get("status").getAsString())) return;
                ClientIntegrationData.require(mc.player != null && mc.level != null && mc.getConnection() != null && !mc.getConnection().getConnection().isMemoryConnection(), "JEI scroll probe requires a real TCP session");
                ClientIntegrationData.require(mc.screen == null, "JEI scroll probe requires the original unobstructed world screen");
                session = ready.get("session").getAsString();
                ClientIntegrationData.require(session.equals(baseline.get("session").getAsString()), "JEI baseline and ready evidence belong to different sessions");
                originalFilter = runtime.getIngredientFilter().getFilterText();
                initializeRuntimeEvidence();
                running = true;
                state = 1;
                deadlineNanos = System.nanoTime() + 1_800_000_000_000L;
                writeResult("running", null);
                ThermalShockReforged.LOGGER.info("Client JEI scroll probe started: session={}, entries={}, TCP=true", session, entries.size());
            }
            if (captureDone) finishCapture(mc);
            if (actionPending || capturePending || captureRequested) return;
            if (state == 1) {
                if (entryIndex >= entries.size()) {
                    runtime.getIngredientFilter().setFilterText(originalFilter);
                    var output = result();
                    output.addProperty("filterRestored", runtime.getIngredientFilter().getFilterText().equals(originalFilter));
                    ClientIntegrationData.require(output.get("filterRestored").getAsBoolean(), "JEI ingredient filter did not restore its original text");
                    finished = true;
                    running = false;
                    writeIndexAudit("passed", null);
                    writeResult("passed", null);
                    if (mc.screen instanceof RecipesGui gui) gui.onClose();
                    ThermalShockReforged.LOGGER.info("Client JEI scroll probe passed: entries={}, captures={}, focusQueries={}, searchQueries={}", results.size(), countCaptures(), focusQueryCount(), searchQueryCount());
                    return;
                }
                NpcTradeEntry entry = entries.get(entryIndex);
                if (!entryOpenRequested) {
                    runtime.getRecipesGui().showRecipes(runtime.getRecipeManager().getRecipeCategory(NpcTradeCategory.TYPE), List.of(entry), List.of());
                    entryOpenRequested = true;
                    deadlineNanos = System.nanoTime() + 120_000_000_000L;
                    return;
                }
                if (!(mc.screen instanceof RecipesGui)) return;
                IRecipeLayoutDrawable<?> drawable = currentLayout(entry);
                if (drawable == null) return;
                validateInitialLayout(entry, drawable);
                expectedFirstRow = 0;
                maxScroll = Math.max(0, entry.rows().size() - VISIBLE_ROWS);
                current = entryMetadata(entry);
                actions = new JsonArray();
                current.add("actions", actions);
                current.add("captures", new JsonArray());
                stage = "top";
                state = 2;
                requestCapture("entry_" + entryIndex + "_top");
                return;
            }
            if (state == 2) driveStage(mc);
        } catch (Throwable problem) {
            fail(mc, problem);
        }
    }

    @SubscribeEvent
    public static void frame(RenderFrameEvent.Post event) {
        if (!enabled() || !running || finished || state != 2 || capturePending) return;
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof RecipesGui)) return;
        try {
            NpcTradeEntry entry = entries.get(entryIndex);
            IRecipeLayoutDrawable<?> drawable = currentLayout(entry);
            if (drawable == null) return;
            JsonObject observation = observeLayout(entry, drawable);
            long frame = ++nextFrameSequence;
            observation.addProperty("renderFrameSequence", frame);
            observation.addProperty("jeiRecipeType", TYPE_ID);
            observation.addProperty("actualRecipesGui", true);
            if (actionPending) {
                ClientIntegrationData.require(actionConsumed, "JEI GUI did not consume the requested production widget input: " + actionKind);
                int firstRow = observation.get("visibleFirstRow").getAsInt();
                ClientIntegrationData.require(firstRow == actionExpectedFirstRow, "Production NPC trade slot visibility differs after " + actionKind + ": expected=" + actionExpectedFirstRow + ", observed=" + firstRow + ", layout=" + observation);
                JsonObject action = new JsonObject();
                action.addProperty("kind", actionKind);
                action.addProperty("consumedByCurrentJeiGuiOrLayoutHandler", actionConsumed);
                action.addProperty("expectedFirstRow", actionExpectedFirstRow);
                action.addProperty("observedFirstRow", firstRow);
                action.addProperty("renderFrameSequence", frame);
                action.add("slots", observation.getAsJsonArray("slots").deepCopy());
                actions.add(action);
                actionPending = false;
                observedFrameSequence = frame;
                if (captureAfterAction != null) {
                    String nextCapture = captureAfterAction;
                    captureAfterAction = null;
                    captureNow(mc, nextCapture, observation);
                }
                return;
            }
            if (captureRequested) {
                ClientIntegrationData.require(observation.get("visibleFirstRow").getAsInt() == expectedFirstRow, "Visible NPC trade row changed before the requested screenshot");
                String name = captureName;
                captureRequested = false;
                captureNow(mc, name, observation);
            }
        } catch (Throwable problem) {
            failure = problem;
        }
    }

    private static void initializeRuntimeEvidence() {
        allEntries = runtime.getRecipeManager().createRecipeLookup(NpcTradeCategory.TYPE).includeHidden().get().toList();
        ClientIntegrationData.require(allEntries.size() == 6, "Expected six production NPC trade entries from the existing JEI runtime audit, found " + allEntries.size());
        entries = allEntries.stream().filter(entry -> entry.rows().size() > VISIBLE_ROWS).toList();
        ClientIntegrationData.require(!entries.isEmpty(), "No production NPC trade entry has more than four rows; the scroll widget path cannot be exercised");
        results = new JsonArray();
        focusChecks = new JsonArray();
        searchChecks = new JsonArray();
        for (NpcTradeEntry entry : allEntries) {
            Set<String> seenForEntry = new HashSet<>();
            for (NpcTradeEntry.Row row : entry.rows()) {
                for (ItemStack cost : row.costs()) checkFocus(entry, RecipeIngredientRole.INPUT, cost, seenForEntry, focusChecks);
                checkFocus(entry, RecipeIngredientRole.OUTPUT, row.result(), seenForEntry, focusChecks);
            }
            addSearchChecks(entry, searchChecks);
        }
        addModIdSearchCheck(searchChecks);
        runtime.getIngredientFilter().setFilterText(originalFilter);
        ClientIntegrationData.require(runtime.getIngredientFilter().getFilterText().equals(originalFilter), "JEI index audit failed to restore the original filter before GUI flow");
        writeIndexAudit("passed", null);
    }

    private static void checkFocus(NpcTradeEntry target, RecipeIngredientRole role, ItemStack stack, Set<String> seen, JsonArray checks) {
        if (stack.isEmpty()) throw new IllegalStateException("NPC trade recipe has an empty ingredient stack: " + target.npc());
        String key = role + "|" + stackKey(stack);
        if (!seen.add(key)) return;
        IFocus<ItemStack> focus = runtime.getJeiHelpers().getFocusFactory().createFocus(role, VanillaTypes.ITEM_STACK, stack.copy());
        boolean containsEntry = runtime.getRecipeManager().createRecipeLookup(NpcTradeCategory.TYPE).limitFocus(List.of(focus)).get().anyMatch(candidate -> sameEntry(candidate, target));
        JsonObject check = new JsonObject();
        check.addProperty("npc", target.npc());
        check.addProperty("role", role.toString());
        check.addProperty("stack", stackKey(stack));
        check.addProperty("containsCurrentEntry", containsEntry);
        checks.add(check);
        ClientIntegrationData.require(containsEntry, "Actual JEI focus lookup omitted an NPC trade entry: npc=" + target.npc() + ", role=" + role + ", stack=" + stackKey(stack));
    }

    private static void addSearchChecks(NpcTradeEntry entry, JsonArray checks) {
        ItemStack input = entry.rows().stream().flatMap(row -> row.costs().stream()).filter(stack -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
        ItemStack output = entry.rows().stream().map(NpcTradeEntry.Row::result).filter(stack -> !stack.isEmpty()).findFirst().orElseThrow();
        if (!input.isEmpty()) {
            searchOne(entry, input, "localized_name", input.getHoverName().getString(), checks);
        } else {
            JsonObject skipped = new JsonObject();
            skipped.addProperty("npc", entry.npc());
            skipped.addProperty("searchKind", "localized_input_name");
            skipped.addProperty("skipped", true);
            skipped.addProperty("reason", "This production entry has no item costs; experience levels are not an item ingredient");
            skipped.addProperty("experienceLevelRows", entry.rows().stream().filter(row -> row.levels() > 0).count());
            checks.add(skipped);
            ThermalShockReforged.LOGGER.debug("JEI input-name check is not applicable to an entry without item costs: npc={}, levelRows={}", entry.npc(), entry.rows().stream().filter(row -> row.levels() > 0).count());
        }
        searchOne(entry, output, "localized_name", output.getHoverName().getString(), checks);
        boolean hasOwnedStack = entry.rows().stream()
                .flatMap(row -> java.util.stream.Stream.concat(row.costs().stream(), java.util.stream.Stream.of(row.result())))
                .anyMatch(stack -> !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().startsWith(ThermalShockReforged.MOD_ID + ":"));
        if (!hasOwnedStack) {
            JsonObject skipped = new JsonObject();
            skipped.addProperty("npc", entry.npc());
            skipped.addProperty("searchKind", "mod_id");
            skipped.addProperty("skipped", true);
            skipped.addProperty("reason", "This production entry contains no TSR-owned input or output stack");
            checks.add(skipped);
        }
    }

    private static void addModIdSearchCheck(JsonArray checks) {
        NpcTradeEntry owner = allEntries.stream()
                .filter(entry -> entry.rows().stream()
                        .flatMap(row -> java.util.stream.Stream.concat(row.costs().stream(), java.util.stream.Stream.of(row.result())))
                        .anyMatch(stack -> !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().startsWith(ThermalShockReforged.MOD_ID + ":")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No TSR-owned stack exists in the production NPC trade entries"));
        ItemStack target = owner.rows().stream()
                .flatMap(row -> java.util.stream.Stream.concat(row.costs().stream(), java.util.stream.Stream.of(row.result())))
                .filter(stack -> !stack.isEmpty())
                .filter(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().startsWith(ThermalShockReforged.MOD_ID + ":"))
                .findFirst()
                .orElseThrow();
        searchOne(owner, target, "mod_id", "@" + ThermalShockReforged.MOD_ID, checks);
    }

    private static void searchOne(NpcTradeEntry entry, ItemStack target, String searchKind, String query, JsonArray checks) {
        runtime.getIngredientFilter().setFilterText(query);
        boolean found = runtime.getIngredientFilter().getFilteredItemStacks().stream().anyMatch(stack -> ItemStack.isSameItemSameComponents(stack, target));
        JsonObject check = new JsonObject();
        check.addProperty("npc", entry.npc());
        check.addProperty("searchKind", searchKind);
        check.addProperty("query", query);
        check.addProperty("target", stackKey(target));
        check.addProperty("actualIngredientFilterContainsTarget", found);
        checks.add(check);
        ClientIntegrationData.require(found, "Actual JEI ingredient filter omitted the representative NPC trade stack: npc=" + entry.npc() + ", query=" + query + ", target=" + stackKey(target));
    }

    private static void validateInitialLayout(NpcTradeEntry entry, IRecipeLayoutDrawable<?> drawable) {
        ClientIntegrationData.require(TYPE_ID.equals(drawable.getRecipeCategory().getRecipeType().getUid().toString()), "The opened layout is not the production npc_trade category");
        ClientIntegrationData.require(sameEntry(entry, (NpcTradeEntry) drawable.getRecipe()), "The opened JEI layout does not contain the requested production NPC trade entry");
        ClientIntegrationData.require(drawable.getInputHandler() != null, "The actual JEI layout has no production input handler");
    }

    private static IRecipeLayoutDrawable<?> currentLayout(NpcTradeEntry entry) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof RecipesGui gui)) return null;
        try {
            Field layoutsField = RecipesGui.class.getDeclaredField("layouts");
            layoutsField.setAccessible(true);
            RecipeGuiLayouts layouts = (RecipeGuiLayouts) layoutsField.get(gui);
            Field listField = RecipeGuiLayouts.class.getDeclaredField("recipeLayoutsWithButtons");
            listField.setAccessible(true);
            List<?> shown = (List<?>) listField.get(layouts);
            for (Object value : shown) {
                IRecipeLayoutDrawable<?> drawable = ((IRecipeLayoutWithButtons<?>) value).getRecipeLayout();
                if (TYPE_ID.equals(drawable.getRecipeCategory().getRecipeType().getUid().toString()) && sameEntry(entry, (NpcTradeEntry) drawable.getRecipe())) return drawable;
            }
            return null;
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot inspect the current real JEI RecipesGui layout", failure);
        }
    }

    private static JsonObject observeLayout(NpcTradeEntry entry, IRecipeLayoutDrawable<?> drawable) {
        var result = new JsonObject();
        result.addProperty("screen", Minecraft.getInstance().screen.getClass().getName());
        result.addProperty("recipeType", drawable.getRecipeCategory().getRecipeType().getUid().toString());
        result.addProperty("npc", entry.npc());
        result.addProperty("entryRows", entry.rows().size());
        var rect = drawable.getRect();
        var recipeRect = new JsonObject();
        recipeRect.addProperty("x", rect.getX());
        recipeRect.addProperty("y", rect.getY());
        recipeRect.addProperty("width", rect.getWidth());
        recipeRect.addProperty("height", rect.getHeight());
        result.add("recipeRect", recipeRect);
        List<mezz.jei.api.gui.ingredient.IRecipeSlotView> views = drawable.getRecipeSlotsView().getSlotViews();
        var slots = new JsonArray();
        int visibleFirst = -1;
        int visibleLast = -1;
        int index = 0;
        for (int rowIndex = 0; rowIndex < entry.rows().size(); rowIndex++) {
            NpcTradeEntry.Row row = entry.rows().get(rowIndex);
            int rowSlotCount = row.costs().size() + 1;
            boolean resultVisible = false;
            for (int slotInRow = 0; slotInRow < rowSlotCount; slotInRow++) {
                ClientIntegrationData.require(index < views.size(), "Actual JEI layout has fewer recipe slots than the production entry");
                var view = views.get(index);
                ItemStack stack = view.getDisplayedItemStack().orElse(ItemStack.EMPTY);
                ClientIntegrationData.require(!stack.isEmpty(), "Actual JEI recipe layout contains an empty NPC trade slot view: npc=" + entry.npc() + ", row=" + rowIndex + ", slot=" + slotInRow);
                ClientIntegrationData.require(IRecipeSlotDrawable.class.isInstance(view), "Actual JEI slot view is not drawable");
                var area = ((IRecipeSlotDrawable) view).getAreaIncludingBackground();
                boolean visible = area.getX() > -5000 && area.getY() > -5000;
                if (slotInRow == rowSlotCount - 1) resultVisible = visible;
                JsonObject slot = new JsonObject();
                slot.addProperty("row", rowIndex);
                slot.addProperty("slotInRow", slotInRow);
                slot.addProperty("role", view.getRole().toString());
                slot.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                slot.addProperty("stack", stackKey(stack));
                slot.addProperty("x", area.getX());
                slot.addProperty("y", area.getY());
                slot.addProperty("width", area.getWidth());
                slot.addProperty("height", area.getHeight());
                slot.addProperty("visible", visible);
                slots.add(slot);
                index++;
            }
            if (resultVisible) {
                if (visibleFirst < 0) visibleFirst = rowIndex;
                visibleLast = rowIndex;
            }
        }
        ClientIntegrationData.require(index == views.size(), "Actual JEI layout slot count differs from production row costs/results: observed=" + views.size() + ", expected=" + index);
        result.add("slots", slots);
        result.addProperty("visibleFirstRow", visibleFirst);
        result.addProperty("visibleLastRow", visibleLast);
        int expectedLast = Math.min(entry.rows().size() - 1, visibleFirst + VISIBLE_ROWS - 1);
        ClientIntegrationData.require(visibleFirst >= 0 && visibleLast == expectedLast && visibleLast - visibleFirst + 1 == VISIBLE_ROWS, "Actual NPC trade layout did not expose exactly four contiguous result rows: " + result);
        return result;
    }

    private static void driveStage(Minecraft mc) {
        switch (stage) {
            case "top" -> issueScroll(mc, 1.0D, "boundary_top", "boundary_top");
            case "boundary_top" -> stage = "scroll_down";
            case "scroll_down" -> {
                if (expectedFirstRow < maxScroll) issueScroll(mc, -1.0D, "wheel_down", expectedFirstRow + 1 == maxScroll ? "bottom" : null);
            }
            case "bottom" -> issueScroll(mc, -1.0D, "boundary_bottom", "boundary_bottom");
            case "boundary_bottom" -> stage = "scroll_up";
            case "scroll_up" -> {
                if (expectedFirstRow > 0) issueScroll(mc, 1.0D, "wheel_up", expectedFirstRow - 1 == 0 ? "wheel_restored_top" : null);
            }
            case "wheel_restored_top" -> performDrag(mc, true);
            case "drag_bottom" -> performDrag(mc, false);
            case "drag_top" -> completeEntry(mc);
            default -> throw new IllegalStateException("Unknown JEI NPC scroll stage: " + stage);
        }
    }

    private static void issueScroll(Minecraft mc, double scrollY, String kind, String screenshot) {
        NpcTradeEntry entry = entries.get(entryIndex);
        IRecipeLayoutDrawable<?> drawable = currentLayout(entry);
        ClientIntegrationData.require(drawable != null && mc.screen instanceof RecipesGui, "The current real JEI recipe layout disappeared before wheel input");
        IJeiInputHandler widgetHandler = currentWidgetHandler(drawable);
        ScreenRectangle area = widgetHandler.getArea();
        double x = drawable.getRect().getX() + area.position().x() + area.width() / 2.0D;
        double y = drawable.getRect().getY() + area.position().y() + area.height() / 2.0D;
        boolean consumed = ((RecipesGui) mc.screen).mouseScrolled(x, y, 0.0D, scrollY);
        ClientIntegrationData.require(consumed, "The actual JEI RecipesGui did not consume a wheel action over the NPC trade widget: " + kind);
        actionKind = kind;
        actionConsumed = consumed;
        actionExpectedFirstRow = scrollY < 0 ? Math.min(maxScroll, expectedFirstRow + 1) : Math.max(0, expectedFirstRow - 1);
        expectedFirstRow = actionExpectedFirstRow;
        actionPending = true;
        captureAfterAction = screenshot == null ? null : "entry_" + entryIndex + "_" + screenshot;
        stage = screenshot == null ? (scrollY < 0 ? "scroll_down" : "scroll_up") : screenshot;
        deadlineNanos = System.nanoTime() + 120_000_000_000L;
    }

    private static void performDrag(Minecraft mc, boolean toBottom) {
        NpcTradeEntry entry = entries.get(entryIndex);
        IRecipeLayoutDrawable<?> drawable = currentLayout(entry);
        ClientIntegrationData.require(drawable != null, "The current production JEI layout disappeared before scrollbar drag");
        IJeiInputHandler handler = currentWidgetHandler(drawable);
        ScreenRectangle area = handler.getArea();
        double x = area.position().x() + area.width() - 3.0D;
        double top = area.position().y() + 1.0D;
        double bottom = area.position().y() + area.height() - 1.0D;
        double startY = toBottom ? top : bottom;
        double endY = toBottom ? bottom : top;
        IJeiUserInput input = new MouseInput(x, startY);
        boolean pressed = handler.handleInput(x, startY, input);
        ClientIntegrationData.require(pressed, "The actual production JEI scrollbar widget did not consume the drag press");
        var leftMouse = com.mojang.blaze3d.platform.InputConstants.Type.MOUSE.getOrCreate(com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT);
        boolean dragged = handler.handleMouseDragged(x, endY, leftMouse, 0.0D, endY - startY);
        ClientIntegrationData.require(dragged, "The actual production JEI scrollbar widget did not consume the drag movement");
        handler.handleInput(area.position().x() + 2.0D, area.position().y() + 2.0D, new MouseInput(area.position().x() + 2.0D, area.position().y() + 2.0D));
        actionKind = toBottom ? "scrollbar_drag_to_bottom" : "scrollbar_drag_to_top";
        actionConsumed = pressed && dragged;
        actionExpectedFirstRow = toBottom ? maxScroll : 0;
        expectedFirstRow = actionExpectedFirstRow;
        actionPending = true;
        captureAfterAction = "entry_" + entryIndex + (toBottom ? "_drag_bottom" : "_drag_top");
        stage = toBottom ? "drag_bottom" : "drag_top";
        deadlineNanos = System.nanoTime() + 120_000_000_000L;
    }

    private static IJeiInputHandler currentWidgetHandler(IRecipeLayoutDrawable<?> drawable) {
        try {
            Object layoutHandler = drawable.getInputHandler();
            Field field = layoutHandler.getClass().getDeclaredField("inputHandlers");
            field.setAccessible(true);
            for (Object handler : (List<?>) field.get(layoutHandler)) {
                if (handler instanceof IJeiInputHandler input && handler.getClass().getName().equals(NpcTradeListWidget.class.getName())) return input;
            }
            throw new IllegalStateException("Current actual JEI layout does not contain the production NpcTradeListWidget input handler");
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot inspect the production input handlers registered on the current JEI layout", failure);
        }
    }

    private static void requestCapture(String name) {
        captureName = name;
        captureRequested = true;
        captureDone = false;
        deadlineNanos = System.nanoTime() + 120_000_000_000L;
    }

    private static void captureNow(Minecraft mc, String name, JsonObject observation) {
        captureName = name;
        capturePending = true;
        captureDone = false;
        observedFrameSequence = observation.get("renderFrameSequence").getAsLong();
        Path path = ClientIntegrationData.directory().resolve("jei-scroll-" + name + ".png");
        JsonObject capture = new JsonObject();
        capture.addProperty("name", name);
        capture.addProperty("image", path.toString());
        capture.addProperty("frameSequence", observedFrameSequence);
        capture.addProperty("session", session);
        capture.add("layout", observation.deepCopy());
        Screenshot.takeScreenshot(mc.getMainRenderTarget(), image -> {
            try (image) {
                image.writeToFile(path);
                mc.execute(() -> {
                    current.getAsJsonArray("captures").add(capture);
                    capturePending = false;
                    captureDone = true;
                });
            } catch (Throwable problem) {
                mc.execute(() -> failure = problem);
            }
        });
    }

    private static void finishCapture(Minecraft mc) {
        captureDone = false;
        if (captureName.endsWith("_boundary_top")) stage = "boundary_top";
        else if (captureName.endsWith("_boundary_bottom")) stage = "boundary_bottom";
        else if (captureName.endsWith("_wheel_restored_top")) stage = "wheel_restored_top";
        else if (captureName.endsWith("_drag_bottom")) stage = "drag_bottom";
        else if (captureName.endsWith("_drag_top")) stage = "drag_top";
        else if (captureName.endsWith("_top")) stage = "top";
        else if (captureName.endsWith("_bottom")) stage = "bottom";
        else throw new IllegalStateException("Unknown JEI scroll screenshot stage: " + captureName);
        deadlineNanos = System.nanoTime() + 120_000_000_000L;
    }

    private static void completeEntry(Minecraft mc) {
        current.addProperty("status", "passed");
        current.addProperty("npc", entries.get(entryIndex).npc());
        current.addProperty("rows", entries.get(entryIndex).rows().size());
        current.addProperty("maximumFirstRow", maxScroll);
        current.addProperty("lastObservedFrameSequence", observedFrameSequence);
        current.addProperty("productionScrollWidget", "NpcTradeListWidget from the actual open JEI recipe layout");
        results.add(current.deepCopy());
        ClientIntegrationData.write("jei-scroll-" + entries.get(entryIndex).npc() + "-verified.json", current.deepCopy());
        entryIndex++;
        state = 1;
        stage = "open";
        current = null;
        actions = null;
        entryOpenRequested = false;
        expectedFirstRow = 0;
        deadlineNanos = System.nanoTime() + 120_000_000_000L;
    }

    private static JsonObject entryMetadata(NpcTradeEntry entry) {
        JsonObject output = new JsonObject();
        output.addProperty("npc", entry.npc());
        output.addProperty("rowCount", entry.rows().size());
        output.addProperty("visibleRows", VISIBLE_ROWS);
        output.addProperty("screen", "real mezz.jei.gui.recipes.RecipesGui");
        output.addProperty("layoutOrigin", "reflected current RecipesGui.layouts.recipeLayoutsWithButtons actual visible layout");
        output.addProperty("inputRoute", "wheel uses RecipesGui.mouseScrolled; drag uses IJeiInputHandler from that same current drawable");
        output.addProperty("screenshotRule", "Screenshot and slot observation are collected from the same RenderFrameEvent.Post after the production widget draw");
        return output;
    }

    private static boolean sameEntry(NpcTradeEntry left, NpcTradeEntry right) {
        return left.npc().equals(right.npc()) && left.rows().equals(right.rows()) && left.info().equals(right.info());
    }

    private static String stackKey(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()) + " x" + stack.getCount() + " " + stack.getComponents();
    }

    private static long countCaptures() {
        long total = 0;
        for (var value : results) total += value.getAsJsonObject().getAsJsonArray("captures").size();
        return total;
    }

    private static long focusQueryCount() {
        JsonObject audit = ClientIntegrationData.read("jei-scroll-index-audit.json");
        return audit.getAsJsonArray("focusChecks").size();
    }

    private static long searchQueryCount() {
        JsonObject audit = ClientIntegrationData.read("jei-scroll-index-audit.json");
        return audit.getAsJsonArray("searchChecks").size();
    }

    private static void fail(Minecraft mc, Throwable problem) {
        failure = problem;
        finished = true;
        running = false;
        writeIndexAudit("failed", problem);
        writeResult("failed", problem);
        ThermalShockReforged.LOGGER.error("Client JEI scroll probe failed: state={}, entry={}, stage={}", state, entryIndex, stage, problem);
    }

    private static void writeIndexAudit(String status, Throwable problem) {
        JsonObject audit = new JsonObject();
        audit.addProperty("status", status);
        audit.addProperty("session", session);
        audit.addProperty("recipeType", TYPE_ID);
        audit.addProperty("entryCount", entries.size());
        audit.addProperty("allRegisteredEntries", allEntries.size());
        audit.addProperty("scrollWidgetVisibleRows", VISIBLE_ROWS);
        JsonArray entrySummaries = new JsonArray();
        for (NpcTradeEntry entry : entries) {
            JsonObject summary = new JsonObject();
            summary.addProperty("npc", entry.npc());
            summary.addProperty("rows", entry.rows().size());
            entrySummaries.add(summary);
        }
        audit.add("scrollEntries", entrySummaries);
        JsonArray allSummaries = new JsonArray();
        for (NpcTradeEntry entry : allEntries) {
            JsonObject summary = new JsonObject();
            summary.addProperty("npc", entry.npc());
            summary.addProperty("rows", entry.rows().size());
            summary.addProperty("scrollable", entry.rows().size() > VISIBLE_ROWS);
            allSummaries.add(summary);
        }
        audit.add("allEntries", allSummaries);
        audit.add("focusChecks", focusChecks.deepCopy());
        audit.add("searchChecks", searchChecks.deepCopy());
        if (originalFilter != null) audit.addProperty("originalFilter", originalFilter);
        if (runtime != null && originalFilter != null) audit.addProperty("filterRestored", runtime.getIngredientFilter().getFilterText().equals(originalFilter));
        if (runtime != null && originalFilter != null) audit.addProperty("searchFilterRestoredBeforeGuiFlow", runtime.getIngredientFilter().getFilterText().equals(originalFilter));
        if (problem != null) audit.addProperty("error", problem.toString());
        ClientIntegrationData.write("jei-scroll-index-audit.json", audit);
    }

    private static void writeResult(String status, Throwable problem) {
        JsonObject output = result();
        output.addProperty("status", status);
        output.addProperty("expectedEntries", entries.size());
        output.addProperty("allRegisteredEntries", allEntries.size());
        output.addProperty("transport", "Real TCP JEI runtime, actual RecipesGui layouts and production widget input handlers");
        output.addProperty("filterRestored", !running && failure == null && runtime != null && runtime.getIngredientFilter().getFilterText().equals(originalFilter));
        output.addProperty("limitations", "Validates JEI focus/search indexes and the rendered production widget/list inputs. Does not claim NPC trade natural reachability or purchase execution.");
        if (problem != null) {
            output.addProperty("error", problem.toString());
            if (current != null) output.add("currentEntry", current.deepCopy());
            if (runtime != null) output.addProperty("currentFilter", runtime.getIngredientFilter().getFilterText());
        }
        ClientIntegrationData.write("jei-scroll-probe-result.json", output);
    }

    private static final class MouseInput implements IJeiUserInput {
        private final InputConstantsKey key = new InputConstantsKey(com.mojang.blaze3d.platform.InputConstants.Type.MOUSE.getOrCreate(com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT));
        private final InputWithModifiers event;

        private MouseInput(double x, double y) {
            event = new MouseButtonEvent(x, y, new MouseButtonInfo(com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT, 0));
        }

        @Override public com.mojang.blaze3d.platform.InputConstants.Key getKey() { return key.value(); }
        @Override public int getModifiers() { return 0; }
        @Override public InputWithModifiers getInputWithModifiers() { return event; }
        @Override public boolean isSimulate() { return false; }
        @Override public boolean is(KeyMapping mapping) { return false; }
        @Override public boolean is(IJeiKeyMapping mapping) { return false; }
    }

    private record InputConstantsKey(com.mojang.blaze3d.platform.InputConstants.Key value) {
    }
}
