package com.hp.thermalshockreforged.client.teslareed;

import com.hp.thermalshockreforged.entity.electricsphere.ElectricSphereContent;
import com.hp.thermalshockreforged.entity.teslareed.TeslaReedEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class TeslaReedRenderer extends GeoEntityRenderer<TeslaReedEntity> {
    public TeslaReedRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .2F; }
    @Override public RenderType getRenderType(TeslaReedEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<TeslaReedEntity> {
        @Override public ResourceLocation getModelResource(TeslaReedEntity entity) { return ElectricSphereContent.id("geo/tesla_reed.geo.json"); }
        @Override public ResourceLocation getAnimationResource(TeslaReedEntity entity) { return ElectricSphereContent.id("animations/tesla_reed.animation.json"); }
        @Override public ResourceLocation getTextureResource(TeslaReedEntity entity) { return ElectricSphereContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(TeslaReedEntity entity, long instance, AnimationState<TeslaReedEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            var bone = getAnimationProcessor().getBone("stem"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (bone != null && data != null) { bone.setRotX(data.headPitch() * Mth.DEG_TO_RAD); bone.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
