package com.hp.thermalshock.entity.gardenling;

import com.hp.thermalshock.ThermalShock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class GardenlingContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShock.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShock.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<GardenlingEntity>> GARDENLING = TYPES.register("gardenling", () ->
            EntityType.Builder.of(GardenlingEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).clientTrackingRange(16).updateInterval(3).build("thermal_shock:gardenling"));
    public static final DeferredBlock<Block> CONSTRUCTOR = BLOCKS.register("gardenling_constructor", GardenlingConstructorBlock::new);

    static { ITEMS.registerSimpleBlockItem(CONSTRUCTOR); }

    private GardenlingContent() { }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        bus.addListener(GardenlingContent::attributes);
    }

    private static void attributes(EntityAttributeCreationEvent event) { event.put(GARDENLING.get(), GardenlingEntity.createAttributes().build()); }
}
