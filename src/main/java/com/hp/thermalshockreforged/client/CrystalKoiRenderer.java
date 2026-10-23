package com.hp.thermalshockreforged.client;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.CrystalKoiEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class CrystalKoiRenderer extends LegacyGeoEntityRenderer<CrystalKoiEntity> {
    public CrystalKoiRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.2F;
    }

    @Override
    public RenderType getRenderType(CrystalKoiEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
        return RenderTypes.entityTranslucent(texture);
    }

    private static final class Model extends LegacyGeoModel<CrystalKoiEntity> {
        @Override
        public Identifier getModelResource(CrystalKoiEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/crystal_koi.geo.json");
        }

        @Override
        public Identifier getTextureResource(CrystalKoiEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public Identifier getAnimationResource(CrystalKoiEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/crystal_koi.animation.json");
        }
    }
}
