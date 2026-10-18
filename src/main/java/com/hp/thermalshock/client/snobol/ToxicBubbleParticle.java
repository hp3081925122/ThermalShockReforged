package com.hp.thermalshock.client.snobol;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

final class ToxicBubbleParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private ToxicBubbleParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        setSize(0.2F, 0.2F);
        quadSize *= 1.3F;
        lifetime = Math.max(1, 30 + random.nextInt(24) - 12);
        gravity = -0.2F;
        hasPhysics = false;
        xd = vx;
        yd = vy;
        zd = vz;
        setSpriteFromAge(sprites);
    }

    @Override
    protected int getLightColor(float partialTick) { return 0xF000F0; }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_LIT; }

    @Override
    public void tick() {
        super.tick();
        if (!removed) setSprite(sprites.get(age / 6 % 10 + 1, 10));
    }

    static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) { return new ToxicBubbleParticle(level, x, y, z, dx, dy, dz, sprites); }
    }
}
