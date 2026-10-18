package com.hp.thermalshockreforged.client.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.ErrFieldEntity;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

public final class ErrFieldRenderer extends MobRenderer<ErrFieldEntity, CodModel<ErrFieldEntity>> {
    public ErrFieldRenderer(EntityRendererProvider.Context context) { super(context, new CodModel<>(context.bakeLayer(ModelLayers.COD)), 0); }
    @Override public ResourceLocation getTextureLocation(ErrFieldEntity entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/zerkglow.png"); }
    @Override protected boolean isBodyVisible(ErrFieldEntity entity) { return false; }
}
