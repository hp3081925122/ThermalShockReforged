package com.hp.thermalshockreforged.content.dependencies;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class DependencyBlocks {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> PURE_MUCK = BLOCKS.register("pure_muck", Muck::new);
    public static final DeferredBlock<Block> COBALT_BLOCK = BLOCKS.register("cobalt_block", Cobalt::new);
    static { ITEMS.registerSimpleBlockItem(PURE_MUCK); ITEMS.registerSimpleBlockItem(COBALT_BLOCK); }

    private DependencyBlocks() { }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }

    private static final class Muck extends Block implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
        private Muck() { super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_GRAY).sound(SoundType.MUD).strength(0.8F, 4).speedFactor(0.6F).jumpFactor(0.7F)); }
        @Override protected int getLightDampening(BlockState state) { return 15; }
        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
            tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.pure_muck.description_0"));
        }
    }

    private static final class Cobalt extends Block {
        private static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);
        private Cobalt() {
            super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANCIENT_DEBRIS)
                    .strength(1, 8.001806F).requiresCorrectToolForDrops().lightLevel(state -> switch (state.getValue(BLOCKSTATE)) { case 1 -> 5; case 2 -> 15; default -> 0; }));
            registerDefaultState(stateDefinition.any().setValue(BLOCKSTATE, 0));
        }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BLOCKSTATE); }
        @Override protected int getLightDampening(BlockState state) { return 15; }
    }
}
