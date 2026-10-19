package com.hp.thermalshockreforged.entity.magnetite;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagnetiteContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<MagnetiteEntity>> MAGNETITE = TYPES.register("magnetite", () -> EntityType.Builder.of(MagnetiteEntity::new, MobCategory.MONSTER).sized(.8F, 2).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:magnetite"));
    public static final DeferredHolder<EntityType<?>, EntityType<MagnetiteBulletEntity>> BULLET = TYPES.register("magnetite_bullet", () -> EntityType.Builder.<MagnetiteBulletEntity>of(MagnetiteBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:magnetite_bullet"));
    public static final DeferredHolder<Item, Item> EGG = ITEMS.register("magnetite_spawn_egg", () -> new DeferredSpawnEggItem(MAGNETITE, -1, -1, new Item.Properties()));
    private MagnetiteContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(MagnetiteContent::attributes); bus.addListener(MagnetiteContent::placements); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(MAGNETITE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> true, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(MAGNETITE.get(), MagnetiteEntity.createAttributes().build()); }
}
