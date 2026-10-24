package com.hp.thermalshockreforged.client;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ForecastEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;

public final class ForecastRenderer extends TsrGeoEntityRenderer<ForecastEntity> {
    public ForecastRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.2F;
        withRenderLayer(new Glow(this));
    }

    @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }

    private static final class Model extends TsrGeoModel<ForecastEntity> {
        @Override
        public Identifier getModelResource(ForecastEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/forecast.geo.json");
        }

        @Override
        public Identifier getTextureResource(ForecastEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public Identifier getAnimationResource(ForecastEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/forecast.animation.json");
        }
    }

    private static final class Glow extends GeoEyesLayer<ForecastEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/forecast_glow.png");

        private Glow(ForecastRenderer renderer) {
            super(renderer);
        }

        @Override protected Identifier texture(ForecastEntity entity) { return TEXTURE; } @Override protected int light(int packedLight) { return 15728640; }
    }
}
