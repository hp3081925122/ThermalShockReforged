package com.hp.thermalshockreforged.content.status;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;

public final class LegacyCooldownVisuals {
    private LegacyCooldownVisuals() { }
    public static ColorParticleOption particle(Iterable<MobEffectInstance> effects) {
        double red = 510, green = 510, blue = 510, weight = 2;
        for (MobEffectInstance effect : effects) {
            if (!effect.isVisible()) continue;
            int color = effect.getEffect().value().getColor();
            double amount = (double)effect.getAmplifier() + 1;
            red += ((color >> 16) & 255) * amount; green += ((color >> 8) & 255) * amount; blue += (color & 255) * amount; weight += amount;
        }
        int color = 0xFF000000 | ((int)(red / weight) << 16) | ((int)(green / weight) << 8) | (int)(blue / weight);
        return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color);
    }
}
