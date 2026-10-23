package com.hp.thermalshockreforged.content.growth;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.terrain.ThermalSaplingBlock;
import com.hp.thermalshockreforged.entity.growth.GrowthEntities;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.core.registries.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.registries.*;

public final class GrowthContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final Map<String, DeferredBlock<Block>> BLOCK_VALUES = new LinkedHashMap<>();
    private static final Map<String, DeferredHolder<BlockEntityType<?>, BlockEntityType<GrowthInventoryBlockEntity>>> INVENTORIES = new LinkedHashMap<>();
    public static final DeferredBlock<Block> STARFRUIT = block("starfruit", () -> new HangingFruitBlock(false));
    public static final DeferredBlock<Block> LUNALEMON = block("lunalemon", () -> new HangingFruitBlock(true));
    public static final DeferredBlock<Block> STARFRUIT_SEED = block("starfruit_seed", () -> new ThermalSaplingBlock("starfruit", "starfruit_tree"));
    public static final DeferredBlock<Block> LUNALEMON_SEED = block("lunalemon_seed", () -> new ThermalSaplingBlock("lunalemon", "lunalemon_tree"));
    public static final DeferredBlock<Block> FUSCIUM = block("fuscium", TallStemBlock.Waterlogged::new);
    public static final DeferredBlock<Block> CERULEUM = block("ceruleum", TallStemBlock.Waterlogged::new);
    public static final DeferredBlock<Block> VERDIUM = block("verdium", TallStemBlock.Waterlogged::new);
    public static final DeferredBlock<Block> SPIRE_BLOSSOM_STEM = block("spire_blossom_stem", TallStemBlock::new);
    public static final DeferredBlock<Block> SPIRE_BLOOM_RUBICON = block("spire_bloom_rubicon", SpireTipBlock::new);
    public static final DeferredBlock<Block> SPARKLER_STEM = block("vermillion_sparkler_stem", () -> new SparklerBlock(0));
    public static final DeferredBlock<Block> SPARKLER_TOP_1 = block("vermillion_sparkler_top_1", () -> new SparklerBlock(3));
    public static final DeferredBlock<Block> SPARKLER_TOP_2 = block("vermillion_sparkler_top_2", () -> new SparklerBlock(5));
    public static final DeferredBlock<Block> SPARKLER_TOP_3 = block("vermillion_sparkler_top_3", () -> new SparklerBlock(10));
    public static final DeferredBlock<Block> GEL_BULB = BLOCKS.register("gel_bulb", GelBulbBlock::new);
    public static final DeferredItem<GelBulbItem> GEL_BULB_ITEM = ITEMS.register("gel_bulb", () -> new GelBulbItem(GEL_BULB.get()));
    public static final DeferredBlock<Block> VISI_BULB = block("visi_bulb", VisiBulbBlock::new);
    public static final DeferredBlock<Block> GELSHOOTER_SEED = block("gelshooter_seed", GelshooterSeedBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GelBulbBlockEntity>> GEL_ENTITY = ENTITIES.register("gel_bulb", () -> new BlockEntityType<>(GelBulbBlockEntity::new, GEL_BULB.get()));
    static {
        for (GrowthKind kind : GrowthKind.values()) block(kind.id + "_seed", () -> new GrowthSeedBlock(kind));
        for (String id : List.of("sparkler_seed", "gel_bulb_seed", "visi_bulb_seed", "visi_bulb")) INVENTORIES.put(id,
                ENTITIES.register(id, () -> new BlockEntityType<>(GrowthInventoryBlockEntity::new, BLOCK_VALUES.get(id).get())));
    }
    private static DeferredBlock<Block> block(String id, Supplier<Block> factory) {
        DeferredBlock<Block> holder = BLOCKS.register(id, factory);
        BLOCK_VALUES.put(id, holder);
        ITEMS.registerSimpleBlockItem(holder);
        return holder;
    }
    public static Block block(String id) { return id.equals("gel_bulb") ? GEL_BULB.get() : BLOCK_VALUES.get(id).get(); }
    public static BlockEntityType<GrowthInventoryBlockEntity> inventoryType(Block block) { return INVENTORIES.get(BuiltInRegistries.BLOCK.getKey(block).getPath()).get(); }
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(GrowthContent::capabilities);
        GrowthEntities.register(bus);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        for (var holder : INVENTORIES.values()) event.registerBlockEntity(Capabilities.Item.BLOCK, holder.get(), (entity, side) -> entity.itemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, GEL_ENTITY.get(), (entity, side) -> entity.itemHandler(side));
    }
}
