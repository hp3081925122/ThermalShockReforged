package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.hp.thermalshockreforged.content.utility.UtilityContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

final class ConstellationBlock extends NativeDecorBlock {
    static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    static final IntegerProperty DESIGN = IntegerProperty.create("blockstate", 0, 6);

    ConstellationBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM)
                        .sound(SoundType.NETHERITE_BLOCK).strength(2, 30)
                        .lightLevel(state -> state.getValue(DESIGN) == 0 ? 0 : 15)
                        .requiresCorrectToolForDrops()
                        .emissiveRendering((state, level, pos) -> true),
                new Traits(15, false, false, false, 0, 0, false, false), null,
                "block.thermal_shock_reforged.constellation_block.description_0");
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(DESIGN, 0));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, DESIGN);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND && stack.is(UtilityContent.CHISEL.get()) && !level.isClientSide()) {
            level.setBlock(pos, state.cycle(DESIGN), 3);
        }
        return InteractionResult.SUCCESS;
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.SUCCESS;
    }
}
