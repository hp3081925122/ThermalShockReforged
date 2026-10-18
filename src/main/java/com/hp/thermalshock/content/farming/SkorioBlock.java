package com.hp.thermalshock.content.farming;

import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;

public final class SkorioBlock extends FlowerBlock {
    public SkorioBlock() {
        super(new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(MobEffects.FIRE_RESISTANCE, 100))),
                Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission().offsetType(OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    }
    @Override public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 100; }
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 60; }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean harvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, harvest, fluid);
        SickleHarvesting.harvest(level, pos, CropKind.SKORIO, player);
        return result;
    }
}
