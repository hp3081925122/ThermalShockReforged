package com.hp.thermalshockreforged.content.remaininggui;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RemainingGuiContent {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<RemainingGuiPage, DeferredHolder<MenuType<?>, MenuType<RemainingGuiMenu>>> TYPES = new EnumMap<>(RemainingGuiPage.class);

    static {
        for (var page : RemainingGuiPage.values()) TYPES.put(page, MENUS.register(page.id,
                () -> IMenuTypeExtension.create((id, inventory, data) -> new RemainingGuiMenu(page, id, inventory, data))));
    }

    private RemainingGuiContent() { }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static MenuType<RemainingGuiMenu> type(RemainingGuiPage page) { return TYPES.get(page).get(); }
    public static void register(IEventBus bus) {
        MENUS.register(bus);
        bus.addListener(VaultCodePayload::register);
    }
}
