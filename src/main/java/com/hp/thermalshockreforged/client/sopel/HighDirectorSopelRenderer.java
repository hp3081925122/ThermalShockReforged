package com.hp.thermalshockreforged.client.sopel;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.sopel.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.geckolib.cache.model.BakedGeoModel;

public final class HighDirectorSopelRenderer extends TsrGeoEntityRenderer<HighDirectorSopelEntity> {
    public HighDirectorSopelRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F;  withRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(SopelEntityContent.SOPEL.get(), HighDirectorSopelRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
    private static final class Model extends TsrGeoModel<HighDirectorSopelEntity> {
        @Override public Identifier getModelResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("geo/sopel.geo.json"); }
        @Override public Identifier getAnimationResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("animations/sopel.animation.json"); }
        @Override public Identifier getTextureResource(HighDirectorSopelEntity entity) { return SopelEntityContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends GeoEyesLayer<HighDirectorSopelEntity, Void, EntityRenderState> {
        private Glow(HighDirectorSopelRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(HighDirectorSopelEntity entity) { return SopelEntityContent.id("textures/entities/sopelglow.png"); }
    }
}
