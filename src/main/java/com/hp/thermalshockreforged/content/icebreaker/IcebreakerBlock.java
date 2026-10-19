package com.hp.thermalshockreforged.content.icebreaker;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class IcebreakerBlock extends GeoDecorBlock.Directional {
    public IcebreakerBlock() { super(GeoDecorKind.ICEBREAKER); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IcebreakerBlockEntity(pos, state);
    }

    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (!previous.is(this) && level instanceof ServerLevel server) activate(server, pos);
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        activate(level, pos);
    }

    private void activate(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(this) || !(level.getBlockEntity(pos) instanceof IcebreakerBlockEntity owner) || owner.activated()) return;
        if (!IcebreakerPulseEntity.submit(level, pos, owner)) {
            level.scheduleTick(pos, this, 20);
            return;
        }
        level.playSound(null, pos, SoundEvents.PLAYER_BREATH, SoundSource.BLOCKS, 1, 1);
    }
}
