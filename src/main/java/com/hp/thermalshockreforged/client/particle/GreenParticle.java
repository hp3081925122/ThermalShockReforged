package com.hp.thermalshockreforged.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class GreenParticle extends LegacySingleQuadParticle {
    private GreenParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z);
        setSize(.2F, .2F);
        quadSize *= 1.3F;
        lifetime = Math.max(1, 16 + random.nextInt(2) - 1);
        gravity = 0;
        hasPhysics = false;
        xd = dx;
        yd = dy;
        zd = dz;
        pickSprite(sprites);
    }
    public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new GreenParticle(level, x, y, z, dx, dy, dz, sprites); }
    }
}
