package com.hp.thermalshockreforged.client.sanctum;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.RippleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class RippleRenderer extends LegacyGeoEntityRenderer<RippleEntity> {
    public RippleRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(1); addRenderLayer(new Glow(this));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(RippleEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<RippleEntity> {
        @Override public Identifier getModelResource(RippleEntity entity) { return resource("geo/trojan.geo.json"); }
        @Override public Identifier getAnimationResource(RippleEntity entity) { return resource("animations/trojan.animation.json"); }
        @Override public Identifier getTextureResource(RippleEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }

    }
    private static final class Glow extends LegacyGeoRenderLayer<RippleEntity> {
        private static final Identifier TEXTURE = resource("textures/entities/ripple_glow.png");
        private Glow(RippleRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, RippleEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
