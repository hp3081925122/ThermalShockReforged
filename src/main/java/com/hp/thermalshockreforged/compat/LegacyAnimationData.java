package com.hp.thermalshockreforged.compat;

import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.DataTickets;

public record LegacyAnimationData(float headPitch, float netHeadYaw) {
    public static LegacyAnimationData from(AnimationTest<?> state) {
        return new LegacyAnimationData(state.getDataOrDefault(DataTickets.ENTITY_PITCH, 0.0F), state.getDataOrDefault(DataTickets.ENTITY_YAW, 0.0F));
    }

    public static LegacyAnimationData from(LegacyAnimationController.LegacyAnimationState<?> state) {
        return new LegacyAnimationData(0.0F, 0.0F);
    }
}
