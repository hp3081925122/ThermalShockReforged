package com.hp.thermalshockreforged.client.sentrarius;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.sentrarius.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class SentrariusRenderer extends TsrGeoEntityRenderer<SentrariusEntity> {
    public SentrariusRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F;   }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SentrariusContent.SENTRARIUS.get(), SentrariusRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<SentrariusEntity> {
        @Override public Identifier getModelResource(SentrariusEntity entity) { return SentrariusContent.id("geo/sentarius.geo.json"); }
        @Override public Identifier getAnimationResource(SentrariusEntity entity) { return SentrariusContent.id("animations/sentarius.animation.json"); }
        @Override public Identifier getTextureResource(SentrariusEntity entity) { return SentrariusContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
}
