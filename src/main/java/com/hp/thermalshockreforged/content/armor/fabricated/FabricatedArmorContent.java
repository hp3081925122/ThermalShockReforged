package com.hp.thermalshockreforged.content.armor.fabricated;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.core.Holder;
import com.hp.thermalshockreforged.content.armor.ArmorMaterialFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FabricatedArmorContent {
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> COOLDOWN_VISUAL = EFFECTS.register("propulsion_boots_cooldown", CooldownVisual::new);
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> PROPULSION_MATERIAL = MATERIALS.register("propulsion_boots", () -> ArmorMaterialFactory.create(Map.of(ArmorType.BOOTS, 3), 9, Holder.direct(SoundEvents.ARMOR_EQUIP_IRON), 1, 0, "corrial_ingot", "propulsion_boots"));
    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> SILENCED_MATERIAL = MATERIALS.register("silenced_armor", () -> ArmorMaterialFactory.create(Map.of(ArmorType.HELMET, 7, ArmorType.CHESTPLATE, 5), 20, Holder.direct(SoundEvents.ARMOR_EQUIP_NETHERITE), 2, .1F, "stratus_alloy", "silenced_armor"));
    public static final DeferredItem<FabricatedArmorItem> PROPULSION_BOOTS = ITEMS.register("propulsion_boots_boots", () -> new FabricatedArmorItem(
            PROPULSION_MATERIAL, ArmorType.BOOTS, FabricatedArmorItem.Style.PROPULSION, new Item.Properties().durability(234)));
    public static final DeferredItem<FabricatedArmorItem> SILENCED_HELMET = ITEMS.register("silenced_armor_helmet", () -> new FabricatedArmorItem(
            SILENCED_MATERIAL, ArmorType.HELMET, FabricatedArmorItem.Style.SILENCED, new Item.Properties().durability(572).fireResistant()));
    public static final DeferredItem<FabricatedArmorItem> SILENCED_CHESTPLATE = ITEMS.register("silenced_armor_chestplate", () -> new FabricatedArmorItem(
            SILENCED_MATERIAL, ArmorType.CHESTPLATE, FabricatedArmorItem.Style.SILENCED, new Item.Properties().durability(832).fireResistant()));
    public static final List<DeferredItem<FabricatedArmorItem>> ALL = List.of(PROPULSION_BOOTS, SILENCED_HELMET, SILENCED_CHESTPLATE);

    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }

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
    }
}
