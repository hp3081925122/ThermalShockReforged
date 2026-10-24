package com.hp.thermalshockreforged.client.geo;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public abstract class TsrGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    private static final DataTicket<Identifier> MODEL = DataTicket.create("thermal_shock_reforged:model", Identifier.class);
    private static final DataTicket<Identifier> TEXTURE = DataTicket.create("thermal_shock_reforged:texture", Identifier.class);

    public abstract Identifier getModelResource(T animatable);

    public abstract Identifier getTextureResource(T animatable);

    @Override
    public void addAdditionalStateData(T animatable, @Nullable Object relatedObject, GeoRenderState state) {
        state.addGeckolibData(MODEL, getModelResource(animatable));
        state.addGeckolibData(TEXTURE, getTextureResource(animatable));
    }

    @Override
    public final Identifier getModelResource(GeoRenderState state) {
        return state.getGeckolibData(MODEL);
    }

    @Override
    public final Identifier getTextureResource(GeoRenderState state) {
        return state.getGeckolibData(TEXTURE);
    }

    public void adjustBones(GeoRenderState state, BoneSnapshots bones) {
    }

    public static float headPitch(GeoRenderState state) {
        return state.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0F);
    }

    public static float netHeadYaw(GeoRenderState state) {
        return state instanceof LivingEntityRenderState living ? living.yRot : 0;
    }

    public static boolean is(GeoRenderState state, Class<?> type) {
        Class<?> animatable = state.getGeckolibData(DataTickets.ANIMATABLE_CLASS);
        return animatable != null && type.isAssignableFrom(animatable);
    }

    public static void setRotX(BoneSnapshot bone, float value) {
        bone.setRotX(value - bone.getBone().baseRotX());
    }

    public static void setRotY(BoneSnapshot bone, float value) {
        bone.setRotY(value - bone.getBone().baseRotY());
    }

    public static void lookAt(GeoRenderState state, BoneSnapshots bones, String bone) {
        float pitch = headPitch(state) * Mth.DEG_TO_RAD;
        float yaw = netHeadYaw(state) * Mth.DEG_TO_RAD;
        bones.ifPresent(bone, snapshot -> {
            setRotX(snapshot, pitch);
            setRotY(snapshot, yaw);
        });
    }
}
