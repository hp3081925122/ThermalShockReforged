package com.hp.thermalshockreforged.client;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.GalvaniteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class GalvaniteRenderer extends TsrGeoEntityRenderer<GalvaniteEntity> {
    public GalvaniteRenderer(EntityRendererProvider.Context context) {
        super(context, new Model()); shadowRadius = 0.5F; withScale(1); withRenderLayer(new Glow(this));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<GalvaniteEntity> {
        @Override public Identifier getModelResource(GalvaniteEntity entity) { return resource("geo/galvanite.geo.json"); }
        @Override public Identifier getAnimationResource(GalvaniteEntity entity) { return resource("animations/galvanite.animation.json"); }
        @Override public Identifier getTextureResource(GalvaniteEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
    private static final class Glow extends GeoEyesLayer<GalvaniteEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/galvaniteglow.png");
        private Glow(GalvaniteRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(GalvaniteEntity entity) { return TEXTURE; }
    }
}
