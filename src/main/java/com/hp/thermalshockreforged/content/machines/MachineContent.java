package com.hp.thermalshockreforged.content.machines;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MachineContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> TASKS = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<TransmutatorBlock> TRANSMUTATOR = BLOCKS.register("transmutator", TransmutatorBlock::new);
    public static final DeferredBlock<ExtractorConstructorBlock> EXTRACTOR_CONSTRUCTOR = BLOCKS.register("extractor_constructor", ExtractorConstructorBlock::new);
    public static final DeferredHolder<EntityType<?>, EntityType<BlasphemerProcessEntity>> BLASPHEMER_PROCESS = TASKS.register("blasphemer_process_task", () -> EntityType.Builder.<BlasphemerProcessEntity>of(BlasphemerProcessEntity::new, MobCategory.MISC)
            .noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":blasphemer_process_task"));
    static {
        ITEMS.registerSimpleBlockItem(TRANSMUTATOR);
        ITEMS.registerSimpleBlockItem(EXTRACTOR_CONSTRUCTOR);
    }
    private MachineContent() { }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static boolean drop(ServerLevel level, double x, double y, double z, String item) {
        var drop = new ItemEntity(level, x, y, z, new ItemStack(MaterialContent.items().get(item).get()));
        drop.setPickUpDelay(10);
        return level.addFreshEntity(drop);
    }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); TASKS.register(bus); }
}
