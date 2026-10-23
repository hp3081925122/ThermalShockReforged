package com.hp.thermalshockreforged.client.fluids;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.fluids.FluidContent;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class FluidClient {
    private FluidClient() { }
    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new Textures("springel", "springel32"), FluidContent.SPRINGELATIN_TYPE.get());
        event.registerFluidType(new Textures("bloodrubicon", "bloodrubicon"), FluidContent.BLOOD_TYPE.get());
    }
    private record Textures(String still, String flowing) implements IClientFluidTypeExtensions { }
}
