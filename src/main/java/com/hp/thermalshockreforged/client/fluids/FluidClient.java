package com.hp.thermalshockreforged.client.fluids;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.fluids.FluidContent;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;

public final class FluidClient {
    private FluidClient() { }
    public static void models(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(
                new Material(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "block/springel")),
                new Material(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "block/springel32")),
                null, null), FluidContent.SPRINGELATIN, FluidContent.FLOWING_SPRINGELATIN);
        event.register(new FluidModel.Unbaked(
                new Material(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "block/bloodrubicon")),
                new Material(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "block/bloodrubicon")),
                null, null), FluidContent.BLOOD, FluidContent.FLOWING_BLOOD);
        ThermalShockReforged.LOGGER.debug("Registered springelatin and blood fluid models with original still and flowing textures");
    }
}
