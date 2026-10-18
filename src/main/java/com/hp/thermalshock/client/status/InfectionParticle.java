package com.hp.thermalshock.client.status;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

final class InfectionParticle extends TextureSheetParticle {
    private final ResourceLocation effect;

    private InfectionParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites, ResourceLocation effect) {
        super(level, x, y, z);
        this.effect = effect;
        setSize(0.2F, 0.2F);
        lifetime = 1;
        gravity = 0;
        hasPhysics = false;
        xd = dx;
        yd = dy;
        zd = dz;
        pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_OPAQUE; }

    @Override
    public void tick() {
        super.tick();
        AAALevel.addParticle(level, new ParticleEmitterInfo(effect).position(new Vec3(x, y, z)));
    }

    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final ResourceLocation effect;
        Provider(SpriteSet sprites, String effect) {
            this.sprites = sprites;
            this.effect = ResourceLocation.fromNamespaceAndPath("thermal_shock", effect);
        }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new InfectionParticle(level, x, y, z, dx, dy, dz, sprites, effect);
        }
    }
}
