package com.hp.thermalshockreforged.content.weatherutility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class WeatherUtilityContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredBlock<RainDrumBlock> RAIN_DRUM = BLOCKS.register("rain_drum", RainDrumBlock::new);
    public static final DeferredBlock<PurifierBlock> PURIFIER = BLOCKS.register("purifier", PurifierBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PurifierBlockEntity>> PURIFIER_ENTITY = BLOCK_ENTITIES.register("purifier", () -> BlockEntityType.Builder.of(PurifierBlockEntity::new, PURIFIER.get()).build(null));
    public static final DeferredHolder<EntityType<?>, EntityType<PurifierPulseEntity>> PURIFIER_PULSE = ENTITIES.register("purifier_pulse", () -> EntityType.Builder.of(PurifierPulseEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(id("purifier_pulse").toString()));
    static { ITEMS.registerSimpleBlockItem(RAIN_DRUM); ITEMS.registerSimpleBlockItem(PURIFIER); }
    private WeatherUtilityContent() { }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); BLOCK_ENTITIES.register(bus); ENTITIES.register(bus); }
    static void award(ServerPlayer player, String path) {
        var advancement = player.server.getAdvancements().get(id(path));
        if (advancement == null) return;
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
    }
}
