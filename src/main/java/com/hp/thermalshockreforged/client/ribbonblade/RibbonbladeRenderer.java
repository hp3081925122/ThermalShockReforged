package com.hp.thermalshockreforged.client.ribbonblade;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.ribbonblade.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;

public final class RibbonbladeRenderer extends TsrGeoEntityRenderer<RibbonbladeGalvaniteEntity> {
    public RibbonbladeRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<RibbonbladeGalvaniteEntity> {
        @Override public Identifier getModelResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("geo/ribbonblade_galvanite.geo.json"); }
        @Override public Identifier getAnimationResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("animations/ribbonblade_galvanite.animation.json"); }
        @Override public Identifier getTextureResource(RibbonbladeGalvaniteEntity entity) { return RibbonbladeContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
}
