package com.hp.thermalshockreforged.world.loqendiaportal;

import com.hp.thermalshockreforged.world.LoqendiaContent;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;

public final class LoqendiaPortalTravel {
    private static final TicketType<BlockPos> PORTAL_TICKET = TicketType.create("thermal_shock_reforged:loqendia_portal", Vec3i::compareTo, 300);
    private final ServerLevel level;

    private LoqendiaPortalTravel(ServerLevel level) { this.level = level; }

    public static DimensionTransition destination(ServerLevel source, ServerLevel destination, Entity entity, BlockPos entrance) {
        double scale = DimensionType.getTeleportationScale(source.dimensionType(), destination.dimensionType());
        WorldBorder border = destination.getWorldBorder();
        BlockPos search = border.clampToBounds(entity.getX() * scale, entity.getY(), entity.getZ() * scale);
        var travel = new LoqendiaPortalTravel(destination);
        var exit = travel.findPortal(search, border);
        if (exit.isEmpty() && entity instanceof ServerPlayer) {
            Direction.Axis axis = source.getBlockState(entrance).getOptionalValue(LoqendiaPortalBlock.AXIS).orElse(Direction.Axis.X);
            exit = travel.createPortal(search, axis);
        }
        if (exit.isEmpty()) {
            return new DimensionTransition(destination, entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND);
        }
        var entryState = source.getBlockState(entrance);
        Direction.Axis entryAxis = entryState.getOptionalValue(LoqendiaPortalBlock.AXIS).orElse(Direction.Axis.X);
        Vec3 relative = new Vec3(0.5, 0, 0);
        if (entryState.hasProperty(LoqendiaPortalBlock.AXIS)) {
            var entryRectangle = BlockUtil.getLargestRectangleAround(entrance, entryAxis, 21, Direction.Axis.Y, 21,
                    pos -> source.getBlockState(pos) == entryState);
            relative = PortalShape.getRelativePosition(entryRectangle, entryAxis, entity.position(), entity.getDimensions(entity.getPose()));
        }
        var rectangle = exit.get();
        var origin = rectangle.minCorner;
        Direction.Axis exitAxis = destination.getBlockState(origin).getOptionalValue(LoqendiaPortalBlock.AXIS).orElse(Direction.Axis.X);
        var dimensions = entity.getDimensions(entity.getPose());
        double across = dimensions.width() / 2.0 + ((double) rectangle.axis1Size - dimensions.width()) * relative.x();
        double height = ((double) rectangle.axis2Size - dimensions.height()) * relative.y();
        double normal = 0.5 + relative.z();
        boolean xAxis = exitAxis == Direction.Axis.X;
        var point = new Vec3(origin.getX() + (xAxis ? across : normal), origin.getY() + height, origin.getZ() + (xAxis ? normal : across));
        point = PortalShape.findCollisionFreePosition(point, destination, entity, dimensions);
        Vec3 velocity = entity.getDeltaMovement();
        boolean rotate = entryAxis != exitAxis;
        if (rotate) velocity = new Vec3(velocity.z, velocity.y, -velocity.x);
        return new DimensionTransition(destination, point, velocity, entity.getYRot() + (rotate ? 90 : 0), entity.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND);
    }

    private Optional<BlockUtil.FoundRectangle> findPortal(BlockPos search, WorldBorder border) {
        var manager = level.getPoiManager();
        manager.ensureLoadedAndValid(level, search, 128);
        return manager.getInSquare(holder -> holder.is(LoqendiaContent.PORTAL_POI.getKey()), search, 128, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos).filter(border::isWithinBounds)
                .filter(pos -> level.getBlockState(pos).is(LoqendiaContent.PORTAL.get()))
                .min(Comparator.<BlockPos>comparingDouble(pos -> pos.distSqr(search)).thenComparingInt(Vec3i::getY))
                .map(pos -> {
                    level.getChunkSource().addRegionTicket(PORTAL_TICKET, new ChunkPos(pos), 3, pos);
                    var state = level.getBlockState(pos);
                    return BlockUtil.getLargestRectangleAround(pos, state.getValue(LoqendiaPortalBlock.AXIS), 21, Direction.Axis.Y, 21,
                            candidate -> level.getBlockState(candidate) == state);
                });
    }

