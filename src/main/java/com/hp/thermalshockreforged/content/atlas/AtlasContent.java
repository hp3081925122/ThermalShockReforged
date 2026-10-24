package com.hp.thermalshockreforged.content.atlas;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;

public final class AtlasContent {
    public static final ResourceKey<Level> PALACE = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "aqeron"));
    public static final Identifier BASE_TEMPLATE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "atlasbase");
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredItem<Item> VIRUSTONE = ITEMS.register("virustone", VirustoneItem::new);
    public static final DeferredItem<Item> KEYSTONE = ITEMS.register("keystone", KeystoneItem::new);
    public static final DeferredHolder<MenuType<?>, MenuType<AtlasMenu>> MENU = MENUS.register("atlas_menu", () -> IMenuTypeExtension.create(AtlasMenu::new));
    public static final DeferredHolder<EntityType<?>, EntityType<AtlasActivationEntity>> ACTIVATION = ENTITIES.register("atlas_activation", () -> EntityType.Builder.of(AtlasActivationEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(0.01F, 0.01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("atlas_activation")));
    private AtlasContent() { }
    private static final DeferredRegister<net.neoforged.neoforge.attachment.AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<net.neoforged.neoforge.attachment.AttachmentType<?>, net.neoforged.neoforge.attachment.AttachmentType<Integer>> ARRIVAL_VISUAL = ATTACHMENTS.register("arrival_protection_visual", () -> net.neoforged.neoforge.attachment.AttachmentType.builder(() -> 0).sync(new net.neoforged.neoforge.attachment.AttachmentSyncHandler<Integer>() {
        @Override public void write(net.minecraft.network.RegistryFriendlyByteBuf buf, Integer ticks, boolean initialSync) { buf.writeVarInt(ticks); }
        @Override public Integer read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, net.minecraft.network.RegistryFriendlyByteBuf buf, Integer previous) {
            int ticks = buf.readVarInt();
            if (holder instanceof AtlasArrivalState arrival) arrival.receiveArrivalProtectionData(ticks);
            return ticks;
        }
    }).build());

    public static void register(IEventBus bus) { ITEMS.register(bus); MENUS.register(bus); ENTITIES.register(bus); ATTACHMENTS.register(bus); }
}
