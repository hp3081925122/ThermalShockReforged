package com.hp.thermalshockreforged.client.weapons;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public final class FlareGunCoreRenderer extends TsrGeoEntityRenderer<FlareGunCoreEntity> {
    public FlareGunCoreRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<FlareGunCoreEntity> {
        @Override public Identifier getModelResource(FlareGunCoreEntity entity) { return resource("geo/flare_core.geo.json"); }
        @Override public Identifier getTextureResource(FlareGunCoreEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(FlareGunCoreEntity entity) { return resource("animations/flare_core.animation.json"); }
    }
}
