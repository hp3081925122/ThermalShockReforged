package com.hp.thermalshockreforged.client.pots;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.pots.SkullscorEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class SkullscorRenderer extends LegacyGeoEntityRenderer<SkullscorEntity> {
    public SkullscorRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0.5F; withScale(1); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    @Override public RenderType getRenderType(SkullscorEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static final class Model extends LegacyGeoModel<SkullscorEntity> {
        @Override public Identifier getModelResource(SkullscorEntity entity) { return resource("geo/skullscor.geo.json"); }
        @Override public Identifier getAnimationResource(SkullscorEntity entity) { return resource("animations/skullscor.animation.json"); }
        @Override public Identifier getTextureResource(SkullscorEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(SkullscorEntity entity, long instanceId, AnimationTest<SkullscorEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
