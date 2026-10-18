package com.hp.thermalshock.content.armor;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.content.MaterialContent;
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
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ThermalShock.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    public static final List<DeferredItem<GeoSetArmorItem>> RUBY = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> CARBORUNDUM = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> RUBY_ZENITH = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> BLOOD_ZENITH = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> BLOOD = new ArrayList<>();
    public static final List<DeferredItem<GeoSetArmorItem>> ALL = new ArrayList<>();
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBY_MATERIAL = MATERIALS.register("ruby_armor", () -> material(false));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> CARBORUNDUM_MATERIAL = MATERIALS.register("carborundum_armor", () -> material(true));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBY_ZENITH_MATERIAL = MATERIALS.register("ruby_zenith_armor", () -> zenithMaterial(false, "ruby_zenith_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLOOD_ZENITH_MATERIAL = MATERIALS.register("blood_zenith_armor", () -> zenithMaterial(true, "blood_zenith_armor"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLOOD_MATERIAL = MATERIALS.register("blood_armor", () -> zenithMaterial(true, "blood_armor"));

    static {
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
    }

    private static ArmorMaterial material(boolean carbon) {
        return new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, carbon ? 5 : 4, ArmorItem.Type.CHESTPLATE, carbon ? 8 : 7,
                ArmorItem.Type.LEGGINGS, carbon ? 5 : 4, ArmorItem.Type.BOOTS, carbon ? 4 : 3), carbon ? 12 : 9,
                carbon ? SoundEvents.ARMOR_EQUIP_NETHERITE : SoundEvents.ARMOR_EQUIP_DIAMOND,
                () -> Ingredient.of(MaterialContent.items().get(carbon ? "carborundum" : "ruby").get()),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, carbon ? "carborundum_armor" : "ruby_armor"))),
                carbon ? 2 : 1, carbon ? 0.2F : 0);
    }

    private static ArmorMaterial zenithMaterial(boolean blood, String layer) {
        return new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, blood ? 5 : 4, ArmorItem.Type.CHESTPLATE, blood ? 8 : 7,
                ArmorItem.Type.LEGGINGS, blood ? 3 : 4, ArmorItem.Type.BOOTS, 3), blood ? 6 : 9, SoundEvents.ARMOR_EQUIP_DIAMOND,
                () -> Ingredient.of(MaterialContent.items().get(blood ? "bloodstone" : "ruby").get()),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, layer))),
                blood ? 2.5F : 1.0F, blood ? 0.1F : 0.0F);
    }

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::tick);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::death);
        NeoForge.EVENT_BUS.addListener(ArmorSetEvents::clonePlayer);
    }
}
