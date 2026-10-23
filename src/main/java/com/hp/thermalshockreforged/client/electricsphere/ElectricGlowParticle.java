package com.hp.thermalshockreforged.client.electricsphere;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;

import com.hp.thermalshockreforged.client.vfx.VfxManager;
import com.hp.thermalshockreforged.entity.electricsphere.ElectricSphereContent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public final class ElectricGlowParticle extends LegacySingleQuadParticle {
    private ElectricGlowParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) { super(level, x, y, z); setSize(.2F, .2F); lifetime = 0; gravity = 0; hasPhysics = true; xd = 0; yd = 0; zd = 0; pickSprite(sprites); }
    public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    @Override public void tick() { super.tick(); VfxManager.play(level, ElectricSphereContent.id("electricglow"), x, y, z); }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new ElectricGlowParticle(level, x, y, z, sprites); }
    }
}
