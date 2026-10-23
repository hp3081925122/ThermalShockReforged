package com.hp.thermalshockreforged.client.farming;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public final class HearthflyParticle extends LegacySingleQuadParticle {
    private HearthflyParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z);
        setSize(.2f, .2f);
        quadSize *= 3;
        lifetime = Math.max(1, 26 + random.nextInt(20) - 10);
        gravity = -.2f;
        hasPhysics = false;
        xd = vx * .5;
        yd = vy * .5;
        zd = vz * .5;
        pickSprite(sprites);
    }
    public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource random) {
            return new HearthflyParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}
