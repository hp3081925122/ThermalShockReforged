package com.hp.thermalshockreforged.client.squall;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

final class SquallParticles {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final SingleQuadParticle.FacingCameraMode FLAT = (rotation, camera, partialTick) -> rotation.rotationX(-Mth.HALF_PI);

    private SquallParticles() { }

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

    static final class Wisp extends Animated {
        private Wisp(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
            super(level, x, y, z, sprites, 18 + level.random.nextInt(9));
            xd = dx;
            yd = dy;
            zd = dz;
            quadSize = .14F + level.random.nextFloat() * .12F;
            roll = level.random.nextFloat() * Mth.TWO_PI;
            oRoll = roll;
        }
        @Override public void tick() {
            super.tick();
            float progress = (float) age / lifetime;
            if (progress > .6F) alpha = 1 - (progress - .6F) / .4F;
        }
    }

    static final class Mark extends Animated {
        private Mark(ClientLevel level, double x, double y, double z, double radius, double ticks, SpriteSet sprites) {
            super(level, x, y, z, sprites, (int) Math.round(ticks));
            quadSize = (float) radius;
            alpha = .9F;
        }
        @Override public SingleQuadParticle.FacingCameraMode getFacingCameraMode() { return FLAT; }
    }

    static final class Burst extends Animated {
        private Burst(ClientLevel level, double x, double y, double z, double size, SpriteSet sprites) {
            super(level, x, y, z, sprites, 12);
            quadSize = (float) size;
        }
    }

    static final class Bolt extends Animated {
        private Bolt(ClientLevel level, double x, double y, double z, double size, SpriteSet sprites) {
            super(level, x, y, z, sprites, 8);
            quadSize = (float) size / 2;
        }
        @Override public SingleQuadParticle.FacingCameraMode getFacingCameraMode() { return SingleQuadParticle.FacingCameraMode.LOOKAT_Y; }
    }

    record WispProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Wisp(level, x, y, z, dx, dy, dz, sprites); }
    }

    record MarkProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Mark(level, x, y, z, dx, dy, sprites); }
    }

    record BurstProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Burst(level, x, y, z, dx, sprites); }
    }

    record BoltProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new Bolt(level, x, y, z, dx, sprites); }
    }
}
