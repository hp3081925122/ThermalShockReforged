package com.hp.thermalshockreforged.content.remainingcombat;

import java.util.ArrayList;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.*;

public final class CryoCooldownVisual extends MobEffect {
    public CryoCooldownVisual() { super(MobEffectCategory.NEUTRAL, -1); }
    public static boolean tickClientEffects(LivingEntity entity, boolean internalCooldown) {
        if (!entity.level().isClientSide() || (!internalCooldown && !entity.hasEffect(RemainingCombatContent.CRYO_VISUAL))) return false;
        for (var effect : new ArrayList<>(entity.getActiveEffects())) {
            effect.tickClient();
        }
        boolean emit = entity.isInvisible() ? entity.getRandom().nextInt(15) == 0 : entity.getRandom().nextBoolean();
        if (emit) LegacyEffectParticles.emit(entity, 0xFFFFFF, internalCooldown ? 2 : 1, RemainingCombatContent.CRYO_VISUAL);
        return true;
    }
}
