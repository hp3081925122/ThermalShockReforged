package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.status.StatusEvents;
import com.hp.thermalshockreforged.content.status.InfectionStatus;
import com.hp.thermalshockreforged.content.status.ParryingStatus;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityStatusMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void thermalShockReforged$infectionAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Player)) {
            InfectionStatus.onAttack(entity, source);
            if (NeoForge.EVENT_BUS.post(new AttackAttemptEvent(entity, source, amount)).isCanceled()) callback.setReturnValue(false);
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void thermalShockReforged$infectionRemoved(MobEffectInstance instance, CallbackInfo callback) {
        if (instance.is(StatusContent.INFECTED)) InfectionStatus.onRemoved((LivingEntity) (Object) this);
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void thermalShockReforged$effectAdded(MobEffectInstance instance, Entity source, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.level().isClientSide && instance.is(StatusContent.ELECTRIFIED)) {
            StatusEvents.playElectrifiedVisual(entity);
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void thermalShockReforged$effectUpdated(MobEffectInstance instance, boolean reapplyModifiers, Entity source, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (reapplyModifiers && !entity.level().isClientSide && instance.is(StatusContent.ELECTRIFIED)) {
            StatusEvents.playElectrifiedVisual(entity);
        }
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", ordinal = 0), require = 1)
    private void thermalShockReforged$parryBeforeArmor(DamageSource source, float amount, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Player)) ParryingStatus.beforeArmor(entity);
    }
}
