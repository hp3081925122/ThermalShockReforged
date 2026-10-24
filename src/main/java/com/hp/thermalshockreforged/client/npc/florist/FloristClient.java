package com.hp.thermalshockreforged.client.npc.florist;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.florist.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.geckolib.cache.model.BakedGeoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

public final class FloristClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(FloristContent.FLORIST.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { for (var page : FloristPage.values()) event.register(FloristContent.menu(page), FloristScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<FloristEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withRenderLayer(new Glow(this)); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Glow extends GeoEyesLayer<FloristEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = id("textures/entities/florist_glow.png");
        Glow(Renderer renderer) { super(renderer); }
        @Override protected Identifier texture(FloristEntity entity) { return TEXTURE; }
    }
    private static final class Model extends TsrGeoModel<FloristEntity> {
        @Override public Identifier getModelResource(FloristEntity entity) { return id("geo/umabel.geo.json"); }
        @Override public Identifier getAnimationResource(FloristEntity entity) { return id("animations/umabel.animation.json"); }
        @Override public Identifier getTextureResource(FloristEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
