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
    @org.spongepowered.asm.mixin.Unique private int thermalShockReforged$arrivalTicks;
    @Override public int arrivalProtectionTicks() { return thermalShockReforged$arrivalTicks; }
    @Override public boolean hasArrivalProtectionVisual() { return ((Player)(Object)this).getData(com.hp.thermalshockreforged.content.atlas.AtlasContent.ARRIVAL_VISUAL) > 0; }
    @Override public void beginArrivalProtection(int ticks) {
        Player player = (Player)(Object)this;
        if (player.level().isClientSide()) return;
        thermalShockReforged$arrivalTicks = Math.clamp(ticks, 0, 180);
        player.setData(com.hp.thermalshockreforged.content.atlas.AtlasContent.ARRIVAL_VISUAL, thermalShockReforged$arrivalTicks);
    }
    @Override public void tickArrivalProtection() {
        Player player = (Player)(Object)this;
        if (thermalShockReforged$arrivalTicks > 0 && --thermalShockReforged$arrivalTicks == 0 && !player.level().isClientSide()) player.setData(com.hp.thermalshockreforged.content.atlas.AtlasContent.ARRIVAL_VISUAL, 0);
    }
    @Override public void receiveArrivalProtectionData(int ticks) {
        if (((Player)(Object)this).level().isClientSide()) thermalShockReforged$arrivalTicks = ticks;
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void thermalShockReforged$saveArrival(net.minecraft.world.level.storage.ValueOutput output, CallbackInfo callback) { if (thermalShockReforged$arrivalTicks > 0) output.putInt("ThermalShockReforgedArrivalTicks", thermalShockReforged$arrivalTicks); }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void thermalShockReforged$loadArrival(net.minecraft.world.level.storage.ValueInput input, CallbackInfo callback) { beginArrivalProtection(input.getIntOr("ThermalShockReforgedArrivalTicks", 0)); }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void thermalShockReforged$infectionAttack(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        Player entity = (Player) (Object) this;
        InfectionStatus.onAttack(entity, source);
        if (NeoForge.EVENT_BUS.post(new AttackAttemptEvent(entity, source, amount)).isCanceled()) callback.setReturnValue(false);
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", ordinal = 0), require = 1)
    private void thermalShockReforged$parryBeforeArmor(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount, CallbackInfo callback) {
        Player player = (Player) (Object) this;
        ParryingStatus.beforeArmor(player);
        NeoForge.EVENT_BUS.post(new PreArmorDamageEvent(player, source, amount));
    }
}
