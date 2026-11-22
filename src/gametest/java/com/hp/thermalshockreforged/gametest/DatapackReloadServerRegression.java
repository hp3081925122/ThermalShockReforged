package com.hp.thermalshockreforged.gametest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeContent;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeBlockEntity;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeMenu;
import com.hp.thermalshockreforged.content.fabrication.FabricationContent;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.registration.ChannelAttributes;
import net.neoforged.neoforge.network.registration.NetworkChannel;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID)
public final class DatapackReloadServerRegression {
    private static final String PACK = "tsr_reload_regression";
    private static final String NAMESPACE = "tsr_regression";
    private static final BlockPos POSITION = new BlockPos(8, 200, 8);

    private DatapackReloadServerRegression() { }

    @SubscribeEvent
    public static void started(ServerStartedEvent event) {
        if (!System.getProperty("tsr.dedicatedRegression", "").equals("reload")) return;
        var result = new JsonObject();
        result.addProperty("mode", "reload");
        var server = event.getServer();
        try {
            var output = safePath(Path.of(System.getProperty("tsr.dedicatedRegressionResultDir", "D:/AI临时文件"))).resolve("tsr-dedicated-reload-result.json");
            try {
                run(server, result);
                result.addProperty("status", "passed");
            } catch (Throwable failure) {
                result.addProperty("status", "failed");
                result.addProperty("failure", failure.toString());
                ThermalShockReforged.LOGGER.error("Datapack reload regression failed", failure);
            }
            Files.createDirectories(output.getParent());
            Files.writeString(output, new GsonBuilder().setPrettyPrinting().create().toJson(result), StandardCharsets.UTF_8);
            ThermalShockReforged.LOGGER.info("Datapack reload regression completed: result={}", result);
        } catch (Throwable failure) {
            ThermalShockReforged.LOGGER.error("Could not write datapack reload regression result", failure);
        } finally {
            server.halt(false);
        }
    }

    private static void run(MinecraftServer server, JsonObject result) throws Exception {
        ServerLevel level = server.overworld();
        var pack = safePath(server.getWorldPath(LevelResource.DATAPACK_DIR)).resolve(PACK);
        if (Files.exists(pack)) throw new IllegalStateException("Reload fixture requires a fresh pack directory: " + pack);
        var recipes = pack.resolve("data/" + NAMESPACE + "/recipe");
        Files.createDirectories(recipes);
        Files.writeString(pack.resolve("pack.mcmeta"), "{\"pack\":{\"description\":\"TSR server reload regression\",\"min_format\":[101,1],\"max_format\":[101,1]}}", StandardCharsets.UTF_8);
        var original = List.copyOf(server.getPackRepository().getSelectedIds());
        int baseline = RecipeContent.recipes(level).values().size();
        var types = List.<RecipeType<?>>of(RecipeContent.TRANSMUTATION.type().get(), RecipeContent.EXTRACTION.type().get(), RecipeContent.CENTRIFUGE.type().get(), RecipeContent.LAPIDARY.type().get(), RecipeContent.ALTAR_CONVERSION.type().get(), RecipeContent.ASSEMBLER.type().get(), RecipeContent.NPC_TRADE.type().get(), FabricationContent.TYPE.get());
        var ids = new ArrayList<Identifier>();
        var ops = level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        for (int index = 0; index < types.size(); index++) {
            var type = types.get(index);
            var source = RecipeContent.recipes(level).values().stream().filter(holder -> holder.value().getType() == type).findFirst().orElseThrow(() -> new IllegalStateException("Missing production recipe type: " + type));
            var json = Recipe.CODEC.encodeStart(ops, source.value()).getOrThrow();
            var id = Identifier.fromNamespaceAndPath(NAMESPACE, "reload_" + index);
            ids.add(id);
            Files.writeString(recipes.resolve(id.getPath() + ".json"), json.toString(), StandardCharsets.UTF_8);
        }
        var centrifugeFile = recipes.resolve("reload_2.json");
        writeConversion(centrifugeFile, "minecraft:diamond", 2);
        var player = player(level);
        var previousState = level.getBlockState(POSITION);
        try {
            level.setBlock(POSITION, CentrifugeContent.BLOCK.get().defaultBlockState(), 3);
            require(CentrifugeMenu.open(player, POSITION), "Could not open the actual centrifuge menu");
            var menu = (CentrifugeMenu) player.containerMenu;
            var storage = (CentrifugeBlockEntity) level.getBlockEntity(POSITION);
            require(storage != null && !menu.getSlot(0).mayPlace(new ItemStack(Items.DIRT)), "New datapack inputs must be rejected before reload");
            server.getPackRepository().reload();
            String packId = "file/" + PACK;
            require(server.getPackRepository().getAvailableIds().contains(packId), "New datapack was not discovered");
            var selected = new ArrayList<>(original);
            selected.add(packId);
            server.reloadResources(selected).join();
            require(RecipeContent.recipes(level).values().size() == baseline + types.size(), "Reload did not add exactly eight custom recipe types");
            for (int index = 0; index < ids.size(); index++) {
                var loaded = RecipeContent.recipes(level).byKey(ResourceKey.create(Registries.RECIPE, ids.get(index)));
                require(loaded != null && loaded.value().getType() == types.get(index), "Reload lost the custom recipe type: " + ids.get(index));
            }
            result.addProperty("newCustomRecipeTypes", types.size());
            convert(player, menu, storage, Items.DIAMOND, 2);
            result.addProperty("openMenuUsesAddedRecipe", true);
            writeConversion(centrifugeFile, "minecraft:emerald", 3);
            server.reloadResources(selected).join();
            require(player.containerMenu == menu, "Reload unexpectedly replaced the open server menu");
            convert(player, menu, storage, Items.EMERALD, 3);
            result.addProperty("openMenuUsesChangedRecipe", true);
            server.reloadResources(original).join();
            for (var id : ids) require(RecipeContent.recipes(level).byKey(ResourceKey.create(Registries.RECIPE, id)) == null, "Disabled datapack recipe remained cached: " + id);
            require(RecipeContent.recipes(level).values().size() == baseline, "Disabling the datapack did not restore recipe count");
            fill(storage);
            require(!menu.getSlot(0).mayPlace(new ItemStack(Items.DIRT)) && !menu.clickMenuButton(player, 0), "Removed recipe remained usable by an open menu");
            require(storage.getItem(0).getCount() == 2 && storage.getItem(1).getCount() == 2 && storage.getItem(2).getCount() == 2, "Removed recipe consumed materials");
            result.addProperty("removedRecipesRejectedWithoutCost", true);
            result.addProperty("baselineRecipes", baseline);
            result.addProperty("reloads", 3);
        } finally {
            player.closeContainer();
            server.getPlayerList().remove(player);
            level.setBlock(POSITION, previousState, 3);
            if (!List.copyOf(server.getPackRepository().getSelectedIds()).equals(original)) server.reloadResources(original).join();
        }
    }

