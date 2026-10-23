package com.hp.thermalshockreforged.client.sanctum;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.GuardianPylonEntity;
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

public final class GuardianPylonRenderer extends LegacyGeoEntityRenderer<GuardianPylonEntity> {
    public GuardianPylonRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0; withScale(1); addRenderLayer(new Glow(this));
    }
    protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState state) { return 0; }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(GuardianPylonEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<GuardianPylonEntity> {
        @Override public Identifier getModelResource(GuardianPylonEntity entity) { return resource("geo/primary_pylon.geo.json"); }
        @Override public Identifier getAnimationResource(GuardianPylonEntity entity) { return resource("animations/primary_pylon.animation.json"); }
        @Override public Identifier getTextureResource(GuardianPylonEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }

    }
    private static final class Glow extends LegacyGeoRenderLayer<GuardianPylonEntity> {
        private static final Identifier TEXTURE = resource("textures/entities/surge_pylon.png");
        private Glow(GuardianPylonRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, GuardianPylonEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
