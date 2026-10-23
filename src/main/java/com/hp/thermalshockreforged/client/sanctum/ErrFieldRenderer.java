package com.hp.thermalshockreforged.client.sanctum;
import com.hp.thermalshockreforged.client.compat.LegacyMobRenderer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.sanctum.ErrFieldEntity;
import net.minecraft.client.model.animal.fish.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;

public final class ErrFieldRenderer extends LegacyMobRenderer<ErrFieldEntity> {
    public ErrFieldRenderer(EntityRendererProvider.Context context) { super(context, new CodModel(context.bakeLayer(ModelLayers.COD)), 0); }
    @Override public Identifier getTextureLocation(ErrFieldEntity entity) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/zerkglow.png"); }
    @Override protected boolean isBodyVisible(ErrFieldEntity entity) { return false; }
}
