package com.hp.thermalshockreforged.client.gaze;
import net.minecraft.client.particle.SingleQuadParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

final class LensVisageParticle extends SingleQuadParticle {
    private LensVisageParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.get(level.getRandom()));
        setSize(.2F, .2F); quadSize = 1.1F; lifetime = 1; gravity = 0; hasPhysics = false;
        xd = 0; yd = 0; zd = 0;
    }
    @Override public int getLightCoords(float partialTick) { return 0xF000F0; }
    @Override protected SingleQuadParticle.Layer getLayer() { return SingleQuadParticle.Layer.OPAQUE; }
    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new LensVisageParticle(level, x, y, z, sprites); }
    }
}
