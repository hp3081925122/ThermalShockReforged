package com.hp.thermalshockreforged.client.sanctum;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.KilolyteEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;

public final class KilolyteRenderer extends TsrGeoEntityRenderer<KilolyteEntity> {
    public KilolyteRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(2); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<KilolyteEntity> {
        @Override public Identifier getModelResource(KilolyteEntity entity) { return resource("geo/kilolyte.geo.json"); }
        @Override public Identifier getTextureResource(KilolyteEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(KilolyteEntity entity) { return resource("animations/kilolyte.animation.json"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }
}
