package com.hp.thermalshockreforged.client.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.MagneticBombEntity;
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

public final class MagneticBombRenderer extends GeoEntityRenderer<MagneticBombEntity> {
    public MagneticBombRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; addRenderLayer(new Glow(this)); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(MagneticBombEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(MagneticBombEntity entity) { return 0; }
    private static final class Model extends GeoModel<MagneticBombEntity> {
        @Override public ResourceLocation getModelResource(MagneticBombEntity entity) { return resource("geo/magnetic_bomb.geo.json"); }
        @Override public ResourceLocation getTextureResource(MagneticBombEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(MagneticBombEntity entity) { return resource("animations/magnetic_bomb.animation.json"); }
    }
    private static final class Glow extends GeoRenderLayer<MagneticBombEntity> {
        private Glow(MagneticBombRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, MagneticBombEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(resource("textures/entities/" + (entity.epsilon() ? "epsilon_bomb" : "magnetic_bomb_glow") + ".png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
