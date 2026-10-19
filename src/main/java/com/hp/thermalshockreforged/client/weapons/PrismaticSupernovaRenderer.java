package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class PrismaticSupernovaRenderer extends GeoEntityRenderer<PrismaticSupernovaEntity> {
    public PrismaticSupernovaRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; addRenderLayer(new Glow(this)); }
    @Override public RenderType getRenderType(PrismaticSupernovaEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(PrismaticSupernovaEntity entity) { return 0; }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<PrismaticSupernovaEntity> {
        @Override public ResourceLocation getModelResource(PrismaticSupernovaEntity entity) { return resource("geo/prism_vortex.geo.json"); }
        @Override public ResourceLocation getTextureResource(PrismaticSupernovaEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(PrismaticSupernovaEntity entity) { return resource("animations/prism_vortex.animation.json"); }
    }
    private static final class Glow extends GeoRenderLayer<PrismaticSupernovaEntity> {
        private Glow(PrismaticSupernovaRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, PrismaticSupernovaEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(resource("textures/entities/prism_supernova.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
