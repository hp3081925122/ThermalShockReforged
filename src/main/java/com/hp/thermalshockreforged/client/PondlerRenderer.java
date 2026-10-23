package com.hp.thermalshockreforged.client;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.PondlerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class PondlerRenderer extends LegacyGeoEntityRenderer<PondlerEntity> {
    public PondlerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.2F;
    }

    @Override
    public RenderType getRenderType(PondlerEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
        return RenderTypes.entityTranslucent(texture);
    }

    private static final class Model extends LegacyGeoModel<PondlerEntity> {
        @Override
        public Identifier getModelResource(PondlerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/pondler.geo.json");
        }

        @Override
        public Identifier getTextureResource(PondlerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public Identifier getAnimationResource(PondlerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/pondler.animation.json");
        }
    }
}
