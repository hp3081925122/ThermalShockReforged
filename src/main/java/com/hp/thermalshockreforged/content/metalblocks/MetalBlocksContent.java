package com.hp.thermalshockreforged.content.metalblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MetalBlocksContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<MetalStorageBlock> STRATOSILVER = BLOCKS.register("stratosilver_block", () -> new MetalStorageBlock(MapColor.GOLD));
    public static final DeferredBlock<MetalStorageBlock> STRATUS_ALLOY = BLOCKS.register("stratus_alloy_block", () -> new MetalStorageBlock(MapColor.COLOR_LIGHT_BLUE));
    static {
        ITEMS.registerSimpleBlockItem(STRATOSILVER);
        ITEMS.registerSimpleBlockItem(STRATUS_ALLOY);
    }
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
