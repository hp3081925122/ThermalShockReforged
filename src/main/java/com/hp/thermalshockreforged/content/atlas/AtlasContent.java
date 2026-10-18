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
    public static final ResourceKey<Level> PALACE = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "aqeron"));
    public static final ResourceLocation BASE_TEMPLATE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "atlasbase");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredItem<Item> VIRUSTONE = ITEMS.register("virustone", VirustoneItem::new);
    public static final DeferredItem<Item> KEYSTONE = ITEMS.register("keystone", KeystoneItem::new);
    public static final DeferredHolder<MenuType<?>, MenuType<AtlasMenu>> MENU = MENUS.register("atlas_menu", () -> IMenuTypeExtension.create(AtlasMenu::new));
    public static final DeferredHolder<EntityType<?>, EntityType<AtlasActivationEntity>> ACTIVATION = ENTITIES.register("atlas_activation", () -> EntityType.Builder.of(AtlasActivationEntity::new, MobCategory.MISC).noSummon().fireImmune().sized(0.01F, 0.01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":atlas_activation"));
    private AtlasContent() { }
    public static void register(IEventBus bus) { ITEMS.register(bus); MENUS.register(bus); ENTITIES.register(bus); }
}