    private static void writeConversion(Path file, String item, int count) throws Exception {
        Files.writeString(file, "{\"type\":\"thermal_shock_reforged:centrifuge\",\"energy\":[\"minecraft:dirt\",\"minecraft:cobblestone\"],\"ingredient\":\"minecraft:coal\",\"result\":{\"id\":\"" + item + "\",\"count\":" + count + "}}", StandardCharsets.UTF_8);
    }

    private static void fill(CentrifugeBlockEntity storage) {
        storage.setItem(0, new ItemStack(Items.DIRT, 2));
        storage.setItem(1, new ItemStack(Items.COBBLESTONE, 2));
        storage.setItem(2, new ItemStack(Items.COAL, 2));
    }

    private static void convert(ServerPlayer player, CentrifugeMenu menu, CentrifugeBlockEntity storage, Item output, int count) {
        player.getInventory().clearContent();
        fill(storage);
        require(menu.getSlot(0).mayPlace(storage.getItem(0)) && menu.getSlot(1).mayPlace(storage.getItem(1)) && menu.getSlot(2).mayPlace(storage.getItem(2)), "Open menu did not refresh its accepted ingredients");
        require(menu.clickMenuButton(player, 0), "Actual server conversion rejected the loaded recipe");
        int outputs = player.getInventory().getNonEquipmentItems().stream().filter(stack -> stack.is(output)).mapToInt(ItemStack::getCount).sum();
        require(outputs == count && storage.getItem(0).getCount() == 1 && storage.getItem(1).getCount() == 1 && storage.getItem(2).getCount() == 1, "Reloaded recipe output or input cost was incorrect");
    }

    @SuppressWarnings("unchecked")
    private static ServerPlayer player(ServerLevel level) throws Exception {
        var server = level.getServer();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "tsr-reload"), false);
        var player = new ServerPlayer(server, level, cookie.gameProfile(), cookie.clientInformation());
        var connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        var field = net.neoforged.neoforge.network.registration.NetworkRegistry.class.getDeclaredField("PAYLOAD_REGISTRATIONS");
        field.setAccessible(true);
        var registrations = (Map<ConnectionProtocol, Map<Identifier, net.neoforged.neoforge.network.registration.PayloadRegistration<?>>>) field.get(null);
        var channels = new HashMap<Identifier, NetworkChannel>();
        registrations.getOrDefault(ConnectionProtocol.PLAY, Map.of()).forEach((id, registration) -> {
            if (!id.equals(net.neoforged.neoforge.network.payload.ConfigFilePayload.TYPE.id())) channels.put(id, new NetworkChannel(id, registration.version()));
        });
        ChannelAttributes.setPayloadSetup(connection, new NetworkPayloadSetup(Map.of(ConnectionProtocol.PLAY, channels)));
        server.getPlayerList().placeNewPlayer(connection, player, cookie);
        player.connection.markClientLoaded();
        player.setGameMode(GameType.SURVIVAL);
        player.setNoGravity(true);
        player.snapTo(8.5, 201, 9.5, 0, 0);
        return player;
    }

    private static Path safePath(Path path) {
        var resolved = path.toAbsolutePath().normalize();
        require(resolved.startsWith(Path.of("D:/AI临时文件").toAbsolutePath().normalize()), "Regression path must remain under the temporary workspace: " + resolved);
        return resolved;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
