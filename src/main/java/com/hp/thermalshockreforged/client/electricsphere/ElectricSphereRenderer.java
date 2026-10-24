package com.hp.thermalshockreforged.client.electricsphere;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.electricsphere.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;

public final class ElectricSphereRenderer extends TsrGeoEntityRenderer<ElectricSphereEntity> {
    public ElectricSphereRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; withRenderLayer(new Glow(this)); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<ElectricSphereEntity> {
        @Override public Identifier getModelResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("geo/electric_sphere.geo.json"); }
        @Override public Identifier getAnimationResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("animations/electric_sphere.animation.json"); }
        @Override public Identifier getTextureResource(ElectricSphereEntity entity) { return ElectricSphereContent.id("textures/entities/" + entity.texture() + ".png"); }
    }
    private static final class Glow extends GeoEyesLayer<ElectricSphereEntity, Void, EntityRenderState> {
        Glow(ElectricSphereRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(ElectricSphereEntity entity) { return ElectricSphereContent.id("textures/entities/electric_sphere_glow.png"); }
    }
}
