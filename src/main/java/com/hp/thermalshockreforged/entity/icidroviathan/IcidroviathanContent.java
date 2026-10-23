package com.hp.thermalshockreforged.entity.icidroviathan;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class IcidroviathanContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<IcidroviathanEntity>> ICIDROVIATHAN = TYPES.register("icidroviathan", () -> EntityType.Builder.of(IcidroviathanEntity::new, MobCategory.MONSTER).sized(2, 1.5F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("icidroviathan")));
    public static void register(IEventBus bus) { TYPES.register(bus); bus.addListener(IcidroviathanContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(ICIDROVIATHAN.get(), IcidroviathanEntity.attributes().build()); }
    private IcidroviathanContent() { }
}
