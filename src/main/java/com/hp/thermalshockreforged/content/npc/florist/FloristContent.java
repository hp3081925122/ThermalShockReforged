package com.hp.thermalshockreforged.content.npc.florist;
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

public final class FloristContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<FloristPage, DeferredHolder<MenuType<?>, MenuType<FloristMenu>>> PAGES = new EnumMap<>(FloristPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<FloristEntity>> FLORIST = TYPES.register("florist", () -> EntityType.Builder.of(FloristEntity::new, MobCategory.MONSTER).sized(.6F, 2F).fireImmune().clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("florist")));
    static {
        ITEMS.register("leafcutter", LeafcutterItem::new);
        ITEMS.register("florist_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(FLORIST.get())));
        for (var page : FloristPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new FloristMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readBlockPos(), data.readVarInt()))));
    }
    public static MenuType<FloristMenu> menu(FloristPage page) { return PAGES.get(page).get(); }
    public static void open(ServerPlayer player, FloristEntity npc, BlockPos anchor, FloristPage page) {
        var fixedAnchor = anchor.immutable();
        int intro = page == FloristPage.MAIN ? 1 + player.getRandom().nextInt(2) : 0;
        boolean continuing = player.containerMenu instanceof FloristMenu previous && previous.npcUuid.equals(npc.getUUID());
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.empty(); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new FloristMenu(page, id, inventory, npc.getId(), npc.getUUID(), fixedAnchor, intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeBlockPos(fixedAnchor); data.writeVarInt(intro); });
    }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus); bus.addListener(FloristContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(FLORIST.get(), FloristEntity.createAttributes().build()); }
}
