package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.world.LoqendiaContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherEffectRenderer.class)
public abstract class LoqendiaWeatherMixin {
    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object thermalShockReforged$reduceWeatherRadius(OptionInstance<?> option) {
        Object value = option.get();
        var level = Minecraft.getInstance().level;
        if (level == null || !level.dimension().equals(LoqendiaContent.DIMENSION) || !(value instanceof Integer radius)) return value;
        return radius * 2 / 5;
    }
}
