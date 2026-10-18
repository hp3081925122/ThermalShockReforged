package com.hp.thermalshock.content;

import com.hp.thermalshock.ThermalShock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShock.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PondlerEggBlockEntity>> PONDLER_EGG = TYPES.register("pondler_egg", () -> BlockEntityType.Builder.of(PondlerEggBlockEntity::new, MaterialContent.blocks().get("pondler_egg").get()).build(null));

    private ModBlockEntities() { }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        bus.addListener(ModBlockEntities::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PONDLER_EGG.get(), (egg, side) -> side == null ? null : new SidedInvWrapper(egg, side));
    }
}
