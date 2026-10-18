package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CavebloomerContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerBombEntity>> MORTAR_BOMB = TYPES.register("mortar_bomb", () -> EntityType.Builder.<CavebloomerBombEntity>of(CavebloomerBombEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":mortar_bomb"));
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerBombEntity>> POISON_BOMB = TYPES.register("poison_bomb", () -> EntityType.Builder.<CavebloomerBombEntity>of(CavebloomerBombEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":poison_bomb"));
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerImpactEntity>> SHOCK_IMPACT = TYPES.register("shock_attack_entity", () -> EntityType.Builder.of(CavebloomerImpactEntity::new, MobCategory.MONSTER)
            .sized(1, 1).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":shock_attack_entity"));
    public static final DeferredHolder<EntityType<?>, EntityType<CavebloomerImpactEntity>> POISON_IMPACT = TYPES.register("poison_attack_entity", () -> EntityType.Builder.of(CavebloomerImpactEntity::new, MobCategory.MONSTER)
            .sized(1, 1).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":poison_attack_entity"));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHOCK_ATTACK = PARTICLES.register("shock_attack", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POISON_CLOUD = PARTICLES.register("poison_cloud", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_ORB = PARTICLES.register("red_orb", () -> new SimpleParticleType(false));
    private CavebloomerContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); PARTICLES.register(bus); bus.addListener(CavebloomerContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(SHOCK_IMPACT.get(), AttackCarrierBody.createAttributes().build());
        event.put(POISON_IMPACT.get(), AttackCarrierBody.createAttributes().build());
    }
}