    private Optional<BlockUtil.FoundRectangle> createPortal(BlockPos target, Direction.Axis axis) {
        Direction along = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        int ceiling = Math.min(level.getMaxBuildHeight(), level.getMinBuildHeight() + level.getLogicalHeight()) - 1;
        WorldBorder border = level.getWorldBorder();
        BlockPos preferred = null;
        BlockPos fallback = null;
        double preferredDistance = -1;
        double fallbackDistance = -1;
        var scratch = target.mutable();
        for (var candidate : BlockPos.spiralAround(target, 16, Direction.EAST, Direction.SOUTH)) {
            int top = Math.min(ceiling, level.getHeight(Heightmap.Types.MOTION_BLOCKING, candidate.getX(), candidate.getZ()));
            if (!border.isWithinBounds(candidate) || !border.isWithinBounds(candidate.move(along, 1))) continue;
            candidate.move(along.getOpposite(), 1);
            for (int y = top; y >= level.getMinBuildHeight(); y--) {
                candidate.setY(y);
                if (!canReplace(candidate)) continue;
                int columnTop = y;
                while (y > level.getMinBuildHeight() && canReplace(candidate.move(Direction.DOWN))) y--;
                int gap = columnTop - y;
                if (y + 4 > ceiling || gap > 0 && gap < 3) continue;
                candidate.setY(y);
                if (!canHostFrame(candidate, scratch, along, 0)) continue;
                double distance = target.distSqr(candidate);
                if (canHostFrame(candidate, scratch, along, -1) && canHostFrame(candidate, scratch, along, 1)
                        && (preferredDistance == -1 || distance < preferredDistance)) {
                    preferredDistance = distance;
                    preferred = candidate.immutable();
                }
                if (preferredDistance == -1 && (fallbackDistance == -1 || distance < fallbackDistance)) {
                    fallbackDistance = distance;
                    fallback = candidate.immutable();
                }
            }
        }
        BlockPos origin = preferred != null ? preferred : fallback;
        if (origin == null) {
            int lowest = Math.max(level.getMinBuildHeight() + 1, 70);
            int highest = ceiling - 9;
            if (highest < lowest) return Optional.empty();
            origin = new BlockPos(target.getX(), Mth.clamp(target.getY(), lowest, highest), target.getZ());
            if (!border.isWithinBounds(origin)) return Optional.empty();
            Direction normal = along.getClockWise();
            for (int side = -1; side < 2; side++) {
                for (int x = 0; x < 2; x++) {
                    for (int y = -1; y < 3; y++) {
                        scratch.setWithOffset(origin, x * along.getStepX() + side * normal.getStepX(), y,
                                x * along.getStepZ() + side * normal.getStepZ());
                        level.setBlockAndUpdate(scratch, y < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        for (int x = -1; x < 3; x++) {
            for (int y = -1; y < 4; y++) {
                if (x == -1 || x == 2 || y == -1 || y == 3) {
                    scratch.setWithOffset(origin, x * along.getStepX(), y, x * along.getStepZ());
                    level.setBlock(scratch, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
        }
        var state = LoqendiaContent.PORTAL.get().defaultBlockState().setValue(LoqendiaPortalBlock.AXIS, axis);
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                scratch.setWithOffset(origin, x * along.getStepX(), y, x * along.getStepZ());
                level.setBlock(scratch, state, 18);
            }
        }
        return Optional.of(new BlockUtil.FoundRectangle(origin.immutable(), 2, 3));
    }

    private boolean canReplace(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() && state.getFluidState().isEmpty();
    }

    private boolean canHostFrame(BlockPos origin, BlockPos.MutableBlockPos scratch, Direction along, int side) {
        Direction normal = along.getClockWise();
        for (int x = -1; x < 3; x++) {
            for (int y = -1; y < 4; y++) {
                scratch.setWithOffset(origin, x * along.getStepX() + side * normal.getStepX(), y,
                        x * along.getStepZ() + side * normal.getStepZ());
                if (y < 0 ? !level.getBlockState(scratch).isSolid() : !canReplace(scratch)) return false;
            }
        }
        return true;
    }
}
