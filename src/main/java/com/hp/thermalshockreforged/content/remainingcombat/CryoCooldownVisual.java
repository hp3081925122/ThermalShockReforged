package com.hp.thermalshockreforged.content.remainingcombat;

import java.util.Set;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.*;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

public final class CryoCooldownVisual extends MobEffect {
    public CryoCooldownVisual() { super(MobEffectCategory.NEUTRAL, -1); }
    @Override public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        cures.clear(); cures.add(EffectCures.PROTECTED_BY_TOTEM);
    }
    public static boolean tickClientEffects(LivingEntity entity, boolean internalCooldown, BiConsumer<MobEffectInstance, Boolean> updated) {
        if (!entity.level().isClientSide || (!internalCooldown && !entity.hasEffect(RemainingCombatContent.CRYO_VISUAL))) return false;
        for (var effect : new ArrayList<>(entity.getActiveEffects())) {
            boolean active = effect.tick(entity, () -> updated.accept(effect, true));
            if (active && effect.getDuration() % 600 == 0) updated.accept(effect, false);
        }
        boolean emit = entity.isInvisible() ? entity.getRandom().nextInt(15) == 0 : entity.getRandom().nextBoolean();
        if (emit) LegacyEffectParticles.emit(entity, 0xFFFFFF, internalCooldown ? 2 : 1, RemainingCombatContent.CRYO_VISUAL);
        return true;
    }
}
