package com.hp.thermalshockreforged.entity.titans;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TitanContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final TagKey<EntityType<?>> SKELETITAN_TAG = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "skeletitan"));
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalTitanEntity>> SKELETITAN = titan(SkeletalTitanEntity.Kind.SKELETITAN);
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalTitanEntity>> FROST_TITAN = titan(SkeletalTitanEntity.Kind.FROST);
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalTitanEntity>> MANTLETITAN = titan(SkeletalTitanEntity.Kind.MANTLE);
    public static final DeferredHolder<EntityType<?>, EntityType<TitanSwingEntity>> SWING = TYPES.register("titan_swing", () -> EntityType.Builder.<TitanSwingEntity>of(TitanSwingEntity::new, MobCategory.MISC)
        .sized(.01F, .01F).noSummon().clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("titan_swing")));

    private static DeferredHolder<EntityType<?>, EntityType<SkeletalTitanEntity>> titan(SkeletalTitanEntity.Kind kind) {
        var type = TYPES.register(kind.id(), () -> EntityType.Builder.<SkeletalTitanEntity>of((entityType, level) -> new SkeletalTitanEntity(entityType, level, kind), MobCategory.MONSTER)
            .sized(1, 2.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey(kind.id())));
        ITEMS.register(kind.id() + "_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(type.get())));
        return type;
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        ITEMS.register(bus);
        bus.addListener(TitanContent::attributes);
        bus.addListener(TitanContent::placements);
        NeoForge.EVENT_BUS.addListener(TitanContent::attack);
    }

    private static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof SkeletalTitanEntity titan && titan.is(SKELETITAN_TAG)) {
            event.setCanceled(true);
            titan.beginAttack(event.getEntity());
        }
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(SKELETITAN.get(), SkeletalTitanEntity.createAttributes(SkeletalTitanEntity.Kind.SKELETITAN).build());
        event.put(FROST_TITAN.get(), SkeletalTitanEntity.createAttributes(SkeletalTitanEntity.Kind.FROST).build());
        event.put(MANTLETITAN.get(), SkeletalTitanEntity.createAttributes(SkeletalTitanEntity.Kind.MANTLE).build());
    }

    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(SKELETITAN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random),
            RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private TitanContent() { }
}
