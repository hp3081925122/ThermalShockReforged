package com.hp.thermalshockreforged.client.giantlaser;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.giantlaser.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.geckolib.cache.model.BakedGeoModel;

public final class GiantLaserTestRenderer extends TsrGeoEntityRenderer<GiantLaserTestEntity> {
    public GiantLaserTestRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; withScale(3); withRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(GiantLaserTestContent.GIANT_LASER.get(), GiantLaserTestRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<GiantLaserTestEntity> {
        @Override public Identifier getModelResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("geo/giantbeacon.geo.json"); }
        @Override public Identifier getAnimationResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("animations/giantbeacon.animation.json"); }
        @Override public Identifier getTextureResource(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends GeoEyesLayer<GiantLaserTestEntity, Void, EntityRenderState> {
        private Glow(GiantLaserTestRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(GiantLaserTestEntity entity) { return GiantLaserTestContent.id("textures/entities/giantbeacon.png"); }
    }
}
