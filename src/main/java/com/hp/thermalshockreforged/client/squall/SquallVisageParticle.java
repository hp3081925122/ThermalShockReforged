package com.hp.thermalshockreforged.client.squall;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

final class SquallVisageParticle extends TextureSheetParticle {
    private SquallVisageParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        setSize(.2F, .2F); quadSize = 2; lifetime = 1; gravity = 0; hasPhysics = false;
        xd = 0; yd = 0; zd = 0; pickSprite(sprites);
    }
    @Override public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_LIT; }
    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new SquallVisageParticle(level, x, y, z, sprites); }
    }
}
