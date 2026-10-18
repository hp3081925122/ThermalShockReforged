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
    @org.spongepowered.asm.mixin.Shadow protected java.util.Stack<net.neoforged.neoforge.common.damagesource.DamageContainer> damageContainers;
    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private void thermalShockReforged$arrivalProtection(DamageSource source, float amount, CallbackInfoReturnable<Float> callback) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (!(entity instanceof com.hp.thermalshockreforged.content.atlas.AtlasArrivalState arrival) || arrival.arrivalProtectionTicks() <= 0 || source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_EFFECTS) || source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_RESISTANCE)) return;
        if (Float.isFinite(amount) && amount > 0) {
            if (!damageContainers.empty()) damageContainers.peek().setReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.MOB_EFFECTS, amount);
            if (amount < 3.4028235E37F) {
                if (entity instanceof net.minecraft.server.level.ServerPlayer player) player.awardStat(net.minecraft.stats.Stats.DAMAGE_RESISTED, Math.round(amount * 10));
                else if (source.getEntity() instanceof net.minecraft.server.level.ServerPlayer attacker) attacker.awardStat(net.minecraft.stats.Stats.DAMAGE_DEALT_RESISTED, Math.round(amount * 10));
            }
        } else if (!damageContainers.empty()) damageContainers.peek().setNewDamage(0);
        callback.setReturnValue(0F);
    }
    @org.spongepowered.asm.mixin.injection.Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;get(Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;", ordinal = 0), require = 1)
    private Object thermalShockReforged$replaceArrivalParticles(net.minecraft.network.syncher.SynchedEntityData data, net.minecraft.network.syncher.EntityDataAccessor<?> key) {
        return com.hp.thermalshockreforged.content.atlas.AtlasArrivalVisuals.active((LivingEntity)(Object)this) ? java.util.List.of() : data.get(key);
    }
    @Inject(method = "tickEffects", at = @At("TAIL"))
    private void thermalShockReforged$arrivalParticles(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity instanceof com.hp.thermalshockreforged.content.atlas.AtlasArrivalState arrival) arrival.tickArrivalProtection();
        com.hp.thermalshockreforged.content.atlas.AtlasArrivalVisuals.tick(entity);
        com.hp.thermalshockreforged.world.sanctum.PylonBuildingLock.tick(entity);
    }
    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void thermalShockReforged$arrivalDataUpdated(net.minecraft.network.syncher.EntityDataAccessor<?> key, CallbackInfo callback) {
        if ((Object)this instanceof com.hp.thermalshockreforged.content.atlas.AtlasArrivalState arrival) arrival.receiveArrivalProtectionData(key);
    }

    @Inject(method = "removeAllEffects", at = @At("TAIL"), cancellable = true)
    private void thermalShockReforged$clearArrival(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity.hasEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE)) return;
        if (!entity.level().isClientSide && entity instanceof com.hp.thermalshockreforged.content.atlas.AtlasArrivalState arrival && arrival.arrivalProtectionTicks() > 0) { arrival.beginArrivalProtection(0); callback.setReturnValue(true); }
    }
    @Inject(method = "removeEffectsCuredBy", at = @At("TAIL"), cancellable = true)
    private void thermalShockReforged$cureArrival(net.neoforged.neoforge.common.EffectCure cure, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity.hasEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE)) return;
        if (!entity.level().isClientSide && net.neoforged.neoforge.common.EffectCures.DEFAULT_CURES.contains(cure) && entity instanceof com.hp.thermalshockreforged.content.atlas.AtlasArrivalState arrival && arrival.arrivalProtectionTicks() > 0) { arrival.beginArrivalProtection(0); callback.setReturnValue(true); }
    }
    @Inject(method = "removeEffect", at = @At("TAIL"), cancellable = true)
    private void thermalShockReforged$removeArrival(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity.hasEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE)) return;
        if (!entity.level().isClientSide && effect.equals(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE) && entity instanceof com.hp.thermalshockreforged.content.atlas.AtlasArrivalState arrival && arrival.arrivalProtectionTicks() > 0) { arrival.beginArrivalProtection(0); callback.setReturnValue(true); }
    }
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
