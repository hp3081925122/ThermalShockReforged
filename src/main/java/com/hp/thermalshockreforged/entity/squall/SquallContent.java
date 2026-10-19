package com.hp.thermalshockreforged.entity.squall;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.world.WorldProgressData;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SquallContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SquallEntity>> SQUALL = TYPES.register("squall", () -> EntityType.Builder.of(SquallEntity::new, MobCategory.MONSTER).sized(.6F, 1.8F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("squall").toString()));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> SMOKE = PARTICLES.register("squall_smoke", () -> new SimpleParticleType(false));
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> VISAGE = PARTICLES.register("squall_visage", () -> new SimpleParticleType(true));
    static { ITEMS.register("squall_spawn_egg", () -> new DeferredSpawnEggItem(SQUALL, -1, -1, new Item.Properties())); }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        ITEMS.register(bus);
        PARTICLES.register(bus);
        bus.addListener(SquallContent::attributes);
        bus.addListener(SquallContent::placements);
        NeoForge.EVENT_BUS.addListener(SquallContent::attack);
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SQUALL.get(), SquallEntity.createAttributes().build()); }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(SQUALL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (type, level, reason, pos, random) -> WorldProgressData.forGeneration(level.getLevel()).squallSpawn() && level.getLevelData().isThundering() && pos.getY() >= 70,
            RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void attack(AttackAttemptEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide && event.getSource().getEntity() instanceof SquallEntity squall && squall.attemptPlayerAttack(player)) event.setCanceled(true);
    }
    private SquallContent() { }
}
