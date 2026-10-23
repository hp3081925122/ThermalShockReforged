package com.hp.thermalshockreforged.client.weapons;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class PrismaticSupernovaRenderer extends LegacyGeoEntityRenderer<PrismaticSupernovaEntity> {
    public PrismaticSupernovaRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; addRenderLayer(new Glow(this)); }
    @Override public RenderType getRenderType(PrismaticSupernovaEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    protected float getDeathMaxRotation(PrismaticSupernovaEntity entity) { return 0; }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<PrismaticSupernovaEntity> {
        @Override public Identifier getModelResource(PrismaticSupernovaEntity entity) { return resource("geo/prism_vortex.geo.json"); }
        @Override public Identifier getTextureResource(PrismaticSupernovaEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(PrismaticSupernovaEntity entity) { return resource("animations/prism_vortex.animation.json"); }
    }
    private static final class Glow extends LegacyGeoRenderLayer<PrismaticSupernovaEntity> {
        private Glow(PrismaticSupernovaRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, PrismaticSupernovaEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            var glow = RenderTypes.eyes(resource("textures/entities/prism_supernova.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
