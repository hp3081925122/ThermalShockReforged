package com.hp.thermalshockreforged.entity.benzor;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.benzor.BenzorMenus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class BenzorContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<BenzorEntity>> BENZOR = TYPES.register("benzor", () -> EntityType.Builder.of(BenzorEntity::new, MobCategory.MONSTER).sized(3, 3.3F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("benzor").toString()));
    static { ITEMS.register("benzor_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(BENZOR.get()))); }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); BenzorMenus.register(bus); bus.addListener(BenzorContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(BENZOR.get(), BenzorEntity.createAttributes().build()); }
    private BenzorContent() { }
}
