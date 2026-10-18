package com.hp.thermalshockreforged.content.crates;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CrateContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> FERRUM_CRATE = BLOCKS.register("ferrum_crate", () -> new CrateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.LANTERN).strength(6, 10).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CORRIAL_CRATE = BLOCKS.register("corrial_crate", () -> new CrateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).sound(SoundType.LANTERN).strength(6, 10).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> FRIGID_CRATE = BLOCKS.register("frigid_crate", () -> new CrateBlock.Frigid(BlockBehaviour.Properties.of().mapColor(MapColor.CLAY).sound(SoundType.GLASS).strength(6, 10).requiresCorrectToolForDrops()));
    static { for (var block : List.of(FERRUM_CRATE, CORRIAL_CRATE, FRIGID_CRATE)) ITEMS.registerSimpleBlockItem(block); }
    private CrateContent() { }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
}
