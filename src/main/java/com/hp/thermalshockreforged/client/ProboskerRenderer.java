package com.hp.thermalshockreforged.client;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ProboskerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class ProboskerRenderer extends LegacyGeoEntityRenderer<ProboskerEntity> {
    public ProboskerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.7F;
    }

    @Override
    public RenderType getRenderType(ProboskerEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) {
        return RenderTypes.entityTranslucent(texture);
    }

    private static final class Model extends LegacyGeoModel<ProboskerEntity> {
        @Override
        public Identifier getModelResource(ProboskerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "geo/probosker.geo.json");
        }

        @Override
        public Identifier getTextureResource(ProboskerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/" + entity.texture() + ".png");
        }

        @Override
        public Identifier getAnimationResource(ProboskerEntity entity) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "animations/probosker.animation.json");
        }

        @Override
        public void setCustomAnimations(ProboskerEntity entity, long instance, AnimationTest<ProboskerEntity> state) {
            var head = getAnimationProcessor().getBone("head");
            if (head != null) {
                var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
                head.setRotX(data.headPitch() * ((float) Math.PI / 180));
                head.setRotY(data.netHeadYaw() * ((float) Math.PI / 180));
            }
        }
    }
}
