package com.hp.thermalshockreforged.client.ribbonblade;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.ribbonblade.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class RibbonbladeRenderer extends LegacyGeoEntityRenderer<RibbonbladeGalvaniteEntity> {
    public RibbonbladeRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    @Override public RenderType getRenderType(RibbonbladeGalvaniteEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<RibbonbladeGalvaniteEntity> {
        @Override public Identifier getModelResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("geo/ribbonblade_galvanite.geo.json"); }
        @Override public Identifier getAnimationResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("animations/ribbonblade_galvanite.animation.json"); }
        @Override public Identifier getTextureResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(RibbonbladeGalvaniteEntity entity, long instance, AnimationTest<RibbonbladeGalvaniteEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            var bone = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (bone != null && data != null) { bone.setRotX(data.headPitch() * Mth.DEG_TO_RAD); bone.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
