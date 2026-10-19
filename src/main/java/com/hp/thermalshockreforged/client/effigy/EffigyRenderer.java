package com.hp.thermalshockreforged.client.effigy;

import com.hp.thermalshockreforged.entity.effigy.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class EffigyRenderer extends GeoEntityRenderer<EffigyEntity> {
    public EffigyRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(EffigyContent.EFFIGY.get(), EffigyRenderer::new); event.registerEntityRenderer(EffigyContent.STRIKE.get(), NoopRenderer::new); }
    @Override public RenderType getRenderType(EffigyEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<EffigyEntity> {
        @Override public ResourceLocation getModelResource(EffigyEntity entity) { return EffigyContent.id("geo/effigy.geo.json"); }
        @Override public ResourceLocation getAnimationResource(EffigyEntity entity) { return EffigyContent.id("animations/effigy.animation.json"); }
        @Override public ResourceLocation getTextureResource(EffigyEntity entity) { return EffigyContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(EffigyEntity entity, long instance, AnimationState<EffigyEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
