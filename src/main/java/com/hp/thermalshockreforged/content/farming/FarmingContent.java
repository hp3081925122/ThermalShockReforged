package com.hp.thermalshockreforged.content.farming;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.*;

public final class FarmingContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    private static final Map<CropKind, DeferredBlock<Block>> CROPS = new EnumMap<>(CropKind.class);
    private static final Map<CropKind, DeferredBlock<FarmSeedBlock>> SEEDS = new EnumMap<>(CropKind.class);
    private static final Map<CropKind, DeferredItem<GeoCropItem>> GEO_ITEMS = new EnumMap<>(CropKind.class);
    private static final Map<CropKind, DeferredHolder<BlockEntityType<?>, BlockEntityType<SeedBlockEntity>>> SEED_ENTITIES = new EnumMap<>(CropKind.class);
    private static final Map<CropKind, DeferredHolder<BlockEntityType<?>, BlockEntityType<GeoCropBlockEntity>>> CROP_ENTITIES = new EnumMap<>(CropKind.class);
    public static final DeferredBlock<PlanterBlock> PLANTER_POT = BLOCKS.register("planter_pot", () -> new PlanterBlock(true));
    public static final DeferredBlock<PlanterBlock> PLANTER_BOX = BLOCKS.register("planter_box", () -> new PlanterBlock(false));
    public static final DeferredItem<BarbubbleMelonItem> BARBUBBLE_MELON = ITEMS.register("barbubble_melon", BarbubbleMelonItem::new);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> HEARTHFLY = PARTICLES.register("hearthfly", () -> new SimpleParticleType(false));

    static {
        ITEMS.registerSimpleBlockItem(PLANTER_POT);
        ITEMS.registerSimpleBlockItem(PLANTER_BOX);
        for (CropKind kind : CropKind.values()) {
            DeferredBlock<Block> crop = BLOCKS.register(kind.id, () -> kind == CropKind.SKORIO ? new SkorioBlock() : kind == CropKind.BARBUBBLE ? new BarbubbleBlock() : kind == CropKind.FROST_LOTUS ? new GeoCropBlock(kind) : new GeoCropBlock.Directional(kind));
            CROPS.put(kind, crop);
            if (kind.geo) GEO_ITEMS.put(kind, ITEMS.register(kind.id, () -> new GeoCropItem(crop.get(), kind)));
            else ITEMS.registerSimpleBlockItem(crop);
            var seed = BLOCKS.register(kind.id + "_seed", () -> new FarmSeedBlock(kind));
            SEEDS.put(kind, seed);
            ITEMS.registerSimpleBlockItem(seed);
            SEED_ENTITIES.put(kind, ENTITIES.register(kind.id + "_seed", () -> new BlockEntityType<>((pos, state) -> new SeedBlockEntity(kind, pos, state), seed.get())));
            if (kind.geo) CROP_ENTITIES.put(kind, ENTITIES.register(kind.id, () -> new BlockEntityType<>((pos, state) -> new GeoCropBlockEntity(kind, pos, state), crop.get())));
        }
    }

    public static Block crop(CropKind kind) { return CROPS.get(kind).get(); }
    public static FarmSeedBlock seed(CropKind kind) { return SEEDS.get(kind).get(); }
    public static BlockEntityType<SeedBlockEntity> seedEntity(CropKind kind) { return SEED_ENTITIES.get(kind).get(); }
    public static BlockEntityType<GeoCropBlockEntity> cropEntity(CropKind kind) { return CROP_ENTITIES.get(kind).get(); }
    public static GeoCropItem geoItem(CropKind kind) { return GEO_ITEMS.get(kind).get(); }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        PARTICLES.register(bus);
        bus.addListener(FarmingContent::capabilities);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        for (var type : SEED_ENTITIES.values()) event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(), (entity, side) -> entity.itemHandler(side));
        for (var type : CROP_ENTITIES.values()) event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(), (entity, side) -> entity.itemHandler(side));
    }
}
