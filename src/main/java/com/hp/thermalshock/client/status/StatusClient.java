package com.hp.thermalshock.client.status;

import com.hp.thermalshock.content.status.StatusContent;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class StatusClient {
    private StatusClient() { }
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) { return false; }
        }, StatusContent.FROSTBITE);
    }
}
