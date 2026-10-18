package com.hp.thermalshockreforged.content.status;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

public final class StatusContent {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> PARRYING = EFFECTS.register("parrying", () -> new PlayerStatusEffect(MobEffectCategory.BENEFICIAL, -1, PlayerStatusEffect.Action.NONE, false));
    public static final DeferredHolder<MobEffect, MobEffect> KNIGHTS_EDGE_CHARGE = EFFECTS.register("knights_edge_charge", () -> new PlayerStatusEffect(MobEffectCategory.BENEFICIAL, -1, PlayerStatusEffect.Action.NONE, true));
    public static final DeferredHolder<MobEffect, MobEffect> INFECTED = EFFECTS.register("infected", InfectedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> STUNNED = EFFECTS.register("stunned", () -> new ControlEffect(false));
    public static final DeferredHolder<MobEffect, MobEffect> TREMORS = EFFECTS.register("tremors", () -> new ControlEffect(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> BLOOD_SPIRAL = PARTICLES.register("blood_spiral", () -> new net.minecraft.core.particles.SimpleParticleType(true));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> BLOOD_ATTACK = PARTICLES.register("blood_attack", () -> new net.minecraft.core.particles.SimpleParticleType(true));
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
        PARTICLES.register(bus);
        NeoForge.EVENT_BUS.addListener(StatusEvents::onHeal);
        NeoForge.EVENT_BUS.addListener(StatusEvents::onDamage);
        NeoForge.EVENT_BUS.addListener(InfectionStatus::tick);
        NeoForge.EVENT_BUS.addListener(InfectionStatus::leave);
        NeoForge.EVENT_BUS.addListener(InfectionStatus::join);

    }
}
