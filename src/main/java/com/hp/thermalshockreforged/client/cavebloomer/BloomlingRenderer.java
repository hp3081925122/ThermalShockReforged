package com.hp.thermalshockreforged.client.cavebloomer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.BloomlingEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
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

public final class BloomlingRenderer extends GeoEntityRenderer<BloomlingEntity> {
    public BloomlingRenderer(EntityRendererProvider.Context context, boolean dandy) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(dandy ? 0.8F : 1);
        if (!dandy) addRenderLayer(new Glow(this));
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(BloomlingEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(BloomlingEntity entity) { return entity.dandy() ? super.getDeathMaxRotation(entity) : 0; }
    private static final class Model extends GeoModel<BloomlingEntity> {
        @Override public ResourceLocation getModelResource(BloomlingEntity entity) { return resource("geo/" + entity.modelStem() + ".geo.json"); }
        @Override public ResourceLocation getAnimationResource(BloomlingEntity entity) { return resource("animations/" + entity.modelStem() + ".animation.json"); }
        @Override public ResourceLocation getTextureResource(BloomlingEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(BloomlingEntity entity, long instanceId, AnimationState<BloomlingEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Glow extends GeoRenderLayer<BloomlingEntity> {
        private static final ResourceLocation TEXTURE = resource("textures/entities/budding_glow.png");
        private Glow(BloomlingRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, BloomlingEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
