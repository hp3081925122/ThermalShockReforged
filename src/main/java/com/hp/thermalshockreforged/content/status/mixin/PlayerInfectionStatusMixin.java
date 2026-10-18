package com.hp.thermalshockreforged.content.status.mixin;

import com.hp.thermalshockreforged.content.status.InfectionStatus;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerInfectionStatusMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void thermalShockReforged$infectionAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        Player entity = (Player) (Object) this;
        InfectionStatus.onAttack(entity, source);
        if (NeoForge.EVENT_BUS.post(new AttackAttemptEvent(entity, source, amount)).isCanceled()) callback.setReturnValue(false);
    }
}
