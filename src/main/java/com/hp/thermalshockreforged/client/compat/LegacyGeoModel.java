package com.hp.thermalshockreforged.client.compat;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public abstract class LegacyGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    private T current;
    @Override public void addAdditionalStateData(T animatable, Object context, GeoRenderState state) { current = animatable; super.addAdditionalStateData(animatable, context, state); }
    @Override public Identifier getModelResource(GeoRenderState state) { return getModelResource(current); }
    @Override public Identifier getTextureResource(GeoRenderState state) { return getTextureResource(current); }
    public abstract Identifier getModelResource(T entity);
    public abstract Identifier getTextureResource(T entity);
    public void setCustomAnimations(T entity, long instanceId, AnimationTest<T> state) { }
    public LegacyAnimationProcessor getAnimationProcessor() { return LegacyAnimationProcessor.INSTANCE; }
    public static final class LegacyAnimationProcessor { private static final LegacyAnimationProcessor INSTANCE = new LegacyAnimationProcessor(); public LegacyBone getBone(String name) { return new LegacyBone(); } }
    public static final class LegacyBone { public void setRotX(float value) { } public void setRotY(float value) { } public void setRotZ(float value) { } public void setScaleX(float value) { } public void setScaleY(float value) { } public void setScaleZ(float value) { } }
}
