package com.hp.thermalshockreforged.client.fauna;
import net.minecraft.client.particle.SingleQuadParticle;

import com.hp.thermalshockreforged.client.vfx.VfxManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;

final class WhiteOrbParticle extends SingleQuadParticle {
    private static final Identifier EFFECT = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "whiteorb");

    private WhiteOrbParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.get(level.getRandom()));
        setSize(0.2F, 0.2F);
        lifetime = 1;
        gravity = 0;
        hasPhysics = false;
        xd = 0;
        yd = 0;
        zd = 0;
       
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() { return SingleQuadParticle.Layer.OPAQUE; }

    @Override
    public void tick() {
        super.tick();
        VfxManager.play(level, EFFECT, x, y, z);
    }

    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) { return new WhiteOrbParticle(level, x, y, z, sprites); }
    }
}
