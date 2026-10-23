package com.hp.thermalshockreforged.content.reactor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class ReactorContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<StratusReactorCoreBlock> CORE = BLOCKS.register("stratus_reactor_core", StratusReactorCoreBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorCoreBlockEntity>> CORE_ENTITY = BLOCK_ENTITIES.register("stratus_reactor_core", () -> new BlockEntityType<>(ReactorCoreBlockEntity::new, CORE.get()));
    public static final DeferredHolder<EntityType<?>, EntityType<ReactorSequenceEntity>> SEQUENCE = ENTITIES.register("stratus_reactor_sequence", () -> EntityType.Builder.of(ReactorSequenceEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("stratus_reactor_sequence")));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHOCKWAVE = PARTICLES.register("reactor_shockwave", () -> new SimpleParticleType(false));
    static { ITEMS.registerSimpleBlockItem(CORE); }
    private ReactorContent() { }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); BLOCK_ENTITIES.register(bus); ENTITIES.register(bus); PARTICLES.register(bus); }
}
