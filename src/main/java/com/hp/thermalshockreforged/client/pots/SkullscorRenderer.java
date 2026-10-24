package com.hp.thermalshockreforged.client.pots;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.pots.SkullscorEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;

public final class SkullscorRenderer extends TsrGeoEntityRenderer<SkullscorEntity> {
    public SkullscorRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; withScale(1); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<SkullscorEntity> {
        @Override public Identifier getModelResource(SkullscorEntity entity) { return resource("geo/skullscor.geo.json"); }
        @Override public Identifier getAnimationResource(SkullscorEntity entity) { return resource("animations/skullscor.animation.json"); }
        @Override public Identifier getTextureResource(SkullscorEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
}
