package com.hp.thermalshock.client.status;

import com.hp.thermalshock.content.status.StatusContent;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class StatusClient {
    private StatusClient() { }
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(StatusContent.BLOOD_SPIRAL.get(), sprites -> new InfectionParticle.Provider(sprites, "bloodspiral"));
        event.registerSpriteSet(StatusContent.BLOOD_ATTACK.get(), sprites -> new InfectionParticle.Provider(sprites, "bloodattack"));
    }
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) { return false; }
        }, StatusContent.FROSTBITE);
    }
}
