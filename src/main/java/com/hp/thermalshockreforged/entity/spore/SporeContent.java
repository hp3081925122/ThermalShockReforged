package com.hp.thermalshockreforged.entity.spore;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.spore.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class SporeContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<BlightedSporeEntity>> BLIGHTED_SPORE = TYPES.register("blighted_spore", () -> EntityType.Builder.of(BlightedSporeEntity::new, MobCategory.AMBIENT).fireImmune().sized(0.6F, 0.6F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:blighted_spore"));
    public static final DeferredBlock<SporePodBlock> SPORE_POD = BLOCKS.register("spore_pod", SporePodBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SporePodBlockEntity>> SPORE_POD_ENTITY = BLOCK_ENTITIES.register("spore_pod", () -> BlockEntityType.Builder.of(SporePodBlockEntity::new, SPORE_POD.get()).build(null));
    public static final DeferredItem<SporePodItem> SPORE_POD_ITEM = ITEMS.register("spore_pod", () -> new SporePodItem(SPORE_POD.get(), new Item.Properties()));
    static { ITEMS.register("blighted_spore_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(BLIGHTED_SPORE.get()))); }
    private SporeContent() { }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus); BLOCKS.register(bus); BLOCK_ENTITIES.register(bus);
        bus.addListener(SporeContent::attributes); bus.addListener(SporeContent::placements); bus.addListener(SporeContent::capabilities);
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(BLIGHTED_SPORE.get(), BlightedSporeEntity.createAttributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) { event.register(BLIGHTED_SPORE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE); }
    private static void capabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) { event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, SPORE_POD_ENTITY.get(), (pod, side) -> side == null ? null : new net.neoforged.neoforge.items.wrapper.SidedInvWrapper(pod, side)); }
}
