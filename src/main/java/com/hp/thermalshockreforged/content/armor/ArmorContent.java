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
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
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
    private static final Holder<ArmorMaterial> DIVING_MATERIAL = Holder.direct(ArmorMaterialFactory.create(30, Map.of(ArmorType.HELMET, 6), 7, net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), 1, .2F, "corrial_ingot", "corrial_diving_helmet"));
    private static final Holder<ArmorMaterial> DEV_SCARF_MATERIAL = Holder.direct(ArmorMaterialFactory.create(23, Map.of(ArmorType.CHESTPLATE, 4), 50, net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SNOW_PLACE), 0, .1F, "none", "dev_scarf"));
    private static final Holder<ArmorMaterial> STRANGESILK_MATERIAL = Holder.direct(ArmorMaterialFactory.create(15, Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 3), 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, "strangesilk", "strangesilk_armour"));
    public static final DeferredItem<GeoSetArmorItem> CORRIAL_DIVING_HELMET = ITEMS.register("corrial_diving_helmet_helmet", () -> new GeoSetArmorItem(DIVING_MATERIAL, ArmorType.HELMET, GeoSetArmorItem.Style.CORRIAL_DIVING, com.hp.thermalshockreforged.ModRegistration.item()));
    public static final DeferredItem<GeoSetArmorItem> DEV_SCARF = ITEMS.register("dev_scarf_chestplate", () -> new GeoSetArmorItem(DEV_SCARF_MATERIAL, ArmorType.CHESTPLATE, GeoSetArmorItem.Style.DEV_SCARF, com.hp.thermalshockreforged.ModRegistration.item().fireResistant()));
    public static final DeferredItem<GeoSetArmorItem> STRANGESILK_HELMET = ITEMS.register("strangesilk_armour_helmet", () -> new GeoSetArmorItem(STRANGESILK_MATERIAL, ArmorType.HELMET, GeoSetArmorItem.Style.STRANGESILK, com.hp.thermalshockreforged.ModRegistration.item()));
    public static final DeferredItem<GeoSetArmorItem> STRANGESILK_CHESTPLATE = ITEMS.register("strangesilk_armour_chestplate", () -> new GeoSetArmorItem(STRANGESILK_MATERIAL, ArmorType.CHESTPLATE, GeoSetArmorItem.Style.STRANGESILK, com.hp.thermalshockreforged.ModRegistration.item()));
    private static final Holder<ArmorMaterial> RUBY_MATERIAL = Holder.direct(material(false));
    private static final Holder<ArmorMaterial> CARBORUNDUM_MATERIAL = Holder.direct(material(true));
    private static final Holder<ArmorMaterial> RUBY_ZENITH_MATERIAL = Holder.direct(zenithMaterial(false, "ruby_zenith_armor"));
    private static final Holder<ArmorMaterial> BLOOD_ZENITH_MATERIAL = Holder.direct(zenithMaterial(true, "blood_zenith_armor"));
    private static final Holder<ArmorMaterial> BLOOD_MATERIAL = Holder.direct(zenithMaterial(true, "blood_armor"));
    private static final Holder<ArmorMaterial> RUSTSILK_MATERIAL = Holder.direct(ArmorMaterialFactory.create(5, Map.of(ArmorType.HELMET, 1, ArmorType.CHESTPLATE, 1), 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, "none", "rustsilk_armour"));
    private static final Holder<ArmorMaterial> SENTRY_MATERIAL = Holder.direct(ArmorMaterialFactory.create(23, Map.of(ArmorType.HELMET, 7, ArmorType.CHESTPLATE, 7), 50, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, "silver_sentrapede_hide", "sentry"));

    static {
        ALL.addAll(List.of(CORRIAL_DIVING_HELMET, DEV_SCARF, STRANGESILK_HELMET, STRANGESILK_CHESTPLATE));
        RUSTSILK.add(ITEMS.register("rustsilk_armour_helmet", () -> new GeoSetArmorItem(RUSTSILK_MATERIAL, ArmorType.HELMET, GeoSetArmorItem.Style.RUSTSILK, com.hp.thermalshockreforged.ModRegistration.item())));
        RUSTSILK.add(ITEMS.register("rustsilk_armour_chestplate", () -> new GeoSetArmorItem(RUSTSILK_MATERIAL, ArmorType.CHESTPLATE, GeoSetArmorItem.Style.RUSTSILK, com.hp.thermalshockreforged.ModRegistration.item())));
        ALL.addAll(RUSTSILK);
        for (ArmorType type : List.of(ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS)) {
            RUBY.add(ITEMS.register("ruby_armor_" + type.getName(), () -> new GeoSetArmorItem(RUBY_MATERIAL, type, false, com.hp.thermalshockreforged.ModRegistration.item())));
            CARBORUNDUM.add(ITEMS.register("carborundum_armor_" + type.getName(), () -> new GeoSetArmorItem(CARBORUNDUM_MATERIAL, type, true, com.hp.thermalshockreforged.ModRegistration.item())));
            RUBY_ZENITH.add(ITEMS.register("ruby_zenith_armor_" + type.getName(), () -> new GeoSetArmorItem(RUBY_ZENITH_MATERIAL, type, GeoSetArmorItem.Style.RUBY_ZENITH, com.hp.thermalshockreforged.ModRegistration.item())));
            BLOOD_ZENITH.add(ITEMS.register("blood_zenith_armor_" + type.getName(), () -> new GeoSetArmorItem(BLOOD_ZENITH_MATERIAL, type, GeoSetArmorItem.Style.BLOOD_ZENITH, com.hp.thermalshockreforged.ModRegistration.item())));
            BLOOD.add(ITEMS.register("blood_armor_" + type.getName(), () -> new GeoSetArmorItem(BLOOD_MATERIAL, type, GeoSetArmorItem.Style.BLOOD, com.hp.thermalshockreforged.ModRegistration.item())));
        }
        ALL.addAll(RUBY);
        ALL.addAll(CARBORUNDUM);
        ALL.addAll(RUBY_ZENITH);
        ALL.addAll(BLOOD_ZENITH);
        ALL.addAll(BLOOD);
        for (ArmorType type : List.of(ArmorType.HELMET, ArmorType.CHESTPLATE)) {
            SENTRY.add(ITEMS.register("sentry_" + type.getName(), () -> new GeoSetArmorItem(SENTRY_MATERIAL, type,
                    GeoSetArmorItem.Style.SENTRY, com.hp.thermalshockreforged.ModRegistration.item())));
        }
        ALL.addAll(SENTRY);
    }

    private static ArmorMaterial material(boolean carbon) {
        return ArmorMaterialFactory.create(35, Map.of(ArmorType.HELMET, carbon ? 5 : 4, ArmorType.CHESTPLATE, carbon ? 8 : 7, ArmorType.LEGGINGS, carbon ? 5 : 4, ArmorType.BOOTS, carbon ? 4 : 3), carbon ? 12 : 9, carbon ? SoundEvents.ARMOR_EQUIP_NETHERITE : SoundEvents.ARMOR_EQUIP_DIAMOND, carbon ? 2 : 1, carbon ? 0.2F : 0, carbon ? "carborundum" : "ruby", carbon ? "carborundum_armor" : "ruby_armor");
    }

    private static ArmorMaterial zenithMaterial(boolean blood, String layer) {
        return ArmorMaterialFactory.create(blood ? 32 : 35, Map.of(ArmorType.HELMET, blood ? 5 : 4, ArmorType.CHESTPLATE, blood ? 8 : 7, ArmorType.LEGGINGS, blood ? 3 : 4, ArmorType.BOOTS, 3), blood ? 6 : 9, SoundEvents.ARMOR_EQUIP_DIAMOND, blood ? 2.5F : 1.0F, blood ? 0.1F : 0.0F, blood ? "bloodstone" : "ruby", layer);
    }

    public static void register(IEventBus bus) {
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
