package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.entity.pots.PotContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;

public final class ShalePotBlock extends GeoDecorBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("blockstate", 0, 2);
    public ShalePotBlock() { super(GeoDecorKind.SHALE_POT); registerDefaultState(defaultBlockState().setValue(VARIANT, 0)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(VARIANT); }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean harvest, FluidState fluid) {
        boolean removed = super.onDestroyedByPlayer(state, level, pos, player, toolStack, harvest, fluid);
        if (removed && level instanceof ServerLevel server && !server.getBlockState(pos).is(this) && server.getRandom().nextDouble() < 0.025) {
            var skullscor = PotContent.SKULLSCOR.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
            if (skullscor != null) skullscor.setDeltaMovement(0, 0, 0);
        }
        return removed;
    }
}
