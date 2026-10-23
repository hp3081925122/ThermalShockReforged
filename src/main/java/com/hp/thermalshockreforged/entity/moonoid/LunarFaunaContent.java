package com.hp.thermalshockreforged.entity.moonoid;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.entity.starrite.StarriteEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class LunarFaunaContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<MoonoidEntity>> MOONOID = TYPES.register("moonoid", () -> EntityType.Builder.of(MoonoidEntity::new, MobCategory.MONSTER).sized(.4F, .4F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("moonoid")));
    public static final DeferredHolder<EntityType<?>, EntityType<StarriteEntity>> STARRITE = TYPES.register("starrite", () -> EntityType.Builder.of(StarriteEntity::new, MobCategory.MONSTER).sized(.4F, .4F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("starrite")));
    public static void register(IEventBus bus) { TYPES.register(bus); bus.addListener(LunarFaunaContent::attributes); NeoForge.EVENT_BUS.addListener(LunarFaunaContent::attack); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(MOONOID.get(), MoonoidEntity.attributes().build()); event.put(STARRITE.get(), StarriteEntity.attributes().build()); }
    private static void attack(AttackAttemptEvent event) { if (event.getEntity() instanceof MoonoidEntity && event.getSource().getEntity() instanceof StarriteEntity) event.setCanceled(true); }
    private LunarFaunaContent() { }
}
