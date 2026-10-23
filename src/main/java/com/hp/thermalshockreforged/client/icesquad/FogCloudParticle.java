package com.hp.thermalshockreforged.client.icesquad;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;

import com.hp.thermalshockreforged.client.vfx.VfxManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;

final class FogCloudParticle extends LegacySingleQuadParticle {
    private static final Identifier EFFECT = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "fogcloud");
    private FogCloudParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z); setSize(0.2F, 0.2F); lifetime = 7; gravity = 0; hasPhysics = false;
        xd = dx; yd = dy; zd = dz; pickSprite(sprites);
    }
    @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    @Override public void tick() {
        super.tick();
        VfxManager.play(level, EFFECT, x, y, z);
    }
    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new FogCloudParticle(level, x, y, z, dx, dy, dz, sprites); }
    }
}
