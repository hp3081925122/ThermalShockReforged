package com.hp.thermalshockreforged.entity.electricsphere;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.registries.*;

public final class ElectricSphereContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ElectricSphereEntity>> ELECTRIC_SPHERE = TYPES.register("electric_sphere", () -> EntityType.Builder.of(ElectricSphereEntity::new, MobCategory.MONSTER).sized(.7F, .7F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":electric_sphere"));
    public static final DeferredBlock<Block> CONSTRUCTOR = BLOCKS.register("electric_sphere_constructor", ElectricSphereConstructorBlock::new);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ELECTRIC_GLOW = PARTICLES.register("electric_glow", () -> new SimpleParticleType(true));
    static { ITEMS.register("electric_sphere_constructor", () -> new BlockItem(CONSTRUCTOR.get(), new Item.Properties())); }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); BLOCKS.register(bus); ITEMS.register(bus); PARTICLES.register(bus); bus.addListener(ElectricSphereContent::attributes); NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ElectricSphereContent::damage); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(ELECTRIC_SPHERE.get(), ElectricSphereEntity.attributes().build()); }
    private static void damage(LivingDamageEvent.Pre event) { if (event.getEntity() instanceof ElectricSphereEntity sphere && Float.isFinite(event.getNewDamage()) && event.getNewDamage() > sphere.getAbsorptionAmount()) sphere.rebound(event.getSource().getEntity()); }
    private ElectricSphereContent() { }
}
