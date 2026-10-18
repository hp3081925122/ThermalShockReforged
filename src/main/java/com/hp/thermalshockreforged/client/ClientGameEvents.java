package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.armor.ArmorClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        ArmorClient.playerTick(event);
    }
}
