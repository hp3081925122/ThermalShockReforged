package com.hp.thermalshockreforged.client.particle;

import com.hp.thermalshockreforged.client.vfx.VfxManager;
import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;

public final class ZapAttackParticle extends LegacySingleQuadParticle {
    private static final Identifier EFFECT = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "zapattack");
    private ZapAttackParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        setSize(0.2F, 0.2F); lifetime = 1; gravity = 0; hasPhysics = true;
        xd = 0; yd = 0; zd = 0; pickSprite(sprites);
    }
    public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    @Override public void tick() {
        super.tick();
        VfxManager.play(level, EFFECT, x, y, z);
    }
    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new ZapAttackParticle(level, x, y, z, sprites);
        }
    }
}
