package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.thermite.ThermiteDetonationEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

final class ThermiteBlock extends NativeDecorBlock {
    ThermiteBlock() {
        super(Properties.of().mapColor(MapColor.TERRACOTTA_RED).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.MUD_BRICKS).strength(0.3F, 0).requiresCorrectToolForDrops(),
                new Traits(15, false, false, false, 0, 0, false, false), null,
                "block.thermal_shock_reforged.thermite.description_0", "block.thermal_shock_reforged.thermite.description_1");
    }
    @Override public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        if (level instanceof ServerLevel server && !ThermiteDetonationEntity.submit(server, pos)) ThermalShockReforged.LOGGER.warn("Thermite detonation submission was rejected: position={}, dimension={}", pos, server.dimension().identifier());
    }
}
