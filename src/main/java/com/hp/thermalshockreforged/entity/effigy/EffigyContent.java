package com.hp.thermalshockreforged.entity.effigy;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class EffigyContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<EffigyEntity>> EFFIGY = TYPES.register("effigy", () -> EntityType.Builder.of(EffigyEntity::new, MobCategory.MONSTER).sized(1, 2.9F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":effigy"));
    public static final DeferredHolder<EntityType<?>, EntityType<EffigyStrikeEntity>> STRIKE = TYPES.register("effigy_strike_task", () -> EntityType.Builder.<EffigyStrikeEntity>of(EffigyStrikeEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":effigy_strike_task"));
    static { ITEMS.register("effigy_spawn_egg", () -> new DeferredSpawnEggItem(EFFIGY, -1, -1, new Item.Properties())); }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(EffigyContent::attributes); bus.addListener(EffigyContent::placements); NeoForge.EVENT_BUS.addListener(EffigyContent::attack); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(EFFIGY.get(), EffigyEntity.attributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(EFFIGY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof EffigyEntity caster) { event.setCanceled(true); caster.beginAttack(event.getEntity()); } }
    private EffigyContent() { }
}
