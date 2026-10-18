package com.hp.thermalshockreforged.client.pots;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.pots.SkullscorEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SkullscorRenderer extends GeoEntityRenderer<SkullscorEntity> {
    public SkullscorRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; withScale(1); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(SkullscorEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<SkullscorEntity> {
        @Override public ResourceLocation getModelResource(SkullscorEntity entity) { return resource("geo/skullscor.geo.json"); }
        @Override public ResourceLocation getAnimationResource(SkullscorEntity entity) { return resource("animations/skullscor.animation.json"); }
        @Override public ResourceLocation getTextureResource(SkullscorEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(SkullscorEntity entity, long instanceId, AnimationState<SkullscorEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
