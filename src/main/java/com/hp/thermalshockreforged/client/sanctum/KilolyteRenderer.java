package com.hp.thermalshockreforged.client.sanctum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.KilolyteEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class KilolyteRenderer extends LegacyGeoEntityRenderer<KilolyteEntity> {
    public KilolyteRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(2); }
    @Override public RenderType getRenderType(KilolyteEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<KilolyteEntity> {
        @Override public Identifier getModelResource(KilolyteEntity entity) { return resource("geo/kilolyte.geo.json"); }
        @Override public Identifier getTextureResource(KilolyteEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(KilolyteEntity entity) { return resource("animations/kilolyte.animation.json"); }
        @Override public void setCustomAnimations(KilolyteEntity entity, long instanceId, AnimationTest<KilolyteEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
