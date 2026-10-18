package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.CrystalKoiEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class CrystalKoiRenderer extends GeoEntityRenderer<CrystalKoiEntity> {
    public CrystalKoiRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.2F;
    }

    @Override
    public RenderType getRenderType(CrystalKoiEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    private static final class Model extends GeoModel<CrystalKoiEntity> {
        @Override
        public ResourceLocation getModelResource(CrystalKoiEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/crystal_koi.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(CrystalKoiEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(CrystalKoiEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/crystal_koi.animation.json");
        }
    }
}
