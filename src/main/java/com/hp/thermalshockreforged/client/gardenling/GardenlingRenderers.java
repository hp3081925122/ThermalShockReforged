package com.hp.thermalshockreforged.client.gardenling;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.gardenling.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.*;

public final class GardenlingRenderers {
    private GardenlingRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(GardenlingContent.GARDENLING.get(), Renderer::new); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }

    private static final class Model extends TsrGeoModel<GardenlingEntity> {
        @Override public Identifier getModelResource(GardenlingEntity entity) { return resource("geo/gardenling.geo.json"); }
        @Override public Identifier getTextureResource(GardenlingEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(GardenlingEntity entity) { return resource("animations/gardenling.animation.json"); }
    }

    private static final class Renderer extends TsrGeoEntityRenderer<GardenlingEntity> {
        private Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; withRenderLayer(new Glow(this)); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }

    private static final class Glow extends GeoEyesLayer<GardenlingEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/gardenling_glow.png");
        private Glow(TsrGeoEntityRenderer<GardenlingEntity> renderer) { super(renderer); }
        @Override protected Identifier texture(GardenlingEntity entity) { return TEXTURE; }
    }
}
