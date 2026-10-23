package com.hp.thermalshockreforged.entity.sanctum;
import net.minecraft.world.item.SpawnEggItem;

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
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> ERR_EFFECT = PARTICLES.register("err_effect", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> TURQ_EXPLODE = PARTICLES.register("turq_explode", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> FROST_RING = PARTICLES.register("frost_ring", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> FIRE_BOMB_EFFECT = PARTICLES.register("fire_bomb_effect", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredItem<FireBombItem> FIRE_BOMB = ITEMS.register("fire_bomb", FireBombItem::new);
    public static final DeferredHolder<EntityType<?>, EntityType<PylonCoreActivationEntity>> CORE_ACTIVATION = TYPES.register("pylon_core_activation", () -> EntityType.Builder.<PylonCoreActivationEntity>of(PylonCoreActivationEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("pylon_core_activation")));
    public static final DeferredHolder<EntityType<?>, EntityType<FireBombActivationEntity>> FIRE_BOMB_ACTIVATION = TYPES.register("fire_bomb_activation", () -> EntityType.Builder.<FireBombActivationEntity>of(FireBombActivationEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("fire_bomb_activation")));
    public static final DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> ERRANT = errant("errant");
    public static final DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> SANCTUM_ERRANT = errant("sanctum_errant");
    public static final DeferredItem<SpawnEggItem> ERRANT_EGG = ITEMS.register("errant_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(ERRANT.get())));
    public static final DeferredHolder<EntityType<?>, EntityType<RippleEntity>> RIPPLE = ripple("ripple");
    public static final DeferredHolder<EntityType<?>, EntityType<RippleEntity>> SANCTUM_RIPPLE = ripple("sanctum_ripple");
    public static final DeferredItem<SpawnEggItem> RIPPLE_EGG = ITEMS.register("ripple_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(RIPPLE.get())));
    public static final DeferredHolder<EntityType<?>, EntityType<ErrFieldEntity>> ERR = TYPES.register("err", () -> EntityType.Builder.of(ErrFieldEntity::new, MobCategory.MONSTER).sized(1, 2).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("err")));
    public static final DeferredHolder<EntityType<?>, EntityType<RippleCastEntity>> RIPPLE_CAST = TYPES.register("ripple_cast_task", () -> EntityType.Builder.<RippleCastEntity>of(RippleCastEntity::new, MobCategory.MISC).sized(.01F, .01F).clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("ripple_cast_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostilyteGunnerEntity>> FROSTILYTE_GUNNER = TYPES.register("frostilyte_gunner", () -> EntityType.Builder.of(FrostilyteGunnerEntity::new, MobCategory.MONSTER).sized(.6F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("frostilyte_gunner")));
    public static final DeferredItem<SpawnEggItem> FROSTILYTE_GUNNER_EGG = ITEMS.register("frostilyte_gunner_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(FROSTILYTE_GUNNER.get())));
    public static final DeferredHolder<EntityType<?>, EntityType<GunnerVolleyEntity>> GUNNER_VOLLEY = TYPES.register("gunner_volley_task", () -> EntityType.Builder.<GunnerVolleyEntity>of(GunnerVolleyEntity::new, MobCategory.MISC).sized(.01F, .01F).clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("gunner_volley_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<KilolyteEntity>> KILOLYTE = TYPES.register("kilolyte", () -> EntityType.Builder.of(KilolyteEntity::new, MobCategory.MONSTER).sized(.6F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("kilolyte")));
    public static final DeferredItem<SpawnEggItem> KILOLYTE_EGG = ITEMS.register("kilolyte_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(KILOLYTE.get())));
    public static final DeferredHolder<EntityType<?>, EntityType<KilolyteAttackEntity>> KILOLYTE_ATTACK = TYPES.register("kilolyte_attack_task", () -> EntityType.Builder.<KilolyteAttackEntity>of(KilolyteAttackEntity::new, MobCategory.MISC).sized(.01F, .01F).clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("kilolyte_attack_task")));
    public static final DeferredHolder<EntityType<?>, EntityType<GuardianPylonEntity>> GUARDIAN_PYLON = TYPES.register("guardian_pylon", () -> EntityType.Builder.of(GuardianPylonEntity::new, MobCategory.MONSTER).sized(1, 1).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("guardian_pylon")));
    public static final DeferredItem<SpawnEggItem> GUARDIAN_PYLON_EGG = ITEMS.register("guardian_pylon_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(GUARDIAN_PYLON.get())));
    public static final DeferredHolder<EntityType<?>, EntityType<PylonActivationEntity>> PYLON_ACTIVATION = TYPES.register("pylon_activation_task", () -> EntityType.Builder.<PylonActivationEntity>of(PylonActivationEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("pylon_activation_task")));
    private static DeferredHolder<EntityType<?>, EntityType<RippleEntity>> ripple(String name) {
        return TYPES.register(name, () -> EntityType.Builder.of(RippleEntity::new, MobCategory.MONSTER).sized(.6F, 2).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey(name)));
    }
    private SanctumContent() { }
    private static DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> errant(String name) {
        return TYPES.register(name, () -> EntityType.Builder.of(ErrantEntity::new, MobCategory.MONSTER).sized(.8F, 1.9F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey(name)));
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        PARTICLES.register(bus);
        ITEMS.register(bus);
        bus.addListener(SanctumContent::attributes);
        bus.addListener(SanctumContent::placements);
        NeoForge.EVENT_BUS.addListener(SanctumContent::attack);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(GUARDIAN_PYLON.get(), GuardianPylonEntity.createAttributes().build());
        event.put(KILOLYTE.get(), KilolyteEntity.createAttributes().build());
        event.put(FROSTILYTE_GUNNER.get(), FrostilyteGunnerEntity.createAttributes().build());
        event.put(RIPPLE.get(), RippleEntity.createAttributes(false).build());
        event.put(SANCTUM_RIPPLE.get(), RippleEntity.createAttributes(true).build());
        event.put(ERR.get(), ErrFieldEntity.createAttributes().build());
        event.put(ERRANT.get(), ErrantEntity.createAttributes(false).build());
        event.put(SANCTUM_ERRANT.get(), ErrantEntity.createAttributes(true).build());
    }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(ERRANT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        for (var type : java.util.List.of(RIPPLE, SANCTUM_RIPPLE)) event.register(type.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, reason, pos, random) -> {
                    if (!level.getLevel().getWeatherData().isThundering() && !level.getLevel().getWeatherData().isRaining()) return false;
                    var ground = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
                    return ground.getNamespace().equals(ThermalShockReforged.MOD_ID) && (ground.getPath().equals("snowy_shale") || ground.getPath().equals("snowy_cobalt"));
                }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof KilolyteEntity kilolyte) { event.setCanceled(true); kilolyte.beginAttack(event.getEntity()); }
        if (event.getSource().getEntity() instanceof FrostilyteGunnerEntity gunner) { event.setCanceled(true); gunner.beginAttack(event.getEntity()); }
        if (event.getSource().getEntity() instanceof RippleEntity ripple) { event.setCanceled(true); ripple.beginAttack(event.getEntity()); }
        if (event.getSource().getEntity() instanceof ErrantEntity errant) { event.setCanceled(true); errant.beginAttack(event.getEntity()); }
    }
}
