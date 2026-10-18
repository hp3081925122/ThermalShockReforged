package com.hp.thermalshock.client;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.entity.PondlerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class PondlerRenderer extends GeoEntityRenderer<PondlerEntity> {
    public PondlerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.2F;
    }

    @Override
    public RenderType getRenderType(PondlerEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    private static final class Model extends GeoModel<PondlerEntity> {
        @Override
        public ResourceLocation getModelResource(PondlerEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "geo/pondler.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(PondlerEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(PondlerEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "animations/pondler.animation.json");
        }
    }
}
