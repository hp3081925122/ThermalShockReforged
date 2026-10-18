package com.hp.thermalshockreforged.content.npc.ly;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
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

public final class LyContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<LyPage, DeferredHolder<MenuType<?>, MenuType<LyMenu>>> PAGES = new EnumMap<>(LyPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<LyEntity>> LY = TYPES.register("ly", () -> EntityType.Builder.of(LyEntity::new, MobCategory.MONSTER).sized(1.1F, 2.1F).fireImmune().clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":ly"));
    static {
        ITEMS.register("ly_spawn_egg", () -> new DeferredSpawnEggItem(LY, -1, -1, new Item.Properties()));
        for (var page : LyPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new LyMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readBlockPos(), data.readVarInt()))));
    }
    private LyContent() { }
    public static MenuType<LyMenu> menu(LyPage page) { return PAGES.get(page).get(); }
    public static boolean open(ServerPlayer player, LyEntity npc, BlockPos anchor, LyPage page) {
        var fixedAnchor = anchor.immutable();
        boolean continuing = player.containerMenu instanceof LyMenu previous && previous.npcUuid.equals(npc.getUUID());
        int intro = page == LyPage.MAIN ? player.getRandom().nextInt(2) + 1 : player.containerMenu instanceof LyMenu previous ? previous.intro : 0;
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(page.title); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new LyMenu(page, id, inventory, npc.getId(), npc.getUUID(), fixedAnchor, intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        return player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeBlockPos(fixedAnchor); data.writeVarInt(intro); }).isPresent();
    }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus); bus.addListener(LyContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(LY.get(), LyEntity.createAttributes().build()); }
}
