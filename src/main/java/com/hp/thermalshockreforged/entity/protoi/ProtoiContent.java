package com.hp.thermalshockreforged.entity.protoi;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
public final class ProtoiContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ProtoiGammaEntity>> GAMMA = TYPES.register("protoi_gamma", () -> EntityType.Builder.of(ProtoiGammaEntity::new, MobCategory.AMBIENT).sized(.9F, .7F).clientTrackingRange(32).updateInterval(3).fireImmune().build("thermal_shock_reforged:protoi_gamma"));
    public static final DeferredHolder<EntityType<?>, EntityType<ProtoiSunbeamEntity>> SUNBEAM = TYPES.register("protoi_sunbeam", () -> EntityType.Builder.of(ProtoiSunbeamEntity::new, MobCategory.AMBIENT).sized(3, 3).clientTrackingRange(32).updateInterval(3).fireImmune().build("thermal_shock_reforged:protoi_sunbeam"));
    public static final DeferredHolder<EntityType<?>, EntityType<ProtoiDeltaEntity>> DELTA = TYPES.register("protoi_delta", () -> EntityType.Builder.of(ProtoiDeltaEntity::new, MobCategory.AMBIENT).sized(3, 3).clientTrackingRange(64).updateInterval(3).fireImmune().build("thermal_shock_reforged:protoi_delta"));
    public static final DeferredHolder<EntityType<?>, EntityType<SunbeamBulletEntity>> SUNBEAM_BULLET = TYPES.register("sunbeam_bullet", () -> EntityType.Builder.<SunbeamBulletEntity>of(SunbeamBulletEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:sunbeam_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<ProtoiEpsilonEntity>> EPSILON = TYPES.register("protoi_epsilon", () -> EntityType.Builder.of(ProtoiEpsilonEntity::new, MobCategory.AMBIENT).sized(2, 2).eyeHeight(.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:protoi_epsilon"));
    public static final DeferredHolder<EntityType<?>, EntityType<EpsilonAttackEntity>> EPSILON_ATTACK = TYPES.register("epsilon_attack_task", () -> EntityType.Builder.<EpsilonAttackEntity>of(EpsilonAttackEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":epsilon_attack_task"));
    public static final DeferredHolder<Item, Item> GAMMA_EGG = ITEMS.register("protoi_gamma_spawn_egg", () -> new DeferredSpawnEggItem(GAMMA, -1, -1, new Item.Properties()));
    public static final DeferredHolder<Item, Item> DELTA_EGG = ITEMS.register("protoi_delta_spawn_egg", () -> new DeferredSpawnEggItem(DELTA, -1, -1, new Item.Properties()));
    public static final DeferredHolder<Item, Item> EPSILON_EGG = ITEMS.register("protoi_epsilon_spawn_egg", () -> new DeferredSpawnEggItem(EPSILON, -1, -1, new Item.Properties()));
    public static final DeferredHolder<Item, Item> SUNBEAM_EGG = ITEMS.register("protoi_sunbeam_spawn_egg", () -> new DeferredSpawnEggItem(SUNBEAM, -1, -1, new Item.Properties()));
    private ProtoiContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(ProtoiContent::attributes); bus.addListener(ProtoiContent::placements); NeoForge.EVENT_BUS.addListener(ProtoiContent::attack); }
    private static void placements(RegisterSpawnPlacementsEvent event) { event.register(EPSILON.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> pos.getY() >= 90, RegisterSpawnPlacementsEvent.Operation.REPLACE); }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof ProtoiEpsilonEntity epsilon) { event.setCanceled(true); epsilon.beginAttack(event.getEntity()); } }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(GAMMA.get(), ProtoiGammaEntity.createAttributes().build()); event.put(DELTA.get(), ProtoiDeltaEntity.createAttributes().build()); event.put(EPSILON.get(), ProtoiEpsilonEntity.createAttributes().build()); event.put(SUNBEAM.get(), ProtoiSunbeamEntity.createAttributes().build()); }
}
