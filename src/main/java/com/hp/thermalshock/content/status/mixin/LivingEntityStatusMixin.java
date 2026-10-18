package com.hp.thermalshock.content.status.mixin;

import com.hp.thermalshock.content.status.StatusContent;
import com.hp.thermalshock.content.status.StatusEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityStatusMixin {
    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void thermalShock$effectAdded(MobEffectInstance instance, Entity source, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.level().isClientSide && instance.is(StatusContent.ELECTRIFIED)) {
            StatusEvents.playElectrifiedVisual(entity);
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void thermalShock$effectUpdated(MobEffectInstance instance, boolean reapplyModifiers, Entity source, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (reapplyModifiers && !entity.level().isClientSide && instance.is(StatusContent.ELECTRIFIED)) {
            StatusEvents.playElectrifiedVisual(entity);
        }
    }
}
