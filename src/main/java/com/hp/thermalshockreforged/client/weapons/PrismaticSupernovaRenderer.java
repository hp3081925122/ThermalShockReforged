package com.hp.thermalshockreforged.client.weapons;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;

public final class PrismaticSupernovaRenderer extends TsrGeoEntityRenderer<PrismaticSupernovaEntity> {
    public PrismaticSupernovaRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withRenderLayer(new Glow(this)); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<PrismaticSupernovaEntity> {
        @Override public Identifier getModelResource(PrismaticSupernovaEntity entity) { return resource("geo/prism_vortex.geo.json"); }
        @Override public Identifier getTextureResource(PrismaticSupernovaEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(PrismaticSupernovaEntity entity) { return resource("animations/prism_vortex.animation.json"); }
    }
    private static final class Glow extends GeoEyesLayer<PrismaticSupernovaEntity, Void, EntityRenderState> {
        private Glow(PrismaticSupernovaRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(PrismaticSupernovaEntity entity) { return resource("textures/entities/prism_supernova.png"); }
    }
}
