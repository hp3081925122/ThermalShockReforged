package com.hp.thermalshockreforged.client.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.ErrFieldEntity;
import net.minecraft.client.model.animal.fish.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public final class ErrFieldRenderer extends MobRenderer<ErrFieldEntity, LivingEntityRenderState, CodModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/zerkglow.png");

    public ErrFieldRenderer(EntityRendererProvider.Context context) { super(context, new CodModel(context.bakeLayer(ModelLayers.COD)), 0); }
    @Override public LivingEntityRenderState createRenderState() { return new LivingEntityRenderState(); }
    @Override public Identifier getTextureLocation(LivingEntityRenderState state) { return TEXTURE; }
    @Override protected boolean isBodyVisible(LivingEntityRenderState state) { return false; }
}
