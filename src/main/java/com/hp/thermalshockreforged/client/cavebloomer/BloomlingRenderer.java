package com.hp.thermalshockreforged.client.cavebloomer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.BloomlingEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;

public final class BloomlingRenderer extends LegacyGeoEntityRenderer<BloomlingEntity> {
    public BloomlingRenderer(EntityRendererProvider.Context context, boolean dandy) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(dandy ? 0.8F : 1);
        if (!dandy) addRenderLayer(new Glow(this));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(BloomlingEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    protected float getDeathMaxRotation(BloomlingEntity entity) { return 0; }
    private static final class Model extends LegacyGeoModel<BloomlingEntity> {
        @Override public Identifier getModelResource(BloomlingEntity entity) { return resource("geo/" + entity.modelStem() + ".geo.json"); }
        @Override public Identifier getAnimationResource(BloomlingEntity entity) { return resource("animations/" + entity.modelStem() + ".animation.json"); }
        @Override public Identifier getTextureResource(BloomlingEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(BloomlingEntity entity, long instanceId, AnimationTest<BloomlingEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Glow extends LegacyGeoRenderLayer<BloomlingEntity> {
        private static final Identifier TEXTURE = resource("textures/entities/budding_glow.png");
        private Glow(BloomlingRenderer renderer) { super(renderer); }
        @Override public void render(PoseStack pose, BloomlingEntity entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            RenderType glow = RenderTypes.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
