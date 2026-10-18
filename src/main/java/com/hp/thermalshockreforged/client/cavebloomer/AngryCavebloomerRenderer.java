package com.hp.thermalshockreforged.client.cavebloomer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.AngryCavebloomerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class AngryCavebloomerRenderer extends GeoEntityRenderer<AngryCavebloomerEntity> {
    public AngryCavebloomerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 1.2F; withScale(1); addRenderLayer(new Glow(this));
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(AngryCavebloomerEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(AngryCavebloomerEntity entity) { return 0; }
    private static final class Model extends GeoModel<AngryCavebloomerEntity> {
        @Override public ResourceLocation getModelResource(AngryCavebloomerEntity entity) { return resource("geo/angry_cavebloomer.geo.json"); }
        @Override public ResourceLocation getAnimationResource(AngryCavebloomerEntity entity) { return resource("animations/angry_cavebloomer.animation.json"); }
        @Override public ResourceLocation getTextureResource(AngryCavebloomerEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(AngryCavebloomerEntity entity, long instanceId, AnimationState<AngryCavebloomerEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Glow extends GeoRenderLayer<AngryCavebloomerEntity> {
        private static final ResourceLocation TEXTURE = resource("textures/entities/cavebloomer_glow.png");
        private Glow(AngryCavebloomerRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, AngryCavebloomerEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
