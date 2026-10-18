package com.hp.thermalshockreforged.world.ore;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OreContent {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ThermalShockReforged.MOD_ID);
    static {
        FEATURES.register("ruby_ore_generation", () -> new ConditionalOreFeature(1, 1));
        FEATURES.register("ruby_mantle_generation", () -> new ConditionalOreFeature(1, 1));
        FEATURES.register("blood_ore_generation", () -> new ConditionalOreFeature(1, 2));
        FEATURES.register("blood_mantle_generation", () -> new ConditionalOreFeature(1, 2));
        FEATURES.register("malachite_generation", () -> new ConditionalOreFeature(2, 1));
        FEATURES.register("bismuth_generation", () -> new ConditionalOreFeature(2, 2));
    }
    private OreContent() { }
    public static void register(IEventBus bus) { FEATURES.register(bus); }
}
