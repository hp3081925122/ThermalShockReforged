package com.hp.thermalshockreforged.content.decor.nativeblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

final class PhaserBlock extends NativeDecorBlock {
    PhaserBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHER_BRICKS).strength(5, 100).lightLevel(state -> 10)
                .requiresCorrectToolForDrops().noCollision().noOcclusion().isRedstoneConductor((state, level, pos) -> false),
                new Traits(15, false, true, false, 0, 0, false, true), null, "block.thermal_shock_reforged.phaser.description_0");
    }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!level.isClientSide() && entity instanceof LivingEntity living) living.addEffect(new MobEffectInstance(entity.isShiftKeyDown() ? MobEffects.SLOW_FALLING : MobEffects.LEVITATION, 5, 1, true, true));
    }
}
