package com.hp.thermalshockreforged.content.decor.nativeblocks;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.content.utility.UtilityContent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

final class CrystalleyeBlock extends NativeDecorBlock {
    private final int style;
    CrystalleyeBlock(int style) {
        super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.BASALT).strength(2, 30).lightLevel(state -> style == 0 ? 0 : 10).requiresCorrectToolForDrops(), new Traits(15, false, false, false, 0, 0, false, false), null, "block.thermal_shock_reforged." + (style == 0 ? "chiseled_mantlestone" : style == 1 ? "red_crystalleye_lamp" : "blue_crystalleye_lamp") + ".description_0");
        this.style = style;
    }
    private boolean chisel(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().is(UtilityContent.CHISEL.get())) return false;
        if (!level.isClientSide()) {
            var next = switch (style) { case 0 -> NativeDecorContent.RED_CRYSTALLEYE_LAMP; case 1 -> NativeDecorContent.BLUE_CRYSTALLEYE_LAMP; default -> NativeDecorContent.CHISELED_MANTLESTONE; };
            if (level.setBlock(pos, next.get().withPropertiesOf(state), 3)) level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.NEUTRAL, 1, 1);
        }
        return true;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return chisel(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return chisel(state, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
}
