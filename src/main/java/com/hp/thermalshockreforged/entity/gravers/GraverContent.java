package com.hp.thermalshockreforged.entity.gravers;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class GraverContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<GraverEntity>> GRAVER = TYPES.register("graver", () -> EntityType.Builder.of(GraverEntity::new, MobCategory.MONSTER).sized(.6F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("graver").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NeonLimeGraverEntity>> NEON = TYPES.register("neon_lime_graver", () -> EntityType.Builder.of(NeonLimeGraverEntity::new, MobCategory.MONSTER).sized(.6F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("neon_lime_graver").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<AssimilatorEntity>> ASSIMILATOR = TYPES.register("assimilator", () -> EntityType.Builder.of(AssimilatorEntity::new, MobCategory.MONSTER).sized(.6F, .5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("assimilator").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<AssimilatedGraverEntity>> ASSIMILATED = TYPES.register("assimilated_graver", () -> EntityType.Builder.of(AssimilatedGraverEntity::new, MobCategory.MONSTER).sized(.6F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("assimilated_graver").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RazoraptorEntity>> RAZORAPTOR = TYPES.register("razoraptor", () -> EntityType.Builder.of(RazoraptorEntity::new, MobCategory.AMBIENT).sized(1.3F, 2.6F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("razoraptor").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RazoraptorStrikeEntity>> STRIKE = TYPES.register("razoraptor_strike_task", () -> EntityType.Builder.<RazoraptorStrikeEntity>of(RazoraptorStrikeEntity::new, MobCategory.MISC).noSummon().sized(.1F, .1F).clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build(id("razoraptor_strike_task").toString()));
    static {
        ITEMS.register("graver_spawn_egg", () -> new DeferredSpawnEggItem(GRAVER, -1, -1, new Item.Properties()));
        ITEMS.register("assimilator_spawn_egg", () -> new DeferredSpawnEggItem(ASSIMILATOR, -1, -1, new Item.Properties()));
        ITEMS.register("assimilated_graver_spawn_egg", () -> new DeferredSpawnEggItem(ASSIMILATED, -1, -1, new Item.Properties()));
        ITEMS.register("razoraptor_spawn_egg", () -> new DeferredSpawnEggItem(RAZORAPTOR, -1, -1, new Item.Properties()));
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus);
        bus.addListener(GraverContent::attributes); bus.addListener(GraverContent::placements);
        NeoForge.EVENT_BUS.addListener(GraverContent::attack);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(GRAVER.get(), GraverFamilyEntity.createAttributes(0).build());
        event.put(NEON.get(), GraverFamilyEntity.createAttributes(1).build());
        event.put(ASSIMILATOR.get(), GraverFamilyEntity.createAttributes(2).build());
        event.put(ASSIMILATED.get(), GraverFamilyEntity.createAttributes(3).build());
        event.put(RAZORAPTOR.get(), GraverFamilyEntity.createAttributes(4).build());
    }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(GRAVER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GraverContent::darkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ASSIMILATOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GraverContent::darkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ASSIMILATED.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GraverContent::darkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(RAZORAPTOR.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static boolean darkSpawn(EntityType<? extends Monster> type, net.minecraft.world.level.ServerLevelAccessor level, MobSpawnType reason, net.minecraft.core.BlockPos pos, net.minecraft.util.RandomSource random) { return level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random); }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof RazoraptorEntity razoraptor) { event.setCanceled(true); razoraptor.beginAttack(event.getEntity()); } }
    private GraverContent() { }
}
