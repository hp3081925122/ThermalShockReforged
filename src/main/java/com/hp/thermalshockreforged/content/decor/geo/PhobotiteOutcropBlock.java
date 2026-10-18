package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.world.AltarProgressPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;

final class PhobotiteOutcropBlock extends GeoDecorBlock {
    PhobotiteOutcropBlock(GeoDecorKind kind) { super(kind); }
    @Override public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return super.canHarvestBlock(state, level, pos, player) && AltarProgressPayload.unlocked(player.level());
    }
    @Override protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
        if (!level.isClientSide && !AltarProgressPayload.unlocked(level) && level.getGameRules().getBoolean(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION)) player.displayClientMessage(Component.literal("§aThis ore is too cold for you to gather..."), true);
    }
}
