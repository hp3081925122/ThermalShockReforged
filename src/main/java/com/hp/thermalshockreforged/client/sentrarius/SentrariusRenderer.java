package com.hp.thermalshockreforged.client.sentrarius;

import com.hp.thermalshockreforged.entity.sentrarius.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SentrariusRenderer extends GeoEntityRenderer<SentrariusEntity> {
    public SentrariusRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F;   }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SentrariusContent.SENTRARIUS.get(), SentrariusRenderer::new); }
    @Override public RenderType getRenderType(SentrariusEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<SentrariusEntity> {
        @Override public ResourceLocation getModelResource(SentrariusEntity entity) { return SentrariusContent.id("geo/sentarius.geo.json"); }
        @Override public ResourceLocation getAnimationResource(SentrariusEntity entity) { return SentrariusContent.id("animations/sentarius.animation.json"); }
        @Override public ResourceLocation getTextureResource(SentrariusEntity entity) { return SentrariusContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
}
