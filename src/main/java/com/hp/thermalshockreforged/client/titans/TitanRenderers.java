package com.hp.thermalshockreforged.client.titans;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.titans.SkeletalTitanEntity;
import com.hp.thermalshockreforged.entity.titans.TitanContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class TitanRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TitanContent.SKELETITAN.get(), Renderer::new);
        event.registerEntityRenderer(TitanContent.FROST_TITAN.get(), Renderer::new);
        event.registerEntityRenderer(TitanContent.MANTLETITAN.get(), Renderer::new);
    }

    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<SkeletalTitanEntity> {
        private String modelName(SkeletalTitanEntity entity) { return entity.kind() == SkeletalTitanEntity.Kind.FROST ? "frosttitan" : "skeletitan"; }
        @Override public Identifier getModelResource(SkeletalTitanEntity entity) { return resource("geo/" + modelName(entity) + ".geo.json"); }
        @Override public Identifier getTextureResource(SkeletalTitanEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(SkeletalTitanEntity entity) { return resource("animations/" + modelName(entity) + ".animation.json"); }
        @Override public void setCustomAnimations(SkeletalTitanEntity entity, long instanceId, AnimationTest<SkeletalTitanEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) {
                head.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
            }
        }
    }

    private static final class Renderer extends LegacyGeoEntityRenderer<SkeletalTitanEntity> {
        private Renderer(EntityRendererProvider.Context context) {
            super(context, new Model());
            shadowRadius = .8F;
            addRenderLayer(new Glow(this));
        }
        @Override public RenderType getRenderType(SkeletalTitanEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
        @Override public void preRender(PoseStack pose, SkeletalTitanEntity entity, BakedGeoModel model, MultiBufferSource buffers, VertexConsumer buffer, boolean isReRender, float partialTick, int light, int overlay, int color) {
            scaleWidth = 1.6F;
            scaleHeight = 1.6F;
            super.preRender(pose, entity, model, buffers, buffer, isReRender, partialTick, light, overlay, color);
        }
    }

    private static final class Glow extends LegacyGeoRenderLayer<SkeletalTitanEntity> {
        private Glow(Renderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, SkeletalTitanEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            if (entity.kind() != SkeletalTitanEntity.Kind.MANTLE) return;
            var glow = RenderTypes.eyes(resource("textures/entities/mantletitanglow.png"));
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }

    private TitanRenderers() { }
}
