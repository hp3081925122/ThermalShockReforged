package com.hp.thermalshockreforged.entity.flashring;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class BlindingLanternFlashRingContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<BlindingLanternFlashRingEntity>> FLASH_RING = TYPES.register("blinding_lantern_flash_ring", () -> EntityType.Builder.of(BlindingLanternFlashRingEntity::new, MobCategory.MONSTER).sized(.1F, .1F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("blinding_lantern_flash_ring").toString()));
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); bus.addListener(BlindingLanternFlashRingContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(FLASH_RING.get(), BlindingLanternFlashRingEntity.createAttributes().build()); }
    private BlindingLanternFlashRingContent() { }
}
