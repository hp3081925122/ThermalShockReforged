package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.hp.thermalshockreforged.world.WorldProgressData;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

public final class AtlasBatch30ServerTests {
    public static void register() {
        TsrGameTests.add("history_atlas_batch30_server_tests_coordinate_defaults_and_finite_persisted_values", AtlasBatch30ServerTests::coordinateDefaultsAndFinitePersistedValues, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_coordinate_malformed_and_non_finite_values_fall_back", AtlasBatch30ServerTests::coordinateMalformedAndNonFiniteValuesFallBack, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_arrival_protection_clamps_and_synchronizes_server_state", AtlasBatch30ServerTests::arrivalProtectionClampsAndSynchronizesServerState, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_arrival_protection_save_and_load_keeps_remaining_ticks", AtlasBatch30ServerTests::arrivalProtectionSaveAndLoadKeepsRemainingTicks, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_arrival_protection_ticks_down_and_ends_at_zero", AtlasBatch30ServerTests::arrivalProtectionTicksDownAndEndsAtZero, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_placement_creates_waterlogged_block_entity_and_inventory", AtlasBatch30ServerTests::atlasPlacementCreatesWaterloggedBlockEntityAndInventory, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_nine_slot_handlers_preserve_items_across_faces", AtlasBatch30ServerTests::atlasNineSlotHandlersPreserveItemsAcrossFaces, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_null_side_handler_is_rejected", AtlasBatch30ServerTests::atlasNullSideHandlerIsRejected, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_placement_animation_ends_on_scheduled_tick", AtlasBatch30ServerTests::atlasPlacementAnimationEndsOnScheduledTick, 130, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_placement_initializes_ore_choices_once", AtlasBatch30ServerTests::atlasPlacementInitializesOreChoicesOnce, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_menu_still_valid_uses_real_block_and_range", AtlasBatch30ServerTests::atlasMenuStillValidUsesRealBlockAndRange, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_menu_rejects_wrong_owner_and_removed_block", AtlasBatch30ServerTests::atlasMenuRejectsWrongOwnerAndRemovedBlock, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_menu_rejects_invalid_buttons_and_wrong_active_menu", AtlasBatch30ServerTests::atlasMenuRejectsInvalidButtonsAndWrongActiveMenu, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_menu_has_zero_slots_and_keeps_position", AtlasBatch30ServerTests::atlasMenuHasZeroSlotsAndKeepsPosition, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_activation_entity_rejects_unarmed_and_invalid_phase_nbt", AtlasBatch30ServerTests::activationEntityRejectsUnarmedAndInvalidPhaseNbt, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_atlas_travel_rejects_invalid_button_without_coordinates", AtlasBatch30ServerTests::atlasTravelRejectsInvalidButtonWithoutCoordinates, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_server_tests_non_atlas_and_empty_menu_slot_interactions_stay_bounded", AtlasBatch30ServerTests::nonAtlasAndEmptyMenuSlotInteractionsStayBounded, 100, "supernova_server");
    }

