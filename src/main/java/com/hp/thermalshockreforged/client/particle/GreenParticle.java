package com.hp.thermalshockreforged.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class GreenParticle extends SingleQuadParticle {
    private GreenParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, sprites.get(level.getRandom()));
        setSize(.2F, .2F);
        quadSize *= 1.3F;
        lifetime = Math.max(1, 16 + random.nextInt(2) - 1);
        gravity = 0;
        hasPhysics = false;
        xd = dx;
        yd = dy;
        zd = dz;
       
    }
    @Override public int getLightCoords(float partialTick) { return 0xF000F0; }
    @Override protected SingleQuadParticle.Layer getLayer() { return SingleQuadParticle.Layer.OPAQUE; }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new GreenParticle(level, x, y, z, dx, dy, dz, sprites); }
    }
}
