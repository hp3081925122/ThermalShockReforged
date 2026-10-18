package com.hp.thermalshockreforged.entity.mangler;

import com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity;

import java.util.Comparator;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.registries.*;

public final class ManglerContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ManglerEntity>> MANGLER = TYPES.register("mangler", () -> EntityType.Builder.of(ManglerEntity::new, MobCategory.AMBIENT).fireImmune().sized(0.9F, 0.8F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:mangler"));
    public static final DeferredHolder<EntityType<?>, EntityType<SummonedManglerEntity>> SUMMONED_MANGLER = TYPES.register("summoned_mangler", () -> EntityType.Builder.of(SummonedManglerEntity::new, MobCategory.AMBIENT).sized(0.9F, 0.8F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:summoned_mangler"));
    public static final DeferredHolder<EntityType<?>, EntityType<HeraldEntity>> HERALD = TYPES.register("herald", () -> EntityType.Builder.of(HeraldEntity::new, MobCategory.MONSTER).fireImmune().sized(2.1F, 7.6F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:herald"));
    public static final DeferredHolder<EntityType<?>, EntityType<BossDeathEffectEntity>> HERALD_DEATH_EFFECT = TYPES.register("herald_death_effect", () -> EntityType.Builder.<BossDeathEffectEntity>of(BossDeathEffectEntity::new, MobCategory.MISC).sized(0.01F, 0.01F).clientTrackingRange(64).updateInterval(20).build("thermal_shock_reforged:herald_death_effect"));
    static {
        ITEMS.register("mangler_spawn_egg", () -> new DeferredSpawnEggItem(MANGLER, -1, -1, new Item.Properties()));
        ITEMS.register("summoned_mangler_spawn_egg", () -> new DeferredSpawnEggItem(SUMMONED_MANGLER, -9632219, -65536, new Item.Properties()));
        ITEMS.register("herald_spawn_egg", () -> new DeferredSpawnEggItem(HERALD, -1, -1, new Item.Properties()));
    }
    private ManglerContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(ManglerContent::attributes); bus.addListener(ManglerContent::placements); NeoForge.EVENT_BUS.addListener(ManglerContent::attack); NeoForge.EVENT_BUS.addListener(ManglerContent::damaged); NeoForge.EVENT_BUS.addListener(ManglerContent::death); NeoForge.EVENT_BUS.addListener(ManglerContent::join); }
    private static void join(EntityJoinLevelEvent event) { if (event.getEntity() instanceof HeraldEntity herald) herald.applyConfiguredAttributes(); if (event.getEntity() instanceof SummonedManglerEntity child && event.getLevel() instanceof net.minecraft.server.level.ServerLevel server && child.expireIfDue(server)) event.setCanceled(true); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(MANGLER.get(), ManglerEntity.createAttributes(false).build()); event.put(SUMMONED_MANGLER.get(), ManglerEntity.createAttributes(true).build()); event.put(HERALD.get(), HeraldEntity.createAttributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) { event.register(MANGLER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE); }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof ManglerEntity caster) { event.setCanceled(true); caster.boost(event.getEntity()); } }
    private static void damaged(LivingDamageEvent.Post event) { if (event.getEntity() instanceof HeraldEntity herald && event.getOriginalDamage() > 0 && event.getNewDamage() > 0 && herald.isAlive()) herald.acceptedDamage(); }
    private static void death(LivingDeathEvent event) {
        var victim = event.getEntity(); if (victim.level().isClientSide) return;
        var position = victim.position(); var heralds = victim.level().getEntitiesOfClass(HeraldEntity.class, new AABB(position, position).inflate(8), candidate -> candidate.isAlive());
        heralds.sort(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(position))); for (var herald : heralds) herald.siphon(victim);
    }
}
