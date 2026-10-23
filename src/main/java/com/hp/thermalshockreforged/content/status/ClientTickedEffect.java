package com.hp.thermalshockreforged.content.status;

import net.minecraft.world.entity.LivingEntity;

public interface ClientTickedEffect {
    void clientEffectTick(LivingEntity entity, int amplifier);
}
