package com.hp.thermalshock.content.plants;

import com.hp.thermalshock.ThermalShock;
import java.util.List;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class SpecialPlantContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShock.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    public static final DeferredBlock<Block> BOUNCEWEED = BLOCKS.register("bounceweed", () -> new BuffWeedBlock(MobEffects.JUMP, glowing()));
    public static final DeferredBlock<Block> FLOATWEED = BLOCKS.register("floatweed", () -> new BuffWeedBlock(MobEffects.SLOW_FALLING, glowing()));
    public static final DeferredBlock<Block> VERMILLION_ROOTS = BLOCKS.register("vermillion_roots", VermillionRootsBlock::new);
    public static final DeferredBlock<Block> LOQUE_DANDELION = BLOCKS.register("loque_dandelion", () -> new DandelionBlock(false, properties()));
    public static final DeferredBlock<Block> WINDBLOWN_LOQUE_DANDELION = BLOCKS.register("windblown_loque_dandelion", () -> new DandelionBlock(true, properties()));

    static {
        for (var block : List.of(BOUNCEWEED, FLOATWEED, VERMILLION_ROOTS, LOQUE_DANDELION, WINDBLOWN_LOQUE_DANDELION)) ITEMS.registerSimpleBlockItem(block);
    }

    private SpecialPlantContent() { }

    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission()
                .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties glowing() {
        return properties().lightLevel(state -> 9).hasPostProcess((state, level, pos) -> true).emissiveRendering((state, level, pos) -> true);
    }
}
