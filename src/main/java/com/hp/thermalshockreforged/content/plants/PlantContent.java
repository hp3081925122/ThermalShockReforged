package com.hp.thermalshockreforged.content.plants;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PlantContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<Block> ANGLER_BULB = BLOCKS.register("angler_bulb", () -> new GardenFlowerBlock(MobEffects.DIG_SPEED, properties().lightLevel(state -> 5)));
    public static final DeferredBlock<Block> BELLFLOWER = BLOCKS.register("bellflower", () -> new GardenFlowerBlock(MobEffects.REGENERATION, properties()));
    public static final DeferredBlock<Block> DULL_SHRUB = BLOCKS.register("dull_shrub", () -> new GardenFlowerBlock.StickClone(properties().replaceable()));
    public static final DeferredBlock<Block> LOQUE_FERN = BLOCKS.register("loque_fern", () -> new GardenFlowerBlock(MobEffects.LUCK, properties()));
    public static final DeferredBlock<Block> LUNEI = BLOCKS.register("lunei", () -> new GardenFlowerBlock(MobEffects.REGENERATION, properties().hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true)));
    public static final DeferredBlock<Block> PIXIETILL = BLOCKS.register("pixietill", () -> new GardenFlowerBlock(MobEffects.REGENERATION, properties().hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true)));
    public static final DeferredBlock<Block> POLEMONIUM = BLOCKS.register("polemonium", () -> new GardenFlowerBlock(MobEffects.REGENERATION, properties()));
    public static final DeferredBlock<Block> PURIFIED_LOQUE_GRASS = BLOCKS.register("purified_loque_grass", () -> new GardenFlowerBlock(MobEffects.FIRE_RESISTANCE, properties().replaceable()));
    public static final DeferredBlock<Block> TOPHAZ = BLOCKS.register("tophaz", () -> new GardenFlowerBlock(MobEffects.REGENERATION, properties().hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true)));
    public static final DeferredBlock<Block> TWILIT_SHRUB = BLOCKS.register("twilit_shrub", () -> new GardenFlowerBlock.Waterlogged(properties()));
    public static final DeferredBlock<Block> VERMILLION_FERN = BLOCKS.register("vermillion_fern", () -> new GardenFlowerBlock(MobEffects.LUCK, properties()));

    static {
        for (var block : List.of(ANGLER_BULB, BELLFLOWER, DULL_SHRUB, LOQUE_FERN, LUNEI, PIXIETILL, POLEMONIUM, PURIFIED_LOQUE_GRASS, TOPHAZ, TWILIT_SHRUB, VERMILLION_FERN)) ITEMS.registerSimpleBlockItem(block);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission()
                .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY);
    }
}
