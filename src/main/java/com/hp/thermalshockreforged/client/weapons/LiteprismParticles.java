package com.hp.thermalshockreforged.client.weapons;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;
import org.joml.Vector3f;

public final class LiteprismParticles {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final float HALF_WIDTH = .25F;
    private static final float TILE = .5F;

    public static final ParticleRenderType BEAM_GROUP = new ParticleRenderType("thermal_shock_reforged:liteprism_beam");
    private static final OutputTarget PARTICLES_TARGET = new OutputTarget("thermal_shock_reforged_particles_target", () -> Minecraft.getInstance().levelRenderer.getParticlesTarget());
    private static final RenderType BEAM_TYPE = RenderType.create("thermal_shock_reforged_liteprism_beam", RenderSetup.builder(RenderPipelines.TRANSLUCENT_PARTICLE).withTexture("Sampler0", TextureAtlas.LOCATION_PARTICLES).useLightmap().setOutputTarget(PARTICLES_TARGET).createRenderSetup());

    private LiteprismParticles() { }

    private abstract static class Glowing extends SingleQuadParticle {
        protected final SpriteSet sprites;
        protected Glowing(ClientLevel level, double x, double y, double z, SpriteSet sprites, int lifetime) {
            super(level, x, y, z, sprites.get(0, 1));
            this.sprites = sprites;
            this.lifetime = lifetime;
            gravity = 0;
            hasPhysics = false;
            xd = 0;
            yd = 0;
            zd = 0;
            setSpriteFromAge(sprites);
        }
        @Override public int getLightCoords(float partialTick) { return FULL_BRIGHT; }
        @Override protected SingleQuadParticle.Layer getLayer() { return SingleQuadParticle.Layer.TRANSLUCENT; }
    }

    static final class Beam extends Particle {
        private final SpriteSet sprites;
        private final float dx;
        private final float dy;
        private final float dz;
        private final float length;
        private TextureAtlasSprite sprite;
        private Beam(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
            super(level, x, y, z);
            this.sprites = sprites;
            lifetime = 6;
            gravity = 0;
            hasPhysics = false;
            xd = 0;
            yd = 0;
            zd = 0;
            sprite = sprites.get(age, lifetime);
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
            if (!removed) sprite = sprites.get(age, lifetime);
        }
        @Override public ParticleRenderType getGroup() { return BEAM_GROUP; }
        private AABB renderBox() { return new AABB(x, y, z, x + dx, y + dy, z + dz).inflate(HALF_WIDTH); }
        private void extract(List<float[]> quads, Vec3 view) {
            if (length < 1.0E-3F) return;
            Vector3f start = new Vector3f((float) (x - view.x), (float) (y - view.y), (float) (z - view.z));
            Vector3f axis = new Vector3f(dx, dy, dz).div(length);
            Vector3f middle = new Vector3f(start).add(dx * .5F, dy * .5F, dz * .5F);
            Vector3f side = new Vector3f(middle).cross(axis);
            if (side.lengthSquared() < 1.0E-6F) return;
            side.normalize(HALF_WIDTH);
            float u0 = sprite.getU0();
            float u1 = sprite.getU1();
            float v0 = sprite.getV0();
            float v1 = sprite.getV1();
            for (float from = 0; from < length; from += TILE) {
                float to = Math.min(from + TILE, length);
                float vTo = v0 + (v1 - v0) * (to - from) / TILE;
                Vector3f a = new Vector3f(axis).mul(from).add(start);
                Vector3f b = new Vector3f(axis).mul(to).add(start);
                Vector3f p0 = new Vector3f(a).sub(side);
                Vector3f p1 = new Vector3f(a).add(side);
                Vector3f p2 = new Vector3f(b).add(side);
                Vector3f p3 = new Vector3f(b).sub(side);
                quads.add(new float[] {p0.x, p0.y, p0.z, u0, v0, p1.x, p1.y, p1.z, u1, v0, p2.x, p2.y, p2.z, u1, vTo, p3.x, p3.y, p3.z, u0, vTo});
            }
        }
    }

    public static final class BeamGroup extends ParticleGroup<Beam> {
        private final BeamRenderState state = new BeamRenderState();
        public BeamGroup(ParticleEngine engine) { super(engine); }
        @Override public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick) {
            Vec3 view = camera.position();
            for (Beam beam : particles) if (frustum.isVisible(beam.renderBox())) beam.extract(state.quads, view);
            return state;
        }
    }

    private static final class BeamRenderState implements ParticleGroupRenderState {
        private final List<float[]> quads = new ArrayList<>();
        @Override public void submit(SubmitNodeCollector collector, CameraRenderState camera) {
            if (quads.isEmpty()) return;
            List<float[]> snapshot = List.copyOf(quads);
            collector.submitCustomGeometry(new PoseStack(), BEAM_TYPE, (pose, consumer) -> {
                for (float[] quad : snapshot) for (int i = 0; i < 20; i += 5) consumer.addVertex(pose, quad[i], quad[i + 1], quad[i + 2]).setUv(quad[i + 3], quad[i + 4]).setColor(1F, 1F, 1F, 1F).setLight(FULL_BRIGHT);
            });
        }
        @Override public void clear() { quads.clear(); }
    }

    public static void groups(RegisterParticleGroupsEvent event) { event.register(BEAM_GROUP, BeamGroup::new); }

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
