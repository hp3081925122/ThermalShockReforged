package com.hp.thermalshockreforged.client.sentrarius;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.sentrarius.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class SentrariusRenderer extends LegacyGeoEntityRenderer<SentrariusEntity> {
    public SentrariusRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F;   }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SentrariusContent.SENTRARIUS.get(), SentrariusRenderer::new); }
    @Override public RenderType getRenderType(SentrariusEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<SentrariusEntity> {
        @Override public Identifier getModelResource(SentrariusEntity entity) { return SentrariusContent.id("geo/sentarius.geo.json"); }
        @Override public Identifier getAnimationResource(SentrariusEntity entity) { return SentrariusContent.id("animations/sentarius.animation.json"); }
        @Override public Identifier getTextureResource(SentrariusEntity entity) { return SentrariusContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
}
