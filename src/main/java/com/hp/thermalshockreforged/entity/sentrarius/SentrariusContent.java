package com.hp.thermalshockreforged.entity.sentrarius;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class SentrariusContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SentrariusEntity>> SENTRARIUS = TYPES.register("sentrarius", () -> EntityType.Builder.of(SentrariusEntity::new, MobCategory.AMBIENT).sized(.8F, 2.4F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("sentrarius")));
    static { ITEMS.register("sentrarius_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(SENTRARIUS.get()))); }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(SentrariusContent::attributes); bus.addListener(SentrariusContent::placements); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SENTRARIUS.get(), SentrariusEntity.createAttributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) { event.register(SENTRARIUS.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getBlockState(pos.below()).is(TerrainContent.SNOWY_SHALE.get()) || level.getBlockState(pos.below()).is(TerrainContent.GRASSY_SHALE.get()), RegisterSpawnPlacementsEvent.Operation.REPLACE); }
    private SentrariusContent() { }
}
