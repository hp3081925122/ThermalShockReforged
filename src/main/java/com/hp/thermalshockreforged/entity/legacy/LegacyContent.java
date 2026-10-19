package com.hp.thermalshockreforged.entity.legacy;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LegacyContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SolaEntity>> SOLA = TYPES.register("sola", () -> EntityType.Builder.of(SolaEntity::new, MobCategory.MONSTER).sized(.9F, 1.8F).clientTrackingRange(32).updateInterval(3).build("thermal_shock_reforged:sola"));
    public static final DeferredHolder<Item, Item> SOLA_EGG = ITEMS.register("sola_spawn_egg", () -> new DeferredSpawnEggItem(SOLA, -1, -1, new Item.Properties()));
    private LegacyContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(LegacyContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SOLA.get(), SolaEntity.createAttributes().build()); }
}
