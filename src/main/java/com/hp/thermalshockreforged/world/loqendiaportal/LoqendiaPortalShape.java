package com.hp.thermalshockreforged.world.loqendiaportal;

import com.hp.thermalshockreforged.world.LoqendiaContent;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class LoqendiaPortalShape {
    private final net.minecraft.world.level.LevelReader level;
    private final Direction.Axis axis;
    private final Direction right;
    private final BlockPos bottomLeft;
    private final int width;
    private final int height;
    private int portalBlocks;

    public LoqendiaPortalShape(net.minecraft.world.level.LevelReader level, BlockPos pos, Direction.Axis axis) {
        this.level = level;
        this.axis = axis;
        right = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        int lowest = Math.max(level.getMinY(), pos.getY() - 21);
        while (pos.getY() > lowest && isEmpty(level.getBlockState(pos.below()))) pos = pos.below();
        int leftDistance = distanceToFrame(pos, right.getOpposite()) - 1;
        bottomLeft = leftDistance < 0 ? null : pos.relative(right.getOpposite(), leftDistance);
        int measuredWidth = bottomLeft == null ? 0 : distanceToFrame(bottomLeft, right);
        width = measuredWidth >= 2 && measuredWidth <= 21 ? measuredWidth : 0;
        height = width == 0 ? 0 : measureHeight();
    }

    public static Optional<LoqendiaPortalShape> findEmpty(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        var first = new LoqendiaPortalShape(level, pos, axis);
        if (first.isValid() && first.portalBlocks == 0) return Optional.of(first);
        var second = new LoqendiaPortalShape(level, pos, axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        return second.isValid() && second.portalBlocks == 0 ? Optional.of(second) : Optional.empty();
    }

    private int distanceToFrame(BlockPos start, Direction direction) {
        for (int i = 0; i <= 21; i++) {
            BlockPos pos = start.relative(direction, i);
            BlockState state = level.getBlockState(pos);
            if (!isEmpty(state)) return state.is(Blocks.OBSIDIAN) ? i : 0;
            if (!level.getBlockState(pos.below()).is(Blocks.OBSIDIAN)) return 0;
        }
        return 0;
    }

    private int measureHeight() {
        for (int y = 0; y < 21; y++) {
            if (!level.getBlockState(bottomLeft.above(y).relative(right, -1)).is(Blocks.OBSIDIAN)
                    || !level.getBlockState(bottomLeft.above(y).relative(right, width)).is(Blocks.OBSIDIAN)) return validTop(y);
            for (int x = 0; x < width; x++) {
                var state = level.getBlockState(bottomLeft.above(y).relative(right, x));
                if (!isEmpty(state)) return validTop(y);
                if (state.is(LoqendiaContent.PORTAL.get())) portalBlocks++;
            }
        }
        return validTop(21);
    }

    private int validTop(int y) {
        if (y < 3 || y > 21) return 0;
        for (int x = 0; x < width; x++) {
            if (!level.getBlockState(bottomLeft.above(y).relative(right, x)).is(Blocks.OBSIDIAN)) return 0;
        }
        return y;
    }

    private static boolean isEmpty(BlockState state) { return state.isAir() || state.is(LoqendiaContent.PORTAL.get()); }
    public boolean isValid() { return bottomLeft != null && width >= 2 && width <= 21 && height >= 3 && height <= 21; }
    public boolean isComplete() { return isValid() && portalBlocks == width * height; }

    public boolean createPortalBlocks() {
        if (!isValid() || !(level instanceof LevelAccessor writable)) return false;
        var portal = LoqendiaContent.PORTAL.get().defaultBlockState().setValue(LoqendiaPortalBlock.AXIS, axis);
        for (BlockPos pos : BlockPos.betweenClosed(bottomLeft, bottomLeft.above(height - 1).relative(right, width - 1))) {
            if (!writable.setBlock(pos, portal, 18) && !level.getBlockState(pos).equals(portal)) return false;
        }
        return true;
    }
}
