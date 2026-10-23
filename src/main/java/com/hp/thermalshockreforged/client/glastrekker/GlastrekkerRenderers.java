package com.hp.thermalshockreforged.client.glastrekker;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.glastrekker.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class GlastrekkerRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GlastrekkerContent.GLASTREKKER.get(), Renderer::new);
        event.registerEntityRenderer(GlastrekkerContent.ARMOURED_GLASTREKKER.get(), Renderer::new);
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<GlastrekkerEntity> {
        @Override public Identifier getModelResource(GlastrekkerEntity entity) { return resource("geo/sea_treader.geo.json"); }
        @Override public Identifier getTextureResource(GlastrekkerEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(GlastrekkerEntity entity) { return resource("animations/sea_treader.animation.json"); }
        @Override public void setCustomAnimations(GlastrekkerEntity entity, long instanceId, AnimationTest<GlastrekkerEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var body = getAnimationProcessor().getBone("body");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (body != null && data != null) {
                body.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
                body.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
            }
        }
    }
    private static final class Renderer extends LegacyGeoEntityRenderer<GlastrekkerEntity> {
        private Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1; }
        @Override public RenderType getRenderType(GlastrekkerEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private GlastrekkerRenderers() { }
}
