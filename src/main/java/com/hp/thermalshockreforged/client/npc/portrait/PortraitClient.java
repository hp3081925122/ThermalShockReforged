package com.hp.thermalshockreforged.client.npc.portrait;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.portrait.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class PortraitClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(PortraitContent.PORTRAIT.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { event.register(PortraitContent.menu(false), PortraitScreen::new); event.register(PortraitContent.menu(true), PortraitScreen::new); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoEntityRenderer<PortraitCharacterEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; withScale(1); }
        @Override public RenderType getRenderType(PortraitCharacterEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<PortraitCharacterEntity> {
        @Override public ResourceLocation getModelResource(PortraitCharacterEntity entity) { return id("geo/portrait_entity.geo.json"); }
        @Override public ResourceLocation getAnimationResource(PortraitCharacterEntity entity) { return id("animations/portrait_entity.animation.json"); }
        @Override public ResourceLocation getTextureResource(PortraitCharacterEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(PortraitCharacterEntity entity, long instanceId, AnimationState<PortraitCharacterEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
