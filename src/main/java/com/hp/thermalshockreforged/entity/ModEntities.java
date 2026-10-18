package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ForecastEntity>> FORECAST = TYPES.register("forecast", () -> EntityType.Builder.of(ForecastEntity::new, MobCategory.MONSTER).sized(0.8F, 1.2F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:forecast"));
    public static final DeferredHolder<EntityType<?>, EntityType<ZapWarningEntity>> ZAP = TYPES.register("zap_attack_entity", () -> EntityType.Builder.<ZapWarningEntity>of(ZapWarningEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:zap_attack_entity"));

    public static final DeferredHolder<EntityType<?>, EntityType<CrystalKoiEntity>> CRYSTAL_KOI = TYPES.register("crystal_koi", () -> EntityType.Builder.of(CrystalKoiEntity::new, MobCategory.WATER_AMBIENT).sized(0.3F, 0.3F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:crystal_koi"));

    public static final DeferredHolder<EntityType<?>, EntityType<ScarletRayEntity>> SCARLET_RAY = TYPES.register("scarlet_ray", () -> EntityType.Builder.of(ScarletRayEntity::new, MobCategory.WATER_AMBIENT).sized(1.0F, 0.3F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:scarlet_ray"));

    public static final DeferredHolder<EntityType<?>, EntityType<ProboskerEntity>> PROBOSKER = TYPES.register("probosker", () -> EntityType.Builder.of(ProboskerEntity::new, MobCategory.AMBIENT).sized(0.9F, 1.0F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:probosker"));

    public static final DeferredHolder<EntityType<?>, EntityType<PondlerEntity>> PONDLER = TYPES.register("pondler", () -> EntityType.Builder.of(PondlerEntity::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.6F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:pondler"));

    static {
        ITEMS.register("pondler_spawn_egg", () -> new DeferredSpawnEggItem(PONDLER, -1, -1, new Item.Properties()));
        ITEMS.register("probosker_spawn_egg", () -> new DeferredSpawnEggItem(PROBOSKER, -1, -1, new Item.Properties()));
        ITEMS.register("scarlet_ray_spawn_egg", () -> new DeferredSpawnEggItem(SCARLET_RAY, -1, -1, new Item.Properties()));
        ITEMS.register("crystal_koi_spawn_egg", () -> new DeferredSpawnEggItem(CRYSTAL_KOI, -1, -1, new Item.Properties()));
        ITEMS.register("forecast_spawn_egg", () -> new DeferredSpawnEggItem(FORECAST, -1, -1, new Item.Properties()));
    }

    private ModEntities() {
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        ITEMS.register(bus);
        bus.addListener(ModEntities::attributes);
        bus.addListener(ModEntities::spawnPlacements);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ForecastEntity::join);
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(PONDLER.get(), PondlerEntity.createAttributes().build());
        event.put(PROBOSKER.get(), ProboskerEntity.createAttributes().build());
        event.put(SCARLET_RAY.get(), ScarletRayEntity.createAttributes().build());
        event.put(CRYSTAL_KOI.get(), CrystalKoiEntity.createAttributes().build());
        event.put(FORECAST.get(), ForecastEntity.createAttributes().build());
    }

    private static void spawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(PONDLER.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, position, random) -> level.getBlockState(position).is(net.minecraft.world.level.block.Blocks.WATER) && level.getBlockState(position.above()).is(net.minecraft.world.level.block.Blocks.WATER), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PROBOSKER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, position, random) -> {
            var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(position.below()).getBlock());
            return id.getNamespace().equals(ThermalShockReforged.MOD_ID) && (id.getPath().equals("grassy_shale") || id.getPath().equals("snowy_shale"))
                    && level.canSeeSkyFromBelowWater(position) && level.getLevel().isDay();
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRYSTAL_KOI.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, position, random) -> level.getBlockState(position).is(net.minecraft.world.level.block.Blocks.WATER) && level.getBlockState(position.above()).is(net.minecraft.world.level.block.Blocks.WATER), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SCARLET_RAY.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, position, random) -> level.getBlockState(position).is(net.minecraft.world.level.block.Blocks.WATER) && level.getBlockState(position.above()).is(net.minecraft.world.level.block.Blocks.WATER), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FORECAST.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, position, random) -> {
            if (!level.getLevel().isThundering()) return false;
            var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(position.below()).getBlock());
            return id.getNamespace().equals(ThermalShockReforged.MOD_ID) && (id.getPath().equals("snowy_shale") || id.getPath().equals("snowy_cobalt"));
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
