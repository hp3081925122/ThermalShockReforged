package com.hp.thermalshockreforged.world.loqendiaportal;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class LoqendiaPortalBlock extends Block implements Portal {
    public static final MapCodec<LoqendiaPortalBlock> CODEC = simpleCodec(LoqendiaPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final VoxelShape X_SHAPE = Block.box(0, 0, 6, 16, 16, 10);
    private static final VoxelShape Z_SHAPE = Block.box(6, 0, 0, 10, 16, 16);

    public LoqendiaPortalBlock() {
        this(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_PURPLE).noCollision().randomTicks().pushReaction(PushReaction.BLOCK)
                .strength(-1).sound(SoundType.GLASS).lightLevel(state -> 15).noLootTable());
    }

    public LoqendiaPortalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override public MapCodec<LoqendiaPortalBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(AXIS); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90
                ? state.setValue(AXIS, state.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X) : state;
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) { return ItemStack.EMPTY; }
    @Override protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        boolean perpendicular = side.getAxis().isHorizontal() && side.getAxis() != state.getValue(AXIS);
        return !perpendicular && !neighbor.is(this) && !new LoqendiaPortalShape(level, pos, state.getValue(AXIS)).isComplete()
                ? Blocks.AIR.defaultBlockState() : state;
    }

    public static boolean portalSpawn(Level level, BlockPos pos) {
        if (level.isClientSide()) return false;
        return LoqendiaPortalShape.findEmpty(level, pos, Direction.Axis.X).map(LoqendiaPortalShape::createPortalBlocks).orElse(false);
    }

    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effects, boolean precise) {
        if (!(level instanceof ServerLevel server) || entity.isRemoved() || entity.isPassenger() || entity.isVehicle()) return;
        ServerLevel destination = server.getServer().getLevel(server.dimension() == LoqendiaContent.DIMENSION ? Level.OVERWORLD : LoqendiaContent.DIMENSION);
        if (destination == null || !entity.canTeleport(server, destination)) return;
        if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
            return;
        }
        entity.setPortalCooldown();
        var transition = LoqendiaPortalTravel.destination(server, destination, entity, pos);
        if (entity.changeDimension(transition) != null) {
            ThermalShockReforged.LOGGER.debug("Loqendia portal travel completed: entity={}, from={}, to={}, entrance={}",
                    entity.getUUID(), server.dimension().identifier(), destination.dimension().identifier(), pos);
        }
    }

    @Override public TeleportTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        ServerLevel destination = level.getServer().getLevel(level.dimension() == LoqendiaContent.DIMENSION ? Level.OVERWORLD : LoqendiaContent.DIMENSION);
        return destination == null ? null : LoqendiaPortalTravel.destination(level, destination, entity, pos);
    }

    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 4; i++) {
            double x = (float) pos.getX() + random.nextFloat();
            double y = (float) pos.getY() + random.nextFloat();
            double z = (float) pos.getZ() + random.nextFloat();
            double vx = (random.nextFloat() - 0.5) / 2;
            double vy = (random.nextFloat() - 0.5) / 2;
            double vz = (random.nextFloat() - 0.5) / 2;
            int direction = random.nextInt(4) - 1;
            if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
                x = pos.getX() + 0.5 + 0.25 * direction;
                vx = random.nextFloat() * 2 * direction;
            } else {
                z = pos.getZ() + 0.5 + 0.25 * direction;
                vz = random.nextFloat() * 2 * direction;
            }
            level.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
        }
        if (random.nextInt(110) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS.value(), SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
    }
}
