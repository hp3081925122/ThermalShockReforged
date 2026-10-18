package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LoqendiaContent {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLD_GLITTER = PARTICLES.register("gold_glitter", () -> new SimpleParticleType(false));
    private static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WaterloggingFixProcessor>> WATERLOGGING_FIX = PROCESSORS.register("waterlogging_fix", () -> () -> WaterloggingFixProcessor.CODEC);
    private LoqendiaContent() { }
    public static void register(IEventBus bus) { PARTICLES.register(bus); PROCESSORS.register(bus); }
}
