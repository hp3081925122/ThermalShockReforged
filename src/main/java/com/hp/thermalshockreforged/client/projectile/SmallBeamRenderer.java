package com.hp.thermalshockreforged.client.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.PartProjectileRenderer;
import com.hp.thermalshockreforged.client.weapons.FirearmBulletRenderer;
import com.hp.thermalshockreforged.entity.projectile.LegacyArrowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public final class SmallBeamRenderer<T extends LegacyArrowEntity> extends PartProjectileRenderer<T> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/entities/zerkglow.png");
    public SmallBeamRenderer(EntityRendererProvider.Context context) { super(context, context.bakeLayer(FirearmBulletRenderer.LAYER).getChild("group"), TEXTURE); }
}
