package com.hp.thermalshockreforged.entity.labmachinist;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;

public abstract class LabUtilityBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    protected LabUtilityBlock(Properties properties) { super(properties); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    public static final class Safe extends LabUtilityBlock {
        public Safe() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).strength(-1, 3600000).pushReaction(PushReaction.BLOCK)); }
        @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
            if (player instanceof ServerPlayer server) {
                if (player.getMainHandItem().is(LabContent.CLIMBING_PICK.get()) || player.getMainHandItem().is(LabContent.JELLY_PICK.get())) {
                    if (level.setBlock(pos, LabContent.STORM_BATTERY.get().defaultBlockState().setValue(FACING, state.getValue(FACING)), 3)) { level.playSound(null, pos, SoundEvents.ITEM_BREAK.value(), SoundSource.BLOCKS, 1, -1); LabContent.openBlock(server, pos, LabPage.SAFE_2); }
                } else LabContent.openBlock(server, pos, LabPage.SAFE_1);
            }
            return InteractionResult.SUCCESS;
        }
    }
    public static final class Battery extends LabUtilityBlock {
        public Battery() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(2, 10).lightLevel(state -> 10).requiresCorrectToolForDrops()); registerDefaultState(defaultBlockState().setValue(FACE, AttachFace.WALL)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACE); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { var direction = context.getNearestLookingDirection(); return super.getStateForPlacement(context).setValue(FACE, direction.getAxis() == Direction.Axis.Y ? direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR : AttachFace.WALL); }
        @Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) { super.onPlace(state, level, pos, oldState, moving); if (level instanceof ServerLevel server) server.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 10, .5, .5, .5, .04); }
    }
    public static final class Constructor extends LabUtilityBlock implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
        public Constructor() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.ANVIL).strength(1, 10)); }
        @Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) { super.onPlace(state, level, pos, oldState, moving); level.setBlock(pos, LabContent.WALLBORE.get().defaultBlockState().setValue(LabMachineBlock.FACING, state.getValue(FACING)), 3); }
        @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) { for (int i = 0; i < 3; i++) tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.wallbore_constructor.description_" + i)); }
    }
}
