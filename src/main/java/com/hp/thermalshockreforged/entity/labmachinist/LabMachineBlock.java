package com.hp.thermalshockreforged.entity.labmachinist;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import com.hp.thermalshockreforged.entity.qo.QoMenu;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public abstract class LabMachineBlock extends Block implements EntityBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final IntegerProperty MODE = IntegerProperty.create("blockstate", 0, 1);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected LabMachineBlock(Properties properties) { super(properties); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ANIMATION, 0)); }
    public abstract boolean wallbore();
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ANIMATION, FACING); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new LabMachineBlockEntity(wallbore(), pos, state); }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return state.getFluidState().isEmpty(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return wallbore() ? Shapes.block() : box(.1, .1, .1, 15.9, 14, 15.9); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) { return super.canHarvestBlock(state, level, pos, player) && (!wallbore() || state.getValue(MODE) == 0); }
    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) { return !wallbore() && level.getBlockEntity(pos) instanceof LabMachineBlockEntity machine ? machine : null; }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int parameter) { super.triggerEvent(state, level, pos, id, parameter); return level.getBlockEntity(pos) instanceof LabMachineBlockEntity machine && machine.triggerEvent(id, parameter); }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock())) { if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LabMachineBlockEntity machine) { Containers.dropContents(level, pos, machine); level.updateNeighbourForOutputSignal(pos, this); } super.onRemove(state, level, pos, replacement, moving); }
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof LabMachineBlockEntity machine ? AbstractContainerMenu.getRedstoneSignalFromContainer(machine) : 0; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer server) {
            if (wallbore()) {
                BlockState current = level.getBlockState(pos);
                if (current.is(this) && current.getValue(MODE) == 1 && player.getMainHandItem().is(Items.LAVA_BUCKET)
                        && level.getBlockEntity(pos) instanceof LabMachineBlockEntity machine) {
                    ItemStack payment = player.getMainHandItem().split(1);
                    double previousEffic = machine.getPersistentData().getDouble("effic");
                    machine.getPersistentData().putDouble("effic", 0);
                    machine.setChanged();
                    if (!level.setBlock(pos, current.setValue(MODE, 0), 3)
                            || level.getBlockEntity(pos) != machine || !level.getBlockState(pos).is(this) || level.getBlockState(pos).getValue(MODE) != 0) {
                        if (level.getBlockEntity(pos) == machine && level.getBlockState(pos).equals(current)) {
                            machine.getPersistentData().putDouble("effic", previousEffic);
                            machine.setChanged();
                        }
                        com.hp.thermalshockreforged.entity.qo.QoContent.give(server, payment);
                        com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Wallbore refuelling rejected: position={}, player={}", pos, player.getUUID());
                        return InteractionResult.FAIL;
                    }
                    com.hp.thermalshockreforged.entity.qo.QoContent.give(server, new ItemStack(Items.BUCKET));
                    level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1, 1);
                    com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Wallbore refuelled: position={}, player={}, previousEffic={}", pos, player.getUUID(), previousEffic);
                }
            } else LabContent.openBlock(server, pos, QoMenu.completed(server, "icebreaker_1") ? LabPage.ASSEMBLY : LabPage.ASSEMBLER_TOUCH);
        }
        return InteractionResult.SUCCESS;
    }
    @Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (wallbore() && !level.isClientSide() && !oldState.is(this)) { level.scheduleTick(pos, this, 60); if (level.getBlockEntity(pos) instanceof LabMachineBlockEntity machine) { machine.getPersistentData().putDouble("effic", 0); machine.setChanged(); } }
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!wallbore()) return;
        if (level.getBlockEntity(pos) instanceof LabMachineBlockEntity machine) {
            machine.beginCycle(state);
            if (machine.getPersistentData().getDouble("effic") == 30) level.setBlock(pos, level.getBlockState(pos).setValue(MODE, 1), 3);
        }
        level.scheduleTick(pos, this, 60);
    }
    public static final class Assembler extends LabMachineBlock implements SimpleWaterloggedBlock {
        public Assembler() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.LODESTONE).strength(10, 999).lightLevel(state -> 13).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((state, level, pos) -> false)); registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)); }
        @Override public boolean wallbore() { return false; }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return super.getStateForPlacement(context).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER); }
        @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
        @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level)); return super.updateShape(state, direction, neighbor, level, pos, neighborPos); }
        @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) { if (random.nextDouble() < .05) level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.NEUTRAL, 1, 1, false); }
    }
    public static final class Wallbore extends LabMachineBlock {
        public Wallbore() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).strength(3, 999)); registerDefaultState(defaultBlockState().setValue(MODE, 0)); }
        @Override public boolean wallbore() { return true; }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(MODE); }
    }
}
