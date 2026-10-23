package com.hp.thermalshockreforged.content.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VeinrootBlock extends Block {
    public VeinrootBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_RED).sound(SoundType.WEEPING_VINES).instabreak()
                .noCollision().noOcclusion().isRedstoneConductor((state, level, pos) -> false));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return 0;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, net.minecraft.world.level.redstone.Orientation from, boolean moving) {
        super.neighborChanged(state, level, pos, neighbor, from, moving);
        if (!level.isClientSide() && level.isEmptyBlock(pos.above())) {
            level.destroyBlock(pos, false);
        }
    }
}
