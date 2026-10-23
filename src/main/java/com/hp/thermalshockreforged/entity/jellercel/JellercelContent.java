package com.hp.thermalshockreforged.entity.jellercel;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class JellercelContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final Map<JellercelKind, DeferredHolder<EntityType<?>, EntityType<JellercelEntity>>> VALUES = new EnumMap<>(JellercelKind.class);
    static {
        for (JellercelKind kind : JellercelKind.values()) {
            var type = TYPES.register(kind.id, () -> EntityType.Builder.<JellercelEntity>of((entityType, level) -> new JellercelEntity(entityType, level, kind), MobCategory.WATER_AMBIENT)
                    .sized(kind.width, kind.height).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey(kind.id)));
            VALUES.put(kind, type);
            ITEMS.register(kind.id + "_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(type.get())));
        }
    }
    private JellercelContent() { }
    public static EntityType<JellercelEntity> type(JellercelKind kind) { return VALUES.get(kind).get(); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(JellercelContent::attributes); bus.addListener(JellercelContent::placements); }
    private static void attributes(EntityAttributeCreationEvent event) { for (JellercelKind kind : JellercelKind.values()) event.put(type(kind), kind.attributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        for (JellercelKind kind : JellercelKind.values()) event.register(type(kind), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).is(Blocks.WATER), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
