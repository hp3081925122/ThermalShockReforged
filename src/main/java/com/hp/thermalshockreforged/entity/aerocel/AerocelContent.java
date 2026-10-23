package com.hp.thermalshockreforged.entity.aerocel;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class AerocelContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<AerocelEntity>> AEROCEL = TYPES.register("aerocel", () -> EntityType.Builder.of(AerocelEntity::new, MobCategory.AMBIENT).sized(4.6F, 4F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("aerocel")));
    static { ITEMS.register("aerocel_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(AEROCEL.get()))); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(AerocelContent::attributes); bus.addListener(AerocelContent::placements); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(AEROCEL.get(), AerocelEntity.attributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) { event.register(AEROCEL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> pos.getY() >= 90, RegisterSpawnPlacementsEvent.Operation.REPLACE); }
    private AerocelContent() { }
}
