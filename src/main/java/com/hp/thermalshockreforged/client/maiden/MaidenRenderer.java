package com.hp.thermalshockreforged.client.maiden;

import com.hp.thermalshockreforged.entity.maiden.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class MaidenRenderer extends GeoEntityRenderer<MaidenEntity> {
    public MaidenRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .7F; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(MaidenContent.MAIDEN.get(), MaidenRenderer::new); event.registerEntityRenderer(MaidenContent.VOLLEY.get(), NoopRenderer::new); }
    @Override public RenderType getRenderType(MaidenEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    private static final class Model extends GeoModel<MaidenEntity> {
        @Override public ResourceLocation getModelResource(MaidenEntity entity) { return MaidenContent.id("geo/maiden.geo.json"); }
        @Override public ResourceLocation getAnimationResource(MaidenEntity entity) { return MaidenContent.id("animations/maiden.animation.json"); }
        @Override public ResourceLocation getTextureResource(MaidenEntity entity) { return MaidenContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
}
