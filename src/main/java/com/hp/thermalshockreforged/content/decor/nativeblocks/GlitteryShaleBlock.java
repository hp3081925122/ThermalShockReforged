package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.hp.thermalshockreforged.world.LoqendiaContent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

final class GlitteryShaleBlock extends NativeDecorBlock {
    GlitteryShaleBlock() {
        super(Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)
                        .sound(SoundType.NETHERRACK).strength(1, 5).lightLevel(state -> 3)
                        .requiresCorrectToolForDrops().randomTicks(),
                new Traits(15, false, false, false, 0, 0, false, false), null,
                "block.thermal_shock_reforged.glittery_shale.description_0");
    }

    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextDouble() < .06) {
            level.addParticle(LoqendiaContent.GOLD_GLITTER.get(), pos.getX() + .5,
                    pos.getY() + 1, pos.getZ() + .5, 0, 0, 0);
        }
    }
}
