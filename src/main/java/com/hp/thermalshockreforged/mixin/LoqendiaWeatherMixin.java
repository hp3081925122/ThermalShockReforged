package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.world.LoqendiaContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LevelRenderer.class)
public abstract class LoqendiaWeatherMixin {
    @ModifyConstant(method = "renderSnowAndRain", constant = @Constant(intValue = 5))
    private int thermalShockReforged$reduceFastWeatherRadius(int radius) {
        return thermalShockReforged$isLoqendia() ? 2 : radius;
    }

    @ModifyConstant(method = "renderSnowAndRain", constant = @Constant(intValue = 10))
    private int thermalShockReforged$reduceFancyWeatherRadius(int radius) {
        return thermalShockReforged$isLoqendia() ? 4 : radius;
    }

    private static boolean thermalShockReforged$isLoqendia() {
        var level = Minecraft.getInstance().level;
        return level != null && level.dimension().equals(LoqendiaContent.DIMENSION);
    }
}
