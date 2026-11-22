package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.content.fabrication.*;
import com.hp.thermalshockreforged.content.machines.MachineContent;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStackTemplate;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.connection.ConnectionType;

public final class FabricatorServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_fabricator_server_behavior_tests_all_thirty_two_recipes_decode_and_craft_through_their_real_menus", FabricatorServerBehaviorTests::allThirtyTwoRecipesDecodeAndCraftThroughTheirRealMenus, 200, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_removal_plan_aggregates_split_stacks_without_mutating_inputs", FabricatorServerBehaviorTests::removalPlanAggregatesSplitStacksWithoutMutatingInputs, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_missing_materials_and_wrong_energy_reject_without_any_cost", FabricatorServerBehaviorTests::missingMaterialsAndWrongEnergyRejectWithoutAnyCost, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_compatible_output_stacks_but_full_or_wrong_output_is_transactional", FabricatorServerBehaviorTests::compatibleOutputStacksButFullOrWrongOutputIsTransactional, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_component_mismatch_blocks_output_but_materials_match_by_item_id", FabricatorServerBehaviorTests::componentMismatchBlocksOutputButMaterialsMatchByItemId, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_menu_rejects_foreign_owner_detached_menu_and_invalid_buttons", FabricatorServerBehaviorTests::menuRejectsForeignOwnerDetachedMenuAndInvalidButtons, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_distance_eight_boundary_and_replacement_identity_are_enforced", FabricatorServerBehaviorTests::distanceEightBoundaryAndReplacementIdentityAreEnforced, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_quick_move_transfers_energy_and_output_through_the_real_block_entity", FabricatorServerBehaviorTests::quickMoveTransfersEnergyAndOutputThroughTheRealBlockEntity, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_all_nine_inventory_slots_survive_block_entity_nbt_restoration", FabricatorServerBehaviorTests::allNineInventorySlotsSurviveBlockEntityNbtRestoration, 100, "supernova_server");
        TsrGameTests.add("history_fabricator_server_behavior_tests_duplicate_material_costs_are_aggregated_and_energy_is_not_double_counted", FabricatorServerBehaviorTests::duplicateMaterialCostsAreAggregatedAndEnergyIsNotDoubleCounted, 100, "supernova_server");
    }

    public static void allThirtyTwoRecipesDecodeAndCraftThroughTheirRealMenus(GameTestHelper helper) {
        var level=helper.getLevel();
        var expectedPages=List.of(FabricatorPage.GHOSTGLASS_PICK,FabricatorPage.FERROUS_ALTAR,FabricatorPage.TRANSMUTATOR,FabricatorPage.THINGAMABOB,FabricatorPage.STRATOSILVER_DRILL,FabricatorPage.STRATOSILVER_CHAINSAW,FabricatorPage.STRATOSILVER_AUGER,FabricatorPage.EXTRACTOR,FabricatorPage.FLINTSTEEL_RIFLE,FabricatorPage.VOLCANO,FabricatorPage.HAILFALL,FabricatorPage.MAGNETIC_CROSSBOW,FabricatorPage.TITANIUM_MACE,FabricatorPage.COBALT_THERMOBLASTER,FabricatorPage.AMALJELLY_CANISTER,FabricatorPage.SHOCK_STICK,FabricatorPage.KNIGHTS_EDGE,FabricatorPage.HI_VOLT_LANCE,FabricatorPage.BUZZSAW,FabricatorPage.LITEPRISM_DISK,FabricatorPage.LITEPRISM_LASER,FabricatorPage.SENTRY_HELMET,FabricatorPage.SENTRY_CHESTPLATE,FabricatorPage.DEV_SCARF_CHESTPLATE,FabricatorPage.CORRIAL_DIVING_HELMET_HELMET,FabricatorPage.PROPULSION_BOOTS_BOOTS,FabricatorPage.GLASTREKKER_ARMOR,FabricatorPage.STRANGESILK_ARMOUR_HELMET,FabricatorPage.STRANGESILK_ARMOUR_CHESTPLATE,FabricatorPage.SILENCED_ARMOR_HELMET,FabricatorPage.SILENCED_ARMOR_CHESTPLATE,FabricatorPage.LITEPRISM_SWORD,FabricatorPage.STRATOSILVER_SICKLE);
        var pages=java.util.Arrays.stream(FabricatorPage.values()).filter(page -> !page.isGuide()).toList();
        helper.assertTrue(pages.size()==expectedPages.size()&&pages.containsAll(expectedPages),"All 32 historical fabrication pages, the extractor mapping, and the added Stratosilver Sickle page must remain available");
        var fixture=fixture(helper,FabricatorPage.TRANSMUTATOR);
        int crafted=0;
        for(var page:pages) {
            var holder=level.getServer().getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, page.recipe)).orElseThrow();
            helper.assertTrue(holder.value() instanceof FabricationRecipe,"Every crafting page must reference a decoded fabrication recipe");
            var recipe=(FabricationRecipe)holder.value();
            var encoded=FabricationContent.SERIALIZER.get().codec().codec().encodeStart(level.registryAccess().createSerializationContext(JsonOps.INSTANCE),recipe).getOrThrow().getAsJsonObject();
            fixture.player.getInventory().clearContent();
            fixture.machine.clearContent();
            int slot=0;
            for(var entry:encoded.getAsJsonArray("materials")) {
                var cost=entry.getAsJsonObject();
                fixture.player.getInventory().setItem(slot++,costStack(cost));
            }
            fixture.machine.setItem(1,costStack(encoded.getAsJsonObject("energy")));
            var menu=menu(fixture.player,fixture.pos,page);
            helper.assertTrue(menu.clickMenuButton(fixture.player,0),"Each real recipe page must craft using its full exact material and energy costs: "+page);
            helper.assertTrue(ItemStack.matches(fixture.machine.getItem(0),recipe.result()),"Crafted output and components must match the decoded recipe: "+page);
            helper.assertTrue(fixture.player.getInventory().getNonEquipmentItems().stream().allMatch(ItemStack::isEmpty)&&fixture.machine.getItem(1).isEmpty(),"Exact recipe inputs must be consumed without leftovers: "+page);
            helper.assertTrue(!menu.clickMenuButton(fixture.player,0),"An empty repeated craft must be rejected: "+page);
            crafted++;
        }
        helper.assertTrue(crafted==expectedPages.size(),"Every historical and added fabrication page must be crafted, not only decoded");
        helper.succeed();
    }

    public static void removalPlanAggregatesSplitStacksWithoutMutatingInputs(GameTestHelper helper) {
        var recipe=recipe(helper);
        var stacks=new ArrayList<>(List.of(new ItemStack(Items.CRYING_OBSIDIAN,2),new ItemStack(Items.CRYING_OBSIDIAN,4),new ItemStack(item("helion_slag"),7),new ItemStack(item("helion_slag"),15),new ItemStack(item("zenith_smithing_template")),new ItemStack(item("cauldron_of_trilobite"))));
        var energy=new ItemStack(item("cryonic_energy"),2);
        var input=new FabricationRecipe.Input(stacks,energy);
        var plan=recipe.removalPlan(input);
        helper.assertTrue(plan!=null&&java.util.Arrays.equals(plan,new int[]{2,3,7,13,1,1}),"Split material stacks must produce the exact per-slot removal plan");
        helper.assertTrue(stacks.get(1).getCount()==4&&energy.getCount()==2,"Planning must leave all input stacks unchanged");
        helper.assertTrue(recipe.matches(input,helper.getLevel())&&recipe.energyCount()==1,"The valid split input must match and use one energy");
        helper.succeed();
    }

    public static void missingMaterialsAndWrongEnergyRejectWithoutAnyCost(GameTestHelper helper) {
        var f=fixture(helper,FabricatorPage.TRANSMUTATOR);
        supply(f,1);
        f.player.getInventory().setItem(0,new ItemStack(Items.CRYING_OBSIDIAN,4));
        helper.assertTrue(!f.menu.clickMenuButton(f.player,0)&&f.machine.getItem(1).getCount()==1&&f.machine.getItem(0).isEmpty()&&f.player.getInventory().getItem(0).getCount()==4,"Missing one material must not partially consume energy or inventory");
        supply(f,1);
        f.machine.setItem(1,new ItemStack(Items.DIAMOND));
        helper.assertTrue(!f.menu.clickMenuButton(f.player,0)&&f.player.getInventory().getItem(0).getCount()==5&&f.machine.getItem(1).is(Items.DIAMOND)&&f.machine.getItem(0).isEmpty(),"Wrong energy must preserve materials and output");
        helper.succeed();
    }

    public static void compatibleOutputStacksButFullOrWrongOutputIsTransactional(GameTestHelper helper) {
        var f=fixture(helper,FabricatorPage.TRANSMUTATOR);
        supply(f,1);
        f.machine.setItem(0,new ItemStack(MachineContent.TRANSMUTATOR.get(),2));
        helper.assertTrue(f.menu.clickMenuButton(f.player,0)&&f.machine.getItem(0).getCount()==3,"Compatible output must stack with the crafted result");
        for(var previous:List.of(new ItemStack(Items.STONE),new ItemStack(MachineContent.TRANSMUTATOR.get(),64))) {
            supply(f,1);
            f.machine.setItem(0,previous.copy());
            helper.assertTrue(!f.menu.clickMenuButton(f.player,0)&&ItemStack.matches(f.machine.getItem(0),previous)&&f.machine.getItem(1).getCount()==1&&f.player.getInventory().getItem(0).getCount()==5,"Wrong or full output must reject before any costs are consumed");
        }
        helper.succeed();
    }

    public static void componentMismatchBlocksOutputButMaterialsMatchByItemId(GameTestHelper helper) {
        var f=fixture(helper,FabricatorPage.TRANSMUTATOR);
        supply(f,1);
        f.player.getInventory().getItem(0).set(DataComponents.CUSTOM_NAME,Component.literal("Named material"));
        var custom=new ItemStack(MachineContent.TRANSMUTATOR.get());
        custom.set(DataComponents.CUSTOM_NAME,Component.literal("Named output"));
        f.machine.setItem(0,custom);
        helper.assertTrue(!f.menu.clickMenuButton(f.player,0)&&f.player.getInventory().getItem(0).getCount()==5,"Output components must prevent merging and preserve the named material");
        f.machine.setItem(0,ItemStack.EMPTY);
        helper.assertTrue(f.menu.clickMenuButton(f.player,0)&&!f.machine.getItem(0).has(DataComponents.CUSTOM_NAME),"Named materials match by item ID and must not transfer components to output");
        var result=recipe(helper).result();
        result.setCount(17);
        helper.assertTrue(recipe(helper).result().getCount()==1,"Recipe result access must return an independent copy");
        helper.succeed();
    }

    public static void menuRejectsForeignOwnerDetachedMenuAndInvalidButtons(GameTestHelper helper) {
        var f=fixture(helper,FabricatorPage.TRANSMUTATOR);
        supply(f,1);
        var foreign=player(helper.getLevel(),f.pos);
        foreign.containerMenu=f.menu;
        helper.assertTrue(!f.menu.stillValid(foreign)&&!f.menu.clickMenuButton(foreign,0),"A foreign player must not use another player's menu");
        helper.assertTrue(!f.menu.clickMenuButton(f.player,-1)&&!f.menu.clickMenuButton(f.player,1),"Unknown craft buttons must be rejected");
        f.player.containerMenu=f.player.inventoryMenu;
        helper.assertTrue(!f.menu.clickMenuButton(f.player,0)&&f.machine.getItem(0).isEmpty()&&f.machine.getItem(1).getCount()==1,"A detached menu must not craft");
        helper.succeed();
    }

    public static void distanceEightBoundaryAndReplacementIdentityAreEnforced(GameTestHelper helper) {
        var f=fixture(helper,FabricatorPage.TRANSMUTATOR);
        f.player.setPos(f.pos.getCenter().add(8,0,0));
        helper.assertTrue(f.menu.stillValid(f.player),"Exactly eight blocks from the center must remain valid");
        f.player.setPos(f.pos.getCenter().add(8.01,0,0));
        helper.assertTrue(!f.menu.stillValid(f.player),"Beyond eight blocks the menu must become invalid");
        f.player.setPos(f.pos.getCenter());
        helper.getLevel().setBlock(f.pos,Blocks.AIR.defaultBlockState(),3);
        helper.getLevel().setBlock(f.pos,GeoDecorContent.block(GeoDecorKind.FABRICATOR).defaultBlockState(),3);
        helper.assertTrue(!f.menu.stillValid(f.player),"A replacement machine at the same position must not inherit the old menu authorization");
        helper.succeed();
    }

    public static void quickMoveTransfersEnergyAndOutputThroughTheRealBlockEntity(GameTestHelper helper) {
        var f=fixture(helper,FabricatorPage.TRANSMUTATOR);
        f.player.getInventory().setItem(9,new ItemStack(item("cryonic_energy"),3));
        helper.assertTrue(!f.menu.quickMoveStack(f.player,2).isEmpty()&&f.machine.getItem(1).getCount()==3&&f.player.getInventory().getItem(9).isEmpty(),"Shift-clicked inventory input must enter the real energy slot");
        f.machine.setItem(0,new ItemStack(MachineContent.TRANSMUTATOR.get(),2));
        helper.assertTrue(!f.menu.quickMoveStack(f.player,0).isEmpty()&&f.machine.getItem(0).isEmpty(),"Shift-clicked output must leave the real BE and enter player inventory");
        helper.assertTrue(!f.menu.getSlot(0).mayPlace(new ItemStack(Items.STONE))&&f.menu.getSlot(1).mayPlace(new ItemStack(Items.STONE)),"Output insertion must be blocked while the original energy slot retains unrestricted item placement");
        helper.assertTrue(f.menu.quickMoveStack(f.player,-1).isEmpty()&&f.menu.quickMoveStack(f.player,1000).isEmpty(),"Invalid shift-click indices must not access slots");
        helper.succeed();
    }

    public static void allNineInventorySlotsSurviveBlockEntityNbtRestoration(GameTestHelper helper) {
        var f=fixture(helper,FabricatorPage.TRANSMUTATOR);
        for(int i=0;i<9;i++) f.machine.setItem(i,new ItemStack(Items.DIAMOND,i+1));
        var saved=f.machine.saveWithoutMetadata(helper.getLevel().registryAccess());
        var restored=new GeoDecorBlockEntity(GeoDecorKind.FABRICATOR,f.pos,helper.getLevel().getBlockState(f.pos));
        restored.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),saved));
        for(int i=0;i<9;i++) helper.assertTrue(restored.getItem(i).is(Items.DIAMOND)&&restored.getItem(i).getCount()==i+1,"Every BE inventory slot must survive NBT restoration: "+i);
        helper.succeed();
    }

    public static void duplicateMaterialCostsAreAggregatedAndEnergyIsNotDoubleCounted(GameTestHelper helper) {
        var diamond=BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND);
        var recipe=new FabricationRecipe(List.of(new FabricationRecipe.Cost(diamond,2),new FabricationRecipe.Cost(diamond,3)),new FabricationRecipe.Cost(diamond,1),new ItemStackTemplate(Items.EMERALD));
        var insufficient=new FabricationRecipe.Input(List.of(new ItemStack(Items.DIAMOND,4)),new ItemStack(Items.DIAMOND));
        helper.assertTrue(recipe.removalPlan(insufficient)==null,"Duplicate material entries must require their sum and not borrow the energy stack");
        var sufficient=new FabricationRecipe.Input(List.of(new ItemStack(Items.DIAMOND,2),new ItemStack(Items.DIAMOND,3)),new ItemStack(Items.DIAMOND));
        helper.assertTrue(java.util.Arrays.equals(recipe.removalPlan(sufficient),new int[]{2,3}),"Duplicate costs must consume each material stack once");
        helper.succeed();
    }

    private static FabricationRecipe recipe(GameTestHelper helper) { return (FabricationRecipe)helper.getLevel().getServer().getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, FabricatorPage.TRANSMUTATOR.recipe)).orElseThrow().value(); }
    private static Item item(String id) { return BuiltInRegistries.ITEM.getOptional(MachineContent.id(id)).orElseThrow(); }
    private static ItemStack costStack(JsonObject cost) { return new ItemStack(BuiltInRegistries.ITEM.getOptional(Identifier.parse(cost.get("item").getAsString())).orElseThrow(),cost.get("count").getAsInt()); }
    private static FakePlayer player(ServerLevel level,BlockPos pos) {
        var p=new FakePlayer(level,new GameProfile(UUID.randomUUID(),"fabricator-test"));
        p.setPos(pos.getCenter());
        return p;
    }
    private static FabricatorMenu menu(FakePlayer player,BlockPos pos,FabricatorPage page) {
        var buffer=new RegistryFriendlyByteBuf(Unpooled.buffer(),player.registryAccess(),ConnectionType.NEOFORGE);
        try {
            buffer.writeBlockPos(pos);
            buffer.writeVarInt(page.ordinal());
            var menu=new FabricatorMenu(1,player.getInventory(),buffer);
            player.containerMenu=menu;
            return menu;
        } finally { buffer.release(); }
    }
    private static Fixture fixture(GameTestHelper helper,FabricatorPage page) {
        var pos=helper.absolutePos(new BlockPos(5,2,8));
        helper.getLevel().setBlock(pos,GeoDecorContent.block(GeoDecorKind.FABRICATOR).defaultBlockState(),3);
        var player=player(helper.getLevel(),pos);
        return new Fixture(pos,player,(GeoDecorBlockEntity)helper.getLevel().getBlockEntity(pos),menu(player,pos,page));
    }
    private static void supply(Fixture f,int count) {
        f.player.getInventory().clearContent();
        f.machine.clearContent();
        f.player.getInventory().setItem(0,new ItemStack(Items.CRYING_OBSIDIAN,5*count));
        f.player.getInventory().setItem(1,new ItemStack(item("helion_slag"),20*count));
        f.player.getInventory().setItem(2,new ItemStack(item("zenith_smithing_template"),count));
        f.player.getInventory().setItem(3,new ItemStack(item("cauldron_of_trilobite"),count));
        f.machine.setItem(1,new ItemStack(item("cryonic_energy"),count));
    }
    private record Fixture(BlockPos pos,FakePlayer player,GeoDecorBlockEntity machine,FabricatorMenu menu) { }
    private FabricatorServerBehaviorTests() { }
}
