package com.hp.thermalshockreforged.entity.pots;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.*;

public final class PotContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SkullscorEntity>> SKULLSCOR = TYPES.register("skullscor", () -> EntityType.Builder.of(SkullscorEntity::new, MobCategory.MONSTER).sized(1, 0.6F).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":skullscor"));
    public static final DeferredItem<SpawnEggItem> SKULLSCOR_EGG = ITEMS.register("skullscor_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(SKULLSCOR.get())));
    private PotContent() { }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus);
        bus.addListener(PotContent::attributes); bus.addListener(PotContent::placements);
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SKULLSCOR.get(), SkullscorEntity.createAttributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(SKULLSCOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random)
                        && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
