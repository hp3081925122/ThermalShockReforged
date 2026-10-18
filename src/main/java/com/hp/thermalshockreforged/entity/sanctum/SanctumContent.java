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
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> ERR_EFFECT = PARTICLES.register("err_effect", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> ERRANT = errant("errant");
    public static final DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> SANCTUM_ERRANT = errant("sanctum_errant");
    public static final DeferredItem<DeferredSpawnEggItem> ERRANT_EGG = ITEMS.register("errant_spawn_egg", () -> new DeferredSpawnEggItem(ERRANT, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<RippleEntity>> RIPPLE = ripple("ripple");
    public static final DeferredHolder<EntityType<?>, EntityType<RippleEntity>> SANCTUM_RIPPLE = ripple("sanctum_ripple");
    public static final DeferredItem<DeferredSpawnEggItem> RIPPLE_EGG = ITEMS.register("ripple_spawn_egg", () -> new DeferredSpawnEggItem(RIPPLE, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<ErrFieldEntity>> ERR = TYPES.register("err", () -> EntityType.Builder.of(ErrFieldEntity::new, MobCategory.MONSTER).sized(1, 2).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":err"));
    public static final DeferredHolder<EntityType<?>, EntityType<RippleCastEntity>> RIPPLE_CAST = TYPES.register("ripple_cast_task", () -> EntityType.Builder.<RippleCastEntity>of(RippleCastEntity::new, MobCategory.MISC).sized(.01F, .01F).clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build(ThermalShockReforged.MOD_ID + ":ripple_cast_task"));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostilyteGunnerEntity>> FROSTILYTE_GUNNER = TYPES.register("frostilyte_gunner", () -> EntityType.Builder.of(FrostilyteGunnerEntity::new, MobCategory.MONSTER).sized(.6F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":frostilyte_gunner"));
    public static final DeferredItem<DeferredSpawnEggItem> FROSTILYTE_GUNNER_EGG = ITEMS.register("frostilyte_gunner_spawn_egg", () -> new DeferredSpawnEggItem(FROSTILYTE_GUNNER, -1, -1, new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<GunnerVolleyEntity>> GUNNER_VOLLEY = TYPES.register("gunner_volley_task", () -> EntityType.Builder.<GunnerVolleyEntity>of(GunnerVolleyEntity::new, MobCategory.MISC).sized(.01F, .01F).clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build(ThermalShockReforged.MOD_ID + ":gunner_volley_task"));
    private static DeferredHolder<EntityType<?>, EntityType<RippleEntity>> ripple(String name) {
        return TYPES.register(name, () -> EntityType.Builder.of(RippleEntity::new, MobCategory.MONSTER).sized(.6F, 2).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":" + name));
    }
    private SanctumContent() { }
    private static DeferredHolder<EntityType<?>, EntityType<ErrantEntity>> errant(String name) {
        return TYPES.register(name, () -> EntityType.Builder.of(ErrantEntity::new, MobCategory.MONSTER).sized(.8F, 1.9F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":" + name));
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
                    if (!level.getLevelData().isThundering()) return false;
                    var ground = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
                    return ground.getNamespace().equals(ThermalShockReforged.MOD_ID) && (ground.getPath().equals("snowy_shale") || ground.getPath().equals("snowy_cobalt"));
                }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof FrostilyteGunnerEntity gunner) { event.setCanceled(true); gunner.beginAttack(event.getEntity()); }
        if (event.getSource().getEntity() instanceof RippleEntity ripple) { event.setCanceled(true); ripple.beginAttack(event.getEntity()); }
        if (event.getSource().getEntity() instanceof ErrantEntity errant) { event.setCanceled(true); errant.beginAttack(event.getEntity()); }
    }
}
