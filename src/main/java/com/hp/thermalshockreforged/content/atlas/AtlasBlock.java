package com.hp.thermalshockreforged.content.atlas;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.world.WorldProgressData;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

public final class AtlasBlock extends GeoDecorBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public AtlasBlock() { super(GeoDecorKind.ATLAS); registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return state.getFluidState().isEmpty(); }
    @Override protected BlockState updateShape(BlockState state, Direction side, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, side, neighbor, level, pos, neighborPos);
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (previous.is(this) || !(level instanceof ServerLevel server)) return;
        if (server.getServer().isSameThread()) initialize(server, pos);
        else {
            var owner = server.getServer(); var dimension = server.dimension(); BlockPos anchor = pos.immutable();
            owner.execute(() -> { var loaded = owner.getLevel(dimension); if (loaded != null && loaded.hasChunkAt(anchor) && loaded.getBlockState(anchor).is(this)) initialize(loaded, anchor); });
        }
    }
    private void initialize(ServerLevel level, BlockPos pos) {
        WorldProgressData.get(level).initializeOreChoices(level);
        level.setBlock(pos, level.getBlockState(pos).setValue(ANIMATION, 1), 3);
        level.scheduleTick(pos, this, 120);
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { if (state.getValue(ANIMATION) == 1) level.setBlock(pos, state.setValue(ANIMATION, 0), 3); }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) { return new SimpleMenuProvider((id, inventory, player) -> new AtlasMenu(id, inventory, pos), Component.translatable("block.thermal_shock_reforged.atlas")); }
    private void open(Level level, BlockPos pos, Player player) { if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof GeoDecorBlockEntity) server.openMenu(getMenuProvider(level.getBlockState(pos), level, pos), pos); }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { open(level, pos, player); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { open(level, pos, player); return InteractionResult.SUCCESS; }
}
