package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.content.atlas.AtlasArrivalState;
import com.hp.thermalshockreforged.content.atlas.AtlasCoordinates;
import com.hp.thermalshockreforged.content.atlas.AtlasMenu;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.SuspiciousCavebloomerBlockEntity;
import com.hp.thermalshockreforged.content.farming.CropKind;
import com.hp.thermalshockreforged.content.farming.FarmingContent;
import com.hp.thermalshockreforged.content.farming.SeedBlockEntity;
import com.hp.thermalshockreforged.content.growth.GrowthContent;
import com.hp.thermalshockreforged.content.growth.GrowthInventoryBlockEntity;
import com.hp.thermalshockreforged.content.growth.GrowthKind;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class ClientFeatureServer {
    private static final List<String> SUPPORTED = createScenarioList();
    private ClientFeatureServer() {
    }

    public static List<String> scenarios() {
        String filter = System.getProperty("tsr.clientIntegrationFeatureFilter", "");
        if (!filter.isBlank()) {
            var selected = java.util.Arrays.asList(filter.split(","));
            for (String name : selected) ClientIntegrationData.require(SUPPORTED.contains(name), "Unknown feature filter: " + name);
            return SUPPORTED.stream().filter(selected::contains).toList();
        }
        return SUPPORTED;
    }

    private static List<String> createScenarioList() {
        var scenarios = new ArrayList<String>();
        scenarios.add("jei_ready");
        scenarios.add("jade_phobotite_outcrop_locked");
        scenarios.add("jade_phobotite_mantle_outcrop_locked");
        scenarios.add("jade_phobotite_outcrop_unlocked");
        scenarios.add("jade_phobotite_mantle_outcrop_unlocked");
        scenarios.add("jade_frigid_altar_locked");
        scenarios.add("jade_ferrous_altar_locked");
        scenarios.add("jade_frigid_altar_unlocked");
        scenarios.add("jade_ferrous_altar_unlocked");
        scenarios.add("jade_frigid_altar_disabled");
        scenarios.add("jade_ferrous_altar_disabled");
        for (CropKind kind : CropKind.values()) {
            scenarios.add("jade_farm_" + kind.id + "_thirsty");
            scenarios.add("jade_farm_" + kind.id + "_watered");
        }
        for (GrowthKind kind : GrowthKind.values()) {
            scenarios.add("jade_growth_" + kind.id + "_thirsty");
            scenarios.add("jade_growth_" + kind.id + "_watered");
        }
        scenarios.add("jade_cavebloomer_suspicious");
        scenarios.add("atlas_arrival_on");
        scenarios.add("atlas_arrival_off");
        scenarios.add("client_resource_reload");
        return List.copyOf(scenarios);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!ClientIntegrationData.enabled()) return;
        event.getDispatcher().register(Commands.literal("tsr_feature")
                .then(Commands.literal("prepare")
                        .then(Commands.argument("scenario", StringArgumentType.word())
                                .executes(context -> prepare(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "scenario"))))));
    }

    private static int prepare(ServerPlayer player, String scenario) {
        JsonObject evidence = new JsonObject();
        evidence.addProperty("scenario", scenario);
        try {
            ClientIntegrationData.require(SUPPORTED.contains(scenario), "Unsupported feature scenario: " + scenario);
            JsonObject ready = ClientIntegrationData.read("server-ready.json");
            JsonObject clock = ClientIntegrationData.read("server-clock.json");
            String session = ready.get("session").getAsString();
            ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()), "TCP test server is not ready");
            ClientIntegrationData.require(session.equals(clock.get("session").getAsString()), "Server clock belongs to another test session");
            evidence.addProperty("session", session);
            evidence.addProperty("serverTick", clock.get("serverTick").getAsLong());
            ServerLevel level = (ServerLevel) player.level();
            evidence.addProperty("dimension", level.dimension().identifier().toString());
            player.closeContainer();
            player.setGameMode(GameType.CREATIVE);
            if (scenario.startsWith("jade_")) prepareJade(player, level, scenario, evidence);
            else if (scenario.startsWith("atlas_")) prepareAtlas(player, level, scenario, evidence);
            else if (scenario.equals("client_resource_reload")) {
                evidence.addProperty("expectedDimension", level.dimension().identifier().toString());
                evidence.addProperty("expectedMenu", "none");
                evidence.addProperty("clientReloadResources", true);
            } else prepareJei(player, level, evidence);
            evidence.addProperty("status", "passed");
        } catch (Throwable failure) {
            evidence.addProperty("status", "failed");
            evidence.addProperty("error", failure.toString());
            ThermalShockReforged.LOGGER.error("Client feature preparation failed: scenario={}", scenario, failure);
        }
        try {
            ClientIntegrationData.write("feature-" + scenario + "-prepared.json", evidence);
        } catch (Throwable failure) {
            ThermalShockReforged.LOGGER.error("Client feature evidence publication failed: scenario={}", scenario, failure);
            return 0;
        }
        return "passed".equals(evidence.get("status").getAsString()) ? 1 : 0;
    }

    private static void prepareJade(ServerPlayer player, ServerLevel level, String scenario, JsonObject evidence) {
        prepareJadeTarget(player, level, scenario, evidence);
    }

    private static void prepareJadeTarget(ServerPlayer player, ServerLevel level, String scenario, JsonObject evidence) {
        int x = 4 + SUPPORTED.indexOf(scenario) * 6;
        BlockPos anchor = new BlockPos(x, 120, 4);
        for (int groundX = x - 2; groundX <= x + 2; groundX++) for (int z = 2; z <= 8; z++) {
            level.setBlock(new BlockPos(groundX, 119, z), Blocks.STONE.defaultBlockState(), 3);
            for (int airY = 120; airY <= 124; airY++) level.setBlock(new BlockPos(groundX, airY, z), Blocks.AIR.defaultBlockState(), 3);
        }
        level.setBlock(anchor, Blocks.AIR.defaultBlockState(), 3);
        ClientIntegrationData.require(player.teleportTo(level, x + 0.5, 120, 7, Set.of(), 180, 10, true), "Cannot position connected client near Jade target");
        evidence.addProperty("requireServerData", !scenario.equals("jade_cavebloomer_suspicious"));
        var expectedData = new JsonObject();
        var fields = new JsonObject();
        var keys = new com.google.gson.JsonArray();
        if (!scenario.equals("jade_cavebloomer_suspicious")) {
            keys.add("x"); keys.add("y"); keys.add("z"); keys.add("BlockId");
            fields.addProperty("x", "integer"); fields.addProperty("y", "integer"); fields.addProperty("z", "integer"); fields.addProperty("BlockId", "string");
        }
        String blockId;
        String expectedTextKey;
        String forbiddenTextKey = "";
        if (scenario.startsWith("jade_phobotite_")) {
            boolean disabled = scenario.endsWith("_disabled");
            boolean unlocked = scenario.endsWith("_unlocked");
            var advancement = level.getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "frigid_altar_get"));
            ClientIntegrationData.require(advancement != null, "Production altar unlock advancement is unavailable");
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            for (String criterion : progress.getCompletedCriteria()) player.getAdvancements().revoke(advancement, criterion);
            if (unlocked) for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
            level.getGameRules().set(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION.get(), !disabled, level.getServer());
            var kind = scenario.contains("phobotite_mantle") ? GeoDecorKind.PHOBOTITE_MANTLE_OUTCROP : GeoDecorKind.PHOBOTITE_OUTCROP;
            blockId = ThermalShockReforged.MOD_ID + ":" + kind.id;
            level.setBlock(anchor, GeoDecorContent.block(kind).defaultBlockState(), 3);
            ClientIntegrationData.require(level.getBlockEntity(anchor) instanceof GeoDecorBlockEntity, "Registered target block did not create its production block entity");
            expectedTextKey = unlocked || disabled ? null : "jade.thermal_shock_reforged.phobotite.locked";
            if (unlocked || disabled) forbiddenTextKey = "jade.thermal_shock_reforged.phobotite.locked";
            if (!unlocked && !disabled) {
                expectedData.addProperty("PhobotiteStatus", true);
                fields.addProperty("PhobotiteStatus", "boolean");
                keys.add("PhobotiteStatus");
            } else {
                var absent = new com.google.gson.JsonArray();
                absent.add("PhobotiteStatus");
                expectedData.add("absenceKeys", absent);
            }
            evidence.addProperty("stateSetup", "Production frigid_altar_get advancement and loqendia_enforce_progression gamerule");
        } else if (scenario.startsWith("jade_frigid_altar_") || scenario.startsWith("jade_ferrous_altar_")) {
            boolean disabled = scenario.endsWith("_disabled");
            boolean unlocked = scenario.endsWith("_unlocked");
            var advancement = level.getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "frigid_altar_get"));
            ClientIntegrationData.require(advancement != null, "Production altar unlock advancement is unavailable");
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            for (String criterion : progress.getCompletedCriteria()) player.getAdvancements().revoke(advancement, criterion);
            if (unlocked) for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
            level.getGameRules().set(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION.get(), !disabled, level.getServer());
            var kind = scenario.contains("frigid_altar") ? GeoDecorKind.FRIGID_ALTAR : GeoDecorKind.FERROUS_ALTAR;
            blockId = ThermalShockReforged.MOD_ID + ":" + kind.id;
            level.setBlock(anchor, GeoDecorContent.block(kind).defaultBlockState(), 3);
            ClientIntegrationData.require(level.getBlockEntity(anchor) instanceof GeoDecorBlockEntity, "Registered target block did not create its production block entity");
            expectedData.addProperty("AltarStatus", disabled ? 2 : unlocked ? 1 : 0);
            fields.addProperty("AltarStatus", "integer");
            keys.add("AltarStatus");
            expectedTextKey = disabled ? "jade.thermal_shock_reforged.altar.not_required" : unlocked ? "jade.thermal_shock_reforged.altar.unlocked" : "jade.thermal_shock_reforged.altar.locked";
            var extra = new com.google.gson.JsonArray();
            extra.add(disabled || unlocked ? "jade.thermal_shock_reforged.altar.use" : "jade.thermal_shock_reforged.altar.how");
            evidence.add("additionalExpectedTextKeys", extra);
            evidence.addProperty("stateSetup", "Production frigid_altar_get advancement and loqendia_enforce_progression gamerule");
        } else if (scenario.equals("jade_cavebloomer_suspicious")) {
            blockId = ThermalShockReforged.MOD_ID + ":suspicious_cavebloomer";
            level.setBlock(anchor, GeoDecorContent.block(GeoDecorKind.SUSPICIOUS_CAVEBLOOMER).defaultBlockState(), 3);
            ClientIntegrationData.require(level.getBlockEntity(anchor) instanceof SuspiciousCavebloomerBlockEntity, "Suspicious Cavebloomer did not create its production block entity");
            expectedTextKey = "jade.thermal_shock_reforged.suspicious_cavebloomer.budding";
            var extra = new com.google.gson.JsonArray();
            extra.add("jade.thermal_shock_reforged.suspicious_cavebloomer.boss");
            evidence.add("additionalExpectedTextKeys", extra);
        } else {
            String id;
            boolean farm = scenario.startsWith("jade_farm_");
            if (farm) id = scenario.substring("jade_farm_".length(), scenario.lastIndexOf('_'));
            else id = scenario.substring("jade_growth_".length(), scenario.lastIndexOf('_'));
            CropKind cropKind = farm ? java.util.Arrays.stream(CropKind.values()).filter(value -> value.id.equals(id)).findFirst().orElseThrow() : null;
            GrowthKind growthKind = farm ? null : java.util.Arrays.stream(GrowthKind.values()).filter(value -> value.id.equals(id)).findFirst().orElseThrow();
            boolean watered = scenario.endsWith("_watered");
            blockId = ThermalShockReforged.MOD_ID + ":" + id + "_seed";
            prepareSeed(player, level, anchor, cropKind, growthKind, watered);
            expectedData.addProperty("SeedThirsty", !watered);
            expectedData.addProperty("SeedWaterings", watered ? 1 : 0);
            fields.addProperty("SeedThirsty", "boolean");
            fields.addProperty("SeedWaterings", "integer");
            keys.add("SeedThirsty"); keys.add("SeedWaterings");
            expectedTextKey = "jade.thermal_shock_reforged.seed." + (watered ? "no" : "yes");
            evidence.addProperty("wateringCount", watered ? 1 : 0);
            evidence.addProperty("stateSetup", watered ? "Seed BE set thirsty, then watered through production BlockState.useItemOn with watering_can" : "Seed BE thirst state directly set for deterministic test setup");
        }
        if (expectedTextKey == null) evidence.add("expectedTextKey", com.google.gson.JsonNull.INSTANCE);
        else evidence.addProperty("expectedTextKey", expectedTextKey);
        evidence.addProperty("forbiddenTextKey", forbiddenTextKey);
        evidence.add("serverExpectedData", expectedData);
        evidence.add("serverDataFields", fields);
        evidence.add("serverDataKeys", keys);
        evidence.addProperty("blockId", blockId);
        evidence.addProperty("x", x); evidence.addProperty("y", 120); evidence.addProperty("z", 4);
        evidence.addProperty("expectedX", x); evidence.addProperty("expectedY", 120); evidence.addProperty("expectedZ", 4); evidence.addProperty("expectedBlockId", blockId);
        var shape = level.getBlockState(anchor).getShape(level, anchor);
        AABB bounds = shape.isEmpty() ? null : shape.bounds();
        Vec3 target = bounds == null ? Vec3.atCenterOf(anchor) : bounds.getCenter().add(anchor.getX(), anchor.getY(), anchor.getZ());
        Vec3 eye = player.getEyePosition();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        ClientIntegrationData.require(player.teleportTo(level, x + 0.5, 120, 7, Set.of(), yaw, pitch, true), "Cannot aim connected client at Jade target");
        evidence.addProperty("playerYaw", yaw); evidence.addProperty("playerPitch", pitch);
        evidence.addProperty("playerX", x + 0.5); evidence.addProperty("playerY", 120); evidence.addProperty("playerZ", 7);
        evidence.addProperty("targetCenterX", target.x); evidence.addProperty("targetCenterY", target.y); evidence.addProperty("targetCenterZ", target.z);
        evidence.addProperty("targetRange", 3.0);
    }

    private static void prepareSeed(ServerPlayer player, ServerLevel level, BlockPos pos, CropKind cropKind, GrowthKind growthKind, boolean watered) {
        player.getInventory().clearContent();
        BlockPos soil = pos.below();
        level.setBlock(soil, FarmingContent.PLANTER_POT.get().defaultBlockState(), 3);
        var soilHit = new BlockHitResult(Vec3.atCenterOf(soil), Direction.UP, soil, false);
        ItemStack seedStack = new ItemStack(cropKind != null ? FarmingContent.seed(cropKind).asItem() : GrowthContent.block(growthKind.id + "_seed").asItem());
        player.setItemInHand(InteractionHand.MAIN_HAND, seedStack);
        var placement = seedStack.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, soilHit));
        ClientIntegrationData.require(placement.consumesAction(), "Production seed item could not be planted on planter_pot");
        var seed = level.getBlockEntity(pos);
        ClientIntegrationData.require(seed instanceof SeedBlockEntity || seed instanceof GrowthInventoryBlockEntity, "Production seed item did not create its inventory block entity");
        var data = seed.getPersistentData();
        data.putBoolean("thirsty", true);
        data.putDouble("waterings", 0);
        seed.setChanged();
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        if (watered) {
            ItemStack can = new ItemStack(MaterialContent.items().get("watering_can").get());
            player.setItemInHand(InteractionHand.MAIN_HAND, can);
            ClientIntegrationData.require(!player.getMainHandItem().isEmpty() && player.getMainHandItem().is(MaterialContent.items().get("watering_can").get()), "watering_can must be held in the main interaction hand before watering");
            level.getBlockState(pos).useItemOn(can, level, player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false));
            ClientIntegrationData.require(!data.getBooleanOr("thirsty", true) && data.getDoubleOr("waterings", 0) == 1, "Production watering_can interaction did not water the seed exactly once");
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    private static void prepareJei(ServerPlayer player, ServerLevel level, JsonObject evidence) {
        BlockPos anchor = new BlockPos(4, 120, 4);
        for (int x = 2; x <= 6; x++) for (int z = 2; z <= 8; z++) level.setBlock(new BlockPos(x, 119, z), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(anchor, Blocks.AIR.defaultBlockState(), 3);
        ClientIntegrationData.require(player.teleportTo(level, 4.5, 120, 7, Set.of(), 180, 10, true), "Cannot position connected client for JEI");
        evidence.addProperty("expectedMenu", "none");
        evidence.addProperty("expectedRecipeTypeCount", 11);
        evidence.addProperty("target", "JEI recipe type list");
    }

    private static void prepareAtlas(ServerPlayer player, ServerLevel source, String scenario, JsonObject evidence) {
        player.setGameMode(GameType.SURVIVAL);
        var loqendia = source.getServer().getLevel(com.hp.thermalshockreforged.world.LoqendiaContent.DIMENSION);
        ClientIntegrationData.require(loqendia != null, "Loqendia destination level is unavailable");
        evidence.addProperty("expectedArrivalVisual", scenario.equals("atlas_arrival_on"));
        evidence.addProperty("expectedArrivalProtection", scenario.equals("atlas_arrival_on"));
        evidence.addProperty("expectedDuration", 180);
        if (scenario.equals("atlas_arrival_off")) {
            ClientIntegrationData.require(source.dimension().equals(com.hp.thermalshockreforged.world.LoqendiaContent.DIMENSION), "atlas_arrival_off requires the connected player to remain in Loqendia");
            int ticks = ((AtlasArrivalState) player).arrivalProtectionTicks();
            evidence.addProperty("status", "passed");
            evidence.addProperty("expectedDimension", com.hp.thermalshockreforged.world.LoqendiaContent.DIMENSION.identifier().toString());
            evidence.addProperty("arrivalProtectionTicksAtPrepare", ticks);
            evidence.addProperty("awaitNaturalExpiry", true);
            evidence.addProperty("limitation", "This scenario observes the production 180-tick arrival timer expire naturally in Loqendia; it does not clear or override the timer.");
            return;
        }
        evidence.addProperty("blockId", ThermalShockReforged.MOD_ID + ":atlas");
        evidence.addProperty("x", 4); evidence.addProperty("y", 120); evidence.addProperty("z", 4);
        evidence.addProperty("clientButton", 0);
        evidence.addProperty("productionTravelPath", "TCP menu button -> AtlasMenu.clickMenuButton -> AtlasTravel.travel");
        ClientIntegrationData.require(source.dimension().equals(Level.OVERWORLD), "atlas_arrival_on must start in the Overworld");
        BlockPos anchor = new BlockPos(4, 120, 4);
        for (int x = 2; x <= 6; x++) for (int z = 2; z <= 8; z++) source.setBlock(new BlockPos(x, 119, z), Blocks.STONE.defaultBlockState(), 3);
        source.setBlock(anchor, GeoDecorContent.block(GeoDecorKind.ATLAS).defaultBlockState(), 3);
        ClientIntegrationData.require(source.getBlockEntity(anchor) instanceof GeoDecorBlockEntity, "Registered Atlas did not create its production block entity");
        var destinationPoint = AtlasCoordinates.get(player, com.hp.thermalshockreforged.world.LoqendiaContent.DIMENSION);
        BlockPos destinationFloor = BlockPos.containing(destinationPoint).below();
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) loqendia.setBlock(destinationFloor.offset(dx, 0, dz), Blocks.STONE.defaultBlockState(), 3);
        ClientIntegrationData.require(player.teleportTo(source, 4.5, 120, 7, Set.of(), 180, 10, true), "Cannot position connected client at production Atlas");
        var useResult = source.getBlockState(anchor).useItemOn(ItemStack.EMPTY, source, player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(anchor), Direction.NORTH, anchor, false));
        ClientIntegrationData.require(useResult.consumesAction() && player.containerMenu instanceof AtlasMenu, "Production Atlas block interaction did not open AtlasMenu");
        evidence.addProperty("status", "passed");
        evidence.addProperty("expectedDimension", com.hp.thermalshockreforged.world.LoqendiaContent.DIMENSION.identifier().toString());
        evidence.addProperty("awaitClientMenuButton", true);
        evidence.addProperty("serverInvokedMenuButton", false);
    }


}
