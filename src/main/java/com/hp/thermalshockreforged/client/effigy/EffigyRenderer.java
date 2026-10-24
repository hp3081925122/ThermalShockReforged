package com.hp.thermalshockreforged.client.effigy;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.effigy.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;

public final class EffigyRenderer extends TsrGeoEntityRenderer<EffigyEntity> {
    public EffigyRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(EffigyContent.EFFIGY.get(), EffigyRenderer::new); event.registerEntityRenderer(EffigyContent.STRIKE.get(), NoopRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<EffigyEntity> {
        @Override public Identifier getModelResource(EffigyEntity entity) { return EffigyContent.id("geo/effigy.geo.json"); }
        @Override public Identifier getAnimationResource(EffigyEntity entity) { return EffigyContent.id("animations/effigy.animation.json"); }
        @Override public Identifier getTextureResource(EffigyEntity entity) { return EffigyContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
}
