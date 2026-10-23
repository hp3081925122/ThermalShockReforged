package com.hp.thermalshockreforged.content.displayutility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DisplayUtilityContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> REINFORCED_WINDOW = BLOCKS.register("reinforced_window", ReinforcedWindowBlock::new);
    public static final DeferredBlock<Block> TRIDENT_LAMP = BLOCKS.register("trident_lamp", TridentLampBlock::new);

    static {
        ITEMS.registerSimpleBlockItem(REINFORCED_WINDOW);
        ITEMS.registerSimpleBlockItem(TRIDENT_LAMP);
    }

    private DisplayUtilityContent() { }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
}
