package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.content.atlas.AtlasEffectDisplay;
import java.util.Collection;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({Gui.class, EffectRenderingInventoryScreen.class})
public abstract class AtlasEffectDisplayMixin {
    @Redirect(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;"), require = 1)
    private Collection<MobEffectInstance> thermalShockReforged$arrivalDisplay(LocalPlayer player) { return AtlasEffectDisplay.effects(player); }
}
