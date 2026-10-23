package com.hp.thermalshockreforged.client.effigy;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.effigy.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class EffigyRenderer extends LegacyGeoEntityRenderer<EffigyEntity> {
    public EffigyRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(EffigyContent.EFFIGY.get(), EffigyRenderer::new); event.registerEntityRenderer(EffigyContent.STRIKE.get(), NoopRenderer::new); }
    @Override public RenderType getRenderType(EffigyEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<EffigyEntity> {
        @Override public Identifier getModelResource(EffigyEntity entity) { return EffigyContent.id("geo/effigy.geo.json"); }
        @Override public Identifier getAnimationResource(EffigyEntity entity) { return EffigyContent.id("animations/effigy.animation.json"); }
        @Override public Identifier getTextureResource(EffigyEntity entity) { return EffigyContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(EffigyEntity entity, long instance, AnimationTest<EffigyEntity> state) {
            super.setCustomAnimations(entity, instance, state);
            var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
