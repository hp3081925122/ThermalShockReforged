package com.hp.thermalshockreforged.content.doors;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.*;

public final class SpecialDoorsContent {
    private static final DeferredRegister.Blocks BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final Map<DoorKind, DeferredBlock<SpecialDoorBlock>> DOORS = new EnumMap<>(DoorKind.class);
    private static final Map<DoorKind, DeferredItem<SpecialDoorItem>> DISPLAY_ITEMS = new EnumMap<>(DoorKind.class);
    private static final Map<DoorKind, DeferredHolder<BlockEntityType<?>, BlockEntityType<SpecialDoorBlockEntity>>> TYPES = new EnumMap<>(DoorKind.class);
    private static final DeferredBlock<MawDoorPartBlock> MAW_PART = BLOCKS.register("maw_door_part", MawDoorPartBlock::new);

    static {
        for (DoorKind kind : DoorKind.values()) {
            var block = BLOCKS.register(kind.id, () -> kind == DoorKind.BUNKER ? new SpecialDoorBlock.Bunker() : new SpecialDoorBlock(kind));
            DOORS.put(kind, block);
            DISPLAY_ITEMS.put(kind, ITEMS.register(kind.id, () -> new SpecialDoorItem(block.get(), kind)));
            TYPES.put(kind, ENTITIES.register(kind.id, () -> new BlockEntityType<>((pos, state) -> new SpecialDoorBlockEntity(kind, pos, state), block.get())));
        }
    }

    private SpecialDoorsContent() { }
    public static SpecialDoorBlock block(DoorKind kind) { return DOORS.get(kind).get(); }
    public static MawDoorPartBlock mawPart() { return MAW_PART.get(); }
    public static SpecialDoorItem item(DoorKind kind) { return DISPLAY_ITEMS.get(kind).get(); }
    public static BlockEntityType<SpecialDoorBlockEntity> entity(DoorKind kind) { return TYPES.get(kind).get(); }
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
        bus.addListener(SpecialDoorsContent::capabilities);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        for (var type : TYPES.values()) event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(), (entity, side) -> entity.itemHandler(side));
    }
}
