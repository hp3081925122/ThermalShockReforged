package com.hp.thermalshockreforged.content.doors;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public class SpecialDoorBlock extends Block implements EntityBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public final DoorKind kind;

    SpecialDoorBlock(DoorKind kind) {
        super(kind.properties());
        this.kind = kind;
        registerDefaultState(defaultBlockState().setValue(BLOCKSTATE, 0).setValue(ANIMATION, 0).setValue(FACING, Direction.NORTH));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BLOCKSTATE, ANIMATION, FACING); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SpecialDoorBlockEntity(kind, pos, state); }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return kind == DoorKind.MAW ? kind.cellShape(state.getValue(FACING), state.getValue(BLOCKSTATE) == 1, BlockPos.ZERO) : kind.shape(state.getValue(FACING), state.getValue(BLOCKSTATE) == 1);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (kind == DoorKind.MAW && !level.isClientSide() && !previous.is(this)) {
            for (BlockPos offset : MawDoorPartBlock.offsets(state.getValue(FACING))) {
                if (offset.equals(BlockPos.ZERO)) continue;
                BlockPos part = pos.offset(offset);
                if (!level.getBlockState(part).canBeReplaced()) {
                    level.destroyBlock(pos, true);
                    return;
                }
            }
            for (BlockPos offset : MawDoorPartBlock.offsets(state.getValue(FACING))) {
                if (!offset.equals(BlockPos.ZERO)) level.setBlock(pos.offset(offset), SpecialDoorsContent.mawPart().state(offset), 3);
            }
        }
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof SpecialDoorBlockEntity entity ? entity : null; }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return level.getBlockEntity(pos) instanceof SpecialDoorBlockEntity entity && entity.triggerEvent(id, param);
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moving) {
        {
            if (kind == DoorKind.MAW) {
                MawDoorPartBlock.clean(() -> {
                    for (BlockPos offset : MawDoorPartBlock.offsets(state.getValue(FACING))) {
                        if (offset.equals(BlockPos.ZERO)) continue;
                        BlockPos part = pos.offset(offset);
                        if (level.getBlockState(part).is(SpecialDoorsContent.mawPart())) level.setBlock(part, Blocks.AIR.defaultBlockState(), 3);
                    }
                });
            }
            Containers.updateNeighboursAfterDestroy(state, level, pos);
            super.affectNeighborsAfterRemoval(state, level, pos, moving);
        }
    }

    private void unlockMaw(BlockState state, Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel)) return;
        var keys = player.getMainHandItem();
        if (keys.is(MaterialContent.items().get("herald_key").get()) && keys.getCount() >= 3) {
            if (state.getValue(BLOCKSTATE) != 0) return;
            BlockState current = level.getBlockState(pos);
            if (!current.is(this) || current.getValue(BLOCKSTATE) != 0) return;
            if (!level.setBlock(pos, current.setValue(BLOCKSTATE, 1).setValue(ANIMATION, 1), 3)) return;
            keys.shrink(3);
            level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 1, 1);
            level.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL, 1, 1);
        } else {
            level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.NEUTRAL, 1, 1);
            player.sendOverlayMessage(Component.translatable("message.thermal_shock_reforged.door.herald_keys"));
        }
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (kind != DoorKind.MAW) return super.useItemOn(stack, state, level, pos, player, hand, hit);
        unlockMaw(state, level, pos, player);
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (kind != DoorKind.MAW) return super.useWithoutItem(state, level, pos, player, hit);
        unlockMaw(state, level, pos, player);
        return InteractionResult.SUCCESS;
    }

    public static final class Bunker extends SpecialDoorBlock implements SimpleWaterloggedBlock {
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
        Bunker() { super(DoorKind.BUNKER); registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return super.getStateForPlacement(context).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
        @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
        @Override protected boolean propagatesSkylightDown(BlockState state) { return state.getFluidState().isEmpty(); }
        @Override protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
            if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            return super.updateShape(state, level, ticks, pos, side, neighborPos, neighbor, random);
        }
        @Override public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return true; }
        @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, net.minecraft.world.level.redstone.Orientation fromPos, boolean moving) {
            super.neighborChanged(state, level, pos, neighbor, fromPos, moving);
            if (!(level instanceof ServerLevel) || state.getValue(BLOCKSTATE) != 0 || level.getBestNeighborSignal(pos) <= 0) return;
            BlockState current = level.getBlockState(pos);
            if (!current.is(this) || current.getValue(BLOCKSTATE) != 0) return;
            if (!level.setBlock(pos, current.setValue(BLOCKSTATE, 1).setValue(ANIMATION, 1), 3)) return;
            level.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "bunker_door")), SoundSource.NEUTRAL, 1, 1);
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int y = -4; y <= 4; y++) for (int x = -4; x <= 4; x++) for (int z = -4; z <= 4; z++) {
                cursor.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                if (level.getBlockState(cursor).is(NativeDecorContent.LIGHT_BLOCKING_BARRIER.get())) level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}
