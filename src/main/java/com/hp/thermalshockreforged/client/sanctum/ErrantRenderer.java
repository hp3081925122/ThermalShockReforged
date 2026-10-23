package com.hp.thermalshockreforged.client.sanctum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.ErrantEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class ErrantRenderer extends LegacyGeoEntityRenderer<ErrantEntity> {
    public ErrantRenderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = .5F; withScale(1); }
    @Override public RenderType getRenderType(ErrantEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends LegacyGeoModel<ErrantEntity> {
        @Override public Identifier getModelResource(ErrantEntity entity) { return resource("geo/errant.geo.json"); }
        @Override public Identifier getTextureResource(ErrantEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(ErrantEntity entity) { return resource("animations/errant.animation.json"); }
        @Override public void setCustomAnimations(ErrantEntity entity, long instanceId, AnimationTest<ErrantEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("bipedHead");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
