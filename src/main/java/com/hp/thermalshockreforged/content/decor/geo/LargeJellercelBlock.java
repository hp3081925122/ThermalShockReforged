package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public final class LargeJellercelBlock extends JellercelBlock {
    public LargeJellercelBlock() { super(GeoDecorKind.LARGE_JELLERCEL); }

    public static BlockPos partPos(BlockPos anchor, int part) { return anchor.offset(part & 1, (part >> 1) & 1, (part >> 2) & 1); }
    private static boolean available(Level level, BlockPos anchor) {
        for (int part = 1; part <= 7; part++) {
            BlockPos pos = partPos(anchor, part);
            if (level.isOutsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos) || !level.hasChunkAt(pos)) return false;
            BlockState state = level.getBlockState(pos);
            if (state.hasBlockEntity() || (!state.canBeReplaced() && !JellercelCollisionBlock.belongsTo(level, pos, state, anchor, part))) return false;
        }
        return true;
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return available(context.getLevel(), context.getClickedPos()) ? super.getStateForPlacement(context) : null;
    }

    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (level.isClientSide || previous.is(this)) return;
        if (!available(level, pos)) {
            ThermalShockReforged.LOGGER.debug("Large Jellercel placement rejected at {}: occupied, unloaded or out-of-bounds part", pos);
            level.destroyBlock(pos, true);
            return;
        }
        for (int part = 1; part <= 7; part++) {
            BlockPos at = partPos(pos, part);
            BlockState current = level.getBlockState(at);
            boolean water = current.getFluidState().getType() == Fluids.WATER;
            level.setBlock(at, GeoDecorContent.JELLERCEL_COLLISION.get().defaultBlockState().setValue(JellercelCollisionBlock.PART, part).setValue(WATERLOGGED, water), 3);
        }
    }

    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!level.isClientSide && !state.is(replacement.getBlock())) {
            for (int part = 1; part <= 7; part++) {
                BlockPos at = partPos(pos, part);
                if (!level.hasChunkAt(at)) continue;
                BlockState current = level.getBlockState(at);
                if (JellercelCollisionBlock.belongsTo(level, at, current, pos, part)) level.setBlock(at, current.getFluidState().createLegacyBlock(), 3);
            }
        }
        super.onRemove(state, level, pos, replacement, moving);
    }
}
