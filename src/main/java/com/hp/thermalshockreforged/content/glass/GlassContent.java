package com.hp.thermalshockreforged.content.glass;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class GlassContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> GHOSTGLASS_BLOCK = BLOCKS.register("ghostglass_block", () -> new DirectionalGlassBlock(MapColor.COLOR_LIGHT_BLUE));
    public static final DeferredBlock<Block> LITEPRISM_WINDOW = BLOCKS.register("liteprism_window", () -> new DirectionalGlassBlock(MapColor.COLOR_CYAN));

    static {
        ITEMS.registerSimpleBlockItem(GHOSTGLASS_BLOCK);
        ITEMS.registerSimpleBlockItem(LITEPRISM_WINDOW);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    private static final class DirectionalGlassBlock extends Block {
        private static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

        private DirectionalGlassBlock(MapColor color) {
            super(Properties.of().mapColor(color).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.5F, 0)
                    .lightLevel(state -> 2).requiresCorrectToolForDrops().noOcclusion()
                    .emissiveRendering((s, l, p) -> true).isRedstoneConductor((s, l, p) -> false));
            registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
        }

        @Override public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos pos, FluidState fluid) { return true; }
        @Override protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) { return adjacent.is(this) || super.skipRendering(state, adjacent, side); }
        @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }
        @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()); }
        @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
        @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    }
}
