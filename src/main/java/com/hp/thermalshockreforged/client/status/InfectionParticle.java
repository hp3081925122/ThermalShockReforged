package com.hp.thermalshockreforged.client.status;
import net.minecraft.client.particle.SingleQuadParticle;

import com.hp.thermalshockreforged.client.vfx.VfxManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;

final class InfectionParticle extends SingleQuadParticle {
    private final Identifier effect;

    private InfectionParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites, Identifier effect) {
        super(level, x, y, z, sprites.get(level.getRandom()));
        this.effect = effect;
        setSize(0.2F, 0.2F);
        lifetime = 1;
        gravity = 0;
        hasPhysics = false;
        xd = dx;
        yd = dy;
        zd = dz;
       
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() { return SingleQuadParticle.Layer.OPAQUE; }

    @Override
    public void tick() {
        super.tick();
        VfxManager.play(level, effect, x, y, z);
    }

    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final Identifier effect;
        Provider(SpriteSet sprites, String effect) {
            this.sprites = sprites;
            this.effect = Identifier.fromNamespaceAndPath("thermal_shock_reforged", effect);
        }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new InfectionParticle(level, x, y, z, dx, dy, dz, sprites, effect);
        }
    }
}
