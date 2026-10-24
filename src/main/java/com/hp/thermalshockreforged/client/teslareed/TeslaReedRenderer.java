package com.hp.thermalshockreforged.client.teslareed;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.electricsphere.ElectricSphereContent;
import com.hp.thermalshockreforged.entity.teslareed.TeslaReedEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;

public final class TeslaReedRenderer extends TsrGeoEntityRenderer<TeslaReedEntity> {
    public TeslaReedRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .2F; }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<TeslaReedEntity> {
        @Override public Identifier getModelResource(TeslaReedEntity entity) { return ElectricSphereContent.id("geo/tesla_reed.geo.json"); }
        @Override public Identifier getAnimationResource(TeslaReedEntity entity) { return ElectricSphereContent.id("animations/tesla_reed.animation.json"); }
        @Override public Identifier getTextureResource(TeslaReedEntity entity) { return ElectricSphereContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "stem"); }
    }
}
