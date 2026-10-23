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
        var properties = com.hp.thermalshockreforged.ModRegistration.block().mapColor(lunar ? MapColor.COLOR_LIGHT_BLUE : MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.FUNGUS).strength(.2f, 0).noCollision().noOcclusion().isRedstoneConductor((s,l,p)->false);
        return lunar ? properties.lightLevel(s->7).emissiveRendering((s,l,p)->true) : properties;
    }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return box(5, lunar ? 11 : 0, 5, 11, 16, 11); }
    private void checkAbove(Level level, BlockPos pos) {
        if (level.isClientSide() || !level.getBlockState(pos.above()).is(Blocks.AIR)) return;
        var state = level.getBlockState(pos);
        if (state.is(this) && level.destroyBlock(pos, false) && !level.getBlockState(pos).is(this)) dropResources(state, level, pos, null);
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) { super.onPlace(state, level, pos, previous, moving); checkAbove(level, pos); }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation from, boolean moving) { super.neighborChanged(state, level, pos, block, from, moving); checkAbove(level, pos); }
}
