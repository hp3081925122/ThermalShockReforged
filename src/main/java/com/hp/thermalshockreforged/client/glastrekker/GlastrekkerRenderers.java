package com.hp.thermalshockreforged.client.glastrekker;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.glastrekker.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class GlastrekkerRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GlastrekkerContent.GLASTREKKER.get(), Renderer::new);
        event.registerEntityRenderer(GlastrekkerContent.ARMOURED_GLASTREKKER.get(), Renderer::new);
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<GlastrekkerEntity> {
        @Override public ResourceLocation getModelResource(GlastrekkerEntity entity) { return resource("geo/sea_treader.geo.json"); }
        @Override public ResourceLocation getTextureResource(GlastrekkerEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(GlastrekkerEntity entity) { return resource("animations/sea_treader.animation.json"); }
        @Override public void setCustomAnimations(GlastrekkerEntity entity, long instanceId, AnimationState<GlastrekkerEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var body = getAnimationProcessor().getBone("body");
            var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (body != null && data != null) {
                body.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
                body.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
            }
        }
    }
    private static final class Renderer extends GeoEntityRenderer<GlastrekkerEntity> {
        private Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1; }
        @Override public RenderType getRenderType(GlastrekkerEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private GlastrekkerRenderers() { }
}
