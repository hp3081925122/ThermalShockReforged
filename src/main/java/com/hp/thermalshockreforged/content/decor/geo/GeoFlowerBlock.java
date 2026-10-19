package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.content.growth.GrowthContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GeoFlowerBlock extends GeoDecorBlock {
    public GeoFlowerBlock(GeoDecorKind kind) { super(kind); }

    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moving) {
        super.neighborChanged(state, level, pos, neighbor, from, moving);
        if (level instanceof ServerLevel server && level.isEmptyBlock(pos.below()) && level.getBlockState(pos).is(this)
                && level.destroyBlock(pos, false) && !level.getBlockState(pos).is(this)) {
            ItemEntity drop = new ItemEntity(server, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(GrowthContent.SPIRE_BLOOM_RUBICON.get()));
            drop.setPickUpDelay(10);
            server.addFreshEntity(drop);
        }
    }
}
