package com.hp.thermalshockreforged.content.decor.nativeblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

final class GlitterLotusBlock extends NativeDecorBlock {
    GlitterLotusBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.FUNGUS).strength(0.2F, 0).lightLevel(state -> 10)
                .noOcclusion().randomTicks().emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false),
                new Traits(0, true, true, false, 0, 0, false, false), Block.box(5, 0, 5, 11, 6, 11));
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        level.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.3, 0);
    }
}
