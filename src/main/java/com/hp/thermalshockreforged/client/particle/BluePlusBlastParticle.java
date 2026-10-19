package com.hp.thermalshockreforged.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class BluePlusBlastParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private BluePlusBlastParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z); this.sprites = sprites;
        setSize(.2F, .2F); quadSize *= 16; lifetime = 8; gravity = 0; hasPhysics = false;
        xd = 0; yd = 0; zd = 0; setSpriteFromAge(sprites);
    }
    @Override public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_LIT; }
    @Override public void tick() { super.tick(); if (!removed) setSprite(sprites.get(age % 9 + 1, 9)); }
    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new BluePlusBlastParticle(level, x, y, z, sprites); }
    }
}
