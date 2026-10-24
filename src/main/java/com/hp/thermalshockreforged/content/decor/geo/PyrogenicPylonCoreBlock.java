package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;

public final class PyrogenicPylonCoreBlock extends GeoDecorBlock {
    public PyrogenicPylonCoreBlock() { super(GeoDecorKind.PYROGENIC_PYLON_CORE); }
    @Override public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, Mob mob) { return PathType.DAMAGING_IN_NEIGHBOR; }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean harvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, toolStack, harvest, fluid);
        if (level.isClientSide()) level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1, 1, false);
        else level.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1, 1);
        if (level instanceof ServerLevel server) server.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 1, .01, .01, .01, 0);
        return result;
    }
}
