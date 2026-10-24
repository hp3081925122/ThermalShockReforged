package com.hp.thermalshockreforged.entity.spore.block;

import com.hp.thermalshockreforged.entity.spore.SporeContent;
import net.minecraft.core.*;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public final class SporePodBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = box(4, 0.1, 4, 12, 12, 12);
    public SporePodBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.SLIME_BLOCK).strength(0.1F, 0).lightLevel(state -> 10).noOcclusion().pushReaction(PushReaction.DESTROY).emissiveRendering((state, level, pos) -> true).isRedstoneConductor((state, level, pos) -> false).noLootTable());
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false).setValue(ANIMATION, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ANIMATION, WATERLOGGED); }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
    @Override
    protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override
    protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighbor, random);
    }
    @Override
    protected boolean propagatesSkylightDown(BlockState state) { return state.getFluidState().isEmpty(); }
    @Override
    protected int getLightDampening(BlockState state) { return 0; }
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SporePodBlockEntity(pos, state); }
    private SporePodBlockEntity pod(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(this) && level.getBlockEntity(pos) instanceof SporePodBlockEntity pod ? pod : null;
    }
    private void attempt(Level level, BlockPos pos, SporePodBlockEntity pod, boolean removed) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server) || pod == null || !removed && pod(level, pos) != pod || !pod.claimConsumption(server.getRandom())) return;
        if (!removed && (!server.destroyBlock(pos, false) || server.getBlockEntity(pos) == pod)) { pod.releaseConsumption(); return; }
        var spore = SporeContent.BLIGHTED_SPORE.get().spawn(server, BlockPos.containing(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), EntitySpawnReason.MOB_SUMMONED);
        if (spore != null) spore.setDeltaMovement(0, 0.2, 0);
    }
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.item.ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        var pod = pod(level, pos);
        boolean removed = super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
        if (removed && !level.getBlockState(pos).is(this)) attempt(level, pos, pod, true);
        return removed;
    }
    @Override
    public void onBlockExploded(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, Explosion explosion) {
        var pod = pod(level, pos);
        super.onBlockExploded(state, level, pos, explosion);
        if (!level.getBlockState(pos).is(this)) attempt(level, pos, pod, true);
    }
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effects, boolean precise) { super.entityInside(state, level, pos, entity, effects, precise); attempt(level, pos, pod(level, pos), false); }
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) { super.stepOn(level, pos, state, entity); attempt(level, pos, pod(level, pos), false); }
    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) { attempt(level, hit.getBlockPos(), pod(level, hit.getBlockPos()), false); }
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof SporePodBlockEntity pod ? pod : null; }
    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int data) { super.triggerEvent(state, level, pos, id, data); return level.getBlockEntity(pos) instanceof SporePodBlockEntity pod && pod.triggerEvent(id, data); }
}
