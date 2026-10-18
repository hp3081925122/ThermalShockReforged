package com.hp.thermalshock.content.status;

import com.hp.thermalshock.ThermalShock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

public final class StatusContent {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShock.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ThermalShock.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> FROSTBITE = EFFECTS.register("frostbite", () -> new PlayerStatusEffect(MobEffectCategory.HARMFUL, -13464321, PlayerStatusEffect.Action.NONE, false));
    public static final DeferredHolder<MobEffect, MobEffect> COCOA_EFFECT = EFFECTS.register("cocoa_effect", () -> new PlayerStatusEffect(MobEffectCategory.BENEFICIAL, -1935513, PlayerStatusEffect.Action.WARM, true));
    public static final DeferredHolder<MobEffect, MobEffect> ANTITOXIN_EFFECT = EFFECTS.register("antitoxin_effect", () -> new PlayerStatusEffect(MobEffectCategory.BENEFICIAL, -65383, PlayerStatusEffect.Action.ANTITOXIN, true));
    public static final DeferredHolder<MobEffect, MobEffect> PURIFIED = EFFECTS.register("purified", () -> new PlayerStatusEffect(MobEffectCategory.BENEFICIAL, -13369600, PlayerStatusEffect.Action.ANTITOXIN, true));
    public static final DeferredHolder<MobEffect, MobEffect> ELECTRIFIED = EFFECTS.register("electrified", () -> new PlayerStatusEffect(MobEffectCategory.HARMFUL, -13369345, PlayerStatusEffect.Action.NONE, true));
    public static final DeferredItem<Item> COCOA = ITEMS.register("cocoa", CocoaItem::new);
    public static final DeferredItem<Item> PANACEA = ITEMS.register("panacea", PanaceaItem::new);
    public static final DeferredHolder<Potion, Potion> ELECTRIFYING = POTIONS.register("electrifying", () -> new Potion(new MobEffectInstance(ELECTRIFIED, 360, 1, false, true)));

    private StatusContent() { }

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
        ITEMS.register(bus);
        POTIONS.register(bus);
        NeoForge.EVENT_BUS.addListener(StatusEvents::onHeal);
        NeoForge.EVENT_BUS.addListener(StatusEvents::onDamage);

    }
}

