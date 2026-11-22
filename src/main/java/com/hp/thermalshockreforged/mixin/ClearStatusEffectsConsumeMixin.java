package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.content.status.StatusCuring;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public abstract class ClearStatusEffectsConsumeMixin {
    @WrapOperation(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"))
    private boolean thermalShockReforged$cure(LivingEntity user, Operation<Boolean> original, Level level, ItemStack stack) {
        return StatusCuring.cure(user, stack.has(DataComponents.DEATH_PROTECTION) && !stack.has(DataComponents.CONSUMABLE) ? StatusCuring.Source.TOTEM : StatusCuring.Source.MILK);
    }
}
