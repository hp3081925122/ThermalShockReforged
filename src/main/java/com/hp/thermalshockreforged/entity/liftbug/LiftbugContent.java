package com.hp.thermalshockreforged.entity.liftbug;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class LiftbugContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<LiftbugConstructorBlock> CONSTRUCTOR = BLOCKS.register("liftbug_constructor", LiftbugConstructorBlock::new);
    public static final DeferredHolder<EntityType<?>, EntityType<LiftbugEntity>> LIFTBUG = TYPES.register("liftbug", () -> EntityType.Builder.of(LiftbugEntity::new, MobCategory.MONSTER).sized(3F, .35F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("liftbug")));
    static { ITEMS.registerSimpleBlockItem(CONSTRUCTOR); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); BLOCKS.register(bus); bus.addListener(LiftbugContent::attributes);  }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(LIFTBUG.get(), LiftbugEntity.attributes().build()); }
    private LiftbugContent() { }
}
