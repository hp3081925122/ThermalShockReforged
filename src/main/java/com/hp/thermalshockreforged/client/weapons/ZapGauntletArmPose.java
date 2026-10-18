package com.hp.thermalshockreforged.client.weapons;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public final class ZapGauntletArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> POSE = new EnumProxy<>(HumanoidModel.ArmPose.class, false,
            (IArmPoseTransformer) (model, entity, arm) -> { if (arm != HumanoidArm.LEFT) model.rightArm.xRot = 80 + model.head.xRot; });
    private ZapGauntletArmPose() { }
}
