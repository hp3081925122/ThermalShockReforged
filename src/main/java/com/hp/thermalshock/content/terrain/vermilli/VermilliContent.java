package com.hp.thermalshock.content.terrain.vermilli;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.content.terrain.SnowCoveredLeaves;
import com.hp.thermalshock.content.terrain.ThermalSaplingBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class VermilliContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShock.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ThermalShock.MOD_ID);
    public static final DeferredBlock<Block> VERMILLI_LEAVES = BLOCKS.register("vermilli_leaves", () -> new SnowCoveredLeaves(MapColor.COLOR_RED, 8, false));
    public static final DeferredBlock<Block> VERMILLION_SAPLING = BLOCKS.register("vermillion_sapling", () -> new ThermalSaplingBlock("vermillion", "vermillion_tree"));
    public static final DeferredHolder<Feature<?>, StructureFeature> STRUCTURE_FEATURE = FEATURES.register("structure_feature", StructureFeature::new);

    static {
        ITEMS.registerSimpleBlockItem(VERMILLI_LEAVES);
        ITEMS.registerSimpleBlockItem(VERMILLION_SAPLING);
    }

    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); FEATURES.register(bus); }
}
