package com.hp.thermalshockreforged.content.status.aura;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class AuraContent {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> DAMAGING_AURA = EFFECTS.register("damaging_aura", DamagingAuraEffect::new);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOMER_AURA = PARTICLES.register("bloomer_aura", () -> new SimpleParticleType(true));
    private AuraContent() { }
    public static void register(IEventBus bus) { EFFECTS.register(bus); PARTICLES.register(bus); }
}
