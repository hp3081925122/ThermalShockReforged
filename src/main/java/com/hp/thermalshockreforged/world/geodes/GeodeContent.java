package com.hp.thermalshockreforged.world.geodes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GeodeContent {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ThermalShockReforged.MOD_ID);

    static {
        FEATURES.register("malachite_geode", () -> new ConditionalGeodeFeature(1));
        FEATURES.register("bismuth_geode", () -> new ConditionalGeodeFeature(2));
    }

    private GeodeContent() { }

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
