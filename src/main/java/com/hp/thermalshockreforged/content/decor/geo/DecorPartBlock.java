package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class DecorPartBlock extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty X = IntegerProperty.create("x", 0, 2);
    public static final IntegerProperty Y = IntegerProperty.create("y", 0, 2);
    public static final IntegerProperty Z = IntegerProperty.create("z", 0, 2);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static boolean cleaning;

    public DecorPartBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().strength(-1, 3600000).noOcclusion().noLootTable().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false).isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false));
        registerDefaultState(defaultBlockState().setValue(X, 1).setValue(Y, 0).setValue(Z, 1).setValue(WATERLOGGED, false));
    }

    public static boolean fits(BlockPos offset) {
        return offset.getX() >= -1 && offset.getX() <= 1 && offset.getY() >= 0 && offset.getY() <= 2 && offset.getZ() >= -1 && offset.getZ() <= 1;
    }

    public BlockState state(BlockPos offset, boolean water) {
        return defaultBlockState().setValue(X, offset.getX() + 1).setValue(Y, offset.getY()).setValue(Z, offset.getZ() + 1).setValue(WATERLOGGED, water);
    }

    public static BlockPos offset(BlockState state) {
        return new BlockPos(state.getValue(X) - 1, state.getValue(Y), state.getValue(Z) - 1);
    }

    public static BlockPos main(BlockGetter level, BlockPos pos, BlockState state) {
        if (!state.is(GeoDecorContent.DECOR_PART.get())) return null;
        var offset = offset(state);
        var main = pos.subtract(offset);
        if (level instanceof LevelReader reader && !reader.hasChunkAt(main)) return null;
        if (level.getBlockState(main).getBlock() instanceof GeoDecorBlock block && block.kind.parts.contains(offset)) return main;
        return null;
    }

    static void clean(Runnable action) {
        boolean previous = cleaning;
        cleaning = true;
        try {
            action.run();
        } finally {
            cleaning = previous;
        }
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(X, Y, Z, WATERLOGGED); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return state.getFluidState().isEmpty(); }
    @Override protected FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) return Fluids.WATER.getSource(false);
        return super.getFluidState(state);
    }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var main = main(level, pos, state);
        if (main == null) return Shapes.empty();
        return level.getBlockState(main).getShape(level, main, context).move(main.getX() - pos.getX(), main.getY() - pos.getY(), main.getZ() - pos.getZ());
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var main = main(level, pos, state);
        if (main == null) return Shapes.empty();
        return level.getBlockState(main).getCollisionShape(level, main, context).move(main.getX() - pos.getX(), main.getY() - pos.getY(), main.getZ() - pos.getZ());
    }

    @Override
    protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, side, neighborPos, neighbor, random);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (!level.isClientSide()) level.scheduleTick(pos, this, 2);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        var main = pos.subtract(offset(state));
        if (!level.hasChunkAt(main) || main(level, pos, state) != null) return;
        ThermalShockReforged.LOGGER.debug("Removing orphan decor part: position={}, expectedMain={}", pos, main);
        level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var main = main(level, pos, state);
        if (main == null) return InteractionResult.PASS;
        return level.getBlockState(main).useItemOn(stack, level, player, hand, hit.withPosition(main));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        var main = main(level, pos, state);
        if (main == null) return InteractionResult.PASS;
        return level.getBlockState(main).useWithoutItem(level, player, hit.withPosition(main));
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        var main = main(level, pos, state);
        if (main != null) level.getBlockState(main).attack(level, main, player);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        var main = main(level, pos, state);
        if (main == null) return 1;
        return level.getBlockState(main).getDestroyProgress(player, level, main);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        var main = main(level, pos, state);
        if (main == null) return false;
        return level.getBlockState(main).canHarvestBlock(level, main, player);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        var main = main(level, pos, state);
        if (main == null || level.isClientSide()) return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
        var target = level.getBlockState(main);
        if (willHarvest && !player.isCreative()) Block.dropResources(target, level, main, level.getBlockEntity(main), player, player.getMainHandItem());
        ThermalShockReforged.LOGGER.debug("Decor part broken by player: part={}, main={}, block={}, harvest={}", pos, main, target.getBlock(), willHarvest);
        boolean destroyed = level.destroyBlock(main, false, player);
        if (level.getBlockState(pos).is(this)) clean(() -> level.setBlock(pos, fluid.createLegacyBlock(), 3));
        return destroyed;
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) { }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moving) {
        if (!cleaning) {
            var main = main(level, pos, state);
            if (main != null) {
                ThermalShockReforged.LOGGER.debug("Decor part removed externally, breaking main block: part={}, main={}", pos, main);
                level.destroyBlock(main, true);
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, moving);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        var main = main(level, pos, state);
        if (main == null) return 0;
        return level.getBlockState(main).getExplosionResistance(level, main, explosion);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        var main = main(level, pos, state);
        if (main == null) return super.getSoundType(state, level, pos, entity);
        return level.getBlockState(main).getSoundType(level, main, entity);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        var main = main(level, pos, state);
        if (main == null) return ItemStack.EMPTY;
        return level.getBlockState(main).getCloneItemStack(main, level, includeData, player);
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState same, LivingEntity entity, int particles) { return true; }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) { return true; }
}
