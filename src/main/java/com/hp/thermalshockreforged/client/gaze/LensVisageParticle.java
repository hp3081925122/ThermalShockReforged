package com.hp.thermalshockreforged.client.gaze;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

final class LensVisageParticle extends LegacySingleQuadParticle {
    private LensVisageParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        setSize(.2F, .2F); quadSize = 1.1F; lifetime = 1; gravity = 0; hasPhysics = false;
        xd = 0; yd = 0; zd = 0; pickSprite(sprites);
    }
    public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new LensVisageParticle(level, x, y, z, sprites); }
    }
}
