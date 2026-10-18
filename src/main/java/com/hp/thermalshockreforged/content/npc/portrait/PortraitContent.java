package com.hp.thermalshockreforged.content.npc.portrait;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class PortraitContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<PortraitCharacterEntity>> PORTRAIT = TYPES.register("portrait_character", () -> EntityType.Builder.of(PortraitCharacterEntity::new, MobCategory.MONSTER).sized(1, 4.5F).fireImmune().clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":portrait_character"));
    private static final DeferredHolder<MenuType<?>, MenuType<PortraitMenu>> NYX = menu("laven_civil_1_tlk", false);
    private static final DeferredHolder<MenuType<?>, MenuType<PortraitMenu>> IBEX = menu("laven_civil_2_tlk", true);
    static { ITEMS.register("portrait_character_spawn_egg", () -> new DeferredSpawnEggItem(PORTRAIT, -1, -1, new Item.Properties())); }
    private PortraitContent() { }
    private static DeferredHolder<MenuType<?>, MenuType<PortraitMenu>> menu(String name, boolean ibex) {
        return MENUS.register(name, () -> IMenuTypeExtension.create((id, inventory, data) -> new PortraitMenu(ibex, id, inventory, data.readVarInt(), data.readUUID())));
    }
    public static MenuType<PortraitMenu> menu(boolean ibex) { return (ibex ? IBEX : NYX).get(); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus); bus.addListener(PortraitContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(PORTRAIT.get(), PortraitCharacterEntity.createAttributes().build()); }
    static void open(ServerPlayer player, PortraitCharacterEntity npc, boolean ibex) {
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(ibex ? "LavenCivil2Tlk" : "LavenCivil1Tlk"); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new PortraitMenu(ibex, id, inventory, npc.getId(), npc.getUUID()); }
        };
        player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); });
    }
}
