package com.hp.thermalshockreforged.client.particle;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.client.vfx.VfxManager;
import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class AaaStationaryParticle extends LegacySingleQuadParticle {
    private final Identifier effect;
    private final ParticleRenderType renderType;
    private final boolean fullBright;
    private AaaStationaryParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, Identifier effect, int life, ParticleRenderType renderType, boolean fullBright, boolean physics, Vec3 velocity) {
        super(level, x, y, z); this.effect = effect; this.renderType = renderType; this.fullBright = fullBright;
        setSize(0.2F, 0.2F); lifetime = life; gravity = 0; hasPhysics = physics;
        xd = velocity.x; yd = velocity.y; zd = velocity.z; pickSprite(sprites);
    }
    public int getLightColor(float partialTick) { return fullBright ? 0xF000F0 : super.getLightColor(partialTick); }
    @Override public ParticleRenderType getGroup() { return renderType; }
    @Override public void tick() {
        super.tick();
        VfxManager.play(level, effect, x, y, z);
    }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final Identifier effect;
        private final int lifetime;
        private final ParticleRenderType renderType;
        private final boolean fullBright;
        private final boolean physics;
        private final double velocityMultiplier;
        public Provider(SpriteSet sprites, String effect, int lifetime, ParticleRenderType renderType, boolean fullBright) {
            this(sprites, effect, lifetime, renderType, fullBright, true, 0);
        }
        public Provider(SpriteSet sprites, String effect, int lifetime, ParticleRenderType renderType, boolean fullBright, boolean physics, double velocityMultiplier) {
            this.physics = physics; this.velocityMultiplier = velocityMultiplier;
            this.sprites = sprites; this.effect = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, effect);
            this.lifetime = lifetime; this.renderType = renderType; this.fullBright = fullBright;
        }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new AaaStationaryParticle(level, x, y, z, sprites, effect, lifetime, renderType, fullBright, physics, new Vec3(dx, dy, dz).scale(velocityMultiplier));
        }
    }
}
