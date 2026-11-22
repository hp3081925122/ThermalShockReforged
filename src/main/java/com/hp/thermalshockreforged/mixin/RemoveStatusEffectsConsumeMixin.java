package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.content.status.StatusCuring;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemoveStatusEffectsConsumeEffect.class)
public abstract class RemoveStatusEffectsConsumeMixin {
    @Inject(method = "apply", at = @At("RETURN"), cancellable = true)
    private void thermalShockReforged$honey(Level level, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> callback) {
        if (stack.is(Items.HONEY_BOTTLE) && StatusCuring.cure(user, StatusCuring.Source.HONEY)) callback.setReturnValue(true);
    }
}
