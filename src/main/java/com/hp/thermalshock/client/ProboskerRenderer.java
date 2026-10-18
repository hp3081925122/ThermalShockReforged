package com.hp.thermalshock.client;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.entity.ProboskerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class ProboskerRenderer extends GeoEntityRenderer<ProboskerEntity> {
    public ProboskerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.7F;
    }

    @Override
    public RenderType getRenderType(ProboskerEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    private static final class Model extends GeoModel<ProboskerEntity> {
        @Override
        public ResourceLocation getModelResource(ProboskerEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "geo/probosker.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ProboskerEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(ProboskerEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "animations/probosker.animation.json");
        }

        @Override
        public void setCustomAnimations(ProboskerEntity entity, long instance, AnimationState<ProboskerEntity> state) {
            var head = getAnimationProcessor().getBone("head");
            if (head != null) {
                var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
                head.setRotX(data.headPitch() * ((float) Math.PI / 180));
                head.setRotY(data.netHeadYaw() * ((float) Math.PI / 180));
            }
        }
    }
}
