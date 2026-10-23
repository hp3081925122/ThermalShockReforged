package com.hp.thermalshockreforged.client.compat;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

public abstract class LegacyMobRenderer<T extends Mob> extends MobRenderer<T, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected LegacyMobRenderer(EntityRendererProvider.Context context, EntityModel model, float shadow) { super(context, model, shadow); }
    @Override public LivingEntityRenderState createRenderState() { return new LivingEntityRenderState(); }
    @Override public Identifier getTextureLocation(LivingEntityRenderState state) { return Identifier.withDefaultNamespace("textures/misc/white.png"); }
    public Identifier getTextureLocation(T entity) { return Identifier.withDefaultNamespace("textures/misc/white.png"); }
    protected boolean isBodyVisible(T entity) { return true; }
}
