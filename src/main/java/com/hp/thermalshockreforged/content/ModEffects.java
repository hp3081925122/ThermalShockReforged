package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> HEART_BOOST = EFFECTS.register("heart_boost", HeartBoostEffect::new);

    private ModEffects() { }

    public static void register(IEventBus bus) { EFFECTS.register(bus); }

    private static final class HeartBoostEffect extends MobEffect {
        private HeartBoostEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFF6699);
            addAttributeModifier(Attributes.MAX_HEALTH, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "heart_boost"), 4, AttributeModifier.Operation.ADD_VALUE);
        }
    }
}
