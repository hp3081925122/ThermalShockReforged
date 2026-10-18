package com.hp.thermalshockreforged.content.status;

import java.util.Set;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

final class PlayerStatusEffect extends MobEffect {
    enum Action { NONE, WARM, ANTITOXIN }
    private final Action action;
    private final boolean milk;

    PlayerStatusEffect(MobEffectCategory category, int color, Action action, boolean milk) {
        super(category, color);
        this.action = action;
        this.milk = milk;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return action != Action.NONE;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (action == Action.WARM) entity.removeEffect(StatusContent.FROSTBITE);
        if (action == Action.ANTITOXIN) entity.removeEffect(MobEffects.POISON);
        return true;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        cures.clear();
        cures.add(EffectCures.PROTECTED_BY_TOTEM);
        if (milk) cures.add(EffectCures.MILK);
    }
}
