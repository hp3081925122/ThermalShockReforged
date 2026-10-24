package com.hp.thermalshockreforged.client.glastrekker;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.glastrekker.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;

public final class GlastrekkerRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GlastrekkerContent.GLASTREKKER.get(), Renderer::new);
        event.registerEntityRenderer(GlastrekkerContent.ARMOURED_GLASTREKKER.get(), Renderer::new);
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<GlastrekkerEntity> {
        @Override public Identifier getModelResource(GlastrekkerEntity entity) { return resource("geo/sea_treader.geo.json"); }
        @Override public Identifier getTextureResource(GlastrekkerEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(GlastrekkerEntity entity) { return resource("animations/sea_treader.animation.json"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "body"); }
    }
    private static final class Renderer extends TsrGeoEntityRenderer<GlastrekkerEntity> {
        private Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private GlastrekkerRenderers() { }
}
