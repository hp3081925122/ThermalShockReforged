package com.hp.thermalshockreforged.client.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.GuardianPylonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class GuardianPylonRenderer extends GeoEntityRenderer<GuardianPylonEntity> {
    public GuardianPylonRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0; withScale(1); addRenderLayer(new Glow(this));
    }
    @Override protected float getDeathMaxRotation(GuardianPylonEntity entity) { return 0; }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(GuardianPylonEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<GuardianPylonEntity> {
        @Override public ResourceLocation getModelResource(GuardianPylonEntity entity) { return resource("geo/primary_pylon.geo.json"); }
        @Override public ResourceLocation getAnimationResource(GuardianPylonEntity entity) { return resource("animations/primary_pylon.animation.json"); }
        @Override public ResourceLocation getTextureResource(GuardianPylonEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }

    }
    private static final class Glow extends GeoRenderLayer<GuardianPylonEntity> {
        private static final ResourceLocation TEXTURE = resource("textures/entities/surge_pylon.png");
        private Glow(GuardianPylonRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, GuardianPylonEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
