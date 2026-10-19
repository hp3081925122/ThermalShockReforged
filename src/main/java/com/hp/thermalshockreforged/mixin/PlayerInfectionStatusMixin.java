package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.content.status.InfectionStatus;
import com.hp.thermalshockreforged.content.status.ParryingStatus;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.combat.PreArmorDamageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerInfectionStatusMixin implements com.hp.thermalshockreforged.content.atlas.AtlasArrivalState {
    @org.spongepowered.asm.mixin.Unique private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> THERMAL_SHOCK_REFORGED_ARRIVAL = net.minecraft.network.syncher.SynchedEntityData.defineId(Player.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
    @org.spongepowered.asm.mixin.Unique private int thermalShockReforged$arrivalTicks;
    @Override public int arrivalProtectionTicks() { return thermalShockReforged$arrivalTicks; }
    @Override public boolean hasArrivalProtectionVisual() { return ((Player)(Object)this).getEntityData().get(THERMAL_SHOCK_REFORGED_ARRIVAL) > 0; }
    @Override public void beginArrivalProtection(int ticks) {
        Player player = (Player)(Object)this;
        if (player.level().isClientSide) return;
        thermalShockReforged$arrivalTicks = Math.clamp(ticks, 0, 180);
        player.getEntityData().set(THERMAL_SHOCK_REFORGED_ARRIVAL, thermalShockReforged$arrivalTicks, true);
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void thermalShockReforged$arrivalData(net.minecraft.network.syncher.SynchedEntityData.Builder builder, CallbackInfo callback) { builder.define(THERMAL_SHOCK_REFORGED_ARRIVAL, 0); }
    @Override public void tickArrivalProtection() {
        Player player = (Player)(Object)this;
        if (thermalShockReforged$arrivalTicks > 0 && --thermalShockReforged$arrivalTicks == 0 && !player.level().isClientSide) player.getEntityData().set(THERMAL_SHOCK_REFORGED_ARRIVAL, 0);
    }
    @Override public void receiveArrivalProtectionData(net.minecraft.network.syncher.EntityDataAccessor<?> key) {
        Player player = (Player)(Object)this;
        if (key == THERMAL_SHOCK_REFORGED_ARRIVAL && player.level().isClientSide) thermalShockReforged$arrivalTicks = player.getEntityData().get(THERMAL_SHOCK_REFORGED_ARRIVAL);
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void thermalShockReforged$saveArrival(net.minecraft.nbt.CompoundTag tag, CallbackInfo callback) { if (thermalShockReforged$arrivalTicks > 0) tag.putInt("ThermalShockReforgedArrivalTicks", thermalShockReforged$arrivalTicks); }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void thermalShockReforged$loadArrival(net.minecraft.nbt.CompoundTag tag, CallbackInfo callback) { beginArrivalProtection(tag.getInt("ThermalShockReforgedArrivalTicks")); }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void thermalShockReforged$infectionAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        Player entity = (Player) (Object) this;
        InfectionStatus.onAttack(entity, source);
        if (NeoForge.EVENT_BUS.post(new AttackAttemptEvent(entity, source, amount)).isCanceled()) callback.setReturnValue(false);
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", ordinal = 0), require = 1)
    private void thermalShockReforged$parryBeforeArmor(DamageSource source, float amount, CallbackInfo callback) {
        Player player = (Player) (Object) this;
        ParryingStatus.beforeArmor(player);
        NeoForge.EVENT_BUS.post(new PreArmorDamageEvent(player, source, amount));
    }
}
