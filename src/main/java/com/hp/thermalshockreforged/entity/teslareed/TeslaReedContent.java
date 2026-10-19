package com.hp.thermalshockreforged.entity.teslareed;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class TeslaReedContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<TeslaReedEntity>> TESLA_REED = TYPES.register("tesla_reed", () -> EntityType.Builder.of(TeslaReedEntity::new, MobCategory.MONSTER).sized(.7F, .7F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":tesla_reed"));
    public static final DeferredHolder<EntityType<?>, EntityType<TeslaPulseEntity>> PULSE = TYPES.register("tesla_pulse_task", () -> EntityType.Builder.<TeslaPulseEntity>of(TeslaPulseEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":tesla_pulse_task"));
    public static final DeferredBlock<Block> SEED = BLOCKS.register("tesla_reed_seed", TeslaReedSeedBlock::new);
    static { ITEMS.register("tesla_reed_seed", () -> new BlockItem(SEED.get(), new Item.Properties())); }
    public static void register(IEventBus bus) { TYPES.register(bus); BLOCKS.register(bus); ITEMS.register(bus); bus.addListener(TeslaReedContent::attributes); NeoForge.EVENT_BUS.addListener(TeslaReedContent::attack); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(TESLA_REED.get(), TeslaReedEntity.attributes().build()); }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof TeslaReedEntity caster) { event.setCanceled(true); caster.beginAttack(event.getEntity()); } }
    private TeslaReedContent() { }
}