    public static void coordinateDefaultsAndFinitePersistedValues(GameTestHelper h) {
        Player p = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5);
        h.assertTrue(AtlasCoordinates.get(p, Level.OVERWORLD).equals(Vec3.ZERO), "Overworld fallback must be zero");
        h.assertTrue(AtlasCoordinates.get(p, LoqendiaContent.DIMENSION).equals(new Vec3(324, 73, 324)), "Loqendia fallback must preserve its configured arrival point");
        h.assertTrue(AtlasCoordinates.get(p, AtlasContent.PALACE).equals(new Vec3(0, 194, 0)), "Palace fallback must preserve its configured arrival point");
        release(h, p); h.succeed();
    }
    public static void coordinateMalformedAndNonFiniteValuesFallBack(GameTestHelper h) {
        Player p = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5);
        CompoundTag persisted = new CompoundTag(); CompoundTag mod = new CompoundTag(); CompoundTag all = new CompoundTag(); CompoundTag bad = new CompoundTag();
        bad.putDouble("X", Double.NaN); bad.putDouble("Y", 5); bad.putDouble("Z", 6); all.put("minecraft:overworld", bad); mod.put("AtlasCoordinates", all); persisted.put("thermal_shock_reforged", mod); p.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
        h.assertTrue(AtlasCoordinates.get(p, Level.OVERWORLD).equals(Vec3.ZERO), "A nonfinite coordinate must use the dimension fallback");
        release(h, p); h.succeed();
    }
    public static void arrivalProtectionClampsAndSynchronizesServerState(GameTestHelper h) {
        Player p = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); AtlasArrivalState a = (AtlasArrivalState)p;
        a.beginArrivalProtection(999); h.assertTrue(a.arrivalProtectionTicks() == 180 && a.hasArrivalProtectionVisual(), "Protection must clamp to 180 and set its server-visible state");
        a.beginArrivalProtection(-8); h.assertTrue(a.arrivalProtectionTicks() == 0 && !a.hasArrivalProtectionVisual(), "Negative protection must clamp to zero and clear its state");
        release(h, p); h.succeed();
    }
    public static void arrivalProtectionSaveAndLoadKeepsRemainingTicks(GameTestHelper h) {
        Player p = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); AtlasArrivalState a = (AtlasArrivalState)p; a.beginArrivalProtection(73);
        CompoundTag tag = save(p); a.beginArrivalProtection(0); p.load(input(h, tag));
        h.assertTrue(a.arrivalProtectionTicks() == 73, "NBT loading must restore remaining ticks without resetting to 180");
        release(h, p); h.succeed();
    }
    public static void arrivalProtectionTicksDownAndEndsAtZero(GameTestHelper h) {
        Player p = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); AtlasArrivalState a = (AtlasArrivalState)p; a.beginArrivalProtection(2); a.tickArrivalProtection();
        h.assertTrue(a.arrivalProtectionTicks() == 1, "One native protection tick must decrement the remaining count"); a.tickArrivalProtection();
        h.assertTrue(a.arrivalProtectionTicks() == 0 && !a.hasArrivalProtectionVisual(), "The terminal tick must clear protection and its synchronized visual state"); release(h, p); h.succeed();
    }
    public static void atlasPlacementCreatesWaterloggedBlockEntityAndInventory(GameTestHelper h) {
        BlockPos p = new BlockPos(3, 2, 3); h.setBlock(p, Blocks.WATER);
        var atlas = GeoDecorContent.block(GeoDecorKind.ATLAS);
        var context = new net.minecraft.world.item.context.BlockPlaceContext(h.getLevel(), null, net.minecraft.world.InteractionHand.MAIN_HAND, atlas.asItem().getDefaultInstance(), new net.minecraft.world.phys.BlockHitResult(Vec3.atCenterOf(h.absolutePos(p)), Direction.UP, h.absolutePos(p), false));
        var placed = atlas.getStateForPlacement(context); h.assertTrue(placed != null && placed.getValue(AtlasBlock.WATERLOGGED), "A real placement context must derive the Atlas waterlogged state"); h.setBlock(p, placed);
        h.assertTrue(h.getBlockState(new BlockPos(3, 2, 3)).getFluidState().is(Fluids.WATER), "Atlas in a water source must retain the real water fluid state");
        h.assertTrue(h.getBlockEntity(new BlockPos(3, 2, 3), com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity.class) != null, "Atlas placement must create its registered Geo BE");
        h.succeed();
    }
    public static void atlasNineSlotHandlersPreserveItemsAcrossFaces(GameTestHelper h) {
        h.setBlock(new BlockPos(3, 2, 3), GeoDecorContent.block(GeoDecorKind.ATLAS));
        var be = h.getBlockEntity(new BlockPos(3, 2, 3), com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity.class); var cap = h.getLevel().getCapability(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, h.absolutePos(new BlockPos(3,2,3)), Direction.NORTH);
        h.assertTrue(be != null && cap != null && cap.size() == 9, "Atlas must expose its actual nine-slot inventory capability");
        int inserted; try (var transaction = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) { inserted = cap.insert(0, net.neoforged.neoforge.transfer.item.ItemResource.of(Items.DIAMOND), 3, transaction); transaction.commit(); }
        h.assertTrue(inserted == 3 && cap.getAmountAsInt(0) == 3, "Side insertion must store all accepted items");
        int extracted; try (var transaction = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) { extracted = cap.extract(0, net.neoforged.neoforge.transfer.item.ItemResource.of(Items.DIAMOND), 2, transaction); transaction.commit(); }
        h.assertTrue(extracted == 2 && cap.getAmountAsInt(0) == 1, "Side extraction must conserve the inventory count");
        h.succeed();
    }
    public static void atlasNullSideHandlerIsRejected(GameTestHelper h) {
        h.setBlock(new BlockPos(3, 2, 3), GeoDecorContent.block(GeoDecorKind.ATLAS));
        var cap = h.getLevel().getCapability(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, h.absolutePos(new BlockPos(3,2,3)), null);
        h.assertTrue(cap == null, "Atlas item handler capability must reject a null side"); h.succeed();
    }
    public static void atlasPlacementAnimationEndsOnScheduledTick(GameTestHelper h) {
        BlockPos p = new BlockPos(3, 2, 3); var atlas = GeoDecorContent.block(GeoDecorKind.ATLAS); h.setBlock(p, atlas);
        h.assertTrue(h.getBlockState(p).getValue(com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock.ANIMATION) == 1, "A fresh Atlas placement must enter its placement animation state");
        h.runAfterDelay(119, () -> h.assertTrue(h.getBlockState(p).getValue(com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock.ANIMATION) == 1, "The animation must remain active before the 120 scheduled ticks"));
        h.runAfterDelay(122, () -> { h.assertTrue(h.getBlockState(p).getValue(com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock.ANIMATION) == 0, "The scheduled block tick must finish the animation"); h.succeed(); });
    }
    public static void atlasPlacementInitializesOreChoicesOnce(GameTestHelper h) {
        BlockPos p = new BlockPos(3, 2, 3); var atlas = GeoDecorContent.block(GeoDecorKind.ATLAS); h.setBlock(p, atlas);
        WorldProgressData data = WorldProgressData.get(h.getLevel()); int first = data.oreTier1(), second = data.oreTier2();
        h.assertTrue((first == 1 || first == 2) && (second == 1 || second == 2), "Actual Atlas placement must initialize both ore choices within their defined range");
        h.setBlock(p, Blocks.AIR); h.setBlock(p, atlas); h.assertTrue(data.oreTier1() == first && data.oreTier2() == second, "Replacing and re-placing Atlas must not reroll world ore choices"); h.succeed();
    }
    public static void atlasMenuStillValidUsesRealBlockAndRange(GameTestHelper h) {
        BlockPos p = new BlockPos(3, 2, 3); h.setBlock(p, GeoDecorContent.block(GeoDecorKind.ATLAS)); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5);
        AtlasMenu menu = new AtlasMenu(41, owner.getInventory(), h.absolutePos(p)); owner.containerMenu = menu;
        h.assertTrue(menu.stillValid(owner), "The owning living player in range of a loaded Atlas must remain valid");
        owner.snapTo(owner.getX() + 20, owner.getY(), owner.getZ()); h.assertTrue(!menu.stillValid(owner), "The menu must invalidate beyond the native container distance"); release(h, owner); h.succeed();
    }
    public static void atlasMenuRejectsWrongOwnerAndRemovedBlock(GameTestHelper h) {
        BlockPos p = new BlockPos(3, 2, 3); h.setBlock(p, GeoDecorContent.block(GeoDecorKind.ATLAS)); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); Player stranger = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5);
        AtlasMenu menu = new AtlasMenu(42, owner.getInventory(), h.absolutePos(p)); owner.containerMenu = menu;
        h.assertTrue(!menu.stillValid(stranger), "A different player must not operate the owner's Atlas menu"); h.setBlock(p, Blocks.AIR);
        h.assertTrue(!menu.stillValid(owner), "Removing the actual Atlas block must invalidate its menu"); release(h, owner); release(h, stranger); h.succeed();
    }
    public static void atlasMenuRejectsInvalidButtonsAndWrongActiveMenu(GameTestHelper h) {
        BlockPos p = new BlockPos(3, 2, 3); h.setBlock(p, GeoDecorContent.block(GeoDecorKind.ATLAS)); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5);
        AtlasMenu menu = new AtlasMenu(43, owner.getInventory(), h.absolutePos(p)); owner.containerMenu = menu;
        h.assertTrue(!menu.clickMenuButton(owner, -1) && !menu.clickMenuButton(owner, 3) && !menu.clickMenuButton(owner, Integer.MAX_VALUE), "Out-of-range travel buttons must be rejected");
        owner.containerMenu = owner.inventoryMenu; h.assertTrue(!menu.clickMenuButton(owner, 0), "A menu button request from a nonactive menu must be rejected"); release(h, owner); h.succeed();
    }
    public static void atlasMenuHasZeroSlotsAndKeepsPosition(GameTestHelper h) {
        BlockPos local = new BlockPos(4, 2, 5); h.setBlock(local, GeoDecorContent.block(GeoDecorKind.ATLAS)); BlockPos p = h.absolutePos(local); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); AtlasMenu menu = new AtlasMenu(44, owner.getInventory(), p);
        h.assertTrue(menu.slots.isEmpty(), "The Atlas travel menu must expose zero inventory slots"); h.assertTrue(menu.stillValid(owner), "A menu opened at a loaded position in range is initially valid"); release(h, owner); h.succeed();
    }
    public static void activationEntityRejectsUnarmedAndInvalidPhaseNbt(GameTestHelper h) {
        AtlasActivationEntity e = AtlasContent.ACTIVATION.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.LOAD); h.assertTrue(e != null, "Atlas activation entity type must create its registered entity");
        CompoundTag tag = new CompoundTag(); tag.putLong("AtlasAnchor", BlockPos.ZERO.asLong()); tag.putString("AtlasDimension", h.getLevel().dimension().identifier().toString()); tag.putInt("AtlasFace", 999); tag.putInt("AtlasRemaining", 999); tag.putInt("AtlasPhase", 99); tag.putBoolean("AtlasArmed", true);
        e.load(input(h, tag)); CompoundTag saved = save(e);
        h.assertTrue(saved.getIntOr("AtlasPhase", -1) == 2 && saved.getIntOr("AtlasFace", -1) == 5 && saved.getIntOr("AtlasRemaining", -1) == 60, "Activation NBT must clamp phase, face, and phase-dependent remaining work"); h.succeed();
    }
    public static void atlasTravelRejectsInvalidButtonWithoutCoordinates(GameTestHelper h) {
        Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); BlockPos origin = h.absolutePos(new BlockPos(4, 2, 4));
        h.assertTrue(!AtlasTravel.travel((net.minecraft.server.level.ServerPlayer)owner, origin, 7), "An unsupported travel destination must fail");
        h.assertTrue(AtlasCoordinates.get(owner, Level.OVERWORLD).equals(Vec3.ZERO), "Rejected travel must not create or change source coordinates"); release(h, owner); h.succeed();
    }
    public static void nonAtlasAndEmptyMenuSlotInteractionsStayBounded(GameTestHelper h) {
        BlockPos p = new BlockPos(5, 2, 5); h.setBlock(p, GeoDecorContent.block(GeoDecorKind.ATLAS)); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); AtlasMenu menu = new AtlasMenu(45, owner.getInventory(), h.absolutePos(p));
        h.assertTrue(menu.quickMoveStack(owner, 0).isEmpty(), "The zero-slot menu must not move an item through quick-move"); h.assertTrue(menu.slots.isEmpty(), "Atlas menu slot count must remain zero after an inventory operation"); release(h, owner); h.succeed();
    }

    private static CompoundTag save(net.minecraft.world.entity.Entity entity) { TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper helper, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag); }
    private static void release(GameTestHelper helper, Player player) { PlayerInteractionTests.release(helper, (net.minecraft.server.level.ServerPlayer)player); }
    private AtlasBatch30ServerTests() { }
}


