package com.hp.thermalshockreforged.content.legacyfabricator;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LegacyFabricatorContent {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<LegacyFabricatorMenu>> ENDER_TENTACLE = MENUS.register("ender_tentacle_page", () -> IMenuTypeExtension.create((id, inventory, data) -> new LegacyFabricatorMenu(id, inventory, data, Page.ENDER_TENTACLE)));
    public static final DeferredHolder<MenuType<?>, MenuType<LegacyFabricatorMenu>> GHOSTGLASS_SWORD = MENUS.register("ghostglass_sword_page", () -> IMenuTypeExtension.create((id, inventory, data) -> new LegacyFabricatorMenu(id, inventory, data, Page.GHOSTGLASS_SWORD)));

    public enum Page {
        ENDER_TENTACLE("ender_tentacle_page"),
        GHOSTGLASS_SWORD("ghostglass_sword_page");

        public final String id;
        Page(String id) { this.id = id; }
        public MenuType<LegacyFabricatorMenu> type() { return (this == ENDER_TENTACLE ? LegacyFabricatorContent.ENDER_TENTACLE : LegacyFabricatorContent.GHOSTGLASS_SWORD).get(); }
    }

    private LegacyFabricatorContent() { }
    public static void register(IEventBus bus) { MENUS.register(bus); }
}
