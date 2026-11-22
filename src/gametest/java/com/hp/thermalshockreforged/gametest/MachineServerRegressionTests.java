package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.fabrication.FabricationRecipe;
import com.hp.thermalshockreforged.content.fabrication.FabricatorPage;
import com.hp.thermalshockreforged.content.recipe.AssemblerRecipe;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import com.hp.thermalshockreforged.content.recipe.RecipeContent;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergeContent;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergePage;
import com.hp.thermalshockreforged.entity.labmachinist.LabContent;
import com.hp.thermalshockreforged.entity.labmachinist.LabMachineBlockEntity;
import com.hp.thermalshockreforged.entity.labmachinist.LabMenu;
import com.hp.thermalshockreforged.entity.labmachinist.LabPage;
import com.hp.thermalshockreforged.entity.qo.QoContent;
import com.hp.thermalshockreforged.entity.qo.QoMenu;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.network.registration.ChannelAttributes;
import net.neoforged.neoforge.network.registration.NetworkChannel;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;

public final class MachineServerRegressionTests {
    private static final BlockPos MACHINE = new BlockPos(3, 1, 3);

    private MachineServerRegressionTests() { }

    static void register() {
        TsrGameTests.add("fabricator_server_buttons_cover_all_recipe_pages", MachineServerRegressionTests::fabricatorButtonsCoverAllRecipes, 200);
        TsrGameTests.add("assembler_server_menu_is_atomic_for_all_recipes", MachineServerRegressionTests::assemblerMenuIsAtomic, 100);
        TsrGameTests.add("npc_trade_server_recipes_pay_atomically", MachineServerRegressionTests::npcTradeRecipesPayAtomically, 200);
    }

