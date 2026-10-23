package com.hp.thermalshockreforged.client.maiden;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.maiden.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class MaidenRenderer extends LegacyGeoEntityRenderer<MaidenEntity> {
    public MaidenRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .7F; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(MaidenContent.MAIDEN.get(), MaidenRenderer::new); event.registerEntityRenderer(MaidenContent.VOLLEY.get(), NoopRenderer::new); }
    @Override public RenderType getRenderType(MaidenEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<MaidenEntity> {
        @Override public Identifier getModelResource(MaidenEntity entity) { return MaidenContent.id("geo/maiden.geo.json"); }
        @Override public Identifier getAnimationResource(MaidenEntity entity) { return MaidenContent.id("animations/maiden.animation.json"); }
        @Override public Identifier getTextureResource(MaidenEntity entity) { return MaidenContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
}
