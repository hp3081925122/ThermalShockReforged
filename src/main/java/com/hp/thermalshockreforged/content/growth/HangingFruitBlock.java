package com.hp.thermalshockreforged.content.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;

public final class HangingFruitBlock extends Block {
    private final boolean lunar;
    public HangingFruitBlock(boolean lunar) { super(properties(lunar)); this.lunar = lunar; }
    private static Properties properties(boolean lunar) {
        var properties = Properties.of().mapColor(lunar ? MapColor.COLOR_LIGHT_BLUE : MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.FUNGUS).strength(.2f, 0).noCollission().noOcclusion().isRedstoneConductor((s,l,p)->false);
        return lunar ? properties.lightLevel(s->7).hasPostProcess((s,l,p)->true).emissiveRendering((s,l,p)->true) : properties;
    }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return box(5, lunar ? 11 : 0, 5, 11, 16, 11); }
    private void checkAbove(Level level, BlockPos pos) {
        if (level.isClientSide || !level.getBlockState(pos.above()).is(Blocks.AIR)) return;
        dropResources(level.getBlockState(pos), level, pos, null);
        level.destroyBlock(pos, false);
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) { super.onPlace(state, level, pos, previous, moving); checkAbove(level, pos); }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos from, boolean moving) { super.neighborChanged(state, level, pos, block, from, moving); checkAbove(level, pos); }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean harvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, harvest, fluid);
        if (level instanceof ServerLevel server && server.random.nextDouble() < .2) {
            var drop = new ItemEntity(level, pos.getX() - .5, pos.getY() - .5, pos.getZ() - .5, new ItemStack(lunar ? GrowthContent.LUNALEMON_SEED.get() : GrowthContent.STARFRUIT_SEED.get()));
            drop.setPickUpDelay(10);
            server.addFreshEntity(drop);
        }
        return result;
    }
}
