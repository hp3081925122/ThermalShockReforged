package com.hp.thermalshockreforged.content.labworldgen;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class LabWorldgenContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<DissolverGemlockBlock> DISSOLVER_GEMLOCK = BLOCKS.register("dissolver_gemlock", DissolverGemlockBlock::new);
    public static final DeferredHolder<EntityType<?>, EntityType<DissolverCycleEntity>> DISSOLVER_CYCLE = TYPES.register("dissolver_cycle", () -> EntityType.Builder.of(DissolverCycleEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("dissolver_cycle")));
    static { ITEMS.registerSimpleBlockItem(DISSOLVER_GEMLOCK); }
    private LabWorldgenContent() { }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); TYPES.register(bus); }
}
