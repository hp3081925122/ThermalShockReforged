package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.registries.*;

public final class GeoDecorContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final Map<GeoDecorKind, DeferredBlock<GeoDecorBlock>> BLOCK_VALUES = new EnumMap<>(GeoDecorKind.class);
    private static final Map<GeoDecorKind, DeferredItem<GeoDecorItem>> ITEM_VALUES = new EnumMap<>(GeoDecorKind.class);
    private static final Map<GeoDecorKind, DeferredHolder<BlockEntityType<?>, BlockEntityType<GeoDecorBlockEntity>>> ENTITY_VALUES = new EnumMap<>(GeoDecorKind.class);
    static {
        for (GeoDecorKind kind : GeoDecorKind.values()) {
            DeferredBlock<GeoDecorBlock> block = BLOCKS.register(kind.id, () -> switch (kind) {
                case CRYONIC_LAMP, GLAUKO_BLOOM -> new GeoDecorBlock.Waterlogged(kind);
                case STATUE_OF_LITANY, ITTY_BITTY, GUNNER_TROPHY, BURNER_TROPHY -> new GeoDecorBlock.Directional(kind);
                default -> new GeoDecorBlock(kind);
            });
            BLOCK_VALUES.put(kind, block);
            ITEM_VALUES.put(kind, ITEMS.register(kind.id, () -> new GeoDecorItem(block.get(), kind, new Item.Properties())));
            ENTITY_VALUES.put(kind, ENTITIES.register(kind.id, () -> BlockEntityType.Builder.of((pos, state) -> new GeoDecorBlockEntity(kind, pos, state), block.get()).build(null)));
        }
    }
    public static GeoDecorBlock block(GeoDecorKind kind) { return BLOCK_VALUES.get(kind).get(); }
    public static GeoDecorItem item(GeoDecorKind kind) { return ITEM_VALUES.get(kind).get(); }
    public static BlockEntityType<GeoDecorBlockEntity> entity(GeoDecorKind kind) { return ENTITY_VALUES.get(kind).get(); }
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(GeoDecorContent::capabilities);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        for (var type : ENTITY_VALUES.values()) event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type.get(), (entity, side) -> entity.itemHandler(side));
    }
}
