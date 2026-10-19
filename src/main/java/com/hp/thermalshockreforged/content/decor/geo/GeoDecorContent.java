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
    public static final DeferredBlock<JellercelCollisionBlock> JELLERCEL_COLLISION = BLOCKS.register("large_jellercell_collision", JellercelCollisionBlock::new);
    static {
        ITEMS.registerSimpleBlockItem(JELLERCEL_COLLISION);
        for (GeoDecorKind kind : GeoDecorKind.values()) {
            DeferredBlock<GeoDecorBlock> block = BLOCKS.register(kind.id, () -> switch (kind) {
                case SHALE_PILLAR, SHALE_PILLAR_VAR_2, SHALE_PILLAR_VAR_3, SHALE_PILLAR_VAR_4, SHALE_PILLAR_VAR_5, SHALE_PILLAR_VAR_6, SHALE_PILLAR_VAR_7, SHALE_PILLAR_VAR_8 -> new ShalePillarBlock(kind);
                case FRIGID_ALTAR, FERROUS_ALTAR -> new AltarBlock(kind);
                case PHOBOTITE_OUTCROP, PHOBOTITE_MANTLE_OUTCROP -> new PhobotiteOutcropBlock(kind);
                case PYLON_CORE -> new PylonCoreBlock();
                case JELLERCEL -> new JellercelBlock(kind);
                case LARGE_JELLERCEL -> new LargeJellercelBlock();
                case SURGE_PYLON -> new SurgePylonBlock();
                case THERMA_DEV_PLUSH -> new ThermaDevPlushBlock();
                case FERRO_CAKE -> new FerroCakeBlock();
                case BOTTLE_OF_STARLIGHT, BOTTLE_OF_LUNARLIGHT -> new StarlightBottleBlock(kind);
                case RINTHIUM_BUTTON, RINTHIUM_LEVER -> new KeystonePuzzleBlock.Directional(kind);
                case INACTIVE_KEYSTONE_PEDESTAL -> new KeystonePuzzleBlock(kind);
                case KEYSTONE_PEDESTAL -> new KeystonePuzzleBlock.Pedestal();
                case CIVILIAN -> new DialogueBlock.Civilian();
                case RUSTY_RADIO -> new DialogueBlock(kind);
                case CAPSULE -> new CapsuleBlock();
                case ATLAS -> new com.hp.thermalshockreforged.content.atlas.AtlasBlock();
                case DESK_DIRECTOR -> new DeskDirectorBlock();
                case FABRICATOR -> new FabricatorBlock();
                case EXTRACTOR -> new com.hp.thermalshockreforged.content.machines.ExtractorBlock();
                case GRAND_BLASPHEMER -> new GrandBlasphemerBlock();
                case CRYONIC_CACHE, SUPER_CRYONIC_CACHE -> new CryonicCacheBlock(kind);
                case ENCHANTED_CACHE -> new EnchantedCacheBlock();
                case SHALE_POT -> new ShalePotBlock();
                case PATHIOLITE_OUTCROP -> new PathioliteOutcropBlock();
                case SUSPICIOUS_CAVEBLOOMER -> new SuspiciousCavebloomerBlock();
                case CRYONIC_LAMP, GLAUKO_BLOOM -> new GeoDecorBlock.Waterlogged(kind);
                case STATUE_OF_LITANY, ITTY_BITTY, GUNNER_TROPHY, BURNER_TROPHY, CAVEBLOOMER_TROPHY, CRYSTAL_SOPEL_TROPHY -> new GeoDecorBlock.Directional(kind);
                default -> new GeoDecorBlock(kind);
            });
            BLOCK_VALUES.put(kind, block);
            ITEM_VALUES.put(kind, ITEMS.register(kind.id, () -> new GeoDecorItem(block.get(), kind, new Item.Properties())));
            ENTITY_VALUES.put(kind, ENTITIES.register(kind.id, () -> BlockEntityType.Builder.<GeoDecorBlockEntity>of((pos, state) -> switch (kind) { case GRAND_BLASPHEMER -> new GrandBlasphemerBlockEntity(pos, state); case SUSPICIOUS_CAVEBLOOMER -> new SuspiciousCavebloomerBlockEntity(pos, state); case ENCHANTED_CACHE, CRYONIC_CACHE, SUPER_CRYONIC_CACHE -> new CacheBlockEntity(kind, pos, state); default -> new GeoDecorBlockEntity(kind, pos, state); }, block.get()).build(null)));
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
