package com.hp.thermalshockreforged.client.attack;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import net.minecraft.client.model.animal.fish.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public final class AttackCarrierRenderer<T extends AttackCarrierBody> extends MobRenderer<T, LivingEntityRenderState, CodModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/zerkglow.png");

    public AttackCarrierRenderer(EntityRendererProvider.Context context) {
        super(context, new CodModel(context.bakeLayer(ModelLayers.COD)), 0);
        ThermalShockReforged.LOGGER.debug("Attack carrier renderer initialized with legacy Cod model and zero shadow");
    }
    @Override public LivingEntityRenderState createRenderState() { return new LivingEntityRenderState(); }
    @Override public Identifier getTextureLocation(LivingEntityRenderState state) { return TEXTURE; }
    @Override protected boolean isBodyVisible(LivingEntityRenderState state) { return false; }
}
