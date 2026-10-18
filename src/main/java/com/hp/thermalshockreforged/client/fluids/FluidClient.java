package com.hp.thermalshockreforged.client.fluids;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.fluids.FluidContent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;

public final class FluidClient {
    private FluidClient() { }
    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new Textures("springel", "springel32"), FluidContent.SPRINGELATIN_TYPE.get());
        event.registerFluidType(new Textures("bloodrubicon", "bloodrubicon"), FluidContent.BLOOD_TYPE.get());
    }
    public static void colors(RegisterColorHandlersEvent.Item event) {
        event.register(new DynamicFluidContainerModel.Colors(), FluidContent.SPRINGELATIN_BUCKET.get(), FluidContent.BLOOD_BUCKET.get());
    }
    public static void layers() {
        ItemBlockRenderTypes.setRenderLayer(FluidContent.SPRINGELATIN.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidContent.FLOWING_SPRINGELATIN.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidContent.BLOOD.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidContent.FLOWING_BLOOD.get(), RenderType.translucent());
    }
    private record Textures(String still, String flowing) implements IClientFluidTypeExtensions {
        @Override public ResourceLocation getStillTexture() { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "block/" + still); }
        @Override public ResourceLocation getFlowingTexture() { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "block/" + flowing); }
    }
}