    private static ServerPlayer player(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "tsr-machine-test"), false);
        var player = new ServerPlayer(server, helper.getLevel(), cookie.gameProfile(), cookie.clientInformation());
        var connection = new Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        negotiateAllChannels(connection);
        server.getPlayerList().placeNewPlayer(connection, player, cookie);
        player.connection.markClientLoaded();
        player.setGameMode(GameType.SURVIVAL);
        var pos = helper.absolutePos(new BlockPos(3, 1, 5)).getCenter();
        player.snapTo(pos.x, pos.y, pos.z, 0, 0);
        return player;
    }

    private static void release(GameTestHelper helper, ServerPlayer player) {
        player.closeContainer();
        helper.getLevel().getServer().getPlayerList().remove(player);
    }

    @SuppressWarnings("unchecked")
    private static void negotiateAllChannels(Connection connection) {
        try {
            var field = net.neoforged.neoforge.network.registration.NetworkRegistry.class.getDeclaredField("PAYLOAD_REGISTRATIONS");
            field.setAccessible(true);
            var registrations = (Map<ConnectionProtocol, Map<net.minecraft.resources.Identifier, net.neoforged.neoforge.network.registration.PayloadRegistration<?>>>) field.get(null);
            Map<net.minecraft.resources.Identifier, NetworkChannel> channels = new java.util.HashMap<>();
            registrations.getOrDefault(ConnectionProtocol.PLAY, Map.of()).forEach((id, registration) -> {
                if (!id.equals(net.neoforged.neoforge.network.payload.ConfigFilePayload.TYPE.id())) channels.put(id, new NetworkChannel(id, registration.version()));
            });
            ChannelAttributes.setPayloadSetup(connection, new NetworkPayloadSetup(Map.of(ConnectionProtocol.PLAY, channels)));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read NeoForge payload registrations", e);
        }
    }

    public static void fabricatorButtonsCoverAllRecipes(GameTestHelper helper) {
        var player = player(helper);
        try {
            var pos = helper.absolutePos(MACHINE);
            helper.getLevel().setBlock(pos, GeoDecorContent.block(GeoDecorKind.FABRICATOR).defaultBlockState(), 3);
            helper.assertTrue(FabricatorMenu.open(player, pos, FabricatorPage.TOOLS), "Fabricator block must open its actual server menu");
            helper.assertTrue(player.containerMenu instanceof FabricatorMenu, "Opened menu must be FabricatorMenu");
            var menu = (FabricatorMenu) player.containerMenu;
            var entity = (GeoDecorBlockEntity) helper.getLevel().getBlockEntity(pos);
            int tested = 0;
            for (var category : FabricatorPage.CATEGORIES) {
                for (var page : category.recipes()) {
                    var holder = helper.getLevel().recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, page.recipe)).orElse(null);
                    helper.assertTrue(holder != null && holder.value() instanceof FabricationRecipe, "Fabricator page must resolve its data recipe: " + page.recipe);
                    var recipe = (FabricationRecipe) holder.value();
                    helper.assertTrue(menu.clickMenuButton(player, FabricatorMenu.SELECT_BUTTON + page.ordinal()), "Recipe button must select " + page);
                    entity.setItem(0, ItemStack.EMPTY);
                    entity.setItem(1, new ItemStack(recipe.energy().item().value(), recipe.energy().count() + 1));
                    player.getInventory().clearContent();
                    for (var cost : recipe.materials()) player.getInventory().add(new ItemStack(cost.item().value(), cost.count()));
                    var before = snapshot(player.getInventory());
                    var energyBefore = entity.getItem(1).copy();
                    entity.setItem(1, new ItemStack(recipe.energy().item().value(), recipe.energy().count() - 1));
                    helper.assertTrue(!menu.clickMenuButton(player, 0), "Insufficient energy must reject " + page.recipe);
                    assertInventoryUnchanged(helper, before, player.getInventory(), page.recipe.toString());
                    helper.assertTrue(entity.getItem(0).isEmpty(), "Insufficient energy must preserve empty output for " + page.recipe);
                    entity.setItem(1, energyBefore.copy());
                    entity.setItem(0, new ItemStack(Items.DIRT, 64));
                    helper.assertTrue(!menu.clickMenuButton(player, 0), "Mismatched full output must reject " + page.recipe);
                    assertInventoryUnchanged(helper, before, player.getInventory(), page.recipe.toString());
                    helper.assertTrue(entity.getItem(1).getCount() == energyBefore.getCount(), "Rejected full output must preserve energy for " + page.recipe);
                    entity.setItem(0, ItemStack.EMPTY);
                    helper.assertTrue(menu.clickMenuButton(player, 0), "Craft button must make recipe " + page.recipe);
                    ItemStack expected = recipe.result();
                    helper.assertTrue(ItemStack.isSameItemSameComponents(entity.getItem(0), expected), "Recipe output item must match " + page.recipe);
                    helper.assertTrue(entity.getItem(0).getCount() == expected.getCount(), "Recipe output count must match " + page.recipe);
                    helper.assertTrue(entity.getItem(1).getCount() == energyBefore.getCount() - recipe.energyCount(), "Energy debit must be exact for " + page.recipe);
                    assertMaterialsConsumed(helper, recipe, before, player.getInventory(), page.recipe.toString());
                    if (tested == 0) {
                        ItemStack collected = entity.removeItem(0, expected.getCount());
                        helper.assertTrue(ItemStack.isSameItemSameComponents(collected, expected) && entity.getItem(0).isEmpty(), "Collecting first output must clear the fabricator buffer");
                        for (var cost : recipe.materials()) player.getInventory().add(new ItemStack(cost.item().value(), cost.count()));
                        entity.setItem(1, new ItemStack(recipe.energy().item().value(), recipe.energyCount()));
                        helper.assertTrue(menu.clickMenuButton(player, 0), "The same recipe must be manufacturable consecutively");
                        helper.assertTrue(ItemStack.isSameItemSameComponents(entity.getItem(0), expected) && entity.getItem(0).getCount() == expected.getCount(), "Consecutive manufacture after collecting output must produce the same result");
                    }
                    tested++;
                }
            }
            helper.assertTrue(tested > 0, "At least one recipe page must be tested");
            ThermalShockReforged.LOGGER.info("Server regression recipe traversal: Fabricator recipes tested={}", tested);
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void assemblerMenuIsAtomic(GameTestHelper helper) {
        var player = player(helper);
        try {
            var advancement = helper.getLevel().getServer().getAdvancements().get(QoContent.id("icebreaker_1"));
            helper.assertTrue(advancement != null, "Assembler access advancement must be loaded");
            for (String criterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
            var pos = helper.absolutePos(MACHINE);
            helper.getLevel().setBlock(pos, LabContent.ASSEMBLER.get().defaultBlockState(), 3);
            var recipes = RecipeContent.ASSEMBLER.all(helper.getLevel());
            helper.assertTrue(!recipes.isEmpty(), "Assembler data recipes must be loaded");
            helper.assertTrue(LabContent.openBlock(player, pos, LabPage.ASSEMBLY), "Unlocked player must open actual assembler menu");
            helper.assertTrue(player.containerMenu instanceof LabMenu, "Assembler must open LabMenu");
            var menu = (LabMenu) player.containerMenu;
            var output = (LabMachineBlockEntity) helper.getLevel().getBlockEntity(pos);
            for (var holder : recipes) {
                AssemblerRecipe recipe = holder.value();
                player.getInventory().clearContent();
                addAssemblerMaterials(player.getInventory(), recipe, false);
                var before = snapshot(player.getInventory());
                helper.assertTrue(!menu.clickMenuButton(player, 0), "Missing one material must reject " + holder.id());
                assertInventoryUnchanged(helper, before, player.getInventory(), holder.id().toString());
                helper.assertTrue(output.getItem(0).isEmpty(), "Missing material must preserve assembler output for " + holder.id());
                player.getInventory().clearContent();
                addAssemblerMaterials(player.getInventory(), recipe, true);
                ItemStack fullOutput = recipe.result().create();
                fullOutput.setCount(fullOutput.getMaxStackSize());
                output.setItem(0, fullOutput);
                before = snapshot(player.getInventory());
                helper.assertTrue(!menu.clickMenuButton(player, 0), "Occupied assembler output must reject " + holder.id());
                assertInventoryUnchanged(helper, before, player.getInventory(), holder.id().toString());
                output.setItem(0, ItemStack.EMPTY);
                helper.assertTrue(menu.clickMenuButton(player, 0), "Valid materials must assemble " + holder.id());
                ItemStack expected = recipe.result().create();
                helper.assertTrue(ItemStack.isSameItemSameComponents(output.getItem(0), expected) && output.getItem(0).getCount() == expected.getCount(), "Assembler result must match " + holder.id());
                assertAssemblerMaterialsConsumed(helper, recipe, before, player.getInventory(), holder.id().toString());
                output.setItem(0, ItemStack.EMPTY);
            }
            ThermalShockReforged.LOGGER.info("Server regression recipe traversal: Assembler recipes tested={}", recipes.size());
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    private static void addAssemblerMaterials(Inventory inventory, AssemblerRecipe recipe, boolean complete) {
        int finalCost = recipe.materials().size() - 1;
        for (int index = 0; index < recipe.materials().size(); index++) {
            var cost = recipe.materials().get(index);
            int count = cost.count() - (!complete && index == finalCost ? 1 : 0);
            if (count > 0) inventory.add(new ItemStack(cost.item().value(), count));
        }
    }

    private static void assertAssemblerMaterialsConsumed(GameTestHelper helper, AssemblerRecipe recipe, Map<Item, Integer> before, Inventory inventory, String recipeId) {
        var expected = new java.util.IdentityHashMap<Item, Integer>();
        for (var cost : recipe.materials()) expected.merge(cost.item().value(), cost.count(), Integer::sum);
        var now = snapshot(inventory);
        for (var entry : expected.entrySet()) helper.assertTrue(before.getOrDefault(entry.getKey(), 0) - now.getOrDefault(entry.getKey(), 0) == entry.getValue(), "Assembler material debit must be exact for " + recipeId);
    }

    public static void npcTradeRecipesPayAtomically(GameTestHelper helper) {
        var player = player(helper);
        try {
            var recipes = RecipeContent.NPC_TRADE.all(helper.getLevel());
            helper.assertTrue(!recipes.isEmpty(), "NPC trade data recipes must be loaded");
            player.giveExperienceLevels(1000);
            for (var holder : recipes) {
                NpcTradeRecipe recipe = holder.value();
                if (recipe.requires().isEmpty()) helper.assertTrue(recipe.allowed(player), "Unrestricted NPC trade must be allowed: " + holder.id());
                else helper.assertTrue(!recipe.allowed(player), "Fresh player must not pass gated NPC trade: " + holder.id());
                int experienceBefore = player.experienceLevel;
                if (!recipe.costs().isEmpty()) {
                    player.getInventory().clearContent();
                    addTradeCosts(player.getInventory(), recipe, false);
                    var rejectedBefore = snapshot(player.getInventory());
                    helper.assertTrue(!recipe.trade(player), "Missing one required item must reject NPC trade: " + holder.id());
                    assertInventoryUnchanged(helper, rejectedBefore, player.getInventory(), holder.id().toString());
                    helper.assertTrue(player.experienceLevel == experienceBefore, "Rejected trade must preserve levels: " + holder.id());
                }
                player.getInventory().clearContent();
                addTradeCosts(player.getInventory(), recipe, true);
                var before = snapshot(player.getInventory());
                helper.assertTrue(recipe.trade(player), "Complete payment must execute NPC trade: " + holder.id());
                var expectedDebit = new java.util.IdentityHashMap<Item, Integer>();
                for (var cost : recipe.costs()) expectedDebit.merge(cost.item().value(), cost.count(), Integer::sum);
                for (var entry : expectedDebit.entrySet()) {
                    int produced = recipe.result().item().value() == entry.getKey() ? recipe.result().count() : 0;
                    int actualDelta = before.getOrDefault(entry.getKey(), 0) - snapshot(player.getInventory()).getOrDefault(entry.getKey(), 0);
                    helper.assertTrue(actualDelta == entry.getValue() - produced, "NPC trade payment must be exact for " + holder.id() + " item=" + entry.getKey());
                }
                int resultBefore = before.getOrDefault(recipe.result().item().value(), 0);
                int resultAfter = snapshot(player.getInventory()).getOrDefault(recipe.result().item().value(), 0);
                int paidResult = expectedDebit.getOrDefault(recipe.result().item().value(), 0);
                helper.assertTrue(resultAfter == resultBefore - paidResult + recipe.result().count(), "NPC trade must grant exact result for " + holder.id());
                helper.assertTrue(player.experienceLevel == experienceBefore - recipe.levels(), "NPC trade must debit exact levels: " + holder.id());
            }
            var lockedRecipe = RecipeContent.NPC_TRADE.get(helper.getLevel(), NpcTradeRecipe.id(ConciergePage.PANACEA.trade));
            helper.assertTrue(lockedRecipe != null && lockedRecipe.requires().isPresent(), "Concierge panacea trade must have an advancement gate");
            helper.assertTrue(!lockedRecipe.allowed(player), "Fresh player must fail the panacea trade advancement gate");
            player.getInventory().clearContent();
            addTradeCosts(player.getInventory(), lockedRecipe, true);
            var lockedBefore = snapshot(player.getInventory());
            int lockedLevels = player.experienceLevel;
            var npc = helper.spawn(ConciergeContent.CONCIERGE.get(), MACHINE);
            helper.assertTrue(ConciergeContent.open(player, npc, helper.absolutePos(MACHINE), ConciergePage.PANACEA), "Concierge panacea page must open for menu gate test");
            helper.assertTrue(!player.containerMenu.clickMenuButton(player, 0), "Concierge menu must reject a locked panacea trade");
            assertInventoryUnchanged(helper, lockedBefore, player.getInventory(), "conc_trade_panacea gated menu");
            helper.assertTrue(player.experienceLevel == lockedLevels, "Locked Concierge trade must preserve player levels");
            ThermalShockReforged.LOGGER.info("Server regression recipe traversal: NPC trade recipes tested={}", recipes.size());
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    private static void addTradeCosts(Inventory inventory, NpcTradeRecipe recipe, boolean complete) {
        int finalCost = recipe.costs().size() - 1;
        for (int index = 0; index < recipe.costs().size(); index++) {
            var cost = recipe.costs().get(index);
            int count = cost.count() - (!complete && index == finalCost ? 1 : 0);
            if (count > 0) inventory.add(new ItemStack(cost.item().value(), count));
        }
    }

    private static Map<Item, Integer> snapshot(Inventory inventory) {
        var result = new java.util.IdentityHashMap<Item, Integer>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            var stack = inventory.getItem(i);
            if (!stack.isEmpty()) result.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }
        return result;
    }

    private static void assertInventoryUnchanged(GameTestHelper helper, Map<Item, Integer> before, Inventory inventory, String recipeId) {
        helper.assertTrue(before.equals(snapshot(inventory)), "Rejected manufacture must not debit materials for " + recipeId);
    }

    private static void assertMaterialsConsumed(GameTestHelper helper, FabricationRecipe recipe, Map<Item, Integer> before, Inventory inventory, String recipeId) {
        var expected = new java.util.IdentityHashMap<Item, Integer>();
        for (var cost : recipe.materials()) expected.merge(cost.item().value(), cost.count(), Integer::sum);
        var nowByItem = new java.util.IdentityHashMap<Item, Integer>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (!stack.isEmpty()) nowByItem.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }
        for (var entry : expected.entrySet()) {
            helper.assertTrue(before.getOrDefault(entry.getKey(), 0) - nowByItem.getOrDefault(entry.getKey(), 0) == entry.getValue(), "Material debit must be exact for " + recipeId + " item=" + entry.getKey());
        }
    }
}
