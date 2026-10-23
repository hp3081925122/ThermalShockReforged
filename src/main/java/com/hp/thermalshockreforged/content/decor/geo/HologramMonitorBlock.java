package com.hp.thermalshockreforged.content.decor.geo;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class HologramMonitorBlock extends GeoDecorBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 9);
    public HologramMonitorBlock() { super(GeoDecorKind.HOLOGRAM_MONITOR); registerDefaultState(defaultBlockState().setValue(VARIANT, 0)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(VARIANT); }
    private void replay(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) return;
        int recording = 0;
        for (int disk = 1; disk <= 9; disk++) if (player.getMainHandItem().is(MaterialContent.items().get("diskette_" + disk).get())) { recording = disk; break; }
        level.setBlock(pos, level.getBlockState(pos).setValue(VARIANT, recording), 3);
        if (recording > 0) player.sendSystemMessage(Component.translatableWithFallback("message.thermal_shock_reforged.hologram_monitor.recording_" + recording, HologramRecordings.text(recording)));
        ThermalShockReforged.LOGGER.debug("Hologram monitor selected: player={}, position={}, recording={}", player.getUUID(), pos, recording);
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { replay(level, pos, player); return InteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { replay(level, pos, player); return InteractionResult.SUCCESS; }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        int particle = switch (state.getValue(VARIANT)) { case 1, 2, 7, 8 -> 1; case 3 -> 2; case 4, 5 -> 3; case 6 -> 5; case 9 -> 4; default -> 0; };
        if (particle != 0) level.addParticle(GeoDecorContent.HOLOGRAM_PARTICLES.get(particle - 1).get(), pos.getX() + .5, pos.getY() + 1.8, pos.getZ() + .5, 0, 0, 0);
    }
}
