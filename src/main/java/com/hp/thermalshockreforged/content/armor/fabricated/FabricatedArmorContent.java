package com.hp.thermalshockreforged.content.armor.fabricated;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FabricatedArmorContent {
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> COOLDOWN_VISUAL = EFFECTS.register("propulsion_boots_cooldown", CooldownVisual::new);
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> PROPULSION_MATERIAL = MATERIALS.register("propulsion_boots", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.BOOTS, 3), 9, SoundEvents.ARMOR_EQUIP_IRON,
            () -> Ingredient.of(MaterialContent.items().get("corrial_ingot").get()),
            List.of(new ArmorMaterial.Layer(id("propulsion_boots"))), 1, 0));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> SILENCED_MATERIAL = MATERIALS.register("silenced_armor", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 7, ArmorItem.Type.CHESTPLATE, 5), 20, SoundEvents.ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.of(MaterialContent.items().get("stratus_alloy").get()),
            List.of(new ArmorMaterial.Layer(id("silenced_armor"))), 2, .1F));
    public static final DeferredItem<FabricatedArmorItem> PROPULSION_BOOTS = ITEMS.register("propulsion_boots_boots", () -> new FabricatedArmorItem(
            PROPULSION_MATERIAL, ArmorItem.Type.BOOTS, FabricatedArmorItem.Style.PROPULSION, new Item.Properties().durability(234)));
    public static final DeferredItem<FabricatedArmorItem> SILENCED_HELMET = ITEMS.register("silenced_armor_helmet", () -> new FabricatedArmorItem(
            SILENCED_MATERIAL, ArmorItem.Type.HELMET, FabricatedArmorItem.Style.SILENCED, new Item.Properties().durability(572).fireResistant()));
    public static final DeferredItem<FabricatedArmorItem> SILENCED_CHESTPLATE = ITEMS.register("silenced_armor_chestplate", () -> new FabricatedArmorItem(
            SILENCED_MATERIAL, ArmorItem.Type.CHESTPLATE, FabricatedArmorItem.Style.SILENCED, new Item.Properties().durability(832).fireResistant()));
    public static final List<DeferredItem<FabricatedArmorItem>> ALL = List.of(PROPULSION_BOOTS, SILENCED_HELMET, SILENCED_CHESTPLATE);

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        EFFECTS.register(bus);
        bus.addListener(FabricatedArmorPayloads::register);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorEvents::tick);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorEvents::death);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorEvents::clonePlayer);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorEvents::tracking);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorEvents::particles);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorEvents::login);
        NeoForge.EVENT_BUS.addListener(FabricatedArmorEvents::dimension);
    }

    private static final class CooldownVisual extends MobEffect {
        private CooldownVisual() { super(MobEffectCategory.NEUTRAL, 0x999999); }
        @Override public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) { cures.clear(); }
    }
}
