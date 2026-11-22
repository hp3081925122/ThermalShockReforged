package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WeatherEffectRenderer.class)
public abstract class LoqendiaWeatherMixin {
    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object thermalShockReforged$reduceWeatherRadius(Object value) {
        var level = Minecraft.getInstance().level;
        if (level == null || !level.dimension().equals(LoqendiaContent.DIMENSION) || !(value instanceof Integer radius)) return value;
        return radius * 2 / 5;
    }
}
