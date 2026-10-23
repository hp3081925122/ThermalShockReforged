package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class JellercelBlock extends GeoDecorBlock.Waterlogged {
    public JellercelBlock(GeoDecorKind kind) { super(kind); }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!level.isClientSide()) entity.hurt(level.damageSources().lightningBolt(), 1);
    }
}
