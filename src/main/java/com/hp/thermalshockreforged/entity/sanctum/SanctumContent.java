package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class SanctumContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> ERRANT = errant("errant");
    public static final DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> SANCTUM_ERRANT = errant("sanctum_errant");
    public static final DeferredItem<DeferredSpawnEggItem> ERRANT_EGG = ITEMS.register("errant_spawn_egg", () -> new DeferredSpawnEggItem(ERRANT, -1, -1, new Item.Properties()));
    private SanctumContent() { }
    private static DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> errant(String name) {
        return TYPES.register(name, () -> EntityType.Builder.of(ErrantEntity::new, MobCategory.MONSTER).sized(.8F, 1.9F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":" + name));
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        ITEMS.register(bus);
        bus.addListener(SanctumContent::attributes);
        bus.addListener(SanctumContent::placements);
        NeoForge.EVENT_BUS.addListener(SanctumContent::attack);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(ERRANT.get(), ErrantEntity.createAttributes(false).build());
        event.put(SANCTUM_ERRANT.get(), ErrantEntity.createAttributes(true).build());
    }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(ERRANT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof ErrantEntity errant) { event.setCanceled(true); errant.beginAttack(event.getEntity()); }
    }
}
