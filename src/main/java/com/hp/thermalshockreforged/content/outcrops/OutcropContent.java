package com.hp.thermalshockreforged.content.outcrops;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.registries.*;

public final class OutcropContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ThermalShockReforged.MOD_ID);
    private static final Map<OutcropKind, DeferredBlock<OutcropBlock>> BLOCK_VALUES = new EnumMap<>(OutcropKind.class);
    private static final Map<OutcropKind, DeferredItem<OutcropItem>> ITEM_VALUES = new EnumMap<>(OutcropKind.class);
    private static final Map<OutcropKind, DeferredHolder<BlockEntityType<?>, BlockEntityType<OutcropBlockEntity>>> ENTITY_VALUES = new EnumMap<>(OutcropKind.class);
    static {
        for (var kind : OutcropKind.values()) {
            var block = BLOCKS.register(kind.id, () -> new OutcropBlock(kind));
            BLOCK_VALUES.put(kind, block);
            ITEM_VALUES.put(kind, ITEMS.register(kind.id, () -> new OutcropItem(block.get(), kind)));
            ENTITY_VALUES.put(kind, ENTITIES.register(kind.id, () -> BlockEntityType.Builder.<OutcropBlockEntity>of((pos, state) -> new OutcropBlockEntity(kind, pos, state), block.get()).build(null)));
            FEATURES.register(kind.id + "_generation", () -> new OutcropFeature(kind.oreChoice));
        }
    }
    private OutcropContent() { }
    public static OutcropBlock block(OutcropKind kind) { return BLOCK_VALUES.get(kind).get(); }
    public static OutcropItem item(OutcropKind kind) { return ITEM_VALUES.get(kind).get(); }
    public static BlockEntityType<OutcropBlockEntity> entity(OutcropKind kind) { return ENTITY_VALUES.get(kind).get(); }
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        FEATURES.register(bus);
        bus.addListener(OutcropContent::capabilities);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        for (var type : ENTITY_VALUES.values()) event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type.get(), (entity, side) -> entity.itemHandler(side));
    }
}
