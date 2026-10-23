package com.hp.thermalshockreforged.client.cavebloomer;

import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import com.hp.thermalshockreforged.client.attack.AttackCarrierRenderer;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class CavebloomerClient {
    private CavebloomerClient() { }
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CavebloomerContent.ANGRY.get(), AngryCavebloomerRenderer::new);
        event.registerEntityRenderer(CavebloomerContent.BUDDING.get(), context -> new BloomlingRenderer(context, false));
        event.registerEntityRenderer(CavebloomerContent.DANDY.get(), context -> new BloomlingRenderer(context, true));
        event.registerEntityRenderer(CavebloomerContent.MORTAR_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CavebloomerContent.POISON_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CavebloomerContent.SHOCK_IMPACT.get(), AttackCarrierRenderer::new);
        event.registerEntityRenderer(CavebloomerContent.POISON_IMPACT.get(), AttackCarrierRenderer::new);
    }
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CavebloomerContent.NUKE_CLOUD.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "boomshroom", 0, ParticleRenderType.SINGLE_QUADS, true));
        event.registerSpriteSet(CavebloomerContent.RED_EXPLODE.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "redexplode", 0, ParticleRenderType.SINGLE_QUADS, false));
        event.registerSpriteSet(CavebloomerContent.MAGNETIC_BOOM.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "greenexplode", 0, ParticleRenderType.SINGLE_QUADS, false, false, 1));
        event.registerSpriteSet(CavebloomerContent.SHOCK_ATTACK.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "shockattack", 0, ParticleRenderType.SINGLE_QUADS, true));
        event.registerSpriteSet(CavebloomerContent.POISON_CLOUD.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "poisoncloud", 0, ParticleRenderType.SINGLE_QUADS, true));
        event.registerSpriteSet(CavebloomerContent.RED_ORB.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "redorb", 1, ParticleRenderType.SINGLE_QUADS, false));
    }
}
