package com.hp.thermalshockreforged.content.vilebathblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.ArrayDeque;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

final class SparklerDetonations {
    private record Pulse(ServerLevel level, BlockPos pos) { }
    private static final ThreadLocal<ArrayDeque<Pulse>> CURRENT = new ThreadLocal<>();
    static void submit(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server)) return;
        var pulse = new Pulse(server, pos.immutable());
        var pending = CURRENT.get();
        if (pending != null) { pending.addLast(pulse); return; }
        pending = new ArrayDeque<>(); pending.addLast(pulse); CURRENT.set(pending);
        int processed = 0;
        try {
            while (!pending.isEmpty()) {
                var next = pending.removeFirst(); var origin = next.pos(); var world = next.level();
                world.explode(null, origin.getX(), origin.getY(), origin.getZ(), 1, Level.ExplosionInteraction.BLOCK);
                world.sendParticles(ParticleTypes.END_ROD, origin.getX(), origin.getY(), origin.getZ(), 20, 1, 1, 1, .3);
                processed++;
            }
        } finally { CURRENT.remove(); ThermalShockReforged.LOGGER.debug("Sparkler bulb detonation drained: pulses={}", processed); }
    }
    private SparklerDetonations() { }
}
