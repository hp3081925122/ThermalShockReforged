package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> THERM_IFRAME_BYPASS = ResourceKey.create(Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "therm_iframe_bypass"));

    private ModDamageTypes() { }
}
