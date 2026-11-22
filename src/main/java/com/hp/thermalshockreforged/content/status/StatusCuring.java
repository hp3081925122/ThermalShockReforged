package com.hp.thermalshockreforged.content.status;

import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorContent;
import com.hp.thermalshockreforged.content.atlas.AtlasArrivalState;
import com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatContent;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public final class StatusCuring {
    public enum Source { MILK, HONEY, TOTEM }

    private StatusCuring() { }

    public static boolean cure(LivingEntity entity, Source source) {
        if (entity.level().isClientSide()) return false;
        boolean removed = false;
        for (var instance : new ArrayList<>(entity.getActiveEffects())) {
            if (canCure(instance.getEffect(), source)) removed |= entity.removeEffect(instance.getEffect());
        }
        if (source != Source.HONEY && !entity.hasEffect(MobEffects.RESISTANCE)
                && entity instanceof AtlasArrivalState arrival && arrival.arrivalProtectionTicks() > 0) {
            arrival.beginArrivalProtection(0);
            removed = true;
        }
        LogUtils.getLogger().debug("Status cure processed: entity={}, source={}, removed={}", entity.getUUID(), source, removed);
        return removed;
    }

    private static boolean canCure(Holder<MobEffect> effect, Source source) {
        if (effect.equals(FabricatedArmorContent.COOLDOWN_VISUAL)) return false;
        if (effect.equals(StatusContent.INFECTED) || effect.equals(AuraContent.DAMAGING_AURA)) return true;
        if (effect.equals(StatusContent.STUNNED) || effect.equals(StatusContent.TREMORS)
                || effect.equals(RemainingCombatContent.CRYO_VISUAL)) return source == Source.TOTEM;
        if (effect.value() instanceof PlayerStatusEffect playerEffect) return source == Source.TOTEM || source == Source.MILK && playerEffect.canCureWithMilk();
        return source != Source.HONEY || effect.equals(MobEffects.POISON);
    }
}
