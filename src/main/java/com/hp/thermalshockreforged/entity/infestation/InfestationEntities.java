package com.hp.thermalshockreforged.entity.infestation;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class InfestationEntities {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<InfestophageSeedEntity>> SEED = TYPES.register("infestophage_seed", () -> EntityType.Builder.of(InfestophageSeedEntity::new, MobCategory.AMBIENT).sized(0, 0).fireImmune().clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("infestophage_seed")));
    public static final DeferredHolder<EntityType<?>, EntityType<InfestophageEntity>> INFESTOPHAGE = TYPES.register("infestophage", () -> EntityType.Builder.of(InfestophageEntity::new, MobCategory.MONSTER).sized(0.8F, 2).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("infestophage")));
    public static final DeferredHolder<EntityType<?>, EntityType<LouseEntity>> LOUSE = TYPES.register("louse", () -> EntityType.Builder.of(LouseEntity::new, MobCategory.AMBIENT).sized(0.2F, 0.2F).clientTrackingRange(32).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("louse")));

    static {
        ITEMS.register("infestophage_seed_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(SEED.get())));
        ITEMS.register("louse_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(LOUSE.get())));
    }

    private InfestationEntities() { }

    static boolean hasType(LivingEntity entity, String path) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path));
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        ITEMS.register(bus);
        bus.addListener(InfestationEntities::attributes);
        bus.addListener(InfestationEntities::placements);
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(SEED.get(), InfestophageSeedEntity.createAttributes().build());
        event.put(INFESTOPHAGE.get(), InfestophageEntity.createAttributes().build());
        event.put(LOUSE.get(), LouseEntity.createAttributes().build());
    }

    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(SEED.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
            return id.getNamespace().equals(ThermalShockReforged.MOD_ID)
                    && java.util.Set.of("snowy_shale", "grassy_shale", "snowy_cobalt", "snowy_sanctum_bricks").contains(id.getPath())
                    && level.canSeeSkyFromBelowWater(pos);
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LOUSE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
