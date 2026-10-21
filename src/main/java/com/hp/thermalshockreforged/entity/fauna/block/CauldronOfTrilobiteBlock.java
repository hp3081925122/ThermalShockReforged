package com.hp.thermalshockreforged.entity.fauna.block;

import com.hp.thermalshockreforged.entity.fauna.FaunaEntities;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public final class CauldronOfTrilobiteBlock extends Block implements EntityBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = box(0.1, 0.1, 0.1, 15.9, 13, 15.9);

    public CauldronOfTrilobiteBlock() {
        super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.METAL).sound(SoundType.METAL).strength(2, 10).noOcclusion().isRedstoneConductor((s, r, p) -> false));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ANIMATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, ANIMATION); }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }

    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CauldronOfTrilobiteBlockEntity(pos, state); }

    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        if (level.isClientSide || type != FaunaEntities.CAULDRON_ENTITY.get()) return null;
        return (world, pos, blockState, entity) -> {
            if (entity instanceof CauldronOfTrilobiteBlockEntity cauldron) cauldron.animationTick();
        };
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CauldronOfTrilobiteBlockEntity cauldron) Containers.dropContents(level, pos, cauldron);
            super.onRemove(state, level, pos, replacement, moving);
        }
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!held.is(Items.BUCKET)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide) {
            held.shrink(1);
            var milk = new ItemStack(FaunaEntities.item("loque_milk"));
            if (!player.getInventory().add(milk)) player.drop(milk, false);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.MOOSHROOM_MILK, net.minecraft.sounds.SoundSource.NEUTRAL, 1, 1);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
