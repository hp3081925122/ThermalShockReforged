package com.hp.thermalshockreforged.client.teslareed;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.electricsphere.ElectricSphereContent;
import com.hp.thermalshockreforged.entity.teslareed.TeslaReedEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class TeslaReedRenderer extends LegacyGeoEntityRenderer<TeslaReedEntity> {
    public TeslaReedRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .2F; }
    @Override public RenderType getRenderType(TeslaReedEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<TeslaReedEntity> {
        @Override public Identifier getModelResource(TeslaReedEntity entity) { return ElectricSphereContent.id("geo/tesla_reed.geo.json"); }
        @Override public Identifier getAnimationResource(TeslaReedEntity entity) { return ElectricSphereContent.id("animations/tesla_reed.animation.json"); }
        @Override public Identifier getTextureResource(TeslaReedEntity entity) { return ElectricSphereContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(TeslaReedEntity entity, long instance, AnimationTest<TeslaReedEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            var bone = getAnimationProcessor().getBone("stem"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (bone != null && data != null) { bone.setRotX(data.headPitch() * Mth.DEG_TO_RAD); bone.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
