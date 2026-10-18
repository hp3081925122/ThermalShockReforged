package com.hp.thermalshockreforged.content.npc.shutterbug;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
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
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class ShutterbugContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<ShutterbugPage, DeferredHolder<MenuType<?>, MenuType<ShutterbugMenu>>> PAGES = new EnumMap<>(ShutterbugPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<ShutterbugEntity>> SHUTTERBUG = TYPES.register("shutterbug", () -> EntityType.Builder.of(ShutterbugEntity::new, MobCategory.MONSTER).sized(.6F, 1.8F).fireImmune().clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":shutterbug"));
    static {
        ITEMS.register("shutterbug_spawn_egg", () -> new DeferredSpawnEggItem(SHUTTERBUG, -1, -1, new Item.Properties()));
        for (var page : ShutterbugPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new ShutterbugMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readBlockPos(), data.readVarInt()))));
    }
    public static MenuType<ShutterbugMenu> menu(ShutterbugPage page) { return PAGES.get(page).get(); }
    public static void open(ServerPlayer player, ShutterbugEntity npc, BlockPos anchor, ShutterbugPage page) {
        var fixedAnchor = anchor.immutable();
        int intro = page == ShutterbugPage.MAIN ? 1 + player.getRandom().nextInt(2) : 0;
        boolean previousIntro2 = player.containerMenu instanceof ShutterbugMenu previous && previous.page == ShutterbugPage.INTRO_2;
        boolean continuing = player.containerMenu instanceof ShutterbugMenu previous && previous.npcUuid.equals(npc.getUUID());
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(page.title); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new ShutterbugMenu(page, id, inventory, npc.getId(), npc.getUUID(), fixedAnchor, intro); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        var opened = player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeBlockPos(fixedAnchor); data.writeVarInt(intro); });
        if (opened.isPresent() && page.voiced()) sound(player, fixedAnchor, previousIntro2 ? "unette_ok" : "unette_main_speak", SoundSource.VOICE);
    }
    static void sound(ServerPlayer player, BlockPos anchor, String name, SoundSource source) { player.connection.send(new ClientboundSoundPacket(Holder.direct(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name))), source, anchor.getX(), anchor.getY(), anchor.getZ(), 1, 1, player.getRandom().nextLong())); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus); bus.addListener(ShutterbugContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SHUTTERBUG.get(), ShutterbugEntity.createAttributes().build()); }
}
