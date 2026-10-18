package com.hp.thermalshock.client.status.aura;

import com.hp.thermalshock.content.status.aura.AuraContent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.*;

public final class AuraClient {
    private AuraClient() { }
    public static void particles(RegisterParticleProvidersEvent event) { event.registerSpriteSet(AuraContent.BLOOMER_AURA.get(), BloomerAuraParticle.Provider::new); }
    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance instance) { return false; }
            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) { return false; }
            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) { return false; }
        }, AuraContent.DAMAGING_AURA);
    }
}
