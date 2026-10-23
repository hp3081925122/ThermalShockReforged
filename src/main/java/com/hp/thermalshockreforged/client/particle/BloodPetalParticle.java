package com.hp.thermalshockreforged.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import com.hp.thermalshockreforged.client.compat.LegacySingleQuadParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class BloodPetalParticle extends LegacySingleQuadParticle {
    private BloodPetalParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z);
        setSize(0.1F, 0.1F);
        lifetime = Math.max(1, 60 + random.nextInt(20) - 10);
        gravity = -0.4F;
        hasPhysics = true;
        xd = dx * 0.6;
        yd = dy * 0.6;
        zd = dz * 0.6;
        pickSprite(sprites);
    }

    @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }

    @Override public void tick() {
        super.tick();
        oRoll = roll;
        roll += 0.05F;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new BloodPetalParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
