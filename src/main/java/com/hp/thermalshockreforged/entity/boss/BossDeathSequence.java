package com.hp.thermalshockreforged.entity.boss;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public final class BossDeathSequence {
    private static final Identifier SOUND = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "mob_death_explode");
    private Vec3 origin = Vec3.ZERO;
    private double eyeHeight;
    private String dimension = "";
    private int emitted;
    private int remaining;

    public void start(ServerLevel level, Vec3 origin, double eyeHeight) {
        if (emitted != 0) return;
        this.origin = origin; this.eyeHeight = eyeHeight; dimension = level.dimension().identifier().toString();
        remaining = 4;
        burst(level);
    }
    public boolean pending() { return emitted > 0 && emitted < 15; }
    public void endTick(ServerLevel level) {
        if (!pending()) return;
        if (!dimension.equals(level.dimension().identifier().toString())) { emitted = 15; return; }
        if (--remaining <= 0) { burst(level); remaining = 4; }
    }
    private void burst(ServerLevel level) {
        emitted++;
        level.sendParticles(ParticleTypes.EXPLOSION, origin.x, origin.y + eyeHeight, origin.z, 1, 2, 3, 2, 0);
        level.playSound(null, BlockPos.containing(origin), BuiltInRegistries.SOUND_EVENT.getValue(SOUND), SoundSource.HOSTILE, 2, 2);
    }
    public void save(CompoundTag root) {
        var tag = new CompoundTag();
        tag.putDouble("X", origin.x); tag.putDouble("Y", origin.y); tag.putDouble("Z", origin.z); tag.putDouble("EyeHeight", eyeHeight);
        tag.putString("Dimension", dimension); tag.putInt("Emitted", emitted); tag.putInt("Remaining", remaining);
        root.put("BossDeathSequence", tag);
    }
    public void load(CompoundTag root) {
        var tag = root.getCompound("BossDeathSequence").orElseGet(CompoundTag::new);
        emitted = Math.clamp(tag.getInt("Emitted").orElse(0), 0, 15); remaining = Math.clamp(tag.getInt("Remaining").orElse(0), 1, 4);
        origin = new Vec3(tag.getDouble("X").orElse(0.0), tag.getDouble("Y").orElse(0.0), tag.getDouble("Z").orElse(0.0)); eyeHeight = tag.getDouble("EyeHeight").orElse(0.0);
        dimension = tag.getString("Dimension").orElse("");
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z) || !Double.isFinite(eyeHeight)) emitted = 15;
    }
}
