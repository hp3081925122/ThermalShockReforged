package com.hp.thermalshockreforged.entity.qo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class QoContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final EnumMap<QoPage, DeferredHolder<MenuType<?>, MenuType<QoMenu>>> PAGES = new EnumMap<>(QoPage.class);
    public static final DeferredHolder<EntityType<?>, EntityType<DefectorMachinistEntity>> DEFECTOR_MACHINIST = TYPES.register("defector_machinist", () -> EntityType.Builder.of(DefectorMachinistEntity::new, MobCategory.MONSTER).sized(.9F, 1.4F).setShouldReceiveVelocityUpdates(true).fireImmune().clientTrackingRange(64).updateInterval(3).build(id("defector_machinist").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThermoidEntity>> THERMOID = TYPES.register("thermoid", () -> EntityType.Builder.of(ThermoidEntity::new, MobCategory.MONSTER).sized(.8F, .8F).setShouldReceiveVelocityUpdates(true).fireImmune().clientTrackingRange(64).updateInterval(3).build(id("thermoid").toString()));
    public static final DeferredItem<IceArtifactItem> ICE_ARTIFACT = ITEMS.register("ice_artifact", IceArtifactItem::new);
    public static final DeferredItem<UneasyAllianceBundleItem> ALLIANCE_BUNDLE = ITEMS.register("uneasy_alliance_loot_bundle", UneasyAllianceBundleItem::new);
    static {
        ITEMS.register("defector_machinist_spawn_egg", () -> new DeferredSpawnEggItem(DEFECTOR_MACHINIST, -1, -1, new Item.Properties()));
        for (var page : QoPage.values()) PAGES.put(page, MENUS.register(page.id, () -> IMenuTypeExtension.create((id, inventory, data) -> new QoMenu(page, id, inventory, data.readVarInt(), data.readUUID(), data.readBlockPos(), data.readUtf()))));
    }
    private QoContent() { }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static MenuType<QoMenu> menu(QoPage page) { return PAGES.get(page).get(); }
    public static boolean open(ServerPlayer player, PathfinderMob npc, BlockPos anchor, QoPage page) {
        if (npc == null || !(npc instanceof DefectorMachinistEntity || npc instanceof com.hp.thermalshockreforged.entity.labmachinist.LabMachinistEntity) || !npc.isAlive() || npc.level() != player.level() || player.distanceToSqr(npc) > 64) return false;
        var fixed = anchor.immutable();
        boolean continuing = player.containerMenu instanceof QoMenu previous && previous.npcUuid.equals(npc.getUUID()) || player.containerMenu instanceof com.hp.thermalshockreforged.entity.labmachinist.LabMenu previousLab && previousLab.npcUuid.equals(npc.getUUID());
        String openingSound = player.containerMenu instanceof QoMenu previous ? previous.page.sound : player.containerMenu instanceof com.hp.thermalshockreforged.entity.labmachinist.LabMenu previousLab ? previousLab.page.sound : "qo_main_speak";
        var provider = new MenuProvider() {
            @Override public Component getDisplayName() { return Component.literal(page.title); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player owner) { return new QoMenu(page, id, inventory, npc.getId(), npc.getUUID(), fixed, openingSound); }
            @Override public boolean shouldTriggerClientSideContainerClosingOnOpen() { return !continuing; }
        };
        return player.openMenu(provider, data -> { data.writeVarInt(npc.getId()); data.writeUUID(npc.getUUID()); data.writeBlockPos(fixed); data.writeUtf(openingSound); }).isPresent();
    }
    public static void give(ServerPlayer player, ItemStack reward) { net.neoforged.neoforge.items.ItemHandlerHelper.giveItemToPlayer(player, reward); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); MENUS.register(bus); bus.addListener(QoContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(DEFECTOR_MACHINIST.get(), DefectorMachinistEntity.createAttributes().build()); event.put(THERMOID.get(), ThermoidEntity.createAttributes().build()); }
}
