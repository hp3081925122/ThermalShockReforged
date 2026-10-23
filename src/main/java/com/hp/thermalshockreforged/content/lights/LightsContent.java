package com.hp.thermalshockreforged.content.lights;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.*;

public final class LightsContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<GlowstickBlock> GLOWSTICK = BLOCKS.register("glowstick", GlowstickBlock::new);
    public static final DeferredItem<GlowstickItem> GLOWSTICK_ITEM = ITEMS.register("glowstick", () -> new GlowstickItem(GLOWSTICK.get(), com.hp.thermalshockreforged.ModRegistration.item()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GlowstickBlockEntity>> GLOWSTICK_ENTITY = ENTITIES.register("glowstick", () -> new BlockEntityType<>(GlowstickBlockEntity::new, GLOWSTICK.get()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(LightsContent::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, GLOWSTICK_ENTITY.get(),
                (entity, side) -> entity.itemHandler(side));
    }
}
