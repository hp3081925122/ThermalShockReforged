package com.hp.thermalshock.content.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

final class DandelionBlock extends GardenFlowerBlock {
    private final boolean windblown;

    DandelionBlock(boolean windblown, Properties properties) { super(MobEffects.REGENERATION, properties); this.windblown = windblown; }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!windblown && !entity.isShiftKeyDown() && level instanceof ServerLevel server && level.getBlockState(pos).is(this)
                && server.setBlock(pos, SpecialPlantContent.WINDBLOWN_LOQUE_DANDELION.get().defaultBlockState(), 3)) {
            server.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.05);
            server.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 1, 0.6F);
        }
    }

    private void interact(Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel server) || !level.getBlockState(pos).is(this)) return;
        if (windblown) {
            ItemStack meal = player.getMainHandItem();
            if (meal.is(Items.BONE_MEAL) && server.setBlock(pos, SpecialPlantContent.LOQUE_DANDELION.get().defaultBlockState(), 3)) {
                meal.shrink(1);
                server.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1, 1);
            }
        } else if (!player.isShiftKeyDown() && server.setBlock(pos, SpecialPlantContent.WINDBLOWN_LOQUE_DANDELION.get().defaultBlockState(), 3)) {
            server.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        interact(level, pos, player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        interact(level, pos, player);
        return InteractionResult.SUCCESS;
    }
}
