package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.*;

public final class WeaponContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredItem<ZapGauntletItem> ZAP_GAUNTLET = ITEMS.register("zap_gauntlet", ZapGauntletItem::new);
    public static final DeferredItem<RustyEdgeItem> RUSTY_EDGE = ITEMS.register("rusty_edge", RustyEdgeItem::new);
    public static final DeferredItem<BlindingLanternItem> BLINDING_LANTERN = ITEMS.register("blinding_lantern", BlindingLanternItem::new);

    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> SICKLE_WOUND = EFFECTS.register("sickle_wound_buildup", SickleItem.WoundEffect::new);
    public static final DeferredItem<SickleItem> RUBY_SICKLE = ITEMS.register("ruby_sickle", () -> new SickleItem("ruby_sickle", 1200, 10, "ruby"));
    public static final DeferredItem<SickleItem> BLOODSTONE_SICKLE = ITEMS.register("bloodstone_sickle", () -> new SickleItem("bloodstone_sickle", 1500, 8, "bloodstone"));
    private WeaponContent() { }

    public static void register(IEventBus bus) {
        ITEMS.register(bus); EFFECTS.register(bus);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::tick);
        NeoForge.EVENT_BUS.addListener(HeldWeaponState::join);
        NeoForge.EVENT_BUS.addListener(WeaponContent::serverTick);
        NeoForge.EVENT_BUS.addListener(WeaponContent::serverStopped);
    }

    private static void serverTick(ServerTickEvent.Post event) { BLINDING_LANTERN.get().pruneAnimations(); ZAP_GAUNTLET.get().pruneAnimations(); }
    private static void serverStopped(ServerStoppedEvent event) { BLINDING_LANTERN.get().clearAnimations(); ZAP_GAUNTLET.get().clearAnimations(); }
}
