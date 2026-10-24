package com.hp.thermalshockreforged.client.armor;
import net.minecraft.client.particle.SingleQuadParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public final class PurpleSparkleParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private PurpleSparkleParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, sprites.get(0, 1)); this.sprites = sprites;
        setSize(.2F, .2F); quadSize *= 1.3F; lifetime = 18 + random.nextInt(24); gravity = -.2F;
        hasPhysics = false; xd = dx; yd = dy; zd = dz; setSpriteFromAge(sprites);
    }
    @Override public int getLightCoords(float partialTick) { return 0xF000F0; }
    @Override protected SingleQuadParticle.Layer getLayer() { return SingleQuadParticle.Layer.OPAQUE; }
    @Override public void tick() { super.tick(); if (!removed) setSprite(sprites.get(age / 6 % 2 + 1, 2)); }
    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new PurpleSparkleParticle(level, x, y, z, dx, dy, dz, sprites); }
    }
}
