package com.hp.thermalshockreforged.content.npc.benzor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.benzor.*;
import java.util.EnumMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.*;

public final class BenzorMenus {
    public static final String INFO = "thermal_shock_reforged_npc_info_benzor";
    static final String INTRO = "thermal_shock_reforged_benzor_spice_introduced";
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<BenzorPage, DeferredHolder<MenuType<?>, MenuType<BenzorMenu>>> PAGES = new EnumMap<>(BenzorPage.class);
    static { for (var page : BenzorPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new BenzorMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readVarInt())))); }
    public static void register(IEventBus bus) { MENUS.register(bus); net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(BenzorMenus::clonePlayer); }
    public static MenuType<BenzorMenu> menu(BenzorPage page) { return PAGES.get(page).get(); }
    public static boolean blackpowder(ServerPlayer player) { var advancement = player.getServer().getAdvancements().get(BenzorContent.id("blackpowder_get")); return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone(); }
    public static boolean open(ServerPlayer player, BenzorEntity npc, BenzorPage page) {
        if (npc == null || !npc.isAlive() || npc.level() != player.level() || player.distanceToSqr(npc) > 64 || page == BenzorPage.BLACKPOWDER && !blackpowder(player)) return false;
        int intro = page == BenzorPage.MAIN || page == BenzorPage.INFO ? player.getRandom().nextInt(2) + 1 : player.containerMenu instanceof BenzorMenu previous ? previous.intro : 0;
        boolean continuing = player.containerMenu instanceof BenzorMenu previous && previous.npcUuid.equals(npc.getUUID());
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.translatable("gui.thermal_shock_reforged.benzor.title_" + page.id); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new BenzorMenu(page, id, inventory, npc.getId(), npc.getUUID(), intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        return player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeVarInt(intro); }).isPresent();
    }
    private static void clonePlayer(PlayerEvent.Clone event) { for (String key : new String[]{INTRO, INFO}) if (event.getOriginal().getPersistentData().getBoolean(key)) event.getEntity().getPersistentData().putBoolean(key, true); }
    private BenzorMenus() { }
}
