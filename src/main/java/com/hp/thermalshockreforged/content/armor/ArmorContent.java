package com.hp.thermalshockreforged.content.armor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import com.hp.thermalshockreforged.content.armor.ArmorMaterialFactory;
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
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> DIVING_MATERIAL = MATERIALS.register("corrial_diving_helmet", () -> ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, 6), 7, Holder.direct(SoundEvents.EMPTY), 1, .2F, "corrial_ingot", "corrial_diving_helmet"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> DEV_SCARF_MATERIAL = MATERIALS.register("dev_scarf", () -> ArmorMaterialFactory.create(Map.of(ArmorType.CHESTPLATE, 4), 50, Holder.direct(SoundEvents.SNOW_PLACE), 0, .1F, "stratus_alloy", "dev_scarf"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> STRANGESILK_MATERIAL = MATERIALS.register("strangesilk_armour", () -> ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 3), 9, Holder.direct(SoundEvents.ARMOR_EQUIP_LEATHER), 0, 0, "rustsilk", "strangesilk_armour"));
    public static final DeferredItem<GeoSetArmorItem> CORRIAL_DIVING_HELMET = ITEMS.register("corrial_diving_helmet_helmet", () -> new GeoSetArmorItem(DIVING_MATERIAL, ArmorType.HELMET, GeoSetArmorItem.Style.CORRIAL_DIVING, new Item.Properties().durability(330)));
    public static final DeferredItem<GeoSetArmorItem> DEV_SCARF = ITEMS.register("dev_scarf_chestplate", () -> new GeoSetArmorItem(DEV_SCARF_MATERIAL, ArmorType.CHESTPLATE, GeoSetArmorItem.Style.DEV_SCARF, new Item.Properties().durability(368).fireResistant()));
    public static final DeferredItem<GeoSetArmorItem> STRANGESILK_HELMET = ITEMS.register("strangesilk_armour_helmet", () -> new GeoSetArmorItem(STRANGESILK_MATERIAL, ArmorType.HELMET, GeoSetArmorItem.Style.STRANGESILK, new Item.Properties().durability(165)));
    public static final DeferredItem<GeoSetArmorItem> STRANGESILK_CHESTPLATE = ITEMS.register("strangesilk_armour_chestplate", () -> new GeoSetArmorItem(STRANGESILK_MATERIAL, ArmorType.CHESTPLATE, GeoSetArmorItem.Style.STRANGESILK, new Item.Properties().durability(240)));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBY_MATERIAL = MATERIALS.register("ruby_armor", () -> material(false));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> CARBORUNDUM_MATERIAL = MATERIALS.register("carborundum_armor", () -> material(true));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBY_ZENITH_MATERIAL = MATERIALS.register("ruby_zenith_armor", () -> zenithMaterial(false, "ruby_zenith_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLOOD_ZENITH_MATERIAL = MATERIALS.register("blood_zenith_armor", () -> zenithMaterial(true, "blood_zenith_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLOOD_MATERIAL = MATERIALS.register("blood_armor", () -> zenithMaterial(true, "blood_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUSTSILK_MATERIAL = MATERIALS.register("rustsilk_armour", () -> ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, 1, ArmorType.CHESTPLATE, 1), 9, Holder.direct(SoundEvents.ARMOR_EQUIP_LEATHER), 0, 0, "rustsilk", "rustsilk_armour"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> SENTRY_MATERIAL = MATERIALS.register("sentry", () -> ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, 7, ArmorType.CHESTPLATE, 7), 50, Holder.direct(SoundEvents.ARMOR_EQUIP_LEATHER), 0, 0, "silver_sentrapede_hide", "sentry"));

    static {
        ALL.addAll(List.of(CORRIAL_DIVING_HELMET, DEV_SCARF, STRANGESILK_HELMET, STRANGESILK_CHESTPLATE));
        RUSTSILK.add(ITEMS.register("rustsilk_armour_helmet", () -> new GeoSetArmorItem(RUSTSILK_MATERIAL, ArmorType.HELMET, GeoSetArmorItem.Style.RUSTSILK, new Item.Properties().durability(55))));
        RUSTSILK.add(ITEMS.register("rustsilk_armour_chestplate", () -> new GeoSetArmorItem(RUSTSILK_MATERIAL, ArmorType.CHESTPLATE, GeoSetArmorItem.Style.RUSTSILK, new Item.Properties().durability(80))));
        ALL.addAll(RUSTSILK);
        for (ArmorType type : List.of(ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS)) {
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
        for (ArmorType type : List.of(ArmorType.HELMET, ArmorType.CHESTPLATE)) {
            SENTRY.add(ITEMS.register("sentry_" + type.getName(), () -> new GeoSetArmorItem(SENTRY_MATERIAL, type,
                    GeoSetArmorItem.Style.SENTRY, new Item.Properties().durability(type.getDurability(23)))));
        }
        ALL.addAll(SENTRY);
    }

    private static ArmorMaterial material(boolean carbon) {
        return ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, carbon ? 5 : 4, ArmorType.CHESTPLATE, carbon ? 8 : 7, ArmorType.LEGGINGS, carbon ? 5 : 4, ArmorType.BOOTS, carbon ? 4 : 3), carbon ? 12 : 9, Holder.direct(carbon ? SoundEvents.ARMOR_EQUIP_NETHERITE : SoundEvents.ARMOR_EQUIP_DIAMOND), carbon ? 2 : 1, carbon ? 0.2F : 0, carbon ? "carborundum" : "ruby", carbon ? "carborundum_armor" : "ruby_armor");
    }

    private static ArmorMaterial zenithMaterial(boolean blood, String layer) {
        return ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, blood ? 5 : 4, ArmorType.CHESTPLATE, blood ? 8 : 7, ArmorType.LEGGINGS, blood ? 3 : 4, ArmorType.BOOTS, 3), blood ? 6 : 9, Holder.direct(SoundEvents.ARMOR_EQUIP_DIAMOND), blood ? 2.5F : 1.0F, blood ? 0.1F : 0.0F, blood ? "bloodstone" : "ruby", layer);
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
