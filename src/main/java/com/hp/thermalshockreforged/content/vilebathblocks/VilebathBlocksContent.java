package com.hp.thermalshockreforged.content.vilebathblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.*;

public final class VilebathBlocksContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> THERMAL_GEYSER = BLOCKS.register("thermal_geyser", ThermalGeyserBlock::new);
    public static final DeferredBlock<Block> HANGING_SPARKLER_BULB = BLOCKS.register("hanging_sparkler_bulb", HangingSparklerBulbBlock::new);
    public static final DeferredBlock<Block> SPARKLER_BULB = BLOCKS.register("sparkler_bulb", HangingSparklerBulbBlock::new);
    public static final DeferredBlock<Block> FOG_MACHINE = BLOCKS.register("fog_machine", FogMachineBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThermalGeyserBlockEntity>> THERMAL_GEYSER_ENTITY = TYPES.register("thermal_geyser", () -> BlockEntityType.Builder.of(ThermalGeyserBlockEntity::new, THERMAL_GEYSER.get()).build(null));
    static { ITEMS.registerSimpleBlockItem(THERMAL_GEYSER); ITEMS.registerSimpleBlockItem(HANGING_SPARKLER_BULB); ITEMS.registerSimpleBlockItem(SPARKLER_BULB); ITEMS.registerSimpleBlockItem(FOG_MACHINE); }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); TYPES.register(bus); bus.addListener(VilebathBlocksContent::capabilities); }
    private static void capabilities(RegisterCapabilitiesEvent event) { event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, THERMAL_GEYSER_ENTITY.get(), (geyser, side) -> geyser.itemHandler(side)); }
    private VilebathBlocksContent() { }
}
