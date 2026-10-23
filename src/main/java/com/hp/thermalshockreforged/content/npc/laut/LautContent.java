package com.hp.thermalshockreforged.content.npc.laut;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class LautContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<LautPage, DeferredHolder<MenuType<?>, MenuType<LautMenu>>> PAGES = new EnumMap<>(LautPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<LautEntity>> LAUT = TYPES.register("laut", () -> EntityType.Builder.of(LautEntity::new, MobCategory.MONSTER).sized(.8F, 2.1F).fireImmune().clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("laut")));
    static {
        ITEMS.register("laut_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(LAUT.get())));
        for (var page : LautPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new LautMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readBlockPos()))));
    }
    public static MenuType<LautMenu> menu(LautPage page) { return PAGES.get(page).get(); }
    public static void open(ServerPlayer player, LautEntity npc, BlockPos anchor, LautPage page) {
        var fixedAnchor = anchor.immutable();
        boolean continuing = player.containerMenu instanceof LautMenu previous && previous.npcUuid.equals(npc.getUUID());
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.empty(); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new LautMenu(page, id, inventory, npc.getId(), npc.getUUID(), fixedAnchor); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeBlockPos(fixedAnchor); });
    }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus); bus.addListener(LautContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(LAUT.get(), LautEntity.createAttributes().build()); }
}
