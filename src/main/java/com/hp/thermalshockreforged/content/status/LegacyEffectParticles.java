package com.hp.thermalshockreforged.content.status;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;

public final class LegacyEffectParticles {
    private LegacyEffectParticles() { }
    public static void emit(LivingEntity entity, int baseColor, int baseWeight) { emit(entity, baseColor, baseWeight, null); }
    public static void emit(LivingEntity entity, int baseColor, int baseWeight, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> replacedEffect) {
        if (replacedEffect != null) {
            var active = entity.getEffect(replacedEffect);
            if (active != null) baseWeight = Math.max(baseWeight, active.getAmplifier() + 1);
        }
        float red = baseWeight * ((baseColor >> 16) & 255) / 255.0F;
        float green = baseWeight * ((baseColor >> 8) & 255) / 255.0F;
        float blue = baseWeight * (baseColor & 255) / 255.0F;
        int weight = baseWeight;
        for (var effect : entity.getActiveEffects()) {
            if (!effect.isVisible() || (replacedEffect != null && effect.is(replacedEffect))) continue;
            int color = effect.getEffect().value().getColor();
            int amount = effect.getAmplifier() + 1;
            red += amount * ((color >> 16) & 255) / 255.0F;
            green += amount * ((color >> 8) & 255) / 255.0F;
            blue += amount * (color & 255) / 255.0F;
            weight += amount;
        }
        int color = 0xFF000000 | ((int)(red / weight * 255) << 16) | ((int)(green / weight * 255) << 8) | (int)(blue / weight * 255);
        entity.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color), entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), ((color >> 16) & 255) / 255.0, ((color >> 8) & 255) / 255.0, (color & 255) / 255.0);
    }
}
