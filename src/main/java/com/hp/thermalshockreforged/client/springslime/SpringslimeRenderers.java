package com.hp.thermalshockreforged.client.springslime;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.springslime.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class SpringslimeRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SpringslimeContent.SPRING.get(), Renderer::new);
        event.registerEntityRenderer(SpringslimeContent.PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SpringslimeContent.FADE.get(), NoopRenderer::new);
    }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoEntityRenderer<SpringslimeEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 1.2F; }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
        @Override protected float getDeathMaxRotation(GeoRenderState state) { return 0; }
    }
    private static final class Model extends TsrGeoModel<SpringslimeEntity> {
        @Override public Identifier getModelResource(SpringslimeEntity entity) { return id("geo/violetsplat.geo.json"); }
        @Override public Identifier getAnimationResource(SpringslimeEntity entity) { return id("animations/violetsplat.animation.json"); }
        @Override public Identifier getTextureResource(SpringslimeEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
}
