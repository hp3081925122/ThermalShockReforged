package com.hp.thermalshockreforged.content.atlas;

import java.util.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;

public final class AtlasEffectDisplay {
    private AtlasEffectDisplay() { }
    public static Collection<MobEffectInstance> effects(LivingEntity entity) {
        var real = entity.getActiveEffects();
        if (!(entity instanceof AtlasArrivalState arrival) || !arrival.hasArrivalProtectionVisual()) return real;
        List<MobEffectInstance> display = new ArrayList<>(real.size() + 1);
        int amplifier = 9, duration = arrival.arrivalProtectionTicks();
        for (var effect : real) {
            if (effect.is(MobEffects.DAMAGE_RESISTANCE)) {
                if (effect.getAmplifier() > amplifier) { amplifier = effect.getAmplifier(); duration = effect.getDuration(); }
                else if (effect.getAmplifier() == amplifier) duration = Math.max(duration, effect.getDuration());
            } else display.add(effect);
        }
        display.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier, false, true));
        return display;
    }
}
