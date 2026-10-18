package com.hp.thermalshockreforged.content.dialogue;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;

public final class DialogueContent {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<DialoguePage, DeferredHolder<MenuType<?>, MenuType<DialogueMenu>>> TYPES = new EnumMap<>(DialoguePage.class);
    static { for (var page : DialoguePage.values()) TYPES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new DialogueMenu(page, id, inventory, data.readBlockPos())))); }
    public static MenuType<DialogueMenu> type(DialoguePage page) { return TYPES.get(page).get(); }
    public static void register(IEventBus bus) { MENUS.register(bus); }
}
