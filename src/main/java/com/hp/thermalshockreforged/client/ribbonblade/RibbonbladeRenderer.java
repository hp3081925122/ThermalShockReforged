package com.hp.thermalshockreforged.client.ribbonblade;

import com.hp.thermalshockreforged.entity.ribbonblade.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class RibbonbladeRenderer extends GeoEntityRenderer<RibbonbladeGalvaniteEntity> {
    public RibbonbladeRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    @Override public RenderType getRenderType(RibbonbladeGalvaniteEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<RibbonbladeGalvaniteEntity> {
        @Override public ResourceLocation getModelResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("geo/ribbonblade_galvanite.geo.json"); }
        @Override public ResourceLocation getAnimationResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("animations/ribbonblade_galvanite.animation.json"); }
        @Override public ResourceLocation getTextureResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(RibbonbladeGalvaniteEntity entity, long instance, AnimationState<RibbonbladeGalvaniteEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            var bone = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (bone != null && data != null) { bone.setRotX(data.headPitch() * Mth.DEG_TO_RAD); bone.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
