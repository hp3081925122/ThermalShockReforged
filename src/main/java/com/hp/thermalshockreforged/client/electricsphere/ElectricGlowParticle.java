package com.hp.thermalshockreforged.client.electricsphere;

import com.hp.thermalshockreforged.entity.electricsphere.ElectricSphereContent;
import mod.chloeprime.aaaparticles.api.common.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public final class ElectricGlowParticle extends TextureSheetParticle {
    private ElectricGlowParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) { super(level, x, y, z); setSize(.2F, .2F); lifetime = 0; gravity = 0; hasPhysics = true; xd = 0; yd = 0; zd = 0; pickSprite(sprites); }
    @Override public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_LIT; }
    @Override public void tick() { super.tick(); AAALevel.addParticle(level, new ParticleEmitterInfo(ElectricSphereContent.id("electricglow")).position(new Vec3(x, y, z))); }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new ElectricGlowParticle(level, x, y, z, sprites); }
    }
}
