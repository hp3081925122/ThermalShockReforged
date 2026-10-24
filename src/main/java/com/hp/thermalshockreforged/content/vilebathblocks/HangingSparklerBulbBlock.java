package com.hp.thermalshockreforged.content.vilebathblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;

final class HangingSparklerBulbBlock extends Block {
    HangingSparklerBulbBlock() { super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.FUNGUS).strength(.45F, 0).lightLevel(state -> 14).randomTicks().emissiveRendering((state, level, pos) -> true)); }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) { if (random.nextDouble() < .4) level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.NEUTRAL, 1, 1, false); }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.item.ItemStack toolStack, boolean harvest, FluidState fluid) { boolean removed = super.onDestroyedByPlayer(state, level, pos, player, toolStack, harvest, fluid); if (removed) SparklerDetonations.submit(level, pos); return removed; }
    @Override public void wasExploded(net.minecraft.server.level.ServerLevel level, BlockPos pos, Explosion explosion) { super.wasExploded(level, pos, explosion); SparklerDetonations.submit(level, pos); }
    @Override protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) { if (level.getBlockState(hit.getBlockPos()).is(this)) SparklerDetonations.submit(level, hit.getBlockPos()); }
}
