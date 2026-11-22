package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class AtlasWaterPlacementFinalTest {
    public static void register() {
        TsrGameTests.add("history_atlas_water_placement_final_test_water_source_sets_atlas_waterlogged_property", AtlasWaterPlacementFinalTest::waterSourceSetsAtlasWaterloggedProperty, 100, "supernova_server");
    }

    public static void waterSourceSetsAtlasWaterloggedProperty(GameTestHelper h) {
        BlockPos local = new BlockPos(3, 2, 3); h.setBlock(local, Blocks.WATER); BlockPos world = h.absolutePos(local); var atlas = GeoDecorContent.block(GeoDecorKind.ATLAS);
        var context = new BlockPlaceContext(h.getLevel(), null, InteractionHand.MAIN_HAND, atlas.asItem().getDefaultInstance(), new BlockHitResult(Vec3.atCenterOf(world), Direction.UP, world, false));
        var placed = atlas.getStateForPlacement(context); h.assertTrue(placed != null && placed.getValue(AtlasBlock.WATERLOGGED), "Waterlogged property must come from the real fluid state at clicked world position"); h.setBlock(local, placed);
        h.assertTrue(h.getBlockState(local).getFluidState().is(net.minecraft.world.level.material.Fluids.WATER) && h.getBlockEntity(local, com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity.class) != null, "Atlas placement must preserve water state and create its Geo BE"); h.succeed();
    }
    private AtlasWaterPlacementFinalTest() { }
}
