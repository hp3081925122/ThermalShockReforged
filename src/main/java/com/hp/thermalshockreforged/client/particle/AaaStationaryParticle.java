package com.hp.thermalshockreforged.client.particle;

import com.hp.thermalshockreforged.ThermalShockReforged;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public final class AaaStationaryParticle extends TextureSheetParticle {
    private final ResourceLocation effect;
    private final ParticleRenderType renderType;
    private final boolean fullBright;
    private AaaStationaryParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, ResourceLocation effect, int life, ParticleRenderType renderType, boolean fullBright) {
        super(level, x, y, z); this.effect = effect; this.renderType = renderType; this.fullBright = fullBright;
        setSize(0.2F, 0.2F); lifetime = life; gravity = 0; hasPhysics = true;
        xd = 0; yd = 0; zd = 0; pickSprite(sprites);
    }
    @Override public int getLightColor(float partialTick) { return fullBright ? 0xF000F0 : super.getLightColor(partialTick); }
    @Override public ParticleRenderType getRenderType() { return renderType; }
    @Override public void tick() {
        super.tick();
        AAALevel.addParticle(level, new ParticleEmitterInfo(effect).position(new Vec3(x, y, z)));
    }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final ResourceLocation effect;
        private final int lifetime;
        private final ParticleRenderType renderType;
        private final boolean fullBright;
        public Provider(SpriteSet sprites, String effect, int lifetime, ParticleRenderType renderType, boolean fullBright) {
            this.sprites = sprites; this.effect = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, effect);
            this.lifetime = lifetime; this.renderType = renderType; this.fullBright = fullBright;
        }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new AaaStationaryParticle(level, x, y, z, sprites, effect, lifetime, renderType, fullBright);
        }
    }
}
