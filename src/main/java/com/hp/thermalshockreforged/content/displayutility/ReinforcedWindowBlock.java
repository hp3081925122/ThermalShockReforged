package com.hp.thermalshockreforged.content.displayutility;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.content.utility.UtilityContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

final class ReinforcedWindowBlock extends Block implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    static final IntegerProperty DESIGN = IntegerProperty.create("blockstate", 0, 5);

    ReinforcedWindowBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.NETHERITE_BLOCK).strength(2, 30).requiresCorrectToolForDrops()
                .noOcclusion().isRedstoneConductor((state, level, pos) -> false));
        registerDefaultState(defaultBlockState().setValue(DESIGN, 0));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DESIGN);
    }

    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos pos, FluidState fluid) { return true; }
    @Override protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return adjacent.is(this) || super.skipRendering(state, adjacent, side);
    }

    private boolean chisel(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().is(UtilityContent.CHISEL.get())) return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.cycle(DESIGN), 3);
            level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.NEUTRAL, 1, 1);
        }
        return true;
    }

    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        return chisel(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return chisel(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.reinforced_window.description_0"));
    }
}
