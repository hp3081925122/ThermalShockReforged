package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.callback.JadeAfterRenderCallback;
import snownee.jade.api.callback.JadeTooltipCollectedCallback;
import snownee.jade.api.ui.BoxElement;

@WailaPlugin(ThermalShockReforged.MOD_ID)
public final class ClientJadeProbe implements IWailaPlugin {
    private static volatile Target expected;
    private static volatile JsonObject latest;
    private static volatile boolean evidenceFrozen;

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        if (!enabled()) return;
        JadeTooltipCollectedCallback collected = ClientJadeProbe::collected;
        JadeAfterRenderCallback rendered = ClientJadeProbe::rendered;
        registration.addTooltipCollectedCallback(collected);
        registration.addAfterRenderCallback(rendered);
    }

    public static void arm(int x, int y, int z, String blockId, String expectedSubstring, String forbiddenSubstring) {
        arm(x, y, z, blockId, expectedSubstring, forbiddenSubstring, new JsonObject(), true);
    }

    public static void arm(int x, int y, int z, String blockId, String expectedSubstring, String forbiddenSubstring,
                           JsonObject expectedServerData, boolean requireServerData) {
        expected = new Target(x, y, z, Objects.requireNonNull(blockId), Objects.requireNonNull(expectedSubstring),
                Objects.requireNonNull(forbiddenSubstring), Objects.requireNonNull(expectedServerData).deepCopy(), requireServerData);
        latest = null;
        evidenceFrozen = false;
    }

    public static JsonObject result() { return latest == null ? null : latest.deepCopy(); }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationJadeProbe");
    }

    private static void collected(BoxElement box, Accessor<?> accessor) { capture("tooltip_collected", false, box, accessor); }

    private static void rendered(BoxElement box, snownee.jade.api.ui.TooltipAnimation animation,
                                net.minecraft.client.gui.GuiGraphicsExtractor graphics, Accessor<?> accessor) {
        capture("after_render", true, box, accessor);
    }

    private static void capture(String stage, boolean rendered, BoxElement box, Accessor<?> accessor) {
        if (!enabled() || evidenceFrozen) return;
        Target target = expected;
        if (target == null || !(accessor instanceof BlockAccessor block) || !(accessor.getHitResult() instanceof BlockHitResult hit)) return;
        BlockPos accessorPos = block.getPosition().immutable();
        BlockPos hitPos = hit.getBlockPos().immutable();
        String blockId = BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString();
        if (!accessorPos.equals(target.pos()) && !hitPos.equals(target.pos())) return;
        boolean targetMatches = accessorPos.equals(target.pos()) && hitPos.equals(target.pos()) && blockId.equals(target.blockId());
        if (rendered && !targetMatches) return;
        boolean serverConnected = accessor.isServerConnected();
        var serverTag = accessor.getServerData();
        boolean serverDataPositionAvailable = serverTag.contains("x") && serverTag.contains("y") && serverTag.contains("z");
        int serverX = serverTag.getIntOr("x", Integer.MIN_VALUE);
        int serverY = serverTag.getIntOr("y", Integer.MIN_VALUE);
        int serverZ = serverTag.getIntOr("z", Integer.MIN_VALUE);
        boolean serverDataPositionMatches = serverDataPositionAvailable && serverX == target.x() && serverY == target.y() && serverZ == target.z();
        String serverDataBlockId = serverTag.getStringOr("BlockId", "");
        boolean serverDataBlockIdMatches = serverDataBlockId.equals(target.blockId());
        boolean expectedServerDataMatches = matchesExpectedServerData(serverTag, target.expectedServerData());
        boolean serverDataMatches = !target.requireServerData() || serverConnected && serverDataPositionMatches && serverDataBlockIdMatches && expectedServerDataMatches;
        String serverData = serverTag.toString();
        String text = box.getTooltip().getNarration();
        var currentScreen = Minecraft.getInstance().screen;
        String screen = currentScreen == null ? "none" : currentScreen.getClass().getName();
        boolean expectedTextMatches = target.expectedSubstring().isEmpty() || text.contains(target.expectedSubstring());
        boolean forbiddenTextAbsent = target.forbiddenSubstring().isEmpty() || !text.contains(target.forbiddenSubstring());
        JsonObject result = new JsonObject();
        boolean evidenceComplete = rendered && targetMatches && serverConnected && serverDataMatches && expectedTextMatches && forbiddenTextAbsent;
        result.addProperty("status", evidenceComplete ? "observed" : "pending");
        result.addProperty("stage", stage);
        result.addProperty("expectedX", target.x());
        result.addProperty("expectedY", target.y());
        result.addProperty("expectedZ", target.z());
        result.addProperty("expectedBlockId", target.blockId());
        result.addProperty("accessorX", accessorPos.getX());
        result.addProperty("accessorY", accessorPos.getY());
        result.addProperty("accessorZ", accessorPos.getZ());
        result.addProperty("hitX", hitPos.getX());
        result.addProperty("hitY", hitPos.getY());
        result.addProperty("hitZ", hitPos.getZ());
        result.addProperty("blockId", blockId);
        result.addProperty("serverDataBlockId", serverDataBlockId);
        result.addProperty("serverDataBlockIdMatches", serverDataBlockIdMatches);
        result.addProperty("requiredServerData", target.requireServerData());
        result.addProperty("expectedServerDataMatches", expectedServerDataMatches);
        result.addProperty("serverDataMatches", serverDataMatches);
        result.add("serverDataKeys", stringArray(serverTag.keySet()));
        result.add("serverDataFields", actualFields(serverTag, target.expectedServerData()));
        result.addProperty("targetMatches", targetMatches);
        result.addProperty("isServerConnected", serverConnected);
        result.addProperty("serverDataPositionAvailable", serverDataPositionAvailable);
        result.addProperty("serverDataX", serverX);
        result.addProperty("serverDataY", serverY);
        result.addProperty("serverDataZ", serverZ);
        result.addProperty("serverDataPositionMatches", serverDataPositionMatches);
        result.addProperty("expectedSubstring", target.expectedSubstring());
        result.addProperty("expectedSubstringMatches", expectedTextMatches);
        result.addProperty("forbiddenSubstring", target.forbiddenSubstring());
        result.addProperty("forbiddenSubstringAbsent", forbiddenTextAbsent);
        result.addProperty("serverData", serverData);
        result.addProperty("text", text);
        result.addProperty("rendered", rendered);
        result.addProperty("currentScreen", screen);
        result.addProperty("screenOpen", currentScreen != null);
        latest = result.deepCopy();
        if (evidenceComplete) {
            evidenceFrozen = true;
            ClientIntegrationData.write("jade-probe.json", result);
            ThermalShockReforged.LOGGER.info("Client Jade probe captured the armed target at {} with serverConnected={}", hitPos, serverConnected);
        }
    }

    private static boolean matchesExpectedServerData(net.minecraft.nbt.CompoundTag tag, JsonObject expectedData) {
        for (var entry : expectedData.entrySet()) {
            if (entry.getKey().equals("absenceKeys")) {
                for (var absent : entry.getValue().getAsJsonArray()) if (tag.contains(absent.getAsString())) return false;
            } else if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isBoolean()) {
                String key = entry.getKey();
                if (!tag.contains(key) || tag.getBooleanOr(key, !entry.getValue().getAsBoolean()) != entry.getValue().getAsBoolean()) return false;
            } else if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) {
                String key = entry.getKey();
                if (!tag.contains(key) || tag.getIntOr(key, Integer.MIN_VALUE) != entry.getValue().getAsInt()) return false;
            }
        }
        return true;
    }

    private static JsonObject actualFields(net.minecraft.nbt.CompoundTag tag, JsonObject expectedData) {
        JsonObject fields = new JsonObject();
        for (var entry : expectedData.entrySet()) {
            String key = entry.getKey();
            if (key.equals("absenceKeys") || !tag.contains(key)) continue;
            if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isBoolean()) fields.addProperty(key, tag.getBooleanOr(key, false));
            else if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) fields.addProperty(key, tag.getIntOr(key, Integer.MIN_VALUE));
        }
        return fields;
    }

    private static com.google.gson.JsonArray stringArray(java.util.Set<String> values) {
        var array = new com.google.gson.JsonArray();
        values.stream().sorted().forEach(array::add);
        return array;
    }

    private record Target(int x, int y, int z, String blockId, String expectedSubstring, String forbiddenSubstring,
                          JsonObject expectedServerData, boolean requireServerData) {
        private BlockPos pos() { return new BlockPos(x, y, z); }
    }
}
