package com.hp.thermalshockreforged.client.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.icesquad.IcidrostriderEntity;
import com.hp.thermalshockreforged.entity.icesquad.StriderFirePayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class StriderInput {
    private static final KeyMapping FIRE = new KeyMapping("key.thermal_shock_reforged.icidrostrider_attack", 32, KeyMapping.Category.GAMEPLAY);
    private StriderInput() { }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        while (FIRE.consumeClick()) {
            if (client.screen == null && client.player != null && client.getConnection() != null && client.player.getVehicle() instanceof IcidrostriderEntity strider && strider.getControllingPassenger() == client.player) ClientPacketDistributor.sendToServer(StriderFirePayload.INSTANCE);
        }
    }
    @EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
    public static final class Registration {
        @SubscribeEvent public static void register(RegisterKeyMappingsEvent event) { event.register(FIRE); }
    }
}
