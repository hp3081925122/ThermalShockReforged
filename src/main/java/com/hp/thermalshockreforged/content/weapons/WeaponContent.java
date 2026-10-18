package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.*;

public final class WeaponContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredItem<BlindingLanternItem> BLINDING_LANTERN = ITEMS.register("blinding_lantern", BlindingLanternItem::new);

    private WeaponContent() { }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::tick);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::join);
        NeoForge.EVENT_BUS.addListener(WeaponContent::serverTick);
        NeoForge.EVENT_BUS.addListener(WeaponContent::serverStopped);
    }

    private static void serverTick(ServerTickEvent.Post event) { BLINDING_LANTERN.get().pruneAnimations(); }
    private static void serverStopped(ServerStoppedEvent event) { BLINDING_LANTERN.get().clearAnimations(); }
}
