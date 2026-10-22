package com.hp.thermalshockreforged.client.icesquad;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

final class BastionParticles {
    private static final int FULL_BRIGHT = 0xF000F0;

    private BastionParticles() { }

    private abstract static class Animated extends TextureSheetParticle {
        protected final SpriteSet sprites;
        protected Animated(ClientLevel level, double x, double y, double z, SpriteSet sprites, int lifetime) {
            super(level, x, y, z);
            this.sprites = sprites;
            this.lifetime = Math.max(1, lifetime);
            gravity = 0;
            friction = 1;
            hasPhysics = false;
            setSpriteFromAge(sprites);
        }
        @Override public void tick() {
            super.tick();
            if (isAlive()) setSpriteFromAge(sprites);
        }
        @Override public int getLightColor(float partialTick) { return FULL_BRIGHT; }
        @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
    }

    static final class Drift extends Animated {
        private final float baseSize;
        private final float grow;
        private Drift(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites, DriftProvider shape) {
            super(level, x, y, z, sprites, shape.life() + level.random.nextInt(shape.lifeRandom() + 1));
            xd = dx;
            yd = dy;
            zd = dz;
            gravity = shape.gravity();
            friction = shape.friction();
            baseSize = shape.size();
            grow = shape.grow();
            quadSize = baseSize;
        }
        @Override public void tick() {
            super.tick();
            float progress = (float) age / lifetime;
            quadSize = baseSize * (1 + grow * progress);
            if (progress > .7F) alpha = 1 - (progress - .7F) / .3F;
        }
    }

    static final class Flat extends Animated {
        private final int frames;
        private final SingleQuadParticle.FacingCameraMode facing;
        private Flat(ClientLevel level, double x, double y, double z, double radius, double ticks, double angle, SpriteSet sprites, int frames) {
            super(level, x, y, z, sprites, (int) Math.round(ticks));
            this.frames = frames;
            float turn = (float) angle;
            facing = (rotation, camera, partialTick) -> rotation.rotationY(turn).rotateX(-Mth.HALF_PI);
            quadSize = (float) radius;
            alpha = .9F;
            if (frames > 0) setSprite(sprites.get(0, Math.max(1, frames - 1)));
        }
        @Override public void tick() {
            if (frames <= 0) {
                super.tick();
                return;
            }
            xo = x;
            yo = y;
            zo = z;
            if (age++ >= lifetime) {
                remove();
                return;
            }
            setSprite(sprites.get(age / 4 % frames, Math.max(1, frames - 1)));
            float progress = (float) age / lifetime;
            if (progress > .8F) alpha = .9F * (1 - (progress - .8F) / .2F);
        }
        @Override public SingleQuadParticle.FacingCameraMode getFacingCameraMode() { return facing; }
    }

    static final class Pillar extends Animated {
        private Pillar(ClientLevel level, double x, double y, double z, double height, SpriteSet sprites, int lifetime) {
            super(level, x, y + height / 2, z, sprites, lifetime);
            quadSize = (float) height / 2;
        }
        @Override public SingleQuadParticle.FacingCameraMode getFacingCameraMode() { return SingleQuadParticle.FacingCameraMode.LOOKAT_Y; }
    }

    static final class Burst extends Animated {
        private Burst(ClientLevel level, double x, double y, double z, double size, SpriteSet sprites, int lifetime) {
            super(level, x, y, z, sprites, lifetime);
            quadSize = (float) size;
        }
    }

    record DriftProvider(SpriteSet sprites, int life, int lifeRandom, float size, float grow, float gravity, float friction) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Drift(level, x, y, z, dx, dy, dz, sprites, this); }
    }

    record FlatProvider(SpriteSet sprites, int frames) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Flat(level, x, y, z, dx, dy, dz, sprites, frames); }
    }

    record PillarProvider(SpriteSet sprites, int life) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Pillar(level, x, y, z, dx, sprites, life); }
    }

    record BurstProvider(SpriteSet sprites, int life) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Burst(level, x, y, z, dx, sprites, life); }
    }
}
