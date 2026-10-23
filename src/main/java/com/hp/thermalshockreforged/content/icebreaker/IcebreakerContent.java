package com.hp.thermalshockreforged.content.icebreaker;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IcebreakerContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<IcebreakerPulseEntity>> ICEBREAKER_PULSE = ENTITIES.register("icebreaker_pulse", () -> EntityType.Builder.of(IcebreakerPulseEntity::new, MobCategory.MISC)
            .noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20)
            .build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("icebreaker_pulse")));

    private IcebreakerContent() { }
    public static void register(IEventBus bus) { ENTITIES.register(bus); }
}
