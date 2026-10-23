package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LevelRenderer.class)
public abstract class DimensionSkyTexturesMixin {
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_SUN = Identifier.withDefaultNamespace("textures/environment/sun.png");
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_MOON = Identifier.withDefaultNamespace("textures/environment/moon_phases.png");
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_LOQUE_SUN = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/loque_sun.png");
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_AQERON_PHASES = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/aqeron_phases.png");
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_LOQENDIA_PHASES = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/loqendia_phases.png");
    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/Identifier;)V"), index = 1, require = 2)
    private Identifier thermalShockReforged$dimensionSkyTexture(Identifier texture) {
        var level = Minecraft.getInstance().level;
        if (level == null) return texture;
        boolean loqendia = level.dimension().equals(LoqendiaContent.DIMENSION);
        boolean aqeron = level.dimension().equals(AtlasContent.PALACE);
        if (!loqendia && !aqeron) return texture;
        if (texture.equals(THERMAL_SHOCK_REFORGED_SUN)) return THERMAL_SHOCK_REFORGED_LOQUE_SUN;
        if (texture.equals(THERMAL_SHOCK_REFORGED_MOON)) return loqendia ? THERMAL_SHOCK_REFORGED_AQERON_PHASES : THERMAL_SHOCK_REFORGED_LOQENDIA_PHASES;
        return texture;
    }
}
