package com.hp.thermalshockreforged.entity.orb;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OrbContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> BLUE_X_BLAST = PARTICLES.register("blue_x_blast", () -> new SimpleParticleType(false));
    public static final DeferredHolder<EntityType<?>, EntityType<StopperOrbEntity>> STOPPER_ORB = TYPES.register("stopper_orb", () -> EntityType.Builder.of(StopperOrbEntity::new, MobCategory.MONSTER).sized(.6F, 1.8F).clientTrackingRange(64).updateInterval(3).fireImmune().build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("stopper_orb")));
    public static final DeferredHolder<EntityType<?>, EntityType<MoverOrbEntity>> MOVER_ORB = TYPES.register("mover_orb", () -> EntityType.Builder.of(MoverOrbEntity::new, MobCategory.MONSTER).sized(.6F, 1.8F).clientTrackingRange(64).updateInterval(3).fireImmune().build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("mover_orb")));
    public static final DeferredHolder<EntityType<?>, EntityType<WhistlerNoteEntity>> WHISTLER_NOTE = TYPES.register("whistler_note", () -> EntityType.Builder.<WhistlerNoteEntity>of(WhistlerNoteEntity::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("whistler_note")));

    private OrbContent() { }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        PARTICLES.register(bus);
        bus.addListener(OrbContent::attributes);
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(STOPPER_ORB.get(), OrbEntityBase.createAttributes().build());
        event.put(MOVER_ORB.get(), OrbEntityBase.createAttributes().build());
    }
}
