package com.hp.thermalshockreforged.content.machines;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

public final class ExtractorConstructorBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public ExtractorConstructorBlock() {
        super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.ANVIL).strength(1, 10));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (previous.is(this)) return;
        if (level.getBlockEntity(pos) != null) {
            ThermalShockReforged.LOGGER.debug("Extractor constructor replacement skipped for an existing block entity: position={}", pos);
            return;
        }
        var machine = GeoDecorContent.block(GeoDecorKind.EXTRACTOR).defaultBlockState().setValue(ExtractorBlock.FACING, state.getValue(FACING));
        boolean water = previous.getFluidState().getType() == Fluids.WATER || level.getFluidState(pos).getType() == Fluids.WATER;
        level.setBlock(pos, machine.setValue(BlockStateProperties.WATERLOGGED, water), 3);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("block.thermal_shock_reforged.extractor_constructor.description_0"));
    }
}
