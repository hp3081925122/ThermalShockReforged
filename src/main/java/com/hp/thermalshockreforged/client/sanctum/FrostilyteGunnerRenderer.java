package com.hp.thermalshockreforged.client.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.FrostilyteGunnerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class FrostilyteGunnerRenderer extends GeoEntityRenderer<FrostilyteGunnerEntity> {
    public FrostilyteGunnerRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1.1F); }
    @Override public RenderType getRenderType(FrostilyteGunnerEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends GeoModel<FrostilyteGunnerEntity> {
        @Override public ResourceLocation getModelResource(FrostilyteGunnerEntity entity) { return resource("geo/frostilyte_gunner_rubicon.geo.json"); }
        @Override public ResourceLocation getTextureResource(FrostilyteGunnerEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public ResourceLocation getAnimationResource(FrostilyteGunnerEntity entity) { return resource("animations/frostilyte_gunner_rubicon.animation.json"); }
        @Override public void setCustomAnimations(FrostilyteGunnerEntity entity, long instanceId, AnimationState<FrostilyteGunnerEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head");
            var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
