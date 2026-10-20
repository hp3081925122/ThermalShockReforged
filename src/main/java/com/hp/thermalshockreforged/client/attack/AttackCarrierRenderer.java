package com.hp.thermalshockreforged.client.attack;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class AttackCarrierRenderer<T extends AttackCarrierBody> extends MobRenderer<T, CodModel<T>> {
    public AttackCarrierRenderer(EntityRendererProvider.Context context) {
        super(context, new CodModel<>(context.bakeLayer(ModelLayers.COD)), 0);
        ThermalShockReforged.LOGGER.debug("Attack carrier renderer initialized with legacy Cod model and zero shadow");
    }
    @Override public ResourceLocation getTextureLocation(T entity) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/zerkglow.png"); }
    @Override protected boolean isBodyVisible(T entity) { return false; }
}
