package com.hp.thermalshockreforged.client.maiden;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.maiden.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class MaidenRenderer extends TsrGeoEntityRenderer<MaidenEntity> {
    public MaidenRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .7F; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(MaidenContent.MAIDEN.get(), MaidenRenderer::new); event.registerEntityRenderer(MaidenContent.VOLLEY.get(), NoopRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<MaidenEntity> {
        @Override public Identifier getModelResource(MaidenEntity entity) { return MaidenContent.id("geo/maiden.geo.json"); }
        @Override public Identifier getAnimationResource(MaidenEntity entity) { return MaidenContent.id("animations/maiden.animation.json"); }
        @Override public Identifier getTextureResource(MaidenEntity entity) { return MaidenContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
}
