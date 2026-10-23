package com.hp.thermalshockreforged.content.glass.variants;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GlassVariantsContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> SHALE_GHOSTGLASS = BLOCKS.register("shale_ghostglass", () -> new FacedGlassBlock(faced(MapColor.STONE)));
    public static final DeferredBlock<Block> MANTLE_GHOSTGLASS = BLOCKS.register("mantle_ghostglass", () -> new FacedGlassBlock(faced(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> COBALT_GHOSTGLASS = BLOCKS.register("cobalt_ghostglass", () -> new FacedGlassBlock(faced(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<Block> SHALE_BRICK_GHOSTGLASS = BLOCKS.register("shale_brick_ghostglass", () -> new FacedGlassBlock(faced(MapColor.COLOR_GRAY)));
    public static final DeferredBlock<Block> MANTLE_BRICK_GHOSTGLASS = BLOCKS.register("mantle_brick_ghostglass", () -> new FacedGlassBlock(faced(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> COBALT_BLOCK_GHOSTGLASS = BLOCKS.register("cobalt_block_ghostglass", () -> new FacedGlassBlock(faced(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<Block> SEAGLASS = BLOCKS.register("seaglass", () -> new VariantGlassBlock(base(MapColor.COLOR_LIGHT_BLUE, SoundType.SUSPICIOUS_GRAVEL, 0.6F, 0).friction(0.9F), 5));
    public static final DeferredBlock<Block> MARINE_SEAGLASS_WINDOW = BLOCKS.register("marine_seaglass_window", () -> new SeaWindowBlock(base(MapColor.COLOR_CYAN, SoundType.SUSPICIOUS_GRAVEL, 0.5F, 0).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> VIOLET_SEAGLASS_WINDOW = BLOCKS.register("violet_seaglass_window", () -> new SeaWindowBlock(base(MapColor.COLOR_PURPLE, SoundType.SUSPICIOUS_GRAVEL, 0.5F, 0).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SHALE_LITEPRISM = BLOCKS.register("shale_liteprism", () -> new FacedGlassBlock(faced(MapColor.STONE)));
    public static final DeferredBlock<Block> SHALE_BRICKS_LITEPRISM = BLOCKS.register("shale_bricks_liteprism", () -> new FacedGlassBlock(faced(MapColor.COLOR_GRAY)));
    public static final DeferredBlock<Block> MANTLE_LITEPRISM = BLOCKS.register("mantle_liteprism", () -> new FacedGlassBlock(faced(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> MANTLE_BRICKS_LITEPRISM = BLOCKS.register("mantle_bricks_liteprism", () -> new FacedGlassBlock(faced(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> COBALT_LITEPRISM = BLOCKS.register("cobalt_liteprism", () -> new FacedGlassBlock(faced(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<Block> COBALT_BLOCK_LITEPRISM = BLOCKS.register("cobalt_block_liteprism", () -> new FacedGlassBlock(faced(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<Block> GREENHOUSE_GLASS = BLOCKS.register("greenhouse_glass", () -> new GreenhouseGlassBlock(base(MapColor.COLOR_LIGHT_GRAY, SoundType.GLASS, 1, 10).requiresCorrectToolForDrops()));

    static {
        for (var block : List.of(SHALE_GHOSTGLASS, MANTLE_GHOSTGLASS, COBALT_GHOSTGLASS, SHALE_BRICK_GHOSTGLASS, MANTLE_BRICK_GHOSTGLASS, COBALT_BLOCK_GHOSTGLASS, SEAGLASS, MARINE_SEAGLASS_WINDOW, VIOLET_SEAGLASS_WINDOW, SHALE_LITEPRISM, SHALE_BRICKS_LITEPRISM, MANTLE_LITEPRISM, MANTLE_BRICKS_LITEPRISM, COBALT_LITEPRISM, COBALT_BLOCK_LITEPRISM, GREENHOUSE_GLASS)) ITEMS.registerSimpleBlockItem(block);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    private static BlockBehaviour.Properties base(MapColor color, SoundType sound, float hardness, float resistance) {
        return com.hp.thermalshockreforged.ModRegistration.block().mapColor(color).sound(sound).strength(hardness, resistance).noOcclusion().isRedstoneConductor((state, level, pos) -> false);
    }

    private static BlockBehaviour.Properties faced(MapColor color) {
        return base(color, SoundType.GLASS, 0.5F, 0).instrument(NoteBlockInstrument.HAT).requiresCorrectToolForDrops().emissiveRendering((state, level, pos) -> true);
    }
}
