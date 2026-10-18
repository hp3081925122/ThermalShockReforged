package com.hp.thermalshockreforged.client.cavebloomer;

import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class CavebloomerClient {
    private CavebloomerClient() { }
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CavebloomerContent.MORTAR_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CavebloomerContent.POISON_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CavebloomerContent.SHOCK_IMPACT.get(), NoopRenderer::new);
        event.registerEntityRenderer(CavebloomerContent.POISON_IMPACT.get(), NoopRenderer::new);
    }
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CavebloomerContent.SHOCK_ATTACK.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "shockattack", 0, ParticleRenderType.PARTICLE_SHEET_LIT, true));
        event.registerSpriteSet(CavebloomerContent.POISON_CLOUD.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "poisoncloud", 0, ParticleRenderType.PARTICLE_SHEET_LIT, true));
        event.registerSpriteSet(CavebloomerContent.RED_ORB.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "redorb", 1, ParticleRenderType.PARTICLE_SHEET_OPAQUE, false));
    }
}
