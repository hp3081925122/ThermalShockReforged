package com.hp.thermalshockreforged.world.thermite;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class ThermiteContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ThermiteDetonationEntity>> DETONATION = TYPES.register("thermite_detonation", () -> EntityType.Builder.of(ThermiteDetonationEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(0.01F, 0.01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":thermite_detonation"));
    private ThermiteContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); }
}
