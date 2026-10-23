package com.hp.thermalshockreforged.client.weapons;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class LiteprismParticles {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final float HALF_WIDTH = .25F;
    private static final float TILE = .5F;

    private LiteprismParticles() { }

    private abstract static class Glowing extends LegacySingleQuadParticle {
        protected final SpriteSet sprites;
        protected Glowing(ClientLevel level, double x, double y, double z, SpriteSet sprites, int lifetime) {
            super(level, x, y, z);
            this.sprites = sprites;
            this.lifetime = lifetime;
            gravity = 0;
            hasPhysics = false;
            xd = 0;
            yd = 0;
            zd = 0;
            setSpriteFromAge(sprites);
        }
        public int getLightColor(float partialTick) { return FULL_BRIGHT; }
        @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    }

    static final class Beam extends Glowing {
        private final float dx;
        private final float dy;
        private final float dz;
        private final float length;
        private Beam(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
            super(level, x, y, z, sprites, 6);
            this.dx = (float) dx;
            this.dy = (float) dy;
            this.dz = (float) dz;
            length = Mth.sqrt(this.dx * this.dx + this.dy * this.dy + this.dz * this.dz);
        }
        @Override public void tick() {
            xo = x;
            yo = y;
            zo = z;
            if (age++ >= lifetime) {
                remove();
                return;
            }
            setSpriteFromAge(sprites);
        }
        @Override public void render(VertexConsumer buffer, Camera camera, float partialTick) {
            if (length < 1.0E-3F) return;
            Vec3 view = camera.getPosition();
            Vector3f start = new Vector3f((float) (x - view.x), (float) (y - view.y), (float) (z - view.z));
            Vector3f axis = new Vector3f(dx, dy, dz).div(length);
            Vector3f middle = new Vector3f(start).add(dx * .5F, dy * .5F, dz * .5F);
            Vector3f side = new Vector3f(middle).cross(axis);
            if (side.lengthSquared() < 1.0E-6F) return;
            side.normalize(HALF_WIDTH);
            float u0 = getU0();
            float u1 = getU1();
            float v0 = getV0();
            float v1 = getV1();
            for (float from = 0; from < length; from += TILE) {
                float to = Math.min(from + TILE, length);
                float vTo = v0 + (v1 - v0) * (to - from) / TILE;
                Vector3f a = new Vector3f(axis).mul(from).add(start);
                Vector3f b = new Vector3f(axis).mul(to).add(start);
                vertex(buffer, new Vector3f(a).sub(side), u0, v0);
                vertex(buffer, new Vector3f(a).add(side), u1, v0);
                vertex(buffer, new Vector3f(b).add(side), u1, vTo);
                vertex(buffer, new Vector3f(b).sub(side), u0, vTo);
            }
        }
        private void vertex(VertexConsumer buffer, Vector3f at, float u, float v) {
            buffer.addVertex(at.x, at.y, at.z).setUv(u, v).setColor(rCol, gCol, bCol, alpha).setLight(FULL_BRIGHT);
        }
        @Override public AABB getRenderBoundingBox(float partialTicks) { return new AABB(x, y, z, x + dx, y + dy, z + dz).inflate(HALF_WIDTH); }
    }

    static final class Flare extends Glowing {
        private Flare(ClientLevel level, double x, double y, double z, double size, SpriteSet sprites) {
            super(level, x, y, z, sprites, 6);
            quadSize = (float) size;
        }
        @Override public void tick() {
            super.tick();
            if (isAlive()) setSpriteFromAge(sprites);
        }
    }

    static final class Spark extends Glowing {
        private Spark(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
            super(level, x, y, z, sprites, 8 + level.getRandom().nextInt(4));
            xd = dx;
            yd = dy;
            zd = dz;
            friction = .88F;
            quadSize = .25F;
        }
        @Override public void tick() {
            super.tick();
            if (!isAlive()) return;
            setSpriteFromAge(sprites);
            float progress = (float) age / lifetime;
            if (progress > .6F) alpha = 1 - (progress - .6F) / .4F;
        }
    }

    public record BeamProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new Beam(level, x, y, z, dx, dy, dz, sprites); }
    }

    public record FlareProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new Flare(level, x, y, z, dx, sprites); }
    }

    public record SparkProvider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new Spark(level, x, y, z, dx, dy, dz, sprites); }
    }
}
