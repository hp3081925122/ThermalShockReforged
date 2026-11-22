package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.content.atlas.AtlasEffectDisplay;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EffectsInInventory.class)
public abstract class AtlasInventoryEffectDisplayMixin {
    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;"), require = 1)
    private Collection<MobEffectInstance> thermalShockReforged$arrivalDisplay(Collection<MobEffectInstance> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null ? original : AtlasEffectDisplay.effects(player, original);
    }
}
