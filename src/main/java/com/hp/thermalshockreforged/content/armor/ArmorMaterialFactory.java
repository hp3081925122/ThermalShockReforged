package com.hp.thermalshockreforged.content.armor;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class ArmorMaterialFactory {
    private ArmorMaterialFactory() { }

    public static ArmorMaterial create(Map<ArmorType, Integer> defense, int enchantment, Holder<SoundEvent> sound, float toughness, float knockback, String repair, String asset) {
        TagKey<Item> repairTag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("thermal_shock_reforged", "repair/" + repair));
        ResourceKey<EquipmentAsset> assetKey = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("thermal_shock_reforged", asset));
        return new ArmorMaterial(35, defense, enchantment, sound, toughness, knockback, repairTag, assetKey);
    }
}
