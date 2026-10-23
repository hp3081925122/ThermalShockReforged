package com.hp.thermalshockreforged.client;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.GalvaniteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class GalvaniteRenderer extends LegacyGeoEntityRenderer<GalvaniteEntity> {
    public GalvaniteRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(1); addRenderLayer(new Glow(this));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(GalvaniteEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<GalvaniteEntity> {
        @Override public Identifier getModelResource(GalvaniteEntity entity) { return resource("geo/galvanite.geo.json"); }
        @Override public Identifier getAnimationResource(GalvaniteEntity entity) { return resource("animations/galvanite.animation.json"); }
        @Override public Identifier getTextureResource(GalvaniteEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(GalvaniteEntity entity, long instanceId, AnimationTest<GalvaniteEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Glow extends LegacyGeoRenderLayer<GalvaniteEntity> {
        private static final Identifier TEXTURE = resource("textures/entities/galvaniteglow.png");
        private Glow(GalvaniteRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, GalvaniteEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
