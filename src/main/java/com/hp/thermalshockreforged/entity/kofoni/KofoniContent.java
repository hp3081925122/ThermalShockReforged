package com.hp.thermalshockreforged.entity.kofoni;
import net.minecraft.world.item.SpawnEggItem;

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

import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class KofoniContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<KofoniPage, DeferredHolder<MenuType<?>, MenuType<KofoniMenu>>> PAGES = new EnumMap<>(KofoniPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<KofoniEntity>> KOFONI = TYPES.register("kofoni", () -> EntityType.Builder.of(KofoniEntity::new, MobCategory.MONSTER).sized(.9F, 1.2F).setShouldReceiveVelocityUpdates(true).fireImmune().clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("kofoni")));
    static {
        ITEMS.register("kofoni_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(KOFONI.get())));
        for (var page : KofoniPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new KofoniMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readBlockPos(), data.readVarInt()))));
    }
    private KofoniContent() { }
    public static net.minecraft.resources.Identifier id(String path) { return net.minecraft.resources.Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static MenuType<KofoniMenu> menu(KofoniPage page) { return PAGES.get(page).get(); }
    public static boolean open(ServerPlayer player, KofoniEntity npc, BlockPos anchor, KofoniPage page) {
        if (npc == null || !npc.isAlive() || npc.level() != player.level() || player.distanceToSqr(npc) > 64) return false;
        var fixedAnchor = anchor.immutable();
        boolean continuing = player.containerMenu instanceof KofoniMenu previous && previous.npcUuid.equals(npc.getUUID());
        int intro = page == KofoniPage.MAIN ? player.getRandom().nextInt(2) + 1 : player.containerMenu instanceof KofoniMenu previous ? previous.intro : 0;
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(page.title); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new KofoniMenu(page, id, inventory, npc.getId(), npc.getUUID(), fixedAnchor, intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        return player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeBlockPos(fixedAnchor); data.writeVarInt(intro); }).isPresent();
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus); bus.addListener(KofoniContent::attributes);
    }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(KOFONI.get(), KofoniEntity.createAttributes().build()); }
}
