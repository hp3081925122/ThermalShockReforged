package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class StarlightBottleBlock extends GeoDecorBlock {
    public StarlightBottleBlock(GeoDecorKind kind) { super(kind); }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return InteractionResult.SUCCESS; }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(MaterialContent.items().get("drinking_straw").get()) && !stack.is(MaterialContent.items().get("silly_straw").get())) return InteractionResult.SUCCESS;
        if (level.isClientSide()) {
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_DRINK.value(), SoundSource.NEUTRAL, 1, 1, false);
            com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Starlight bottle drink sound played locally: player={}, position={}", player.getUUID(), pos);
        }
        if (!level.isClientSide() && level.getBlockState(pos).is(this)) {
            BlockState empty = GeoDecorContent.block(GeoDecorKind.BIG_GLASS_BOTTLE).defaultBlockState().setValue(ANIMATION, state.getValue(ANIMATION));
            if (level.setBlock(pos, empty, 3)) {
                player.getFoodData().eat(5, .6F);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
