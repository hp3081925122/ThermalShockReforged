package com.hp.thermalshockreforged.client.squall;

import com.hp.thermalshockreforged.client.particle.AaaStationaryParticle;
import com.hp.thermalshockreforged.entity.squall.SquallContent;
import net.minecraft.client.particle.ParticleRenderType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class SquallRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SquallContent.SQUALL.get(), SquallRenderer::new); }
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SquallContent.VISAGE.get(), SquallVisageParticle.Provider::new);
        event.registerSpriteSet(SquallContent.SMOKE.get(), sprites -> new AaaStationaryParticle.Provider(sprites, "squallsmoke", 1, ParticleRenderType.PARTICLE_SHEET_OPAQUE, false, false, 0));
    }
    private SquallRenderers() { }
}
