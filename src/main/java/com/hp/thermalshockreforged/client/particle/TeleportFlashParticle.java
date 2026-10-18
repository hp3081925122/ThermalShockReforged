package com.hp.thermalshockreforged.client.particle;

import com.hp.thermalshockreforged.ThermalShockReforged;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public final class TeleportFlashParticle extends TextureSheetParticle {
    private static final ResourceLocation EFFECT = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "teleportflash");
    private TeleportFlashParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z); setSize(0.2F, 0.2F); lifetime = 0; gravity = 0; hasPhysics = true;
        xd = 0; yd = 0; zd = 0; pickSprite(sprites);
    }
    @Override public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_LIT; }
    @Override public void tick() {
        super.tick();
        AAALevel.addParticle(level, new ParticleEmitterInfo(EFFECT).position(new Vec3(x, y, z)));
    }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new TeleportFlashParticle(level, x, y, z, sprites);
        }
    }
}
