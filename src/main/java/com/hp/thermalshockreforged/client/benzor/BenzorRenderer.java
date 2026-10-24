package com.hp.thermalshockreforged.client.benzor;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.benzor.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class BenzorRenderer extends TsrGeoEntityRenderer<BenzorEntity> {
    public BenzorRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 2.2F; withRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BenzorContent.BENZOR.get(), BenzorRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<BenzorEntity> {
        @Override public Identifier getModelResource(BenzorEntity entity) { return BenzorContent.id("geo/benzor.geo.json"); }
        @Override public Identifier getAnimationResource(BenzorEntity entity) { return BenzorContent.id("animations/benzor.animation.json"); }
        @Override public Identifier getTextureResource(BenzorEntity entity) { return BenzorContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
    private static final class Glow extends GeoEyesLayer<BenzorEntity, Void, EntityRenderState> {
        private Glow(BenzorRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(BenzorEntity entity) { return BenzorContent.id("textures/entities/benzorglow.png"); }
    }
}
