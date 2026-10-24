package com.hp.thermalshockreforged.client.squall;

import com.hp.thermalshockreforged.entity.squall.SquallContent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class SquallRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SquallContent.SQUALL.get(), SquallRenderer::new); }
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SquallContent.SMOKE.get(), sprites -> new com.hp.thermalshockreforged.client.particle.AaaStationaryParticle.Provider(sprites, "squallsmoke", 1, net.minecraft.client.particle.SingleQuadParticle.Layer.OPAQUE, false, false, 0));
        event.registerSpriteSet(SquallContent.WISP.get(), SquallParticles.WispProvider::new);
        event.registerSpriteSet(SquallContent.MARK.get(), SquallParticles.MarkProvider::new);
        event.registerSpriteSet(SquallContent.BURST.get(), SquallParticles.BurstProvider::new);
        event.registerSpriteSet(SquallContent.BOLT.get(), SquallParticles.BoltProvider::new);
    }
    private SquallRenderers() { }
}
