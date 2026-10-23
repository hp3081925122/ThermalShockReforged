package com.hp.thermalshockreforged.content.gemlocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class GemlocksContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final Map<GemlockKind, DeferredBlock<GemlockBlock>> VALUES = new EnumMap<>(GemlockKind.class);
    static {
        for (var kind : GemlockKind.values()) {
            var block = BLOCKS.register(kind.id, () -> new GemlockBlock(kind));
            VALUES.put(kind, block);
            ITEMS.registerSimpleBlockItem(block);
        }
    }
    private GemlocksContent() { }
    public static GemlockBlock block(GemlockKind kind) { return VALUES.get(kind).get(); }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
}
