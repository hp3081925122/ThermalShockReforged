package com.hp.thermalshockreforged.content.decor.nativeblocks;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;

class IcicleDecorBlock extends NativeDecorBlock {
    IcicleDecorBlock(boolean glitter) {
        super(Properties.of().mapColor(MapColor.ICE).sound(SoundType.GLASS).strength(0.1F, 0).lightLevel(state -> glitter ? 15 : 0)
                .requiresCorrectToolForDrops().noCollision().noOcclusion().isRedstoneConductor((state, level, pos) -> false),
                new Traits(0, true, true, false, 0, 0, false, false), Shapes.block());
    }
    private void checkAttachment(Level level, BlockPos pos) {
        if (!level.isClientSide() && level.getBlockState(pos).is(this) && level.getBlockState(pos.above()).is(Blocks.AIR)) {
            BlockState current = level.getBlockState(pos);
            if (level.destroyBlock(pos, false) && !level.getBlockState(pos).is(this)) Block.dropResources(current, level, pos, null);
        }
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving); checkAttachment(level, pos);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block source, net.minecraft.world.level.redstone.Orientation from, boolean moving) {
        super.neighborChanged(state, level, pos, source, from, moving); checkAttachment(level, pos);
    }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effects, boolean precise) {
        super.entityInside(state, level, pos, entity, effects, precise);
        if (!level.isClientSide() && level.getBlockState(pos).is(this) && level.destroyBlock(pos, false)) entity.hurt(entity.damageSources().source(DamageTypes.STALAGMITE), 1);
    }
    static final class Plain extends IcicleDecorBlock {
        static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 2);
        Plain() { super(false); registerDefaultState(defaultBlockState().setValue(VARIANT, 0)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(VARIANT); }
        @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide()) {
                boolean changed = false;
                if (stack.is(UtilityContent.CHISEL.get())) { level.setBlock(pos, state.cycle(VARIANT), 3); changed = true; }
                else if (stack.is(Items.GOLD_NUGGET) && state.getValue(VARIANT) == 0) { stack.shrink(1); level.setBlock(pos, NativeDecorContent.GLITTERDROP_ICICLE.get().defaultBlockState(), 3); changed = true; }
                if (changed) {
                    level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1, 2);
                    level.levelEvent(2001, pos, Block.getId(MaterialContent.blocks().get("permafrost").get().defaultBlockState()));
                }
            }
            return InteractionResult.SUCCESS;
        }
        @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return InteractionResult.SUCCESS; }
    }
}
