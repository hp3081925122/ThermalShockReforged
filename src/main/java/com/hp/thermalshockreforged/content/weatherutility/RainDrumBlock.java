package com.hp.thermalshockreforged.content.weatherutility;

import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class RainDrumBlock extends Block {
    public RainDrumBlock() { super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.METAL).strength(.5F, 12).requiresCorrectToolForDrops()); }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return true; }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, net.minecraft.world.level.redstone.Orientation fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, neighbor, fromPos, moving);
        if (level.getBestNeighborSignal(pos) > 0) rainNote(level, pos);
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) { super.animateTick(state, level, pos, random); rainNote(level, pos); }
    private void rainNote(Level level, BlockPos pos) {
        if (!level.isRaining() || !level.isEmptyBlock(pos.above())) return;
        boolean angry = level.getBlockState(pos.below()).is(NativeDecorContent.THERMITE.get());
        var sound = BuiltInRegistries.SOUND_EVENT.getValue(WeatherUtilityContent.id(angry ? "angry_rain_drum_random" : "rain_drum_random"));
        if (level.isClientSide()) level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), sound, SoundSource.BLOCKS, 1, 1, false);
        else level.playSound(null, pos, sound, SoundSource.BLOCKS, 1, 1);
        level.addParticle(ParticleTypes.NOTE, pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5, 0, 1, 0);
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (entity instanceof ServerPlayer player && level.getBlockState(pos.below()).is(NativeDecorContent.THERMITE.get())) WeatherUtilityContent.award(player, "rain_drum_get");
    }
}
