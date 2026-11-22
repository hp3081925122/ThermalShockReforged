package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;

public final class AtlasBatch30TargetedTests {
    public static void register() {
        TsrGameTests.add("history_atlas_batch30_targeted_tests_player_nbt_restores_remaining_arrival_protection", AtlasBatch30TargetedTests::playerNbtRestoresRemainingArrivalProtection, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_targeted_tests_waterlogged_atlas_uses_placement_context_and_creates_be", AtlasBatch30TargetedTests::waterloggedAtlasUsesPlacementContextAndCreatesBe, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_targeted_tests_atlas_menu_validity_uses_absolute_world_position_and_range", AtlasBatch30TargetedTests::atlasMenuValidityUsesAbsoluteWorldPositionAndRange, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_targeted_tests_atlas_menu_rejects_different_owner_and_missing_block", AtlasBatch30TargetedTests::atlasMenuRejectsDifferentOwnerAndMissingBlock, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_targeted_tests_activation_entity_read_clamps_phase_face_and_remaining", AtlasBatch30TargetedTests::activationEntityReadClampsPhaseFaceAndRemaining, 100, "supernova_server");
        TsrGameTests.add("history_atlas_batch30_targeted_tests_atlas_menu_button_bounds_are_rejected_before_travel_dispatch", AtlasBatch30TargetedTests::atlasMenuButtonBoundsAreRejectedBeforeTravelDispatch, 100, "supernova_server");
    }

    public static void playerNbtRestoresRemainingArrivalProtection(GameTestHelper h) {
        Player source = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); AtlasArrivalState state = (AtlasArrivalState)source; state.beginArrivalProtection(73);
        CompoundTag tag = save(source); state.beginArrivalProtection(0); source.load(input(h, tag));
        h.assertTrue(state.arrivalProtectionTicks() == 73, "A player loaded from NBT must continue the saved remaining ticks"); release(h, source); h.succeed();
    }
    public static void waterloggedAtlasUsesPlacementContextAndCreatesBe(GameTestHelper h) {
        BlockPos p = new BlockPos(3, 2, 3); h.setBlock(p, Blocks.WATER); var atlas = GeoDecorContent.block(GeoDecorKind.ATLAS);
        BlockPos worldPos = h.absolutePos(p); var context = new net.minecraft.world.item.context.BlockPlaceContext(h.getLevel(), null, InteractionHand.MAIN_HAND, atlas.asItem().getDefaultInstance(), new BlockHitResult(Vec3.atCenterOf(worldPos), Direction.UP, worldPos, false));
        var placed = atlas.getStateForPlacement(context); h.assertTrue(placed != null && placed.getValue(AtlasBlock.WATERLOGGED), "Real placement context must derive Atlas WATERLOGGED from the source fluid"); h.setBlock(p, placed);
        h.assertTrue(h.getBlockState(p).getFluidState().is(Fluids.WATER) && h.getBlockEntity(p, com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity.class) != null, "Placed waterlogged Atlas must retain fluid state and its registered block entity"); h.succeed();
    }
    public static void atlasMenuValidityUsesAbsoluteWorldPositionAndRange(GameTestHelper h) {
        BlockPos local = new BlockPos(3, 2, 3); h.setBlock(local, GeoDecorContent.block(GeoDecorKind.ATLAS)); BlockPos world = h.absolutePos(local); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); owner.snapTo(world.getX() + 0.5, world.getY() + 0.5, world.getZ() + 0.5);
        AtlasMenu menu = new AtlasMenu(41, owner.getInventory(), world); owner.containerMenu = menu;
        h.assertTrue(menu.stillValid(owner), "Owner in range of a loaded real Atlas must pass native menu validity"); owner.snapTo(owner.getX() + 20, owner.getY(), owner.getZ());
        h.assertTrue(!menu.stillValid(owner), "Owner beyond native container range must fail validity"); release(h, owner); h.succeed();
    }
    public static void atlasMenuRejectsDifferentOwnerAndMissingBlock(GameTestHelper h) {
        BlockPos local = new BlockPos(3, 2, 3); h.setBlock(local, GeoDecorContent.block(GeoDecorKind.ATLAS)); BlockPos world = h.absolutePos(local); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); owner.snapTo(world.getX() + 0.5, world.getY() + 0.5, world.getZ() + 0.5); Player stranger = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); stranger.snapTo(owner.position());
        AtlasMenu menu = new AtlasMenu(42, owner.getInventory(), world); owner.containerMenu = menu;
        h.assertTrue(!menu.stillValid(stranger), "A different player must fail Atlas menu ownership validation"); h.setBlock(local, Blocks.AIR); h.assertTrue(!menu.stillValid(owner), "Replacing Atlas with air must invalidate its menu"); release(h, owner); release(h, stranger); h.succeed();
    }
    public static void activationEntityReadClampsPhaseFaceAndRemaining(GameTestHelper h) {
        AtlasActivationEntity entity = AtlasContent.ACTIVATION.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.LOAD); h.assertTrue(entity != null, "Registered activation entity type must instantiate");
        CompoundTag tag = new CompoundTag(); tag.putLong("AtlasAnchor", BlockPos.ZERO.asLong()); tag.putString("AtlasDimension", h.getLevel().dimension().identifier().toString()); tag.putInt("AtlasFace", 999); tag.putInt("AtlasRemaining", 999); tag.putInt("AtlasPhase", 99); tag.putBoolean("AtlasArmed", true);
        entity.load(input(h, tag)); CompoundTag saved = save(entity);
        h.assertTrue(saved.getIntOr("AtlasPhase", -1) == 2 && saved.getIntOr("AtlasFace", -1) == 5 && saved.getIntOr("AtlasRemaining", -1) == 60, "Read must clamp phase, face, and phase-dependent remaining work"); h.succeed();
    }
    public static void atlasMenuButtonBoundsAreRejectedBeforeTravelDispatch(GameTestHelper h) {
        BlockPos local = new BlockPos(3, 2, 3); h.setBlock(local, GeoDecorContent.block(GeoDecorKind.ATLAS)); BlockPos world = h.absolutePos(local); Player owner = PlayerInteractionTests.player(h, GameType.SURVIVAL, 3.5, 1, 3.5); owner.snapTo(world.getX() + 0.5, world.getY() + 0.5, world.getZ() + 0.5);
        AtlasMenu menu = new AtlasMenu(43, owner.getInventory(), world); owner.containerMenu = menu;
        h.assertTrue(!menu.clickMenuButton(owner, -1) && !menu.clickMenuButton(owner, 3) && !menu.clickMenuButton(owner, Integer.MAX_VALUE), "Invalid button indices must be rejected before casting or dispatching to travel"); release(h, owner); h.succeed();
    }

    private static CompoundTag save(net.minecraft.world.entity.Entity entity) { TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper helper, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag); }
    private static void release(GameTestHelper helper, Player player) { PlayerInteractionTests.release(helper, (net.minecraft.server.level.ServerPlayer)player); }
    private AtlasBatch30TargetedTests() { }
}
