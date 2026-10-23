package com.hp.thermalshockreforged.entity.gaze;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.*;

public final class GazeContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<LanternbearerEntity>> LANTERNBEARER = TYPES.register("lanternbearer", () -> EntityType.Builder.of(LanternbearerEntity::new, MobCategory.MONSTER).sized(.6F, 1.7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(16).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("lanternbearer")));
    public static final DeferredHolder<EntityType<?>, EntityType<LensEntity>> LENS = TYPES.register("lens", () -> EntityType.Builder.of(LensEntity::new, MobCategory.MONSTER).sized(.6F, 1.8F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("lens")));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiritEntity>> SPIRIT = TYPES.register("spirit", () -> EntityType.Builder.of(SpiritEntity::new, MobCategory.MONSTER).sized(.8F, .8F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("spirit")));
    public static final DeferredHolder<EntityType<?>, EntityType<LanternDeathEntity>> LANTERN_DEATH = TYPES.register("lantern_death_task", () -> EntityType.Builder.<LanternDeathEntity>of(LanternDeathEntity::new, MobCategory.MISC).noSummon().sized(.1F, .1F).clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("lantern_death_task")));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LENS_VISAGE_1 = PARTICLES.register("lens_visage_1", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LENS_VISAGE_2 = PARTICLES.register("lens_visage_2", () -> new SimpleParticleType(true));
    static {
        ITEMS.register("lanternbearer_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(LANTERNBEARER.get())));
        ITEMS.register("lens_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(LENS.get())));
        ITEMS.register("spirit_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(SPIRIT.get())));
    }
    private GazeContent() { }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus); PARTICLES.register(bus);
        bus.addListener(GazeContent::attributes); bus.addListener(GazeContent::placements);
        NeoForge.EVENT_BUS.addListener(GazeContent::attack);
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> LensEntity.clearGazeCache());
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(LANTERNBEARER.get(), LanternbearerEntity.createAttributes().build());
        event.put(LENS.get(), LensEntity.createAttributes().build());
        event.put(SPIRIT.get(), SpiritEntity.createAttributes().build());
    }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(LANTERNBEARER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SPIRIT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, random) && Mob.checkMobSpawnRules(type, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LENS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> (level.getLevel().getWeatherData().isThundering() || level.getLevel().getWeatherData().isRaining()) && (level.getBlockState(pos.below()).is(TerrainContent.SNOWY_SHALE.get()) || level.getBlockState(pos.below()).is(TerrainContent.SNOWY_COBALT.get())), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getEntity() instanceof Player player && event.getSource().getEntity() instanceof SpiritEntity spirit) { event.setCanceled(true); spirit.beginAttack(player); }
    }
}
