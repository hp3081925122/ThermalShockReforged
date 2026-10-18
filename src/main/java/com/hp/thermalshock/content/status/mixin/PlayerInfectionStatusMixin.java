package com.hp.thermalshock.content.status.mixin;

import com.hp.thermalshock.content.status.InfectionStatus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerInfectionStatusMixin {
    @Inject(method = "hurt", at = @At("HEAD"))
    private void thermalShock$infectionAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        InfectionStatus.onAttack((Player) (Object) this, source);
    }
}
