package com.hp.thermalshockreforged.content.npc.huntress;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;

public final class HuntressMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<HuntressPage, DeferredHolder<MenuType<?>, MenuType<HuntressMenu>>> PAGES = new EnumMap<>(HuntressPage.class);
    static { for (var page : HuntressPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new HuntressMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readVarInt())))); }
    public static void register(IEventBus bus) { MENUS.register(bus); }
    public static MenuType<HuntressMenu> menu(HuntressPage page) { return PAGES.get(page).get(); }
    public static boolean open(ServerPlayer player, HuntressEntity npc, HuntressPage page) {
        if (npc == null || !npc.isAlive() || npc.level() != player.level() || player.distanceToSqr(npc) > 64) return false;
        int intro = player.getRandom().nextInt(2) + 1;
        boolean continuing = player.containerMenu instanceof HuntressMenu previous && previous.npcUuid.equals(npc.getUUID());
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(page.title); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new HuntressMenu(page, id, inventory, npc.getId(), npc.getUUID(), intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        return player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeVarInt(intro); }).isPresent();
    }
    private HuntressMenus() { }
}
