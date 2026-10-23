package com.hp.thermalshockreforged.content.vilebathblocks;

import com.hp.thermalshockreforged.entity.icesquad.IceSquadContent;
import com.hp.thermalshockreforged.entity.squall.SquallContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

final class FogMachineBlock extends Block {
    FogMachineBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(1, 10)
                .requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false));
    }

    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return true; }

    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.hasNeighborSignal(pos)) return;
        if (level.getBlockState(pos.below()).is(Blocks.COAL_BLOCK)) {
            level.addParticle(SquallContent.SMOKE.get(), pos.getX() + .5, pos.getY(), pos.getZ() + .5, 0, 0, 0);
        } else if (random.nextDouble() < .5) {
            level.addParticle(IceSquadContent.FOG_CLOUD.get(), pos.getX() - 2 + random.nextDouble() * 5,
                    pos.getY(), pos.getZ() - 2 + random.nextDouble() * 5, 0, 0, 0);
        }
    }
}
