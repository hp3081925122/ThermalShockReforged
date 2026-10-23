package com.hp.thermalshockreforged.client.moonoid;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.moonoid.LunarFaunaContent;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class LunarFaunaRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LunarFaunaContent.MOONOID.get(), Renderer::new);
        event.registerEntityRenderer(LunarFaunaContent.STARRITE.get(), Renderer::new);
    }
    private static final class Renderer extends LegacyGeoEntityRenderer<AnimatedMonsterEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .2F; }
        @Override public RenderType getRenderType(AnimatedMonsterEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends LegacyGeoModel<AnimatedMonsterEntity> {
        private Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
        @Override public Identifier getModelResource(AnimatedMonsterEntity entity) { return id("geo/moonoid.geo.json"); }
        @Override public Identifier getAnimationResource(AnimatedMonsterEntity entity) { return id("animations/moonoid.animation.json"); }
        @Override public Identifier getTextureResource(AnimatedMonsterEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
    }
    private LunarFaunaRenderers() { }
}
