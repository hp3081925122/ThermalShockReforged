package com.hp.thermalshockreforged.client.projectile;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.MagneticBombEntity;
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

public final class MagneticBombRenderer extends LegacyGeoEntityRenderer<MagneticBombEntity> {
    public MagneticBombRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; addRenderLayer(new Glow(this)); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(MagneticBombEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState state) { return 0; }
    private static final class Model extends LegacyGeoModel<MagneticBombEntity> {
        @Override public Identifier getModelResource(MagneticBombEntity entity) { return resource("geo/magnetic_bomb.geo.json"); }
        @Override public Identifier getTextureResource(MagneticBombEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(MagneticBombEntity entity) { return resource("animations/magnetic_bomb.animation.json"); }
    }
    private static final class Glow extends LegacyGeoRenderLayer<MagneticBombEntity> {
        private Glow(MagneticBombRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, MagneticBombEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(resource("textures/entities/" + (entity.epsilon() ? "epsilon_bomb" : "magnetic_bomb_glow") + ".png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
