package com.hp.thermalshockreforged.client.flashring;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.flashring.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.geckolib.cache.model.BakedGeoModel;

public final class BlindingLanternFlashRingRenderer extends TsrGeoEntityRenderer<BlindingLanternFlashRingEntity> {
    public BlindingLanternFlashRingRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0;  withRenderLayer(new Glow(this)); }
    public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(BlindingLanternFlashRingContent.FLASH_RING.get(), BlindingLanternFlashRingRenderer::new); }
    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends TsrGeoModel<BlindingLanternFlashRingEntity> {
        @Override public Identifier getModelResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("geo/lantern_flash_ring.geo.json"); }
        @Override public Identifier getAnimationResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("animations/lantern_flash_ring.animation.json"); }
        @Override public Identifier getTextureResource(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("textures/entities/" + entity.texture() + ".png"); }
    }

    private static final class Glow extends GeoEyesLayer<BlindingLanternFlashRingEntity, Void, EntityRenderState> {
        private Glow(BlindingLanternFlashRingRenderer renderer) { super(renderer); }
        @Override protected Identifier texture(BlindingLanternFlashRingEntity entity) { return BlindingLanternFlashRingContent.id("textures/entities/yellow_light_flash.png"); }
    }
}
