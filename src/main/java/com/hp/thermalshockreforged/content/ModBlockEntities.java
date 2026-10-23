package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PondlerEggBlockEntity>> PONDLER_EGG = TYPES.register("pondler_egg", () -> new BlockEntityType<>(PondlerEggBlockEntity::new, MaterialContent.blocks().get("pondler_egg").get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.hp.thermalshockreforged.content.decor.nativeblocks.SpiceGeyserBlockEntity>> SPICE_GEYSER = TYPES.register("spice_geyser", () -> new BlockEntityType<>(com.hp.thermalshockreforged.content.decor.nativeblocks.SpiceGeyserBlockEntity::new, com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent.SPICE_GEYSER.get()));

    private ModBlockEntities() { }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        bus.addListener(ModBlockEntities::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, SPICE_GEYSER.get(), (geyser, side) -> geyser.itemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, PONDLER_EGG.get(), (egg, side) -> side == null ? null : new WorldlyContainerWrapper(egg, side));
    }
}
