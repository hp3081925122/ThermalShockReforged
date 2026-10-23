package com.hp.thermalshockreforged.entity.magnetite;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagnetiteContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<MagnetiteEntity>> MAGNETITE = TYPES.register("magnetite", () -> EntityType.Builder.of(MagnetiteEntity::new, MobCategory.MONSTER).sized(.8F, 2).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("magnetite")));
    public static final DeferredHolder<EntityType<?>, EntityType<MagnetiteBulletEntity>> BULLET = TYPES.register("magnetite_bullet", () -> EntityType.Builder.<MagnetiteBulletEntity>of(MagnetiteBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("magnetite_bullet")));
    public static final DeferredHolder<Item, Item> EGG = ITEMS.register("magnetite_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(MAGNETITE.get())));
    private MagnetiteContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(MagnetiteContent::attributes); bus.addListener(MagnetiteContent::placements); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(MAGNETITE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(MAGNETITE.get(), MagnetiteEntity.createAttributes().build()); }
}
