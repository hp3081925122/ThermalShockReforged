package com.hp.thermalshockreforged.content.armor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ArmorContent {
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> PURPLE_SPARKLE = PARTICLES.register("purple_sparkle", () -> new net.minecraft.core.particles.SimpleParticleType(false));
    public static final List<DeferredItem<GeoSetArmorItem>> RUBY = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> CARBORUNDUM = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> RUBY_ZENITH = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> BLOOD_ZENITH = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> BLOOD = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> SENTRY = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> RUSTSILK = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> ALL = new ArrayList<>();
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> DIVING_MATERIAL = MATERIALS.register("corrial_diving_helmet", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 6), 7, net.minecraft.core.Holder.direct(SoundEvents.EMPTY),
            () -> Ingredient.of(MaterialContent.items().get("corrial_ingot").get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "corrial_diving_helmet"))), 1, .2F));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> DEV_SCARF_MATERIAL = MATERIALS.register("dev_scarf", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.CHESTPLATE, 4), 50, net.minecraft.core.Holder.direct(SoundEvents.SNOW_PLACE),
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "dev_scarf"))), 0, .1F));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> STRANGESILK_MATERIAL = MATERIALS.register("strangesilk_armour", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 3), 9, SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(RUSTSILK.get(0).get(), RUSTSILK.get(1).get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "strangesilk_armour"))), 0, 0));
    public static final DeferredItem<GeoSetArmorItem> CORRIAL_DIVING_HELMET = ITEMS.register("corrial_diving_helmet_helmet", () -> new GeoSetArmorItem(DIVING_MATERIAL, ArmorItem.Type.HELMET, GeoSetArmorItem.Style.CORRIAL_DIVING, new Item.Properties().durability(330)));
    public static final DeferredItem<GeoSetArmorItem> DEV_SCARF = ITEMS.register("dev_scarf_chestplate", () -> new GeoSetArmorItem(DEV_SCARF_MATERIAL, ArmorItem.Type.CHESTPLATE, GeoSetArmorItem.Style.DEV_SCARF, new Item.Properties().durability(368).fireResistant()));
    public static final DeferredItem<GeoSetArmorItem> STRANGESILK_HELMET = ITEMS.register("strangesilk_armour_helmet", () -> new GeoSetArmorItem(STRANGESILK_MATERIAL, ArmorItem.Type.HELMET, GeoSetArmorItem.Style.STRANGESILK, new Item.Properties().durability(165)));
    public static final DeferredItem<GeoSetArmorItem> STRANGESILK_CHESTPLATE = ITEMS.register("strangesilk_armour_chestplate", () -> new GeoSetArmorItem(STRANGESILK_MATERIAL, ArmorItem.Type.CHESTPLATE, GeoSetArmorItem.Style.STRANGESILK, new Item.Properties().durability(240)));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBY_MATERIAL = MATERIALS.register("ruby_armor", () -> material(false));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> CARBORUNDUM_MATERIAL = MATERIALS.register("carborundum_armor", () -> material(true));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBY_ZENITH_MATERIAL = MATERIALS.register("ruby_zenith_armor", () -> zenithMaterial(false, "ruby_zenith_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLOOD_ZENITH_MATERIAL = MATERIALS.register("blood_zenith_armor", () -> zenithMaterial(true, "blood_zenith_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLOOD_MATERIAL = MATERIALS.register("blood_armor", () -> zenithMaterial(true, "blood_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUSTSILK_MATERIAL = MATERIALS.register("rustsilk_armour", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 1, ArmorItem.Type.CHESTPLATE, 1), 9, SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "rustsilk_armour"))), 0, 0));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> SENTRY_MATERIAL = MATERIALS.register("sentry", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 7, ArmorItem.Type.CHESTPLATE, 7), 50, SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(MaterialContent.items().get("silver_sentrapede_hide").get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sentry"))), 0, 0));

    static {
        ALL.addAll(List.of(CORRIAL_DIVING_HELMET, DEV_SCARF, STRANGESILK_HELMET, STRANGESILK_CHESTPLATE));
        RUSTSILK.add(ITEMS.register("rustsilk_armour_helmet", () -> new GeoSetArmorItem(RUSTSILK_MATERIAL, ArmorItem.Type.HELMET, GeoSetArmorItem.Style.RUSTSILK, new Item.Properties().durability(55))));
        RUSTSILK.add(ITEMS.register("rustsilk_armour_chestplate", () -> new GeoSetArmorItem(RUSTSILK_MATERIAL, ArmorItem.Type.CHESTPLATE, GeoSetArmorItem.Style.RUSTSILK, new Item.Properties().durability(80))));
        ALL.addAll(RUSTSILK);
        for (ArmorItem.Type type : List.of(ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS)) {
            RUBY.add(ITEMS.register("ruby_armor_" + type.getName(), () -> new GeoSetArmorItem(RUBY_MATERIAL, type, false, new Item.Properties().durability(type.getDurability(35)))));
            CARBORUNDUM.add(ITEMS.register("carborundum_armor_" + type.getName(), () -> new GeoSetArmorItem(CARBORUNDUM_MATERIAL, type, true, new Item.Properties().durability(type.getDurability(35)))));
            RUBY_ZENITH.add(ITEMS.register("ruby_zenith_armor_" + type.getName(), () -> new GeoSetArmorItem(RUBY_ZENITH_MATERIAL, type, GeoSetArmorItem.Style.RUBY_ZENITH, new Item.Properties().durability(type.getDurability(35)))));
            BLOOD_ZENITH.add(ITEMS.register("blood_zenith_armor_" + type.getName(), () -> new GeoSetArmorItem(BLOOD_ZENITH_MATERIAL, type, GeoSetArmorItem.Style.BLOOD_ZENITH, new Item.Properties().durability(type.getDurability(32)))));
            BLOOD.add(ITEMS.register("blood_armor_" + type.getName(), () -> new GeoSetArmorItem(BLOOD_MATERIAL, type, GeoSetArmorItem.Style.BLOOD, new Item.Properties().durability(type.getDurability(32)))));
        }
        ALL.addAll(RUBY);
        ALL.addAll(CARBORUNDUM);
        ALL.addAll(RUBY_ZENITH);
        ALL.addAll(BLOOD_ZENITH);
        ALL.addAll(BLOOD);
        for (ArmorItem.Type type : List.of(ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE)) {
            SENTRY.add(ITEMS.register("sentry_" + type.getName(), () -> new GeoSetArmorItem(SENTRY_MATERIAL, type,
                    GeoSetArmorItem.Style.SENTRY, new Item.Properties().durability(type.getDurability(23)))));
        }
        ALL.addAll(SENTRY);
    }

    private static ArmorMaterial material(boolean carbon) {
        return new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, carbon ? 5 : 4, ArmorItem.Type.CHESTPLATE, carbon ? 8 : 7,
                ArmorItem.Type.LEGGINGS, carbon ? 5 : 4, ArmorItem.Type.BOOTS, carbon ? 4 : 3), carbon ? 12 : 9,
                carbon ? SoundEvents.ARMOR_EQUIP_NETHERITE : SoundEvents.ARMOR_EQUIP_DIAMOND,
                () -> Ingredient.of(MaterialContent.items().get(carbon ? "carborundum" : "ruby").get()),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, carbon ? "carborundum_armor" : "ruby_armor"))),
                carbon ? 2 : 1, carbon ? 0.2F : 0);
    }

    private static ArmorMaterial zenithMaterial(boolean blood, String layer) {
        return new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, blood ? 5 : 4, ArmorItem.Type.CHESTPLATE, blood ? 8 : 7,
                ArmorItem.Type.LEGGINGS, blood ? 3 : 4, ArmorItem.Type.BOOTS, 3), blood ? 6 : 9, SoundEvents.ARMOR_EQUIP_DIAMOND,
                () -> Ingredient.of(MaterialContent.items().get(blood ? "bloodstone" : "ruby").get()),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, layer))),
                blood ? 2.5F : 1.0F, blood ? 0.1F : 0.0F);
    }

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        PARTICLES.register(bus);
        ITEMS.register(bus);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::tick);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::death);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::clonePlayer);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::serverTick);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::fall);
        NeoForge.EVENT_BUS.addListener(SpecialArmorEvents::hurt);
    }
}
