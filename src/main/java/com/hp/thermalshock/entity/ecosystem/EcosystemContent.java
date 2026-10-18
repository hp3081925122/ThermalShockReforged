package com.hp.thermalshock.entity.ecosystem;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.entity.ecosystem.block.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class EcosystemContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShock.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShock.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShock.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ChillooEntity>> CHILLOO = TYPES.register("chilloo", () -> EntityType.Builder.of(ChillooEntity::new, MobCategory.AMBIENT).sized(1.2F, 1.3F).clientTrackingRange(64).updateInterval(3).build("thermal_shock:chilloo"));
    public static final DeferredHolder<EntityType<?>, EntityType<SpringlingEntity>> SPRINGLING = TYPES.register("springling", () -> EntityType.Builder.of(SpringlingEntity::new, MobCategory.AMBIENT).sized(0.9F, 0.9F).clientTrackingRange(64).updateInterval(3).build("thermal_shock:springling"));
    public static final DeferredHolder<EntityType<?>, EntityType<RaincatcherEntity>> RAINCATCHER = TYPES.register("raincatcher", () -> EntityType.Builder.of(RaincatcherEntity::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(64).updateInterval(3).build("thermal_shock:raincatcher"));
    public static final DeferredBlock<ChillooEggBlock> CHILLOO_EGG = BLOCKS.register("chilloo_egg", ChillooEggBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChillooEggBlockEntity>> CHILLOO_EGG_ENTITY = BLOCK_ENTITIES.register("chilloo_egg", () -> BlockEntityType.Builder.of(ChillooEggBlockEntity::new, CHILLOO_EGG.get()).build(null));

    static {
        ITEMS.registerSimpleBlockItem(CHILLOO_EGG);
        ITEMS.register("chilloo_spawn_egg", () -> new DeferredSpawnEggItem(CHILLOO, -1, -1, new Item.Properties()));
        ITEMS.register("springling_spawn_egg", () -> new DeferredSpawnEggItem(SPRINGLING, -1, -1, new Item.Properties()));
        ITEMS.register("raincatcher_spawn_egg", () -> new DeferredSpawnEggItem(RAINCATCHER, -1, -1, new Item.Properties()));
    }

    private EcosystemContent() { }

    static boolean hasType(LivingEntity entity, String path) { return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, path)); }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        ITEMS.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        bus.addListener(EcosystemContent::attributes);
        bus.addListener(EcosystemContent::placements);
        bus.addListener(EcosystemContent::capabilities);
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(CHILLOO.get(), ChillooEntity.createAttributes().build());
        event.put(SPRINGLING.get(), SpringlingEntity.createAttributes().build());
        event.put(RAINCATCHER.get(), RaincatcherEntity.createAttributes().build());
    }

    private static void capabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, CHILLOO_EGG_ENTITY.get(), (egg, side) -> side == null ? null : new net.neoforged.neoforge.items.wrapper.SidedInvWrapper(egg, side));
    }

    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(CHILLOO.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SPRINGLING.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
            return id.getNamespace().equals(ThermalShock.MOD_ID) && id.getPath().equals("shale");
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(RAINCATCHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
            return level.getLevelData().isThundering() && id.getNamespace().equals(ThermalShock.MOD_ID)
                    && (id.getPath().equals("snowy_shale") || id.getPath().equals("snowy_cobalt"));
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
