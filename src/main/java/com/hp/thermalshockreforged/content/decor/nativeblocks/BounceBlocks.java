package com.hp.thermalshockreforged.content.decor.nativeblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

final class BounceBlocks {
    static void fall(LivingFallEvent event) {
        var entity = event.getEntity();
        var pos = entity.blockPosition();
        var current = entity.level().getBlockState(pos).getBlock();
        var below = entity.level().getBlockState(pos.below()).getBlock();
        if (current == NativeDecorContent.BOUNCER.get() || current == NativeDecorContent.LITTLE_SPRING.get() || current == NativeDecorContent.SUPERBOUNCER.get()
                || below == NativeDecorContent.BOUNCER.get() || below == NativeDecorContent.LITTLE_SPRING.get() || below == NativeDecorContent.SUPERBOUNCER.get()) event.setCanceled(true);
    }

    private static void bounce(Level level, BlockPos pos, Entity entity, boolean strong) {
        if (entity.isShiftKeyDown()) return;
        if (strong && !level.isClientSide) level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.BLOCKS, 1, 1);
        entity.setDeltaMovement(0, strong ? 3 : .9, 0);
    }
    static final class Full extends FacingDecorBlock {
        Full() { super(Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.25F, 5).requiresCorrectToolForDrops(), new Traits(15, false, false, false, 0, 0, false, false), Outline.FULL); }
        @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) { super.stepOn(level, pos, state, entity); bounce(level, pos, entity, false); }
    }
    static final class Slab extends SlabBlock {
        private final boolean strong;
        Slab(boolean strong) { super(properties(strong)); this.strong = strong; }
        private static Properties properties(boolean strong) {
            var properties = Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.SLIME_BLOCK).dynamicShape();
            return strong ? properties.strength(-1, 3600000).hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true) : properties.strength(1.25F, 0).requiresCorrectToolForDrops();
        }
        @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) { super.stepOn(level, pos, state, entity); bounce(level, pos, entity, strong); }
    }
}
